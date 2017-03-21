package com.wzk.mvc.model;

public class LogIcecoldmonitor {
    private Integer logId;

    private String logLevel;

    private String logCategory;

    private String logThread;

    private String logTime;

    private String logLocation;

    private String logNote;

    public Integer getLogId() {
        return logId;
    }

    public void setLogId(Integer logId) {
        this.logId = logId;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel == null ? null : logLevel.trim();
    }

    public String getLogCategory() {
        return logCategory;
    }

    public void setLogCategory(String logCategory) {
        this.logCategory = logCategory == null ? null : logCategory.trim();
    }

    public String getLogThread() {
        return logThread;
    }

    public void setLogThread(String logThread) {
        this.logThread = logThread == null ? null : logThread.trim();
    }

    public String getLogTime() {
        return logTime;
    }

    public void setLogTime(String logTime) {
        this.logTime = logTime == null ? null : logTime.trim();
    }

    public String getLogLocation() {
        return logLocation;
    }

    public void setLogLocation(String logLocation) {
        this.logLocation = logLocation == null ? null : logLocation.trim();
    }

    public String getLogNote() {
        return logNote;
    }

    public void setLogNote(String logNote) {
        this.logNote = logNote == null ? null : logNote.trim();
    }
}