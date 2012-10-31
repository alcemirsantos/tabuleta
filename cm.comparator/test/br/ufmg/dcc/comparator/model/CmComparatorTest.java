/**
 * 
 */
package br.ufmg.dcc.comparator.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * @author jean
 *
 */
public class CmComparatorTest {
	
	private CmComparator cm;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		String oracle = "/home/jean/workspace/CmComparator/resources/test.cm";
		String test = "/home/jean/workspace/CmComparator/resources/test.cm";
		
		this.cm = new CmComparator(oracle, test);
	}

	@Test
	public void testExtractModel() {
		this.cm.extractModel();
	}
	
	@Test
	public void testExtractConcerns() {
		this.cm.extractModel();
		this.cm.extractConcerns();
	}

}
