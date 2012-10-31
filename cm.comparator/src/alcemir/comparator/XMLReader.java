/**
 * 
 */
package alcemir.comparator;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author alcemir
 *
 */
public class XMLReader {

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
		doIntersection(files);
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

			oracleElements = (ArrayList<String>) getElements(docOracle);
			t2fElements = (ArrayList<String>) getElements(docT2f);
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
	 * faz a intersecção de arquivos .cm contidos no vetor passado como parâmtro.
	 * 
	 * @param files
	 */
	private static void doIntersection(String[] files){
		Map<Integer,Document> docs = new HashMap<Integer,Document>();
		
		Stack elements = new Stack();
		
		ArrayList<String> intersectionElements = new ArrayList<String>();
		
		for (int i = 0; i < files.length; i++) {
			try {
				docs.put(i, getDocument(files[i]) );
				elements.add( (ArrayList<String>) getElements(docs.get(i)) );
			} catch (Exception e) {
				e.printStackTrace();
			}		
		}
		
		intersectionElements = (ArrayList<String>) elements.pop();		
		Set<ArrayList<String>> conjunto = new HashSet<ArrayList<String>>();

		while (!elements.empty()) {
			conjunto.add((ArrayList<String>) elements.pop()); 
		}
				
		for (ArrayList<String> arrayList : conjunto) {
			intersectionElements.retainAll(arrayList);			
		}
		
		for (String string : intersectionElements) {
			System.out.println(string);
		}
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
		
		File oracleFile = new File(filename);
		
		Document doc = dBuilder.parse(oracleFile);
		doc.getDocumentElement().normalize();
		
		return doc;
	}
	
	/**
	 * retorna uma lista de elementos dentro;
	 * @param doc
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private static List getElements(Document doc) {

		// TODO especificar o concern pra retornar os elements. do jeito que está retorna os elements de todos os concerns. isto provoca contagem incorreta;

		List list = null;
		NodeList tConcernsList = doc.getElementsByTagName("concern");				
		for (int i=0; i<tConcernsList.getLength(); i++) {
			
			NodeList elementsList = doc.getElementsByTagName("element");
			list = getElementsIDs(elementsList);
		}
		return list;
	}

	/**
	 * Retorna lista de IDs dos dos elementos do .cm representados pela tag
	 *  < element >.
	 * @param elementsList
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private static List getElementsIDs(NodeList elementsList) {
		ArrayList<String> aux = new ArrayList<String>();
		
		for (int temp = 0; temp < elementsList.getLength(); temp++) {
			
			Node nNode = elementsList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				
				Element eElement = (Element) nNode;
				String id = eElement.getAttribute("id");
				
				aux.add(id);
//				System.out.println("id:"+ id);
			}
		}
		return aux;
	}
	
	/**
	 * Retorna lista de elementos de representados pela tag < element >.
	 * @param elementsList
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private static List getElementsList(NodeList elementsList) {
		ArrayList<String> aux = new ArrayList<String>();
		
		for (int temp = 0; temp < elementsList.getLength(); temp++) {
			
			Node nNode = elementsList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				
				Element eElement = (Element) nNode;

				String degree = eElement.getAttribute("degree");
				String id = eElement.getAttribute("id");
				String type = eElement.getAttribute("type");
				String elementTag = "<"+eElement.getNodeName()+" degree=\""+degree+"\" id=\""+id+"\" type=\""+type+"\"/>";
				
				aux.add(elementTag);
			}
		}
		return aux;
	}

	/**
	 * Retorna o nome do <code>concern</code> especificado no .cm. 
	 * @param elementsList
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private static String getConcernName(NodeList elementsList) {
		String concernName="";
		
		for (int temp = 0; temp < elementsList.getLength(); temp++) {
			
			Node nNode = elementsList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				
				Element eElement = (Element) nNode;
				concernName = eElement.getAttribute("name");
			}
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


	@SuppressWarnings("unchecked")
	public static void printCM(Document d){
		/*
		 <?xml version="1.0" encoding="UTF-8" standalone="no"?>
		 	<model>
		 		<concern name="clusterers">
		 			<element degree="100" id="=weka/" type="method"/>
		 		</concern>
			</model>
		 */		
		String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";
		String openModel = "<model>";
		String openConcern= "<concern name=\"";
		List<String> elements = null;
		String closeConcern= "</concern>";
		String closeModel = "</model>";
		
		NodeList tConcernsList = d.getElementsByTagName("concern");
		String concernName = getConcernName(tConcernsList);
		openConcern+= concernName+"\">";
				
		for (int i=0; i<tConcernsList.getLength(); i++) {
			
			NodeList elementsList = d.getElementsByTagName("element");
			elements = getElementsList(elementsList);
		}
		
		System.out.println(header);
		System.out.println(openModel);
		System.out.println(openConcern);
		for (String s : elements) {
			System.out.println(s);
		}
		System.out.println(closeConcern);
		System.out.println(closeModel);
	}
}