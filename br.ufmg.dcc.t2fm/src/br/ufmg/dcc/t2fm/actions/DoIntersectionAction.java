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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

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

import br.ufmg.dcc.t2fm.Test2FeatureMapper;
import br.ufmg.dcc.t2fm.actions.util.CMElementTag;
import br.ufmg.dcc.t2fm.actions.util.CmFilesOperations;

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
		if (CM_PATH.isEmpty()) {
			CmFilesOperations.showMessage("Do Intersection Action",
					"You must set the path to .cm files on the Test2FeatureMapper preference page.");
			return;
		}		
		String[] files = null;
		
		IStructuredSelection isSelection = null;
		if (selection instanceof IStructuredSelection) {
			isSelection = (IStructuredSelection) selection;
		}
		if (isSelection == null){
			CmFilesOperations.showMessage("Do Intersection Action",
					"Triggered \"Do Intersection\" with none selection");
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
		Set<String> concerns = null;
		try {
			concerns = CmFilesOperations.getCMConcernNames(files);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String concern = null;
	    if(concerns.isEmpty()){
	    	CmFilesOperations.showMessage("Do Intersection Action",
	    			"Triggered \"Do Intersection\" with .cm files with no concerns.");
			return;
	    }else if (concerns.size()==1) {
			concern = (String) concerns.toArray()[0];
		}else{
			concern = askByConcern(concerns.toArray());
		}
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
		dialog.setTitle("What Concern do you want to do interserction?");
		// enquanto o usuário não disser qual é o concern
		while (dialog.open() != Window.OK){
			CmFilesOperations.showMessage("Do Intersection Action",
					"You must point which concern do you want to make a intersection.");
		}
		Object[] result = dialog.getResult();
		return (String)result[0];
	}

	/**
	 * Faz a intersecção de arquivos .cm contidos no vetor passado como parâmetro.
	 * 
	 *  OBSERVAÇÃO: setar concern caso tenha mais de um concern no <code>.cm</code>.
	 * 
	 * @param files
	 */
	private void doIntersection(String[] files, String concern){
		Map<Integer,Document> docs = new HashMap<Integer,Document>();
		
		Stack elements = new Stack();
		
		ArrayList<CMElementTag> intersectionElements = new ArrayList<CMElementTag>();
		
		for (int i = 0; i < files.length; i++) {
			try {
				docs.put(i, CmFilesOperations.getDocument(files[i]) );
				elements.add( (ArrayList<CMElementTag>) CmFilesOperations.getConcernElements(docs.get(i), concern));
			} catch (Exception e) {
				e.printStackTrace();
			}		
		}
		
		intersectionElements = (ArrayList<CMElementTag>) elements.pop();		
		Set<ArrayList<CMElementTag>> conjunto = new HashSet<ArrayList<CMElementTag>>();

		while (!elements.empty()) {
			conjunto.add((ArrayList<CMElementTag>) elements.pop()); 
		}
				
		for (ArrayList<CMElementTag> arrayList : conjunto) {
			intersectionElements.retainAll(arrayList);			
		}
				
		CmFilesOperations.writeToCMFile(concern, CmFilesOperations.buildCMFileString(concern, intersectionElements), CM_PATH);
	}
}
