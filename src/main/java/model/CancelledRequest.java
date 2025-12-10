package model;

public class CancelledRequest {
    private String requestId;
    private String studentTp;
    private String lecturerTp;
    private String slotId;
    private String originalReason;
    private String cancellationReason;
    private String cancelledBy;
    private String cancelledDate;

    public CancelledRequest(String requestId, String studentTp, String lecturerTp, String slotId, 
                           String originalReason, String cancellationReason, String cancelledBy, String cancelledDate) {
        this.requestId = requestId;
        this.studentTp = studentTp;
        this.lecturerTp = lecturerTp;
        this.slotId = slotId;
        this.originalReason = originalReason;
        this.cancellationReason = cancellationReason;
        this.cancelledBy = cancelledBy;
        this.cancelledDate = cancelledDate;
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

    public String getOriginalReason() {
        return originalReason;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public String getCancelledBy() {
        return cancelledBy;
    }

    public String getCancelledDate() {
        return cancelledDate;
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

    public void setOriginalReason(String originalReason) {
        this.originalReason = originalReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public void setCancelledBy(String cancelledBy) {
        this.cancelledBy = cancelledBy;
    }

    public void setCancelledDate(String cancelledDate) {
        this.cancelledDate = cancelledDate;
    }

    @Override
    public String toString() {
        return requestId + "|" + studentTp + "|" + lecturerTp + "|" + slotId + "|" + 
               originalReason + "|" + cancellationReason + "|" + cancelledBy + "|" + cancelledDate;
    }
}
