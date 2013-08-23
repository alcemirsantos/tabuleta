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
package br.ufmg.dcc.tabuleta.views.components;

/**
 * @author Alcemir R. Santos
 * 
 */
public class TreeMapNode {
	private String id;
	private String description;
	private String path;

	public TreeMapNode(String id, String description, String path) {
		this.id = id;
		this.description = description;
		this.path = path;
	}

	public String getid() {
		return this.id;
	}

	public String getdesc() {
		return this.description;
	}
	
	public String getPath(){
		return this.path;
	}
}
