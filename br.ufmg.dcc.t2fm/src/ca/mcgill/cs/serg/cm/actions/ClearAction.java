/* ConcernMapper - A concern modeling plug-in for Eclipse
 * Copyright (C) 2006  McGill University (http://www.cs.mcgill.ca/~martin/cm)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Revision: 1.5 $
 */

package ca.mcgill.cs.serg.cm.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

import ca.mcgill.cs.serg.cm.ConcernMapper;

/**
 * Clears the concern model.
 */
public class ClearAction extends Action
{
	/**
	 * The constructor. Sets the text label and tooltip
	 */
	public ClearAction()
	{
		setText( ConcernMapper.getResourceString( "actions.ClearAction.Label" ) );
		setImageDescriptor( ConcernMapper.imageDescriptorFromPlugin( ConcernMapper.ID_PLUGIN, "icons/clear.gif"));
		setToolTipText( ConcernMapper.getResourceString( "actions.ClearAction.ToolTip" ) );
	}
	/**
	 *  @see org.eclipse.jface.action.IAction#run()
	 */
	public void run()
	{
		if( shouldProceed() )
		{
			ConcernMapper.getDefault().resetDirty();
			ConcernMapper.getDefault().getConcernModel().reset();
			ConcernMapper.getDefault().setDefaultResource( null );
		}
	}
	
	private boolean shouldProceed()
	{
		boolean lReturn = true;
		if( ConcernMapper.getDefault().isDirty() )
		{
			lReturn = MessageDialog.openQuestion( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
					ConcernMapper.getResourceString( "actions.ClearAction.QuestionDialogTitle" ),
					ConcernMapper.getResourceString( "actions.ClearAction.WarningOverwrite"	));
		}
		return lReturn;
	}
	
}
