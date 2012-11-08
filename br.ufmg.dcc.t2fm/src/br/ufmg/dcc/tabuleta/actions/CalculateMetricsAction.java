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
package br.ufmg.dcc.tabuleta.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.w3c.dom.Document;

import br.ufmg.dcc.tabuleta.Tabuleta;
import br.ufmg.dcc.tabuleta.actions.util.CMElementTag;
import br.ufmg.dcc.tabuleta.actions.util.CmFilesOperations;
import br.ufmg.dcc.tabuleta.views.MetricsView;
import br.ufmg.dcc.tabuleta.views.components.MetricsManager;
import br.ufmg.dcc.tabuleta.views.components.MetricsReport;

/**
 * Esta classe é responsável por calcular as métricas relacionadas a 
 *  <code>true positives (TP)</code>, <code>false positives (FP)</code>,
 *  <code>false negatives (FN)</code>, <code>recall</code>, <code>precision</code>
 *  e <code>f1-score</code> que popularam a visão {@link MetricsView}.
 *  
 * @author Alcemir R. Santos
 *
 */
public class CalculateMetricsAction implements IObjectActionDelegate {

	private String CM_PATH;
	private ISelection selection;
	
	private static ArrayList<CMElementTag> oracleElements = new ArrayList<CMElementTag>();
	private static ArrayList<CMElementTag> t2fElements = new ArrayList<CMElementTag>();

