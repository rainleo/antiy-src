package com.antiy.request.vul;

import com.antiy.exception.RequestParamValidateException;
import com.antiy.validation.ObjectValidator;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;

/**
 * 审批信息
 *
 * @author lvliang
 * @since 2020-02-06
 */

public class VulExamineInfoRequest implements ObjectValidator {
    @ApiModelProperty("任务id")
    private Integer taskId;
    @ApiModelProperty("漏洞id")
    private Integer vulId;
    @ApiModelProperty("系统属性:1.普通系统2.重要或设计用户敏感信息系统")
    private Integer systemType;
    @ApiModelProperty("2通过3未通过")
    private Integer result;
    @ApiModelProperty("审批理由")
    @Length(max = 512, message = "审核理由不能超过512")
    private String  reason;
    @ApiModelProperty("审批材料名称")
    private String  examineMaterialName;
    @ApiModelProperty("审批材料路径")
    private String  examineMaterialUrl;

    public Integer getSystemType() {
        return systemType;
    }

    public void setSystemType(Integer systemType) {
        this.systemType = systemType;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public Integer getVulId() {
        return vulId;
    }

    public void setVulId(Integer vulId) {
        this.vulId = vulId;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getExamineMaterialName() {
        return examineMaterialName;
    }

    public void setExamineMaterialName(String examineMaterialName) {
        this.examineMaterialName = examineMaterialName;
    }

    public String getExamineMaterialUrl() {
        return examineMaterialUrl;
    }

    public void setExamineMaterialUrl(String examineMaterialUrl) {
        this.examineMaterialUrl = examineMaterialUrl;
    }

    @Override
    public void validate() throws RequestParamValidateException {

    }

}