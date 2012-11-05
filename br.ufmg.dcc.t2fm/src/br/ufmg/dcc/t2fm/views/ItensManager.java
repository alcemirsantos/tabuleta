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
package br.ufmg.dcc.t2fm.views;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Alcemir R. Santos
 */
public class ItensManager {
	private static ItensManager manager;
	private Collection<Item> itens;

	private ItensManager() {
	}

	public static ItensManager getInstance() {
		if (manager == null) {
			manager = new ItensManager();
		}
		return manager;
	}

	public void addItem(Item i) {
		if (itens == null) {
			itens = new ArrayList<Item>();
		}
		itens.add(i);
	}

	public Item[] getItens() {
		if (itens == null) {
			// TODO load itens
			addItem(new Item("a.cm", "b", "c", "d", "e", "f"));
		}
		return itens.toArray(new Item[itens.size()]);
	}
	
	class Item {
		String a;
		String b;
		String c;
		String d;
		String e;
		String f;
		public Item(String a,String b,String c,String d,String e,String f){
			this.a = a;
			this.b = b;
			this.c = c;
			this.d = d;
			this.e = e;
			this.f = f;
		}
		/**
		 * @return the a
		 */
		public String getA() {
			return a;
		}
		/**
		 * @param a the a to set
		 */
		public void setA(String a) {
			this.a = a;
		}
		/**
		 * @return the b
		 */
		public String getB() {
			return b;
		}
		/**
		 * @param b the b to set
		 */
		public void setB(String b) {
			this.b = b;
		}
		/**
		 * @return the c
		 */
		public String getC() {
			return c;
		}
		/**
		 * @param c the c to set
		 */
		public void setC(String c) {
			this.c = c;
		}
		/**
		 * @return the d
		 */
		public String getD() {
			return d;
		}
		/**
		 * @param d the d to set
		 */
		public void setD(String d) {
			this.d = d;
		}
		/**
		 * @return the e
		 */
		public String getE() {
			return e;
		}
		/**
		 * @param e the e to set
		 */
		public void setE(String e) {
			this.e = e;
		}
		/**
		 * @return the f
		 */
		public String getF() {
			return f;
		}
		/**
		 * @param f the f to set
		 */
		public void setF(String f) {
			this.f = f;
		}
		
	}
}