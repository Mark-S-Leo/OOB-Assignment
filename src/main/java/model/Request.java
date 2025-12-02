package model;

public class Request {
    private String requestId;
    private String studentTp;
    private String lecturerTp;
    private String slotId;
    private String reason;
    private String status;

    public Request(String requestId, String studentTp, String lecturerTp, String slotId, String reason, String status) {
        this.requestId = requestId;
        this.studentTp = studentTp;
        this.lecturerTp = lecturerTp;
        this.slotId = slotId;
        this.reason = reason;
        this.status = status;
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

    public String getReason() {
        return reason;
    }

    public String getStatus() {
        return status;
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

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return requestId + "|" + studentTp + "|" + lecturerTp + "|" + slotId + "|" + reason + "|" + status;
    }
}
