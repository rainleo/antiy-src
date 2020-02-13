package com.antiy.service.vul.impl;

import com.antiy.base.BaseConverter;
import com.antiy.base.PageResult;
import com.antiy.common.utils.LogUtils;
import com.antiy.dao.vul.TaskInfoDao;
import com.antiy.dao.vul.VulExamineInfoDao;
import com.antiy.dao.vul.VulInfoDao;
import com.antiy.entity.vul.TaskInfo;
import com.antiy.entity.vul.VulInfo;
import com.antiy.enums.user.VulStatusEnum;
import com.antiy.enums.user.VulTypeEnum;
import com.antiy.query.vul.VulInfoQuery;
import com.antiy.request.vul.VulInfoRequest;
import com.antiy.response.vul.VulExamineInfoResponse;
import com.antiy.response.vul.VulInfoResponse;
import com.antiy.service.vul.IVulInfoService;
import com.antiy.util.BusinessExceptionUtils;
import com.antiy.util.LoginUserUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

/**
 * <p> 服务实现类 </p>
 *
 * @author lvliang
 * @since 2020-02-06
 */
@Service
public class VulInfoServiceImpl implements IVulInfoService {

    private Logger                                 logger        = LogUtils.get(this.getClass());
    @Resource
    private LoginUserUtil                          loginUserUtil;
    @Resource
    private VulInfoDao                             vulInfoDao;
    @Resource
    private TaskInfoDao                            taskInfoDao;
    @Resource
    private VulExamineInfoDao                      vulExamineInfoDao;
    private BaseConverter<VulInfoRequest, VulInfo> baseConverter = new BaseConverter();

    @Override
    public void saveSingle(VulInfoRequest vulInfoRequest) {
        // 检查任务是否已经过期
        TaskInfo taskInfo = taskInfoDao.queryById(vulInfoRequest.getTaskId());
        if (Objects.isNull(taskInfo) || taskInfo.getEndTime() < System.currentTimeMillis()) {
            BusinessExceptionUtils.isTrue(false, "任务不存在或已过期,提交失败");
        }
        // 检查漏洞是否存在
        Integer count = vulInfoDao.checkRepeat(vulInfoRequest.getVulName(), vulInfoRequest.getVulType(),
            vulInfoRequest.getVulAddress());
        if (count > 0) {
            logger.info("漏洞已存在");
            BusinessExceptionUtils.isTrue(false, "该漏洞已存在,提交失败");
        }
        VulInfo vulInfo = baseConverter.convert(vulInfoRequest, VulInfo.class);
        vulInfo.setVulStatus(VulStatusEnum.WAIT_EXAMINE.getCode());
        vulInfo.setVulDepartment(loginUserUtil.getUser().getDepartmentId());
        vulInfo.setCreateUser(loginUserUtil.getUser().getBusinessId());
        vulInfo.setGmtCreate(System.currentTimeMillis());
        vulInfo.setModifyUser(loginUserUtil.getUser().getBusinessId());
        vulInfo.setStatus(1);
        vulInfoDao.saveSingle(vulInfo);
        // 更新编号
        vulInfoDao.updateVulNo(getVulNo(vulInfoRequest.getVulType(), vulInfo.getId()), vulInfo.getId());
    }

    @Override
    public PageResult<VulInfoResponse> queryList(VulInfoQuery vulInfoQuery) {
        /* Integer role = loginUserUtil.getUser().getRoleId(); if (role == 4) { //普通用户,只能查看自己提交的
         * vulInfoQuery.setUserId(loginUserUtil.getUser().getBusinessId()); } */
        Integer count = vulInfoDao.queryCount(vulInfoQuery);
        if (count <= 0) {
            return new PageResult<>(vulInfoQuery.getPageSize(), 0, vulInfoQuery.getCurrentPage(), Lists.newArrayList());
        }
        List<VulInfoResponse> vulInfoList = vulInfoDao.queryList(vulInfoQuery);
        return new PageResult<>(vulInfoQuery.getPageSize(), count, vulInfoQuery.getCurrentPage(), vulInfoList);
    }

    @Override
    public Integer updateSingle(VulInfoRequest vulInfoRequest) {
        VulInfo vulInfo = baseConverter.convert(vulInfoRequest, VulInfo.class);
        vulInfo.setGmtModify(System.currentTimeMillis());
        vulInfo.setModifyUser(loginUserUtil.getUser().getBusinessId());
        return vulInfoDao.updateSingle(vulInfo);
    }

    @Override
    public VulInfoResponse queryDetail(Integer id) {
        return vulInfoDao.queryDetail(id);
    }

    @Override
    public List<VulExamineInfoResponse> queryExamineHistory(Integer vulId) {
        List<VulExamineInfoResponse> vulExamineInfoResponseList = vulExamineInfoDao.queryList(vulId);
        return vulExamineInfoResponseList;
    }

    @Override
    public void exportData(VulInfoQuery vulInfoQuery, HttpServletResponse response,
                           HttpServletRequest request) throws IOException {
        String[] header = { "漏洞编号", "漏洞名称", "漏洞等级", "任务名称", "漏洞状态", "提交时间", "提交人员", "漏洞地址", "漏洞端口", "漏洞所属部门" };
        List<VulInfoResponse> vulInfoList = vulInfoDao.queryList(vulInfoQuery);
        if (CollectionUtils.isEmpty(vulInfoList)) {
            BusinessExceptionUtils.isTrue(false, "暂无数据");
        }
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("漏洞信息");
        HSSFRow row;
        row = sheet.createRow(0);
        row.setHeight((short) (22.50 * 25));// 设置行高
        for (int i = 0; i < header.length; i++) {
            row.createCell(i).setCellValue(header[i]);// 为第一个单元格设值
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 遍历所获取的数据
        for (int i = 0; i < vulInfoList.size(); i++) {
            row = sheet.createRow(i + 1);
            VulInfoResponse vulInfo = vulInfoList.get(i);
            row.createCell(0).setCellValue(vulInfo.getVulNo());
            row.createCell(1).setCellValue(vulInfo.getVulName());
            row.createCell(2).setCellValue(vulInfo.getVulLevelName());
            row.createCell(3).setCellValue(vulInfo.getTaskName());
            row.createCell(4).setCellValue(vulInfo.getVulStatusName());
            row.createCell(5).setCellValue(format.format(vulInfo.getCommitDate()));
            row.createCell(6).setCellValue(vulInfo.getCommitUserName());
            row.createCell(7).setCellValue(vulInfo.getVulAddress());
            row.createCell(8).setCellValue(vulInfo.getVulPort());
            row.createCell(9).setCellValue(vulInfo.getAddressOwnerName());
        }
        sheet.setDefaultRowHeight((short) (16.5 * 20));
        // 列宽自适应
        for (int i = 0; i <= 9; i++) {
            sheet.autoSizeColumn(i);
        }
        response.setCharacterEncoding("utf-8");
        response.setContentType("multipart/form-data");
        response.addHeader("Content-Disposition", "attachment;filename=ttt.xls");
        OutputStream outputStream = response.getOutputStream();
        wb.write(outputStream);
        outputStream.flush();
        outputStream.close();

    }


    private static String getVulNo(Integer type, Integer id) {
        Calendar date = Calendar.getInstance();
        String year = String.valueOf(date.get(Calendar.YEAR));
        return String.join("", year, VulTypeEnum.getType(type), String.valueOf(id));
    }
}
