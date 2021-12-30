import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * 
 */

/**
 * @author AnhTu
 *
 */
class AppTest {
	
	@Test
	void emptyLog() {
		TelephoneBillCalculator calculatorImp = new CalculatorImp();
		String logs = "" ;
		BigDecimal result = calculatorImp.calculate(logs);
		BigDecimal expectValue = BigDecimal.valueOf(0);
		assertEquals(expectValue, result);
	}
	
	@Test
	void breakRule() {
		TelephoneBillCalculator calculatorImp = new CalculatorImp();
		String logs = "" 
				+ "420776562354,00,18-01-2020 08:10:00\n";
		BigDecimal result = calculatorImp.calculate(logs);
		BigDecimal expectValue = BigDecimal.valueOf(0);
		assertEquals(expectValue, result);
	}
	
	@Test
	void onlyOneLog() {
		TelephoneBillCalculator calculatorImp = new CalculatorImp();
		String logs = "" 
				+ "420776562354,18-01-2020 07:10:00,18-01-2020 08:10:00\n";
		BigDecimal result = calculatorImp.calculate(logs);
		BigDecimal expectValue = BigDecimal.valueOf(0);
		assertEquals(expectValue, result);
	}
	
	// START DOUPLICATE
	
	@Test
	void sameDoublicate() {
		TelephoneBillCalculator calculatorImp = new CalculatorImp();
		String logs = "" 
				+ "420776562354,18-01-2020 07:10:00,18-01-2020 08:10:00\n"
				+ "420776562355,18-01-2020 08:20:00,18-01-2020 08:26:10\n"
				+ "420776562355,18-01-2020 08:20:00,18-01-2020 08:26:10\n"
				+ "420776562354,18-01-2020 07:10:00,18-01-2020 08:10:00\n";
		BigDecimal result = calculatorImp.calculate(logs);
		BigDecimal expectValue = BigDecimal.valueOf(13.2);
		assertEquals(expectValue, result);
	}
	
	// END OF DOUPLICATE
	
	
	// START OF THE MINUTES < 5
	
	@Test
	void LT5() {
		TelephoneBillCalculator calculatorImp = new CalculatorImp();
		String logs = "420776562354,18-01-2020 08:20:00,18-01-2020 08:21:00\n" // The minutes < 5
				+ "420776562355,18-01-2020 08:20:00,18-01-2020 08:26:10\n"
				+ "420776562355,18-01-2020 08:20:00,18-01-2020 08:26:10\n";
		BigDecimal result = calculatorImp.calculate(logs);
		BigDecimal expectValue = BigDecimal.valueOf(1);
		assertEquals(expectValue, result);
	}
	
	@Test
	void InRushingHoursGT5() {
		TelephoneBillCalculator calculatorImp = new CalculatorImp();
		String logs = "420776562354,18-01-2020 08:20:00,18-01-2020 08:22:00\n" // The minutes < 5, In rush ours
				+ "420776562355,18-01-2020 08:20:00,18-01-2020 08:26:10\n"
				+ "420776562355,18-01-2020 08:20:00,18-01-2020 08:26:10\n";
		BigDecimal result = calculatorImp.calculate(logs);
		BigDecimal expectValue = BigDecimal.valueOf(2);
		assertEquals(expectValue, result);
	}
	
	@Test
	void overRushingHoursLT5() {
		TelephoneBillCalculator calculatorImp = new CalculatorImp();
		String logs = "420776562354,18-01-2020 20:20:00,18-01-2020 20:22:00\n" // The minutes < 5, Out rush hours
				+ "420776562355,18-01-2020 08:20:00,18-01-2020 08:26:10\n"
				+ "420776562355,18-01-2020 08:20:00,18-01-2020 08:26:10\n";
		BigDecimal result = calculatorImp.calculate(logs);
		BigDecimal expectValue = BigDecimal.valueOf(1.0);
		assertEquals(expectValue, result);
	}
	
	@Test
	void crossHourLT5() {
		TelephoneBillCalculator calculatorImp = new CalculatorImp();
		String logs = "420776562354,18-01-2020 07:59:00,18-01-2020 08:02:00\n" // The minutes < 5, Both in and out rush hours
				+ "420776562355,18-01-2020 08:20:00,18-01-2020 08:26:10\n"
				+ "420776562355,18-01-2020 08:20:00,18-01-2020 08:26:10\n";
		BigDecimal result = calculatorImp.calculate(logs);
		BigDecimal expectValue = BigDecimal.valueOf(2.5);
		assertEquals(expectValue, result);
	}
	
	@Test
	void crossDayLT5() {
		TelephoneBillCalculator calculatorImp = new CalculatorImp();
		String logs = "420776562354,18-01-2020 23:59:00,19-01-2020 00:01:00\n" // The minutes < 5, Out rush hours, Passed day
				+ "420776562355,18-01-2020 08:20:00,18-01-2020 08:26:10\n"
				+ "420776562355,18-01-2020 08:20:00,18-01-2020 08:26:10\n";
		BigDecimal result = calculatorImp.calculate(logs);
		BigDecimal expectValue = BigDecimal.valueOf(1.0);
		assertEquals(expectValue, result);
	}
	
