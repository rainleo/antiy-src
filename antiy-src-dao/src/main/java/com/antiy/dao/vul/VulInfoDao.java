package com.antiy.dao.vul;

import com.antiy.entity.vul.FileInfo;
import com.antiy.entity.vul.VulInfo;
import com.antiy.query.vul.VulInfoQuery;
import com.antiy.request.BaseRequest;
import com.antiy.response.vul.VulInfoResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p> Mapper 接口 </p>
 *
 * @author lvliang
 * @since 2020-02-06
 */
public interface VulInfoDao {

    Integer checkRepeat(@Param("id") Integer id, @Param("taskId") Integer taskId, @Param("vulName") String vulName,
                        @Param("ip") String ip, @Param("vulAddress") String vulAddress,
                        @Param("vulPort") Integer vulPort);

    void saveSingle(VulInfo vulInfo);

    Integer queryCount(VulInfoQuery vulInfoQuery);

    List<VulInfoResponse> queryList(VulInfoQuery vulInfoQuery);

    void updateVulNo(@Param("vulNo") String vulNo, @Param("id") Integer id);

    Integer updateSingle(VulInfo vulInfo);

    VulInfoResponse queryDetail(@Param("id") Integer id);

    void updateVulStatus(VulInfo v);

    List<Map<String, Object>> getVulSubmitTrend(@Param("start") Long start, @Param("end") Long end,
                                                @Param("taskId") Integer taskId);

    List<Map<String, Object>> getVulRepairTrend(@Param("start") Long start, @Param("end") Long end,
                                                @Param("taskId") Integer taskId);

    List<String> queryNoPassVulByUserId(@Param("userId") long userId);

    FileInfo queryFilePath(BaseRequest request);
}
