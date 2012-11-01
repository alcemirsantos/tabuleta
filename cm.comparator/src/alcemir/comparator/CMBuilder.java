/**
 * 
 */
package alcemir.comparator;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author alcemir
 *
 */
public class CMBuilder {

	private static final String RESOURCES_FOLDER = System.getProperty("user.dir")+File.separator+"resources"+File.separator;
	
	static ArrayList<String> oracleElements = new ArrayList<String>();
	static ArrayList<String> t2fElements = new ArrayList<String>();
	
	public static void main(String argv[]) {
//		String oracle_FILE = "src_mapping.cm";
//
//		String[] files = {"CMclusterers.cm"};
//		
//		for (String file : files) {
//			System.out.println();
//			System.out.println(file+" comparison...");
//			compareCMFiles(RESOURCES_FOLDER+oracle_FILE, RESOURCES_FOLDER+file);
//		}
		
		String[] files = {
				RESOURCES_FOLDER+"CMplayer.cm",
				RESOURCES_FOLDER+"CMprojectcard.cm",
				RESOURCES_FOLDER+"CMsoftwareengineer.cm"
				};
		// setar concern caso tenha mais de um concen no <code>.cm</code>.
		doIntersection(files, null);
	}
 
	
	/**
	 * compara 2 arquivos .cm cujos nomes são passados como parametro;
	 * 
	 * @param oracle
	 * @param t2f
	 */
	@SuppressWarnings("unchecked")
	public static void compareCMFiles(String oracle,String t2f ){

		// true positives
		List<String> tpElements = new ArrayList<String>();
		// false positives
		List<String> fpElements = new ArrayList<String>();
		// false negatives
		List<String> fnElements = new ArrayList<String>();
		
		Document docOracle = null;
		Document docT2f = null;
		try {
			docOracle = getDocument(oracle);
			docT2f = getDocument(t2f);

			oracleElements = (ArrayList<String>) getConcernElements(docOracle, null);
			t2fElements = (ArrayList<String>) getConcernElements(docT2f, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (String s : oracleElements) {
			if (t2fElements.contains(s)) {
				tpElements.add( s );				
			}else{
				fnElements.add( s );
			}
		}
		
		for (String s : t2fElements) {
			if (!oracleElements.contains(s)) {
				fpElements.add( s );
			}					
		}
		
		printMetrics(tpElements, fpElements, fnElements);		
	}

	/**
	 * faz a intersecção de arquivos .cm contidos no vetor passado como parâmetro.
	 * 
	 *  OBSERVAÇÃO: setar concern caso tenha mais de um concern no <code>.cm</code>.
	 * 
	 * @param files
	 */
	private static void doIntersection(String[] files, String concern){
		Map<Integer,Document> docs = new HashMap<Integer,Document>();
		
		Stack elements = new Stack();
		
		ArrayList<ConcernElement> intersectionElements = new ArrayList<ConcernElement>();
		
		for (int i = 0; i < files.length; i++) {
			try {
				docs.put(i, getDocument(files[i]) );
				elements.add( (ArrayList<ConcernElement>) getConcernElements(docs.get(i), concern));
			} catch (Exception e) {
				e.printStackTrace();
			}		
		}
		
		intersectionElements = (ArrayList<ConcernElement>) elements.pop();		
		Set<ArrayList<ConcernElement>> conjunto = new HashSet<ArrayList<ConcernElement>>();

		while (!elements.empty()) {
			conjunto.add((ArrayList<ConcernElement>) elements.pop()); 
		}
				
		for (ArrayList<ConcernElement> arrayList : conjunto) {
			intersectionElements.retainAll(arrayList);			
		}
		
//		for (ConcernElement concernElement : intersectionElements) {
//			System.out.println(concernElement.toString());
//		}
		buildCMString(concern, intersectionElements);
	}

	/**
	 * Cria um <code>{@link Document}</code> para manipular o arquivo .cm passado como 
	 * 	parâmetro.
	 * 
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	private static Document getDocument(String filename) throws Exception{
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		
		File file = new File(filename);
		
		Document doc = dBuilder.parse(file);
		doc.getDocumentElement().normalize();
		
		return doc;
	}
	
	/**
	 * retorna uma lista de elementos dentro;
	 * @param doc
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private static List getConcernElements(Document doc, String concernName) {
		// TODO especificar o concern pra retornar os elements. 

		ArrayList<ConcernElement> list = new ArrayList<ConcernElement>();
		NodeList tConcernsList = doc.getElementsByTagName("concern");				
		
		for (int i=0; i<tConcernsList.getLength(); i++) {
			Node concernNode = tConcernsList.item(i);
			String name = "";
			if (concernNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) concernNode;
				name = eElement.getAttribute("name");
			}
			if (concernName!=null && !concernName.equals(name) ) {
				break;
			}
			NodeList elementsList = doc.getElementsByTagName("element");
			for (int temp = 0; temp < elementsList.getLength(); temp++) {
				
				Node nNode = elementsList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					
					Element eElement = (Element) nNode;
		
					String degree = eElement.getAttribute("degree");
					String id = eElement.getAttribute("id");
					id = fixSpecialCharacters(id);
					String type = eElement.getAttribute("type");
					ConcernElement concernElement = new ConcernElement(degree, id, type);
					
					list.add(concernElement);
				}
			}
		}
		return list;
	}


	/**
	 * Constrói uma string representando um arquivo .cm como no modelo abaixo:
	 * 
	 * <p><blockquote><pre>
	 *     <?xml version="1.0" encoding="UTF-8" standalone="no"?>
	 *	 	<model>
	 *	 		<concern name="clusterers">
	 *	 			<element degree="100" id="=weka/" type="method"/>
	 *	 		</concern>
	 *		</model>
	 *	</pre></blockquote>
	 * @param concern
	 * @param concernElements
	 */
	@SuppressWarnings("unchecked")
	public static void buildCMString(String concern, List<ConcernElement> concernElements){
		String cmString;
		String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";
		String openModel = "<model>";
		String openConcern= "<concern name=\""+concern+"\">";
		String closeConcern= "</concern>";
		String closeModel = "</model>";
		
		cmString = header+openModel+openConcern;
		
		System.out.println(header);
		System.out.println(openModel);
		System.out.println(openConcern);
		for (ConcernElement s : concernElements) {
			System.out.println(s.toString());
			cmString += s.toString();
		}
		System.out.println(closeConcern);
		System.out.println(closeModel);
		
		cmString += closeConcern+closeModel;
		
		writeToCMFile(concern, cmString);
	}
	
	/**
	 * escreve um arquivo .cm correspondentes à string xml para o concern dado.
	 * @param concern
	 * @param xml
	 */
	public static void writeToCMFile(String concern, String xml){
		// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer;
				try {
					transformer = transformerFactory.newTransformer();
					DOMSource source = new DOMSource(stringToDom(xml));
					StreamResult result = new StreamResult(new File(RESOURCES_FOLDER+concern+"CM-intersection.cm"));
					
					transformer.transform(source, result);
					
					System.out.println("File saved!");
				} catch (Exception e) {
					e.printStackTrace();
				}
	}
	
	/**
	 * Retorna a mesma string com caracteres especiais (<,>) substituídos.
	 * @param aText
	 * @return
	 */
	public static String fixSpecialCharacters(String aText){
		final StringBuilder result = new StringBuilder();
	     final StringCharacterIterator iterator = new StringCharacterIterator(aText);
	     char character =  iterator.current();
	     while (character != CharacterIterator.DONE ){
	       if (character == '<') {
	         result.append("&lt;");
	       }else if (character == '>') {
	         result.append("&gt;");
	       }else{
	         //the char is not a special one, add it to the result as is
	         result.append(character);
	       }
	       character = iterator.next();
	     }
	     return result.toString();
	}

	/**
	 * Converte string para Document
	 * @param xmlSource
	 * @return
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 */
	public static Document stringToDom(String xmlSource) 
	        throws SAXException, ParserConfigurationException, IOException {
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    return builder.parse(new InputSource(new StringReader(xmlSource)));
	}


	/**
	 * Retorna o nome do <code>concern</code> especificado no .cm. 
	 * @param elementsList
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private static String getConcernName(Node aNode) {
		String concernName="";
		if (aNode.getNodeType() == Node.ELEMENT_NODE) {
			Element eElement = (Element) aNode;
			concernName = eElement.getAttribute("name");
		}
		return concernName;
	}

	/**
	 * imprime as métricas de TP; FP; FN; precision e recall;
	 * 
	 * @param tpElements
	 * @param fpElements
	 * @param fnElements
	 */
	private static void printMetrics(List<String> tpElements,
			List<String> fpElements, List<String> fnElements) {
		double tp = tpElements.size();
		double fn = fnElements.size();
		double fp = fpElements.size();
			
		System.out.println("~~~~~~~~~~~~~~~~");
		System.out.println("TP size: "+tp);
		System.out.println("FP size: "+fp);
		System.out.println("FN size: "+fn);
		double recall = tp/(tp+fn);
		double precision = tp/(tp+fp);
		System.out.println("recall: "+recall);
		System.out.println("precision: "+precision);
		System.out.println("~~~~~~~~~~~~~~~~");
	}
	
	
}