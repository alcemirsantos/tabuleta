/* ConcernMapper - A concern modeling plug-in for Eclipse
 * Copyright (C) 2006  McGill University (http://www.cs.mcgill.ca/~martin/cm)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Revision: 1.7 $
 */

package ca.mcgill.cs.serg.cm.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import ca.mcgill.cs.serg.cm.ConcernMapper;
import ca.mcgill.cs.serg.cm.views.ConcernMapperView;

/**
 * An action to add a new concern to the model.
 */
public class NewConcernAction extends Action
{
	private ConcernMapperView aViewer;
	
	/**
	 * Creates the action.
	 * @param pViewer The view from where the action is triggered
	 */
	public NewConcernAction( ConcernMapperView pViewer )
	{
		aViewer = pViewer;
		setText( ConcernMapper.getResourceString( "actions.NewConcernAction.Label") );
		setImageDescriptor( PlatformUI.getWorkbench().getSharedImages().getImageDescriptor( ISharedImages.IMG_OBJ_ELEMENT )); 
		setToolTipText( ConcernMapper.getResourceString( "actions.NewConcernAction.ToolTip" ));  
	}
	
	/**
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() 
	{
		InputDialog lDialog = new InputDialog( aViewer.getViewSite().getShell(), 
					ConcernMapper.getResourceString( "actions.NewConcernAction.DialogTitle" ),
					ConcernMapper.getResourceString( "actions.NewConcernAction.DialogLabel" ), "", 
					new IInputValidator()
					{
						public String isValid( String pName )
						{
							String lReturn = null;
							if( ConcernMapper.getDefault().getConcernModel().exists( pName ))
							{
								lReturn = ConcernMapper.getResourceString( "actions.NewConcernAction.NameInUse" );
							}
							else if( pName.length() < 1 )
							{
								lReturn = ConcernMapper.getResourceString( "actions.NewConcernAction.NoName" );
							}
							return lReturn;
						}
					});
		
		int lAction = lDialog.open();
			
		if( lAction == Window.OK )
		{
			ConcernMapper.getDefault().getConcernModel().newConcern( lDialog.getValue() );
			aViewer.setConcernSelection( lDialog.getValue() );
		}
	}
}

