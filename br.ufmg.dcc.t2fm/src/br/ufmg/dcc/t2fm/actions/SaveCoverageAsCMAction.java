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

import java.util.Set;

import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.jacoco.core.analysis.ICoverageNode;

import br.ufmg.dcc.t2fm.views.MapView;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.core.ScopeUtils;;

/**
 * @author Alcemir R. Santos
 *
 */
public class SaveCoverageAsCMAction extends Action {

	MapView aView;
	/**
	 * @param mapView
	 */
	public SaveCoverageAsCMAction(MapView mapView) {
		this.aView = mapView;
		setText("Save Coverage into a cm File");
		setToolTipText("Save Coverage into a cm File");
		setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FILE));
	}

	public void run(){
	
		boolean thereIsActiveCoverageSession = false;
		ICoverageSession activeSession = CoverageTools.getSessionManager().getActiveSession();
		thereIsActiveCoverageSession =  activeSession == null ? false:true ;
		
		// se existe uma sessão ativa
		if(thereIsActiveCoverageSession ){
			Set<IPackageFragmentRoot> escopo =  ScopeUtils.filterJREEntries( activeSession.getScope() );
			//&& e se o elemento está no escopo
			for(IPackageFragmentRoot pakkage: escopo){
				CoverageTools.getCoverageInfo(object);
				ICoverageNode coverage = (ICoverageNode) someJavaElement.getAdapter(ICoverageNode.class);
				// TODO criar um concernmodel com o que foi coberto;
			}
			
			
		}
		

		
		// TODO escrever no cm file;
	}
	

}
