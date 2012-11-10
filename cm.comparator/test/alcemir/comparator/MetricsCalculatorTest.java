package alcemir.comparator;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MetricsCalculatorTest {

	private static final String RESOURCES_FOLDER = System.getProperty("user.dir")+File.separator+"resources"+File.separator;
	private MetricsCalculator calculator;
	
	@Before public void setup() throws Exception{
		populate();
	}
	@After public void teardown(){
		calculator = null;
	}
	
	@Test public void testCalculateTP(){
		assertEquals(4, calculator.getTP());
	}
	
	@Test public void testCalculateFP(){
		assertEquals(1, calculator.getFP());
	}
	
	@Test public void testCalculateFN(){
		assertEquals(2, calculator.getFN());
	}
	
	@Test public void testCalculateRecall(){
		assertEquals((4.0/6.0), calculator.getRecall(), 0.05);
	}
	
	@Test public void testCalculatePrecision(){
		assertEquals((4.0/5.0), calculator.getPrecision(), 0.05);
	}
	
	@Test public void testCalculateF1Score(){
		assertEquals(((2*0.6*0.75)/(0.6+0.75)), calculator.getF1Score(),0.1);
	}
	
	private void populate() throws Exception{
		List<CMElementTag> oracle = new ArrayList<CMElementTag>();
		List<CMElementTag> test = new ArrayList<CMElementTag>();
		
		// 6 elementos
		oracle = CmFilesOperations.getConcernElements(CmFilesOperations.getDocument(RESOURCES_FOLDER+"oracle.cm"),"cards");
		// 5 elementos
		test = CmFilesOperations.getConcernElements(CmFilesOperations.getDocument(RESOURCES_FOLDER+"test.cm"),"cards");

		calculator = new MetricsCalculator(oracle, test);
	}
}
