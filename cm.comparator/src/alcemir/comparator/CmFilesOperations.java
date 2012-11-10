/*************************************************************************
 * Copyright (c) 2012 Federal University of Minas Gerais - UFMG 
 * All rights avaiable. This program and the accompanying materials
 * are made avaiable under the terms of the Eclipse Public Lincense v1.0
 * which accompanies this distribution, and is avaiable at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Alcemir R. Santos - improvements on the ConcernMapper
 * 			architeture. ConcernMapper is available at
 * 			http://www.cs.mcgill.ca/~martin/cm/
 *************************************************************************/
package alcemir.comparator;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

//import org.eclipse.jface.dialogs.MessageDialog;
//import org.eclipse.ui.PlatformUI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

//import br.ufmg.dcc.tabuleta.actions.CalculateMetricsAction;
//import br.ufmg.dcc.tabuleta.actions.DoIntersectionAction;

/**
 * Esta classe contém métodos para a manipulação de arquivos <code>.cm</code>. Estes métodos são
 *  são utilizados nas ações  {@link DoIntersectionAction} e {@link CalculateMetricsAction}.
 *   
 * @author Alcemir R. Santos
 */
public class CmFilesOperations {
	
	/**
	 * @param files
	 * @return
	 * @throws Exception 
	 */
	public static Set<String> getCMConcernNames(String[] files) throws Exception {
		Set<String> concerns = new TreeSet<String>();
		Document doc;
		for (int j = 0; j < files.length; j++) {
			doc = getDocument(files[j]);
			NodeList tConcernsList = doc.getElementsByTagName("concern");				
			
			for (int i=0; i<tConcernsList.getLength(); i++) {
				Node concernNode = tConcernsList.item(i);
				if (concernNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) concernNode;
					concerns.add( eElement.getAttribute("name") );					
				}
			}
		}
		return concerns;
	}
	
	/**
	 * Cria um <code>{@link Document}</code> para manipular o arquivo .cm passado como 
	 * 	parâmetro.
	 * 
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	public static Document getDocument(String filename) throws Exception{
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		
		File file = new File(filename);
		
		Document doc = dBuilder.parse(file);
		doc.getDocumentElement().normalize();
		
		return doc;
	}
	
	/**
	 * retorna uma lista de elementos <code>&lt;element&gt;</code> dentro da tag <code>&lt;concern&gt;</code> 
	 *  especificado pelo parâmetro <code>concernName</code>. Caso o concern não seja especificado retorna lista 
	 *  de todos os elementos encontrados.
	 * @param doc
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static List getConcernElements(Document doc, String concernName) {

		doc.normalize();
		ArrayList<CMElementTag> result = new ArrayList<CMElementTag>();
			
		NodeList elementsList = doc.getElementsByTagName("element");
		for (int temp = 0; temp < elementsList.getLength(); temp++) {
			Node nNode = elementsList.item(temp);
			String wrappedByConcenernNamed = nNode.getParentNode().getAttributes().item(0).getNodeValue();
			boolean b = wrappedByConcenernNamed.equals(concernName);
			if (b != true) {
				continue;
			}
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				
				Element eElement = (Element) nNode;
				String degree = eElement.getAttribute("degree");
				String id = eElement.getAttribute("id");
				id = fixSpecialCharacters(id);
				String type = eElement.getAttribute("type");
				CMElementTag concernElement = new CMElementTag(degree, id, type);
				
				result.add(concernElement);
			}
		}
		return result;
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
	public static String buildCMFileString(String concern, List<CMElementTag> concernElements){
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
		for (CMElementTag s : concernElements) {
			System.out.println(s.toString());
			cmString += s.toString();
		}
		System.out.println(closeConcern);
		System.out.println(closeModel);
		
		cmString += closeConcern+closeModel;
		return cmString;		
	}
	
	/**
	 * escreve um arquivo .cm correspondentes à string xml para o concern dado.
	 * @param concern
	 * @param xml
	 * @param cmPath
	 */
	public static void writeToCMFile(String concern, String xml, String cmPath){
		// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer;
				try {
					transformer = transformerFactory.newTransformer();
					DOMSource source = new DOMSource(stringToDom(xml));
					StreamResult result = new StreamResult(
							new File(cmPath+File.separator+"Feature-"+concern.toUpperCase() +"-intersectionCM.cm"));
					
					transformer.transform(source, result);
					
					System.out.println("File saved!");
				} catch (Exception e) {
					e.printStackTrace();
				}
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
	 * Mosta um diálogo de informação com a <code>String</code> informada.
	 *   os parâmetros se referem ao título do diálogo e a mensagem a ser
	 *   informada.
	 * 
	 * @param title
	 * @param message
	 */	
	public static void showMessage(String title, String message) {
//		MessageDialog.openInformation(
//				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
//				title, 
//				message);
	}
	
}
