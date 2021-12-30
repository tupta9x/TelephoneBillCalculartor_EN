import java.math.BigDecimal;
import java.security.AlgorithmConstraints;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class CalculatorImp implements TelephoneBillCalculator{

	@Override
	public BigDecimal calculate(String phoneLog) {
		// TODO Auto-generated method stub
		final BigDecimal FEE_NORMAL_HOUR = BigDecimal.ONE;
        final BigDecimal FEE_OUTOF_HOUR = BigDecimal.valueOf(0.5);
        final BigDecimal FEE_REDUCE = BigDecimal.valueOf(0.2);

        List<Log> logs = new ArrayList<Log>();
        List<Double> listPhoneNumber = new ArrayList<Double>();
        
//        if(logs.isEmpty()) {
//        	return BigDecimal.ZERO;
//        }
        
        for(String row : phoneLog.split("\n")) {
        	String[] data = row.split(",");
        	Log log = new Log();
        	
//        	if(data.length != 3) {
//        		return BigDecimal.ZERO;
//        	}
        	
        	listPhoneNumber.add(Double.parseDouble(data[0]));
        	
        	log.setPhoneNumber(data[0]);
        	log.setStartTime(StringToDate(data[1]));
        	log.setEndTime(StringToDate(data[2]));
        	
        	logs.add(log);
        	
        }        

        BigDecimal charge = BigDecimal.ZERO;

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

            int startHours = startTime.getHour();
            int startMinute = startTime.getMinute();
            int endHours = endTime.getHour();
///////////////////////////////////////////////////////////////////////////////
//            List<Log> test =findMostBeCalled(logs);
//            Set<Log> t = findDuplicates(logs);
//            System.out.println(test.size());
//            for(int n = 0; n < test.size(); n++) {
//            	System.out.println("()()() => " + test.get(n).getPhoneNumber());
//            }
            
//            for (Log element : t) {
//                System.out.println(">>>>> " +element);
//            }
//            System.out.println(">>>>> ");
//            System.out.println(findMostBeCalled(logs));////////////////////////
            if (!phoneOfMostBeCalled(logs).equals(logs.get(i).getPhoneNumber())) {
            	
                long totalMinuteCalled = minuteCalculator(logs.get(i).getStartTime(), logs.get(i).getEndTime());
                System.out.println("total minutes: " + totalMinuteCalled);
                int minuteCalled = 0;
                
                int count_tmp = 0;
                
                while (!startTime.isAfter(endTime) && totalMinuteCalled != 0) {
                    if (minuteCalled < 5) {
                        if (startHours >= 8 && startHours <= 16 && endHours >= 8 && endHours <= 16) {
                        	charge = charge.add(FEE_NORMAL_HOUR);
                            minuteCalled++;
                            startTime = startTime.plusMinutes(1).withSecond(0);
                            
                            startMinute++;
                            if(startMinute % 60 == 0) {
                                startTime.plusHours(1).withMinute(0).withSecond(0);
                                startHours++;
                            }
                            totalMinuteCalled--;
                            count_tmp++;
                            System.out.println("Trong gio hanh chinh < 5 phut: " + startTime.getHour() + " gio: "  + " phut thu " + count_tmp + " => charge: " + charge);
                        } else {
                        	charge = charge.add(FEE_OUTOF_HOUR);
                            minuteCalled++;
                            
                            startTime = startTime.plusMinutes(1).withSecond(0);
                            
                            startMinute++;
                            if(startMinute % 60 == 0) {
                                startTime.plusHours(1).withMinute(0).withSecond(0);
                                startHours++;
                            }
                            totalMinuteCalled--;
                            count_tmp++;
                            System.out.println("Ngoai gio hanh chinh < 5 phut: " + startTime.getHour() + " gio: "  + " phut thu " + count_tmp + " => charge: " + charge);
                        }
                    } else {
                        if (startHours >= 8 && startHours <= 16 && endHours >= 8 && endHours <= 16) {
                        	charge = charge.add(FEE_NORMAL_HOUR).subtract(FEE_REDUCE);
                            minuteCalled++;
                            startTime = startTime.plusMinutes(1).withSecond(0);
                            
                            startMinute++;
                            if(startMinute % 60 == 0) {
                                startTime.plusHours(1).withMinute(0).withSecond(0);
                                startHours++;
                            }
                            totalMinuteCalled--;
                            count_tmp++;
                            System.out.println("Trong gio hanh chinh > 5 phut: " + startTime.getHour() + " gio: "  + " phut thu " + count_tmp + " => charge: " + charge);
                        } else {
                        	charge = charge.add(FEE_OUTOF_HOUR).subtract(FEE_REDUCE);
                            minuteCalled++;
                            startTime = startTime.plusMinutes(1).withSecond(0);
                            
                            startMinute++;
                            if(startMinute % 60 == 0) {
                                startTime.plusHours(1).withMinute(0).withSecond(0);
                                startHours++;
                            }
                            totalMinuteCalled--;
                            count_tmp++;
                            System.out.println("Ngoai gio hanh chinh > 5 phut: " + startTime.getHour() + " gio: "  + " phut thu " + count_tmp + " => charge: " + charge);
                        }
                    }
                }
            } else {
                charge.add(BigDecimal.ZERO);
            }
            System.out.println("cost: " + charge);
        }
        System.out.println("total cost: " + charge);
        return charge;
	}
	


