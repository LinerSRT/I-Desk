package com.liner.i_desk.Firebase;

import java.io.Serializable;

public class CheckObject implements Serializable {
    private String checkName;
    private Boolean checkStatus;

    public CheckObject(String checkName, Boolean checkStatus) {
        this.checkName = checkName;
        this.checkStatus = checkStatus;
    }

    public CheckObject() {
    }

    public String getCheckName() {
        return checkName;
    }

    public void setCheckName(String checkName) {
        this.checkName = checkName;
    }

    public Boolean getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(Boolean checkStatus) {
        this.checkStatus = checkStatus;
    }
}
