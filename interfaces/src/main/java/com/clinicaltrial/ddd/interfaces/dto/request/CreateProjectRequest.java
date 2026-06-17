package com.clinicaltrial.ddd.interfaces.dto.request;

import java.util.Date;

/**
 * 创建项目请求.
 */
public class CreateProjectRequest {

    private String title;
    private String type;           // ProjectType name: INTERVENTIONAL / OBSERVATIONAL
    private String abbreviation;
    private String prefix;
    private Boolean openScreen;
    private Integer expectedSubjectSize;
    private String clinicalNumber;
    private String registrationNo;
    private Date expectStartAt;
    private Date expectEndAt;
    private String purpose;
    private String createUserId;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getAbbreviation() { return abbreviation; }
    public void setAbbreviation(String abbreviation) { this.abbreviation = abbreviation; }
    public String getPrefix() { return prefix; }
    public void setPrefix(String prefix) { this.prefix = prefix; }
    public Boolean getOpenScreen() { return openScreen; }
    public void setOpenScreen(Boolean openScreen) { this.openScreen = openScreen; }
    public Integer getExpectedSubjectSize() { return expectedSubjectSize; }
    public void setExpectedSubjectSize(Integer expectedSubjectSize) { this.expectedSubjectSize = expectedSubjectSize; }
    public String getClinicalNumber() { return clinicalNumber; }
    public void setClinicalNumber(String clinicalNumber) { this.clinicalNumber = clinicalNumber; }
    public String getRegistrationNo() { return registrationNo; }
    public void setRegistrationNo(String registrationNo) { this.registrationNo = registrationNo; }
    public Date getExpectStartAt() { return expectStartAt; }
    public void setExpectStartAt(Date expectStartAt) { this.expectStartAt = expectStartAt; }
    public Date getExpectEndAt() { return expectEndAt; }
    public void setExpectEndAt(Date expectEndAt) { this.expectEndAt = expectEndAt; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public String getCreateUserId() { return createUserId; }
    public void setCreateUserId(String createUserId) { this.createUserId = createUserId; }
}
