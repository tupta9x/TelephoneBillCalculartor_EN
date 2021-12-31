import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class CalculatorImp implements TelephoneBillCalculator {

	// Generic method to convert a set to a list
	public static <T> List<T> convertToList(Set<T> set) {
		return new ArrayList<>(set);
	}

	@Override
	public BigDecimal calculate(String phoneLog) {

		final BigDecimal FEE_NORMAL_HOUR = BigDecimal.ONE;
		final BigDecimal FEE_OUTOF_HOUR = BigDecimal.valueOf(0.5);
		final BigDecimal FEE_REDUCE = BigDecimal.valueOf(0.2);
		
		List<Log> logs = new ArrayList<Log>();
		List<Double> listPhoneNumber = new ArrayList<Double>();

		for (String row : phoneLog.split("\n")) {
			String[] data = row.split(",");
			Log log = new Log();

			if (data.length != 3) {
				System.err.println("The data is not valid");
				return null;
			}

			if (data[0].matches("^[0-9]{12}$") && validateJavaDate(data[1]) && validateJavaDate(data[2])) {

				listPhoneNumber.add(Double.parseDouble(data[0]));
				log.setPhoneNumber(data[0]);
				log.setStartTime(StringToDate(data[1]));
				log.setEndTime(StringToDate(data[2]));
				logs.add(log);
			}
			else {
				return null;
			}
		}

		if (logs.isEmpty() || logs == null) {
			System.err.println("Don't have any data");
			return null;
		}

		BigDecimal charge = BigDecimal.ZERO;
//		Collections.sort(logs);
//		System.out.println("==============");
//		for (Log log : logs) {
//			System.out.println(log.getPhoneNumber());
//		}
//		System.out.println("==============");

		for (int i = 0; i < logs.size(); i++) {

			LocalDateTime startTime = Instant.ofEpochMilli(logs.get(i).getStartTime().getTime())
					.atZone(ZoneId.systemDefault()).toLocalDateTime();

			LocalDateTime endTime = Instant.ofEpochMilli(logs.get(i).getEndTime().getTime())
					.atZone(ZoneId.systemDefault()).toLocalDateTime();

			int startHours = startTime.getHour();
			int startMinute = startTime.getMinute();
			int endHours = endTime.getHour();

//			if (!phoneOfMostBeCalled(logs).equals(logs.get(i).getPhoneNumber())) {
			if (!mostBeCalled(logs).equals(logs.get(i).getPhoneNumber())) {
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
							if (startMinute % 60 == 0) {
								startTime.plusHours(1).withMinute(0).withSecond(0);
								startHours++;
							}
							totalMinuteCalled--;
							count_tmp++;
							System.out.println("Trong gio hanh chinh < 5 phut: " + startTime.getHour() + " gio: "
									+ " phut thu " + count_tmp + " => charge: " + charge);
						} else {
							charge = charge.add(FEE_OUTOF_HOUR);
							minuteCalled++;

							startTime = startTime.plusMinutes(1).withSecond(0);

							startMinute++;
							if (startMinute % 60 == 0) {
								startTime.plusHours(1).withMinute(0).withSecond(0);
								startHours++;
							}
							totalMinuteCalled--;
							count_tmp++;
							System.out.println("Ngoai gio hanh chinh < 5 phut: " + startTime.getHour() + " gio: "
									+ " phut thu " + count_tmp + " => charge: " + charge);
						}
					} else {
						if (startHours >= 8 && startHours <= 16 && endHours >= 8 && endHours <= 16) {
							charge = charge.add(FEE_NORMAL_HOUR).subtract(FEE_REDUCE);
							minuteCalled++;
							startTime = startTime.plusMinutes(1).withSecond(0);

							startMinute++;
							if (startMinute % 60 == 0) {
								startTime.plusHours(1).withMinute(0).withSecond(0);
								startHours++;
							}
							totalMinuteCalled--;
							count_tmp++;
							System.out.println("Trong gio hanh chinh > 5 phut: " + startTime.getHour() + " gio: "
									+ " phut thu " + count_tmp + " => charge: " + charge);
						} else {
							charge = charge.add(FEE_OUTOF_HOUR).subtract(FEE_REDUCE);
							minuteCalled++;
							startTime = startTime.plusMinutes(1).withSecond(0);

							startMinute++;
							if (startMinute % 60 == 0) {
								startTime.plusHours(1).withMinute(0).withSecond(0);
								startHours++;
							}
							totalMinuteCalled--;
							count_tmp++;
							System.out.println("Ngoai gio hanh chinh > 5 phut: " + startTime.getHour() + " gio: "
									+ " phut thu " + count_tmp + " => charge: " + charge);
						}
					}
				}
			} else {
				charge.add(BigDecimal.ZERO);
			}
			System.out.println("cost: " + charge);
		}
