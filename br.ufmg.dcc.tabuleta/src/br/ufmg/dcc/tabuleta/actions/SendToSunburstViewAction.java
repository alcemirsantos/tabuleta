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

import java.util.Iterator;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import prefuse.data.Graph;
import br.ufmg.dcc.tabuleta.Tabuleta;
import br.ufmg.dcc.tabuleta.actions.util.CmFilesOperations;
import br.ufmg.dcc.tabuleta.views.components.GraphManager;

/**
 * @author Alcemir R. Santos
 *
 */
public class SendToSunburstViewAction implements IObjectActionDelegate {
	private String CM_PATH;
	private ISelection selection;
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	@Override
	public void run(IAction action) {
		
		String[] cmFilePath = null;
		String[] cmFileName = null;
		
		IStructuredSelection isSelection = null;
		if (selection instanceof IStructuredSelection) {
			isSelection = (IStructuredSelection) selection;
		}
		if (isSelection == null){
			CmFilesOperations.showMessage("CM File Selection",
					"You must choose a .cm file to show in this view.");
			return;
		}else if(isSelection.size()>1){
			CmFilesOperations.showMessage("CM File Selection",
					"You can choose only one .cm file to show in this view.");
			return;
		}else{
			cmFilePath = new String[1];
			cmFileName = new String[1];
			
			Iterator<IResource> srcIterator = isSelection.iterator(); 
			if(srcIterator.hasNext()){
				IResource file = srcIterator.next();
				cmFilePath[0] = file.getLocation().toString();
				cmFileName[0] = file.getName();
			}
		}
		Graph g = new Graph();
		try {
			g = CmFilesOperations.getCMGraphML(cmFilePath[0]);
			CmFilesOperations.writeCMGraphMLFile(g, cmFileName[0]);
		} catch (Exception e) {
			// TODO tratar exceção no getDocument()
			e.printStackTrace();
		}
		
		GraphManager.getInstance().addGraph( g );
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection =  selection;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
	 */
	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO Auto-generated method stub
	}
	
//	protected Graph cmGraphMLBuilder(){
//		String[] cmFilePath = null;
//		String[] cmFileName = null;
//		
//		IStructuredSelection isSelection = null;
//		if (selection instanceof IStructuredSelection) {
//			isSelection = (IStructuredSelection) selection;
//		}
//		if (isSelection == null){
//			CmFilesOperations.showMessage("CM File Selection",
//					"You must choose a .cm file to show in this view.");
//			return null;
//		}else if(isSelection.size()>1){
//			CmFilesOperations.showMessage("CM File Selection",
//					"You must choose a .cm file to show in this view.");
//			return null;
//		}else{
//			cmFilePath = new String[1];
//			cmFileName = new String[1];
//			
//			Iterator<IResource> srcIterator = isSelection.iterator(); 
//			if(srcIterator.hasNext()){
//				IResource file = srcIterator.next();
//				cmFilePath[0] = file.getLocation().toString();
//				cmFileName[0] = file.getName();
//			}
//		}
//		Graph g = new Graph();
//		try {
//			g = CmFilesOperations.getCMGraphML(cmFilePath[0], cmFileName[0]);
//		} catch (Exception e) {
//			// TODO tratar exceção no getDocument()
//			e.printStackTrace();
//		}
//		
//		return g;		
//	}
}