//    public static List<String> listLog() {
//        List<String> list = new ArrayList<String>();
//        list.add("420776562354,18-01-2020 08:20:00,18-01-2020 08:24:00");
//        list.add("420776562354,18-01-2020 09:20:00,18-01-2020 09:24:00");
//        list.add("420776562354,18-01-2020 09:20:00,18-01-2020 09:24:00");
////        list.add("420776562354,18-01-2020 08:59:20,18-01-2020 09:10:00");
//
//        list.add("420776562355,18-01-2020 07:50:20,18-01-2020 09:00:20");
//        list.add("420776562355,18-01-2020 07:50:20,18-01-2020 09:00:20");
//        list.add("420776562355,18-01-2020 07:50:20,18-01-2020 09:00:20");
//        return list;
//    }

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
//            System.out.println(minute + 1);
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

        int extraIndex = 0;

        int index = 0;

        int maxCount = 0;

        for (int i = 0; i < logs.size() - 1; i++) {

            int count = 0;
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
//                	System.out.println(logs.get(extraIndex).getPhoneNumber());
                    return logs.get(extraIndex).getPhoneNumber();
                }
            }

            if (maxCount < count) {
                maxCount = count;
                index = i;
            }
        }
//        System.out.println(logs.get(index).getPhoneNumber());
        return logs.get(index).getPhoneNumber();
    }
    
    
//	String logs = "" 
//	+ "420776562357,18-01-2020 07:10:00,18-01-2020 07:20:00\n" // 10
//	
//	+ "420776562356,18-01-2020 07:10:00,18-01-2020 07:30:10\n" // 20
//	+ "420776562356,18-01-2020 07:10:00,18-01-2020 07:30:00\n"
//	
//	+ "420776562355,18-01-2020 07:10:00,18-01-2020 07:40:10\n" // 30
//	+ "420776562355,18-01-2020 07:10:00,18-01-2020 07:40:10\n"
//	
//	+ "420776562354,18-01-2020 07:10:00,18-01-2020 07:50:00\n" // 40
//	+ "420776562354,18-01-2020 07:10:00,18-01-2020 07:50:00\n";
    public static List<Log> findMostBeCalled(List<Log> logs) {
    	
    	List<Log> logDouplicate = new ArrayList<Log>();
    	
    	for (int i = 0; i < logs.size() - 1; i++) {
    		
    		int count = 0;    		
    		for (int j = i + 1; j < logs.size(); j++) {
    			if (logs.get(i).getPhoneNumber().equals(logs.get(j).getPhoneNumber())) {
                    count++;
                    logDouplicate.add(logs.get(j));
    			} 
    		}
    	}
    	
    	
    	return logDouplicate;
    }
    
    private <T> Set<T> findDuplicates(Collection<T> collection) {

        Set<T> duplicates = new LinkedHashSet<>();
        Set<T> uniques = new HashSet<>();

        for(T t : collection) {
            if(!uniques.add(t)) {
                duplicates.add(t);
            }
        }

        return duplicates;
    }
}