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

import br.ufmg.dcc.tabuleta.views.MetricsView;

/**
 * Esta classe fornece os dados para exibição na {@link MetricsView}.
 *  esta classe implementa o padrão de projeto <code>Singleton</code>
 *  
 * @author Alcemir R. Santos
 */
public class MetricsManager {
	private static MetricsManager manager;
	private Collection<MetricsReport> reports;

	private MetricsManager() {
	}

	public static MetricsManager getInstance() {
		if (manager == null) {
			manager = new MetricsManager();
		}
		return manager;
	}

	public void addMetricsReport(MetricsReport i) {
		if (reports == null) {
			reports = new ArrayList<MetricsReport>();
		}
		reports.add(i);
		MetricsView.getMetricsView().refresh();
	}

	public Collection<MetricsReport> getItens() {
		if (reports == null) {
			reports = new ArrayList<MetricsReport>();
		}
		return reports;
	}
}