//		System.out.println("total cost: " + charge);
		return charge;
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
			return minute + 1;
		}
		return minute;
	}

	public static String mostBeCalled(List<Log> logs) {
		Map<String, Long> phoneInfos = countByPhones(logs);
		LinkedHashMap<String, Long> sortedPhoneInfos = sortByValue(phoneInfos);

		Set entrySet = sortedPhoneInfos.entrySet();

		Iterator it = entrySet.iterator();
		System.out.println("HashMap Key-Value Pairs : ");

		while (it.hasNext()) {
			Map.Entry me = (Map.Entry) it.next();
			System.out.println("phone number: " + me.getKey() + " & " + " counting: " + me.getValue());
		}

		long maxValue = -1;
		String phone = null;
		for (Entry<String, Long> entry : sortedPhoneInfos.entrySet()) {
			if (maxValue == -1) {
				maxValue = entry.getValue();
				phone = entry.getKey();
				System.out.println("-1");
//				break;
				continue;
			}

			if (entry.getValue() < maxValue) {
				System.out.println("<");
				continue;
//				break;
			}

			if (Long.parseLong(entry.getKey()) > Long.parseLong(phone)) {
				System.out.println(">");
				phone = entry.getKey();
			}
		}
		System.out.println(phone);
		return phone;
	}

	private static Map<String, Long> countByPhones(List<Log> logs) {
		return logs.stream().filter(Objects::nonNull).map(Log::getPhoneNumber)
				.collect(Collectors.groupingBy(phone -> phone, Collectors.counting()));
	}

	private static LinkedHashMap<String, Long> sortByValue(final Map<String, Long> phoneInfos) {
		return phoneInfos.entrySet().stream().sorted((Map.Entry.<String, Long>comparingByValue().reversed()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}

	public static boolean validateJavaDate(String strDate)
	   {
		/* Check if date is 'null' */
		if (strDate.trim().equals(""))
		{
		    return true;
		}
		/* Date is not 'null' */
		else
		{
		    /*
		     * Set preferred date format,
		     * For example MM-dd-yyyy, MM.dd.yyyy,dd.MM.yyyy etc.*/
		    SimpleDateFormat sdfrmt = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		    sdfrmt.setLenient(false);
		    /* Create Date object
		     * parse the string into date 
	             */
		    try
		    {
		        Date javaDate = sdfrmt.parse(strDate); 
		        System.out.println(strDate+" is valid date format");
		    }
		    /* Date format is invalid */
		    catch (ParseException e)
		    {
		        System.out.println(strDate+" is Invalid Date format");
		        return false;
		    }
		    /* Return true if date format is valid */
		    return true;
		}
	   }
//	public static String phoneOfMostBeCalled(List<Log> logs) {
//
//		int extraIndex = 0;
//
//		int index = 0;
//
//		int maxCount = 0;
//
//		for (int i = 0; i < logs.size() - 1; i++) {
//
//			int count = 0;
//			for (int j = i + 1; j < logs.size(); j++) {
//				if (logs.get(i).getPhoneNumber().equals(logs.get(j).getPhoneNumber())) {
//					count++;
//				}
//				if (maxCount == count) {
//					extraIndex = j;
//				}
//			}
//
//			if (maxCount == count) {
//				if (Double.parseDouble(logs.get(i).getPhoneNumber()) < Double
//						.parseDouble(logs.get(extraIndex).getPhoneNumber())) {
////                	System.out.println(logs.get(extraIndex).getPhoneNumber());
//					return logs.get(extraIndex).getPhoneNumber();
//				}
//			}
//
//			if (maxCount < count) {
//				maxCount = count;
//				index = i;
//			}
//		}
////        System.out.println(logs.get(index).getPhoneNumber());
//		return logs.get(index).getPhoneNumber();
//	}

//	public static List<Log> phoneCouting(List<Log> logs) {
//
//		List<Log> logDouplicate = new ArrayList<Log>();
//
//		for (int i = 0; i < logs.size() - 1; i++) {
//
//			int count = 0;
//			for (int j = i + 1; j < logs.size(); j++) {
//				if (logs.get(i).getPhoneNumber().equals(logs.get(j).getPhoneNumber())) {
//					count++;
//				}
//				
//			}
//		}
//
//		return logDouplicate;
//	}

//	private static <T> Set<T> findDuplicates(Collection<T> collection) {
//
//		Set<T> duplicates = new LinkedHashSet<>();
//		Set<T> uniques = new HashSet<>();
//
//		for (T t : collection) {
//			if (!uniques.add(t)) {
//				duplicates.add(t);
//			}
//		}
//
//		return duplicates;
//	}
}