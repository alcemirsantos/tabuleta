package alcemir.comparator;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

public class OperationsTest {
	
	private static final String RESOURCES_FOLDER = System.getProperty("user.dir")+File.separator+"resources"+File.separator;
	private String[] concerns;
	private Document doc;

	@Before public void setup() throws Exception{
		concerns = new String[] {"um","dois","tres"};
		doc = CmFilesOperations.getDocument(RESOURCES_FOLDER+"cm3concerns.cm");
	}
	
	@After public void teardown(){
		concerns = null;
		doc = null;
	}
	
	@Test public void testGetElementsPrimeiro() throws Exception{
		
		List<CMElementTag> list = CmFilesOperations.getConcernElements(doc, "um");
		
		assertEquals(5,list.size());
		assertEquals("a", list.get(0).getId());
		assertEquals("b", list.get(1).getId());
		assertEquals("c", list.get(2).getId());
		assertEquals("d", list.get(3).getId());
		assertEquals("e", list.get(4).getId());
	}
	
	@Test public void testGetElementsSegundo() throws Exception{
		
		List<CMElementTag> list = CmFilesOperations.getConcernElements(doc, "dois");
		
		assertEquals(5,list.size());
		assertEquals("f", list.get(0).getId());
		assertEquals("g", list.get(1).getId());
		assertEquals("h", list.get(2).getId());
		assertEquals("i", list.get(3).getId());
		assertEquals("j", list.get(4).getId());
	}
	
	@Test public void testGetElementsTerceiro() throws Exception{
		
		List<CMElementTag> list = CmFilesOperations.getConcernElements(doc, "tres");
		
		assertEquals(5,list.size());
		assertEquals("k", list.get(0).getId());
		assertEquals("l", list.get(1).getId());
		assertEquals("m", list.get(2).getId());
		assertEquals("n", list.get(3).getId());
		assertEquals("o", list.get(4).getId());
	}
	
	private ArrayList<CMElementTag> populate(int i){
		ArrayList<CMElementTag> oracle = new ArrayList<CMElementTag>();
		
		switch (i) {
		case 1:
			oracle.add(new CMElementTag("", "a", ""));
			oracle.add(new CMElementTag("", "b", ""));
			oracle.add(new CMElementTag("", "c", ""));
			oracle.add(new CMElementTag("", "d", ""));
			oracle.add(new CMElementTag("", "e", ""));
			break;
		case 2:
			oracle.add(new CMElementTag("", "f", ""));
			oracle.add(new CMElementTag("", "g", ""));
			oracle.add(new CMElementTag("", "h", ""));
			oracle.add(new CMElementTag("", "i", ""));
			oracle.add(new CMElementTag("", "j", ""));
			break;
		case 3:			
			oracle.add(new CMElementTag("", "k", ""));
			oracle.add(new CMElementTag("", "l", ""));
			oracle.add(new CMElementTag("", "m", ""));
			oracle.add(new CMElementTag("", "n", ""));
			oracle.add(new CMElementTag("", "o", ""));
			break;
		default:
			break;
		}
		return oracle;
	}
	
}
