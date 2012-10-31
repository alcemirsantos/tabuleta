/**
 * 
 */
package br.ufmg.dcc.comparator.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.ufmg.dcc.comparator.util.Type;

/**
 * @author jean
 *
 */
public class Element implements Comparable<Element>{
	
	private String project;
	private String sourceFolder;
	private String pack;
	private String file;
	private String struct;
	private String name;
	private Type type;
	
	/*
	 * recebe um element
	 * extrai o element
	 * depois do element extraído, a comparação já pode ser realizada
	 */
	public Element(String element) {
		
		//project
		Pattern pattern = Pattern.compile("\\w*");
		Matcher matcher = pattern.matcher(element);
		matcher.find();
		this.project = matcher.group();
		element = element.replace(this.project + "/", "");
		
		//sourceFolder
		Pattern pattern2 = Pattern.compile("\\w*");
		Matcher matcher2 = pattern2.matcher(element);
		matcher2.find();
		this.sourceFolder = matcher2.group();
		element = element.replace(this.sourceFolder + "&lt;", "");
		
		//pack
		this.pack = element.substring(0, element.indexOf("{"));
		element = element.replace(this.pack + "{", "");
		
		//file
		this.file = element.substring(0, element.indexOf("["));
		element = element.replace(this.file + "[", "");
		
		//struct
		Pattern pattern3 = Pattern.compile("\\w*[~^]");
		Matcher matcher3 = pattern3.matcher(element);
		matcher3.find();
		this.struct = matcher3.group();
		element = element.replace(this.struct, "");
		
		//name
		Pattern pattern4 = Pattern.compile("\\w*");
		Matcher matcher4 = pattern4.matcher(element);
		matcher4.find();
		this.name = matcher4.group();
		element = element.replace(this.name + "\" type=\"", "");
		element = element.replace("\"", "");
		
		//type
		if(element.equals("method")) {
			this.type = Type.method;
			this.struct = this.struct.replace("~", "");
		} else {
			this.type = Type.attribute;
			this.struct = this.struct.replace("^", "");
		}
		
		element = element.replaceAll(".*", "");
	}
	
	@Override
	public int compareTo(Element o) {
		
		if(this.project.equals(o.getProject()) &&
				this.sourceFolder.equals(o.getSourceFolder()) &&
				this.pack.equals(o.getPack()) &&
				this.file.equals(o.getFile()) &&
				this.struct.equals(o.getStruct()) &&
				this.name.equals(o.getName()) &&
				this.type.toString().equals(o.getType().toString())) {
			return 0;
		}
		return 1;
	}
	
	public void print() {
		System.out.println(this.project+this.sourceFolder+this.pack+this.file+this.struct+this.name+this.type.toString());
	}
	
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}
	public String getSourceFolder() {
		return sourceFolder;
	}
	public void setSourceFolder(String sourceFolder) {
		this.sourceFolder = sourceFolder;
	}
	public String getPack() {
		return pack;
	}
	public void setPack(String pack) {
		this.pack = pack;
	}
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	public String getStruct() {
		return struct;
	}
	public void setStruct(String struct) {
		this.struct = struct;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
}
