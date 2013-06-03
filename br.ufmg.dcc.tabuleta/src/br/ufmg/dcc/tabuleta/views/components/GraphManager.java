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

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ForkJoinPool.ManagedBlocker;

import br.ufmg.dcc.tabuleta.views.FeatureSunburstView;
import br.ufmg.dcc.tabuleta.views.MetricsView;

import prefuse.data.Graph;

/**
 * @author Alcemir R. Santos
 *
 */
public class GraphManager {

	private static GraphManager manager;
	private Collection<Graph> graphs;
	
	private GraphManager(){}
	public static GraphManager getInstance(){
		if (manager == null) {
			manager = new GraphManager();
		}
		return manager;
	}
	
	public Collection<Graph> getGraphs(){
		return graphs;
	}

	public void addGraph(Graph g) {
		if (graphs == null) {
			graphs = new ArrayList<Graph>();
		}
		graphs.add(g);
		FeatureSunburstView.getGraphsViewer().refresh();
	}
	/**
	 * @return
	 */
	public Graph getLastGraph() {
		ArrayList<Graph> glist = (ArrayList<Graph>) graphs;
		return glist.get(glist.size()-1);
	}

}
