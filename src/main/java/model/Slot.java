package model;

public class Slot {
    private String slotId;
    private String lecturerTp;
    private String date;
    private String startTime;
    private String endTime;
    private String status;

    public Slot(String slotId, String lecturerTp, String date, String startTime, String endTime, String status) {
        this.slotId = slotId;
        this.lecturerTp = lecturerTp;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    // Getters
    public String getSlotId() {
        return slotId;
    }

    public String getLecturerTp() {
        return lecturerTp;
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

    public String getStatus() {
        return status;
    }

    // Setters
    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public void setLecturerTp(String lecturerTp) {
        this.lecturerTp = lecturerTp;
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

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return slotId + "|" + lecturerTp + "|" + date + "|" + startTime + "|" + endTime + "|" + status;
    }
}
