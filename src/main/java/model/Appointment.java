package model;

public class Appointment {
    private String appointmentId;
    private String requestId;
    private String studentTp;
    private String lecturerTp;
    private String slotId;
    private String date;
    private String startTime;
    private String status;

    public Appointment(String appointmentId, String requestId, String studentTp, String lecturerTp, 
                      String slotId, String date, String startTime, String status) {
        this.appointmentId = appointmentId;
        this.requestId = requestId;
        this.studentTp = studentTp;
        this.lecturerTp = lecturerTp;
        this.slotId = slotId;
        this.date = date;
        this.startTime = startTime;
        this.status = status;
    }

    // Getters
    public String getAppointmentId() {
        return appointmentId;
    }

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

    public String getStatus() {
        return status;
    }

    // Setters
    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

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

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return appointmentId + "|" + requestId + "|" + studentTp + "|" + lecturerTp + "|" + 
               slotId + "|" + date + "|" + startTime + "|" + status;
    }
}
