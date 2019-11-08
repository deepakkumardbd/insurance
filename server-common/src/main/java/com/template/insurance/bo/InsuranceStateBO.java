package com.template.insurance.bo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.template.insurance.deserializer.InsuranceStateDeserializer;

@JsonDeserialize(using = InsuranceStateDeserializer.class)
public class InsuranceStateBO {
    private String hospitalName;
    private String insuranceComp;
    private String insuranceId;
    private String initialComments;
    private Integer roomNo;
    private Integer bedNo;
    private String doctorName;
    private String dept;
    private String state;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public void setInsuranceComp(String insuranceComp) {
        this.insuranceComp = insuranceComp;
    }

    public void setInsuranceId(String insuranceId) {
        this.insuranceId = insuranceId;
    }

    public void setInitialComments(String initialComments) {
        this.initialComments = initialComments;
    }

    public void setRoomNo(Integer roomNo) {
        this.roomNo = roomNo;
    }

    public void setBedNo(Integer bedNo) {
        this.bedNo = bedNo;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public String getInsuranceComp() {
        return insuranceComp;
    }

    public String getInsuranceId() {
        return insuranceId;
    }

    public String getInitialComments() {
        return initialComments;
    }

    public Integer getRoomNo() {
        return roomNo;
    }

    public Integer getBedNo() {
        return bedNo;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getDept() {
        return dept;
    }
}
