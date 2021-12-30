import java.math.BigDecimal;
import java.util.Date;

public class Log implements Comparable<Log>{
    private String phoneNumber;
    private Date startTime;
    private Date endTime;

    public Log() {
    }

    public Log(String phoneNumber, Date startTime, Date endTime) {
        this.phoneNumber = phoneNumber;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @Override
    public int compareTo(Log o) {
        return this.getPhoneNumber().compareTo(o.getPhoneNumber());
//        return o.getPhoneNumber().compareTo(this.getPhoneNumber());
    }
}
