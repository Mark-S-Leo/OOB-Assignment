package model;

public class Request {
    private String requestId;
    private String studentTp;
    private String lecturerTp;
    private String slotId;
    private String date;
    private String startTime;
    private String endTime;
    private String reason;
    private String status;
    private String cancelReason;

    // Constructor with date and time (preferred)
    public Request(String requestId, String studentTp, String lecturerTp, String slotId, 
                   String date, String startTime, String endTime, String reason, String status) {
        this(requestId, studentTp, lecturerTp, slotId, date, startTime, endTime, reason, status, "");
    }
    
    // Full constructor with cancelReason
    public Request(String requestId, String studentTp, String lecturerTp, String slotId,
                   String date, String startTime, String endTime, String reason, String status, String cancelReason) {
        this.requestId = requestId;
        this.studentTp = studentTp;
        this.lecturerTp = lecturerTp;
        this.slotId = slotId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reason = reason;
        this.status = status;
        this.cancelReason = cancelReason != null ? cancelReason : "";
    }

    // Getters
    public String getRequestId() {
        return requestId;
    }

    public String getStudentTp() {
        return studentTp;
    }

    public String getLecturerTp() {
        return lecturerTp;
    }

    public String getSlotId() {
        return slotId;
    }

    public String getDate() {
        return date;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getReason() {
        return reason;
    }

    public String getStatus() {
        return status;
    }
    
    public String getCancelReason() {
        return cancelReason;
    }

    // Setters
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public void setStudentTp(String studentTp) {
        this.studentTp = studentTp;
    }

    public void setLecturerTp(String lecturerTp) {
        this.lecturerTp = lecturerTp;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    @Override
    public String toString() {
        return requestId + "|" + studentTp + "|" + lecturerTp + "|" + slotId + "|" + 
               date + "|" + startTime + "|" + endTime + "|" + reason + "|" + status + "|" + 
               (cancelReason != null ? cancelReason : "");
    }
}
