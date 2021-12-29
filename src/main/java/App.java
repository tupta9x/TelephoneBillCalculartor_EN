import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.Temporal;
import java.util.*;

public class App {
    public static void main(String[] args) {

        final float FEE_NORMAL_HOUR = 1;
        final float FEE_OUTOF_HOUR = 0.5F;
        final float FEE_REDUCE = 0.2F;


        List<String> list = listLog();

        List<Log> logs = new ArrayList<Log>();

        List<Double> listPhoneNumber = new ArrayList<Double>();

        float charge = 0;

        for (int i = 0; i < list.size(); i++) {
            String[] parts = list.get(i).split(",", 3);

            String phoneNumber = parts[0];
            listPhoneNumber.add(Double.parseDouble(phoneNumber));

            Date start = StringToDate(parts[1]);
            Date end = StringToDate(parts[2]);

            logs.add(new Log(phoneNumber, start, end));
        }

        Collections.sort(logs);
        System.out.println("==============");
        for(Log log: logs) {
            System.out.println(log.getPhoneNumber());
        }
        System.out.println("==============");

        for (int i = 0; i < logs.size(); i++) {

            LocalDateTime startTime = Instant
                    .ofEpochMilli(logs.get(i).getStartTime().getTime())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            LocalDateTime endTime = Instant
                    .ofEpochMilli(logs.get(i).getEndTime().getTime())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

//            int startHours = logs.get(i).getStartTime().getHours();
            int startHours = startTime.getHour();

//            int endHours = logs.get(i).getEndTime().getHours();
            int endHours = endTime.getHour();



            if (!phoneOfMostBeCalled(logs).equals(logs.get(i).getPhoneNumber())) {
                long totalMinuteCalled = minuteCalculator(logs.get(i).getStartTime(), logs.get(i).getEndTime());
                int minuteCalled = 0;
                int count_tmp = 0;
                while (!startTime.isAfter(endTime)) {
                    if (minuteCalled < 5) {
                        if (startHours >= 8 && startHours <= 16 && endHours >= 8 && endHours <= 16) {
                            charge = charge + FEE_NORMAL_HOUR;

                            minuteCalled++;
                            startTime = startTime.plusMinutes(1).withSecond(0);
                            if(minuteCalled == 60) {
                                startTime.plusHours(1).withMinute(0).withSecond(0);
                                startHours++;
                            }
                            count_tmp++;
                            System.out.println("trong gio hanh chinh < 5 phut: gio : " + startTime.getHour() + " phut :" + count_tmp + " ... charge: " + charge);
                        } else {
                            charge = charge + FEE_OUTOF_HOUR;

                            minuteCalled++;
                            startTime = startTime.plusMinutes(1).withSecond(0);
                            if(minuteCalled == 60) {
                                startTime.plusHours(1).withMinute(0).withSecond(0);
                                startHours++;
                            }
                            count_tmp++;
                            System.out.println("ngoai gio hanh chinh < 5 phut: gio : " + startTime.getHour() + " phut :" + count_tmp + " ... charge: " + charge);
                        }
                    } else {
                        if (startHours >= 8 && startHours <= 16 && endHours >= 8 && endHours <= 16) {
                            charge += (FEE_NORMAL_HOUR - FEE_REDUCE) ;

                            minuteCalled++;
                            startTime = startTime.plusMinutes(1).withSecond(0);
                            if(minuteCalled == 60) {
                                startTime.plusHours(1).withMinute(0).withSecond(0);
                                startHours++;
                            }
                            count_tmp++;
                            System.out.println("trong gio hanh chinh > 5 phut: gio : " + startTime.getHour() + " phut :" + count_tmp + " ... charge: " + charge);
                        } else {
                            charge += (FEE_OUTOF_HOUR - FEE_REDUCE);
                            count_tmp++;
//                            System.out.println("ngoai gio hanh chinh > 5 phut: phut thu :" + count_tmp + " ... charge: " + charge);
                            minuteCalled++;
                            startTime = startTime.plusMinutes(1).withSecond(0);
                            if(minuteCalled == 60) {
                                startTime.plusHours(1).withMinute(0).withSecond(0);
                                startHours++;
                            }
                            System.out.println("ngoai gio hanh chinh > 5 phut: gio : " + startTime.getHour() + " phut :" + count_tmp + " ... charge: " + charge);
                        }
                    }
                }
            } else {
                System.out.println("So bi goi nhieu nhat");
                charge += 0;
            }
            System.out.println("cost: " + charge);
        }
        System.out.println("total cost: " + charge);
    }

    public static List<String> listLog() {
        List<String> list = new ArrayList<String>();
        list.add("420776562354,18-01-2020 08:20:00,18-01-2020 08:24:00");
        list.add("420776562354,18-01-2020 09:20:00,18-01-2020 09:24:00");
        list.add("420776562354,18-01-2020 09:20:00,18-01-2020 09:24:00");
//        list.add("420776562354,18-01-2020 08:59:20,18-01-2020 09:10:00");

        list.add("420776562355,18-01-2020 07:50:20,18-01-2020 09:00:20");
        list.add("420776562355,18-01-2020 07:50:20,18-01-2020 09:00:20");
        list.add("420776562355,18-01-2020 07:50:20,18-01-2020 09:00:20");
        return list;
    }

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
