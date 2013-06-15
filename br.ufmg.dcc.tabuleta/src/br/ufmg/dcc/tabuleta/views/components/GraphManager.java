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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import prefuse.data.Graph;
import br.ufmg.dcc.tabuleta.views.SunburstView;

/**
 * @author Alcemir R. Santos
 *
 */
public class GraphManager {

	private static GraphManager manager;
	private Collection<Graph> graphs;
	private Graph active;
	private HashMap<String, Graph> graphsmap;
	private String current;
	
	private GraphManager(){}
	public static GraphManager getInstance(){
		if (manager == null) {
			manager = new GraphManager();
		}
		return manager;
	}
	
	public Collection<Graph> getGraphs(){
//		return graphs;
		return graphsmap.values();
	}

	public Collection<String> getGraphsIDs(){
		return graphsmap.keySet();
	}
	
	public void addGraph(Graph g) {
		if(graphsmap == null){
//		if (graphs == null) {
//			graphs = new ArrayList<Graph>();
			graphsmap = new HashMap<String, Graph>();
		}
//		graphs.add(g);
		setActiveGraph((new Timestamp(new Date().getTime())).toString());
		graphsmap.put(current, g);
//		active = g;
	}
	
	public String getCurrentGraphTimestamp(){
		return current;
	}
	
	/**
	 * @return
	 */
	public Graph getActiveGraph() {
//		return active;
		return graphsmap.get(current);
	}
	
	public void setActiveGraph(String graphKey){
		current = graphKey;
		SunburstView.getGraphsViewer().update();
	}

}