	private String cmTarget;
	/**
	 * 
	 */
	public CalculateMetricsAction() {
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	@Override
	public void run(IAction action) {
		CM_PATH = Tabuleta.getDefault().getPreferenceStore().getString("CMPATH");
		if (CM_PATH.isEmpty()) {
			CmFilesOperations.showMessage("Calculate Metrics Action",
					"You must set the path to .cm files on the TaBuLeTa preferences page.");
			return;
		}		
		
		String[] cmFilesPath = null;
		String[] cmFilesName = null;
		IStructuredSelection isSelection = null;
		if (selection instanceof IStructuredSelection) {
			isSelection = (IStructuredSelection) selection;
		}
		if (isSelection == null){
			CmFilesOperations.showMessage("Calculate Metrics Action",
					"Triggered \"Calculate Metrics\" with none selection");
			return;
		}else{
			cmFilesPath = new String[isSelection.size()];
			cmFilesName = new String[isSelection.size()];
			int i=0;
			for (@SuppressWarnings("unchecked")
			Iterator<IResource> srcIterator = isSelection.iterator(); srcIterator.hasNext(); ){
				IResource file = srcIterator.next();
				cmFilesPath[i] = file.getLocation().toString();
				cmFilesName[i] = file.getName();
				i++;
			}
		}
	    
		Set<String> concerns = null;
		try {
			concerns = CmFilesOperations.getCMConcernNames(cmFilesPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String concern = null;
	    if(concerns.isEmpty()){
	    	CmFilesOperations.showMessage("Calculate Metrics Action",
	    			"Triggered \"Calculate Metrics\" with .cm files with no concerns.");
			return;
	    }else if (concerns.size()==1) {
			concern = (String) concerns.toArray()[0];
		}else{
			concern = askByConcern(concerns.toArray());
		}
		
	    int answer = askByOracle(cmFilesName);
	    String oracle = cmFilesPath[answer];
	    String t2f=null;
	    if (answer==0) {
	    	t2f = cmFilesPath[1];
	    	cmTarget = cmFilesName[1];
		}else {
			t2f = cmFilesPath[0];
			cmTarget = cmFilesName[0];
		}
	    
		compareCMFiles(oracle, t2f, concern);
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
	}
	
	/**
	 * Pergunta ao usuário qual o concern deve ser considerado para a intersecção.
	 *  lista de opções preparada a partir o vetor passado como parâmetro.
	 *  
	 * @param concerns
	 * @return
	 */
	private String askByConcern(Object[] concerns){
		ElementListSelectionDialog dialog = 
				new ElementListSelectionDialog(
						Display.getCurrent().getActiveShell(),
						new LabelProvider());
		
		dialog.setElements(concerns);
		dialog.setTitle("What Concern do you want to calculate metrics?");
		// enquanto o usuário não disser qual é o concern
		while (dialog.open() != Window.OK){
			CmFilesOperations.showMessage("Calculate Metrics Action",
					"You must point which concern do you want to make a intersection.");
		}
		Object[] result = dialog.getResult();
		return (String)result[0];
	}
	
	/**
	 * Pergunta ao usuário qual dos arquivos é o oráculo da comparação.
	 *  lista de opções preparada a partir o vetor passado como parâmetro.
	 *  
	 * @param filesSelected
	 * @return
	 */
	private int askByOracle(Object[] filesSelected){
		ElementListSelectionDialog dialog = 
				new ElementListSelectionDialog(
						Display.getCurrent().getActiveShell(),
						new LabelProvider());
		
		dialog.setElements(filesSelected);
		dialog.setTitle("Which one is the oracle?");
		// enquanto o usuário não disser qual é o oracle
		while (dialog.open() != Window.OK){
			CmFilesOperations.showMessage("Calculate Metrics Action",
					"You must point which file is the oracle.");
		}
		
		int oracleID=0;
		Object[] result = dialog.getResult();		
		if (!filesSelected[0].equals(result[0])) {
			oracleID = 1;
		}
		
		return oracleID;
	}
	/**
	 * compara 2 arquivos .cm cujos nomes são passados como parametro;
	 * 
	 * @param oracle
	 * @param t2f
	 * @param concern 
	 */
	@SuppressWarnings("unchecked")
	private void compareCMFiles(String oracle,String t2f, String concern ){

		// true positives
		List<CMElementTag> tpElements = new ArrayList<CMElementTag>();
		// false positives
		List<CMElementTag> fpElements = new ArrayList<CMElementTag>();
		// false negatives
		List<CMElementTag> fnElements = new ArrayList<CMElementTag>();
		
		Document docOracle = null;
		Document docT2f = null;
		try {
			docOracle = CmFilesOperations.getDocument(oracle);
			docT2f = CmFilesOperations.getDocument(t2f);

			oracleElements = (ArrayList<CMElementTag>) CmFilesOperations.getConcernElements(docOracle, concern);
			t2fElements = (ArrayList<CMElementTag>) CmFilesOperations.getConcernElements(docT2f, concern);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (CMElementTag s : oracleElements) {
			if (t2fElements.contains(s)) {
				tpElements.add( s );				
			}else{
				fnElements.add( s );
			}
		}
		
		for (CMElementTag s : t2fElements) {
			if (!oracleElements.contains(s)) {
				fpElements.add( s );
			}					
		}
		
		printMetrics(cmTarget, tpElements.size(), fpElements.size(), fnElements.size());		
	}
	

	

	


	/**
	 * imprime as métricas de TP; FP; FN; precision e recall;
	 * 
	 * @param filename
	 * @param tp
	 * @param fp
	 * @param fn
	 */
	private void printMetrics(String filename, double tp,
			double fp, double fn) {
			
		System.out.println("~~~~~~~~~~~~~~~~");
		System.out.println("TP size: "+tp);
		System.out.println("FP size: "+fp);
		System.out.println("FN size: "+fn);
		double r = tp/(tp+fn);
		double p = tp/(tp+fp);
		double f = (2*p*r)/(p+r);
		System.out.println("recall: "+r);
		System.out.println("precision: "+p);
		System.out.println("~~~~~~~~~~~~~~~~");
		
		String truePositives = String.valueOf(tp);
		String falsePositives = String.valueOf(fp);
		String falseNegatives = String.valueOf(fn);
		String recall = String.valueOf(r);
		String precision = String.valueOf(p);
		String f1Score = String.valueOf(f);
		
		MetricsManager.getInstance().addMetricsReport(
				new MetricsReport(filename, truePositives, falsePositives, falseNegatives, recall, precision, f1Score));
	}

}
