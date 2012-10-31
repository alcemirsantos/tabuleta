/**
 * 
 */
package br.ufmg.dcc.comparator.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author jean
 *
 */
public class Concern implements Comparable<Concern> {
	
	private String title;
	private List<Element> elements;
	
	/*
	 * recebe uma concern. Uma concern possui uma lista de elements
	 * percorre elas e extrai a lista de elements em cada delas
	 */
	public Concern(String concern) {
				
		this.elements = new ArrayList<Element>();
		
		concern = concern.replaceAll("name=\"", "");
		
		Pattern pattern = Pattern.compile("\\w*");
		Matcher matcher = pattern.matcher(concern);
		matcher.find();
		this.title = matcher.group();
		
		concern = concern.replaceAll(this.title + "\">", "");
		String[] elements = concern.split("/>");
		for(String i : elements) {
			i = i.replaceAll("<element degree=\"100\" id=\"=", "");
			Element element = new Element(i);
			this.elements.add(element);
		}
	}
	
	@Override
	public int compareTo(Concern o) {
		if(this.title.equals(o.getTitle())) {
			return 0;
		}
		return 1;
	}
	
	public void print() {
		System.out.println(this.title);
//		for(Element e : this.elements) {
//			e.print();
//		}
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<Element> getElements() {
		return elements;
	}
	public void setElements(List<Element> elements) {
		this.elements = elements;
	}
}
