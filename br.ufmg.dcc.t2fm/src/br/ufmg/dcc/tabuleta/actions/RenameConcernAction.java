/* ConcernMapper - A concern modeling plug-in for Eclipse
 * Copyright (C) 2006  McGill University (http://www.cs.mcgill.ca/~martin/cm)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Revision: 1.9 $
 */

package br.ufmg.dcc.tabuleta.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;

import br.ufmg.dcc.tabuleta.Tabuleta;
import br.ufmg.dcc.tabuleta.views.T2FMappingView;

/**
 * An action to rename a concern to the model.
 */
public class RenameConcernAction extends Action
{
	private T2FMappingView aViewer;
	private String aConcern; // The concern to rename
	
	/**
	 * Creates the action.
	 * @param pConcern The view from where the action is triggered
	 * @param pViewer The viewer controlling this action.
	 */
	public RenameConcernAction( T2FMappingView pViewer, String pConcern )
	{
		aConcern = pConcern;
		aViewer = pViewer;
		setText( Tabuleta.getResourceString( "actions.RenameConcernAction.Label") );
		setToolTipText( Tabuleta.getResourceString( "actions.RenameConcernAction.ToolTip" ) ); 
	}

	/**
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() 
	{
		InputDialog lDialog = new InputDialog( aViewer.getViewSite().getShell(), 
					Tabuleta.getResourceString( "actions.RenameConcernAction.DialogTitle" ),
					Tabuleta.getResourceString( "actions.RenameConcernAction.DialogLabel" ), aConcern, 
					new IInputValidator()
					{
						public String isValid( String pName )
						{
							String lReturn = null;
							if( pName.equals( aConcern ))
							{
								return null;
							}
							if( Tabuleta.getDefault().getConcernModel().exists( pName ))
							{
								lReturn = Tabuleta.getResourceString( "actions.RenameConcernAction.NameInUse" );
							}
							else if( pName.length() < 1 )
							{
								lReturn = Tabuleta.getResourceString( "actions.RenameConcernAction.NoName" );
							}
							return lReturn;
						}
					});
		
		int lAction = lDialog.open();
			
		if( (lAction == Window.OK) && !(lDialog.getValue().equals( aConcern )) )
		{
			Tabuleta.getDefault().getConcernModel().renameConcern( aConcern, lDialog.getValue() );
		}
	}
}
