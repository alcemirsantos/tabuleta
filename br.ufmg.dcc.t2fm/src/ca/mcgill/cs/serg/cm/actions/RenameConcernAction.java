/* ConcernMapper - A concern modeling plug-in for Eclipse
 * Copyright (C) 2006  McGill University (http://www.cs.mcgill.ca/~martin/cm)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Revision: 1.9 $
 */

package ca.mcgill.cs.serg.cm.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;

import ca.mcgill.cs.serg.cm.ConcernMapper;
import ca.mcgill.cs.serg.cm.views.ConcernMapperView;

/**
 * An action to rename a concern to the model.
 */
public class RenameConcernAction extends Action
{
	private ConcernMapperView aViewer;
	private String aConcern; // The concern to rename
	
	/**
	 * Creates the action.
	 * @param pConcern The view from where the action is triggered
	 * @param pViewer The viewer controlling this action.
	 */
	public RenameConcernAction( ConcernMapperView pViewer, String pConcern )
	{
		aConcern = pConcern;
		aViewer = pViewer;
		setText( ConcernMapper.getResourceString( "actions.RenameConcernAction.Label") );
		setToolTipText( ConcernMapper.getResourceString( "actions.RenameConcernAction.ToolTip" ) ); 
	}

	/**
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() 
	{
		InputDialog lDialog = new InputDialog( aViewer.getViewSite().getShell(), 
					ConcernMapper.getResourceString( "actions.RenameConcernAction.DialogTitle" ),
					ConcernMapper.getResourceString( "actions.RenameConcernAction.DialogLabel" ), aConcern, 
					new IInputValidator()
					{
						public String isValid( String pName )
						{
							String lReturn = null;
							if( pName.equals( aConcern ))
							{
								return null;
							}
							if( ConcernMapper.getDefault().getConcernModel().exists( pName ))
							{
								lReturn = ConcernMapper.getResourceString( "actions.RenameConcernAction.NameInUse" );
							}
							else if( pName.length() < 1 )
							{
								lReturn = ConcernMapper.getResourceString( "actions.RenameConcernAction.NoName" );
							}
							return lReturn;
						}
					});
		
		int lAction = lDialog.open();
			
		if( (lAction == Window.OK) && !(lDialog.getValue().equals( aConcern )) )
		{
			ConcernMapper.getDefault().getConcernModel().renameConcern( aConcern, lDialog.getValue() );
		}
	}
}
