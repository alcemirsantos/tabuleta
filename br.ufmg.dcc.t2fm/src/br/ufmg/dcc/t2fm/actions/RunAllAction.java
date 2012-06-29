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

import org.eclipse.jface.action.Action;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import br.ufmg.dcc.t2fm.Test2FeatureMapper;

/**
 * @author alcemir
 *
 */
public class RunAllAction extends Action {
	
	
	public RunAllAction(){
		setText( Test2FeatureMapper.getResourceString( "actions.RunAllAction.Label") );
		setImageDescriptor( PlatformUI.getWorkbench().getSharedImages().getImageDescriptor( ISharedImages.IMG_OBJ_ELEMENT )); 
		setToolTipText( Test2FeatureMapper.getResourceString( "actions.RunAllAction.ToolTip" ));  
	}
	/** 
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run(){
		// TODO executar o eclemma com os testes associados a uma feature;
	}

}
