import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.*;

public class App {
    public static void main(String[] args) {

        List<String> list = listLog();
        List<Log> logs = new ArrayList<Log>();
        List<Double> listPhoneNumber = new ArrayList<Double>();
        float charge = 0;
        float fee = 0;

//        tạo ra cái list chứa logs
        for (int i = 0; i < list.size(); i++) {
            String[] parts = list.get(i).split(",", 3);

            String phoneNumber = parts[0];
            listPhoneNumber.add(Double.parseDouble(phoneNumber));

            Date start = StringToDate(parts[1]);
            Date end = StringToDate(parts[2]);

            logs.add(new Log(phoneNumber, start, end));
        }

        for (int i = 0; i < logs.size(); i++) {
            long minute = minuteCalculator(logs.get(i).getStartTime(), logs.get(i).getEndTime());
            int startDate = logs.get(i).getStartTime().getDate();
            int startHours = logs.get(i).getStartTime().getHours();

            int endDate = logs.get(i).getEndTime().getDate();
            int endHours = logs.get(i).getEndTime().getHours();

            if (!phoneOfMostBeCalled(logs).equals(logs.get(i).getPhoneNumber())) {
                if (minute < 5) {
                    if (startDate == endDate) {
                        if (startHours >= 8 && startHours <= 16 && endHours >= 8 && endHours <= 16) {
                            fee = 1;
                            charge = charge + (fee * minute);
                        } else {
                            fee = 0.5F;
                            charge = charge + (fee * minute);
                        }
                    }

                } else {
                    if (startHours >= 8 && startHours <= 16) {
                        fee = 0.8F;
                        charge = 4F + (fee * (minute - 4));
                    } else {
                        fee = 0.3F;
                        charge = 2F + (fee * (minute - 4));
                    }
                }
            } else {
                charge = 0;
            }
            System.out.println("cost: " + charge);
        }

        // Show Most be called
//        String phone = phoneOfMostBeCalled(logs);

//        chỉ tính phút lẻ ban đầu, hay là tính làm tròn phút nếu có giấy lẻ
//        sau phút thứ 5 là mỗi giây đều đc giảm 0.2 hay chỉ có giây thứ 5 thôi
//        có 2 số cuộc gọi cao nhất thì lấy số nào

//       đổi hết ra giấy, trừ nhau => *100 * 60 = số phút đem làm tròn lên
//       lấy giờ bất đầu với giờ kết thúc
//       nếu giờ hành

        // tạo List lưu giá trị củ
        // Lấy ra thằng đc gọi nhiều nhất => free
        // ngược lại
        // số phút
        // tính số phút trước 6:01

        // case trước 5 phút với sau 5 phút
        // trong trước 5 phút nằm trong 8 - 16 thì giá là 1 kc
        // trong trước 5 phút nằm trong khoảng còn lại thì giá là 0.5 kc
        // trong tren 5 phút nằm trong khoảng nằm trong 8 - 16 thì giá là 0.8 kc
        // trong tren 5 phút nằm trong khoảng nằm trong còn lại thì giá là 0.3 kc
    }

    public static List<String> listLog() {
        List<String> list = new ArrayList<String>();
        list.add("420776562354,18-01-2020 08:20:00,18-01-2020 08:24:00");
        list.add("420776562354,18-01-2020 08:59:20,18-01-2020 09:10:00");

        list.add("420776562355,18-01-2020 08:59:20,18-01-2020 09:10:00");
        list.add("420776562355,18-01-2020 08:00:20,18-01-2020 18:00:00");
        // 8h - 16h: 4 + 0.8 * ((10 * 60) - 4) = 388
        // 16h - 18h: 2 + 0.3 * (2 * 60) =
//        list.add("420776562355,18-01-2020 08:59:20,18-01-2020 09:10:00");
//        list.add("420776562351,18-01-2020 08:59:20,18-01-2020 09:10:00");
        return list;
    }

    //    420776562353,18-01-2020 08:59:20,18-01-2020 09:10:00
    public static Date StringToDate(String date_s) {
        SimpleDateFormat simpledateformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date tempDate = null;

        try {
            tempDate = simpledateformat.parse(date_s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return tempDate;
    }

    public static long minuteCalculator(Date start, Date end) {
        long second = end.getTime() - start.getTime();
        long minute = (second / 1000) / 60;

        if (((second / 1000) % 60) != 0) {
            System.out.println(minute + 1);
            return minute + 1;
        }
        return minute;
    }

    /**
     * logic business
     *
     * @param logs
     * @return
     */
    public static String phoneOfMostBeCalled(List<Log> logs) {
        int extraIndex = 0; // hold index of bigest value WHEN there are two or more the same times be called
        int index = 0; // hold index of bigest value WHEN there are only
        int maxCount = 0; // time counting of the bigest value

        for (int i = 0; i < logs.size() - 1; i++) {

            int count = 0; // time counting
            for (int j = i + 1; j < logs.size(); j++) {
                if (logs.get(i).getPhoneNumber().equals(logs.get(j).getPhoneNumber())) {
                    count++;
                }
                if (maxCount == count) {
                    extraIndex = j;
                }
            }

            if (maxCount == count) {
                if (Double.parseDouble(logs.get(i).getPhoneNumber()) < Double.parseDouble(logs.get(extraIndex).getPhoneNumber())) {
                    return logs.get(extraIndex).getPhoneNumber();
                }
            }

            if (maxCount < count) {
                maxCount = count;
                index = i;
            }
        }
        return logs.get(index).getPhoneNumber();
    }
}
