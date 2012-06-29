/* ConcernMapper - A concern modeling plug-in for Eclipse
 * Copyright (C) 2006  McGill University (http://www.cs.mcgill.ca/~martin/cm)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Revision: 1.9 $
 */

package br.ufmg.dcc.t2fm.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;

import br.ufmg.dcc.t2fm.Test2FeatureMapper;
import br.ufmg.dcc.t2fm.views.MapView;

/**
 * An action to rename a concern to the model.
 */
public class RenameConcernAction extends Action
{
	private MapView aViewer;
	private String aConcern; // The concern to rename
	
	/**
	 * Creates the action.
	 * @param pConcern The view from where the action is triggered
	 * @param pViewer The viewer controlling this action.
	 */
	public RenameConcernAction( MapView pViewer, String pConcern )
	{
		aConcern = pConcern;
		aViewer = pViewer;
		setText( Test2FeatureMapper.getResourceString( "actions.RenameConcernAction.Label") );
		setToolTipText( Test2FeatureMapper.getResourceString( "actions.RenameConcernAction.ToolTip" ) ); 
	}

	/**
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() 
	{
		InputDialog lDialog = new InputDialog( aViewer.getViewSite().getShell(), 
					Test2FeatureMapper.getResourceString( "actions.RenameConcernAction.DialogTitle" ),
					Test2FeatureMapper.getResourceString( "actions.RenameConcernAction.DialogLabel" ), aConcern, 
					new IInputValidator()
					{
						public String isValid( String pName )
						{
							String lReturn = null;
							if( pName.equals( aConcern ))
							{
								return null;
							}
							if( Test2FeatureMapper.getDefault().getConcernModel().exists( pName ))
							{
								lReturn = Test2FeatureMapper.getResourceString( "actions.RenameConcernAction.NameInUse" );
							}
							else if( pName.length() < 1 )
							{
								lReturn = Test2FeatureMapper.getResourceString( "actions.RenameConcernAction.NoName" );
							}
							return lReturn;
						}
					});
		
		int lAction = lDialog.open();
			
		if( (lAction == Window.OK) && !(lDialog.getValue().equals( aConcern )) )
		{
			Test2FeatureMapper.getDefault().getConcernModel().renameConcern( aConcern, lDialog.getValue() );
		}
	}
}
