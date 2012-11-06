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
package br.ufmg.dcc.t2fm.actions;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import br.ufmg.dcc.t2fm.Test2FeatureMapper;
import br.ufmg.dcc.t2fm.views.components.ConcernNode;

/**
 * @author Alcemir R. Santos
 *
 */
public class DoIntersectionAction implements IObjectActionDelegate {

	private String CM_PATH;
	private IWorkbenchPart targetPart;
	private ISelection selection;

	/**
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	@Override
	public void run(IAction action) {
		CM_PATH = Test2FeatureMapper.getDefault().getPreferenceStore().getString("CMPATH");
		String[] files=null;

		
		IStructuredSelection isSelection = null;
		if (selection instanceof IStructuredSelection) {
			isSelection = (IStructuredSelection) selection;
		}
		if (isSelection == null){
			MessageDialog.openInformation(
					targetPart.getSite().getShell(),
					"Do Intersection Action",
					"Triggered the none selection");
			return;
		}else{
			files = new String[isSelection.size()];
			int i=0;
			for (@SuppressWarnings("unchecked")
			Iterator<IResource> srcIterator = isSelection.iterator(); srcIterator.hasNext(); ){
				IResource file = srcIterator.next();
				files[i] = file.getLocation().toString();
				i++;
			}
		}
		// TODO pegar o concern a fazer a intersecção intersecção
		String concern = null;
		doIntersection(files, concern);

	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection =  selection;
	}

	/** 
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
	 */
	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetPart = targetPart;
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
	private List getConcernElements(Document doc, String concernName) {
		// TODO especificar o concern pra retornar os elements. 

		ArrayList<CMElement> list = new ArrayList<CMElement>();
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
					CMElement concernElement = new CMElement(degree, id, type);
					
					list.add(concernElement);
				}
			}
		}
		return list;
	}
	
	/**
	 * faz a intersecção de arquivos .cm contidos no vetor passado como parâmetro.
	 * 
	 *  OBSERVAÇÃO: setar concern caso tenha mais de um concern no <code>.cm</code>.
	 * 
	 * @param files
	 */
	private void doIntersection(String[] files, String concern){
		Map<Integer,Document> docs = new HashMap<Integer,Document>();
		
		Stack elements = new Stack();
		
		ArrayList<CMElement> intersectionElements = new ArrayList<CMElement>();
		
		for (int i = 0; i < files.length; i++) {
			try {
				docs.put(i, getDocument(files[i]) );
				elements.add( (ArrayList<CMElement>) getConcernElements(docs.get(i), concern));
			} catch (Exception e) {
				e.printStackTrace();
			}		
		}
		
		intersectionElements = (ArrayList<CMElement>) elements.pop();		
		Set<ArrayList<CMElement>> conjunto = new HashSet<ArrayList<CMElement>>();

		while (!elements.empty()) {
			conjunto.add((ArrayList<CMElement>) elements.pop()); 
		}
				
		for (ArrayList<CMElement> arrayList : conjunto) {
			intersectionElements.retainAll(arrayList);			
		}
		
//		for (CMElement concernElement : intersectionElements) {
//			System.out.println(concernElement.toString());
//		}
		buildCMFile(concern, intersectionElements);
	}
	
	/**
	 * Retorna a mesma string com caracteres especiais (<,>) substituídos.
	 * @param aText
	 * @return
	 */
	public  String fixSpecialCharacters(String aText){
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
	@SuppressWarnings("unchecked")
	public void buildCMFile(String concern, List<CMElement> concernElements){
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
		for (CMElement s : concernElements) {
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
	private void writeToCMFile(String concern, String xml){
		// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer;
				try {
					transformer = transformerFactory.newTransformer();
					DOMSource source = new DOMSource(stringToDom(xml));
					StreamResult result = new StreamResult(new File(CM_PATH+concern+"CM-intersection.cm"));
					
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
	public Document stringToDom(String xmlSource) 
	        throws SAXException, ParserConfigurationException, IOException {
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    return builder.parse(new InputSource(new StringReader(xmlSource)));
	}
	
	/**
	 * representa a tag <code>&lt;element&gt;</code> dos arquivos .cm
	 * @author Alcemir R. Santos
	 */
	class CMElement {
		private String degree;
		private String id;
		private String type;
		
		public CMElement(String degree, String id, String type) {
			this.degree = degree;
			this.id = id;
			this.type = type;
		}
		public String getDegree() {
			return degree;
		}
		public void setDegree(String degree) {
			this.degree = degree;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		
		@Override
		public String toString(){
			return "<element degree=\""+this.degree+"\" id=\""+this.id+"\" type=\""+this.type+"\"/>";
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((degree == null) ? 0 : degree.hashCode());
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			return result;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CMElement other = (CMElement) obj;
			if (degree == null) {
				if (other.degree != null)
					return false;
			} else if (!degree.equals(other.degree))
				return false;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			if (type == null) {
				if (other.type != null)
					return false;
			} else if (!type.equals(other.type))
				return false;
			return true;
		}
	}
}