	@Test
	void crossYearLT5() {
		TelephoneBillCalculator calculatorImp = new CalculatorImp();
		String logs ="420776562354,31-12-2021 23:59:00,01-01-2022 00:01:00\n" // The minutes < 5, Out rush hours, Passed day, Passed year 
				+ "420776562355,18-01-2020 08:20:00,18-01-2020 08:26:10\n"
				+ "420776562355,18-01-2020 08:20:00,18-01-2020 08:26:10\n";
		BigDecimal result = calculatorImp.calculate(logs);
		BigDecimal expectValue = BigDecimal.valueOf(1.0);
		assertEquals(expectValue, result);
	}
	
	// END OF THE MINUTES < 5
	
	
	// START OF THE MINUTES > 5
		
	@Test
	void GT5() {
		TelephoneBillCalculator calculatorImp = new CalculatorImp();
		String logs = "420776562354,18-01-2020 08:20:00,18-01-2020 08:30:00\n" // The minutes > 5
				+ "420776562355,18-01-2020 08:20:00,18-01-2020 08:26:10\n"
				+ "420776562355,18-01-2020 08:20:00,18-01-2020 08:26:10\n";
		BigDecimal result = calculatorImp.calculate(logs);
		BigDecimal expectValue = BigDecimal.valueOf(9.0);
		assertEquals(expectValue, result);
	}

	@Test
	void InRushingHoursLessThanFiveMinutes() {
		TelephoneBillCalculator calculatorImp = new CalculatorImp();
		String logs = "420776562354,18-01-2020 07:20:00,18-01-2020 07:26:00\n" // The minutes > 5, In rush hours
				+ "420776562355,18-01-2020 08:20:00,18-01-2020 08:26:10\n"
				+ "420776562355,18-01-2020 08:20:00,18-01-2020 08:26:10\n";
		BigDecimal result = calculatorImp.calculate(logs);
		BigDecimal expectValue = BigDecimal.valueOf(2.8);
		assertEquals(expectValue, result);
	}
	
	@Test
	void overRushingHoursGT5() {
		TelephoneBillCalculator calculatorImp = new CalculatorImp();
		String logs = "420776562354,18-01-2020 18:20:00,18-01-2020 18:27:00\n" // The minutes > 5, Out rush hours
				+ "420776562355,18-01-2020 08:20:00,18-01-2020 08:26:10\n"
				+ "420776562355,18-01-2020 08:20:00,18-01-2020 08:26:10\n";
		BigDecimal result = calculatorImp.calculate(logs);
		BigDecimal expectValue = BigDecimal.valueOf(3.1);
		assertEquals(expectValue, result);
	}

	@Test
	void crossHourGT5() {
		TelephoneBillCalculator calculatorImp = new CalculatorImp();
		String logs = "420776562354,18-01-2020 07:10:00,18-01-2020 08:10:00\n" // The minutes > 5, Both in and out rush hours
				+ "420776562355,18-01-2020 08:20:00,18-01-2020 08:26:10\n"
				+ "420776562355,18-01-2020 08:20:00,18-01-2020 08:26:10\n";
		BigDecimal result = calculatorImp.calculate(logs);
		BigDecimal expectValue = BigDecimal.valueOf(24.0);
		assertEquals(expectValue, result);
	}
	
	@Test
	void crossDayGT5() {
		TelephoneBillCalculator calculatorImp = new CalculatorImp();
		String logs = "420776562354,18-01-2020 23:55:00,19-01-2020 08:10:00\n" // The minutes > 5, Both in and out rush hours, Passed day
				+ "420776562355,18-01-2020 08:20:00,18-01-2020 08:26:10\n"
				+ "420776562355,18-01-2020 08:20:00,18-01-2020 08:26:10\n";
		BigDecimal result = calculatorImp.calculate(logs);
		BigDecimal expectValue = BigDecimal.valueOf(149.5);
		assertEquals(expectValue, result);
	}
	
	@Test
	void crossYearGT5() {
		TelephoneBillCalculator calculatorImp = new CalculatorImp();
		String logs ="420776562354,31-12-2021 23:55:00,01-01-2022 08:10:00\n" // The minutes > 5, Both in and out rush hours, Pass day, Passed year 
				+ "420776562355,18-01-2020 08:20:00,18-01-2020 08:26:10\n"
				+ "420776562355,18-01-2020 08:20:00,18-01-2020 08:26:10\n";
		BigDecimal result = calculatorImp.calculate(logs);
		BigDecimal expectValue = BigDecimal.valueOf(149.5);
		assertEquals(expectValue, result);
	}
	
	// END OF THE MINUTES > 5
}
