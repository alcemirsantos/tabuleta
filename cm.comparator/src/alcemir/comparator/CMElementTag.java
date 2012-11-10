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

/**
 * Esta classe representa uma tag <code>&lt;element&gt;</code> de um arquivo <code>.cm</code>.
 *   esta classe é utilizada em algumas operações de manipulação de arquivos <code>.cm</code> 
 *   na classe {@link CmFilesOperations}.
 *   
 * @author Alcemir R. Santos
 *
 */
public class CMElementTag {
	private String degree;
	private String id;
	private String type;
	
	public CMElementTag(String degree, String id, String type) {
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
	public boolean equals(Object obj) {
		CMElementTag other = (CMElementTag)obj; 
		
		if ( this.id.equals(other.getId()) ){
			return true;
		}
		return false;
	}
}
