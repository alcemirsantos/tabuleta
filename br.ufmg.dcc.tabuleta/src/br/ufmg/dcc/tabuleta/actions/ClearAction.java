/* ConcernMapper - A concern modeling plug-in for Eclipse
 * Copyright (C) 2006  McGill University (http://www.cs.mcgill.ca/~martin/cm)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Revision: 1.5 $
 */

package br.ufmg.dcc.tabuleta.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

import br.ufmg.dcc.tabuleta.Tabuleta;

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
		setText( Tabuleta.getResourceString( "actions.ClearAction.Label" ) );
		setImageDescriptor( Tabuleta.imageDescriptorFromPlugin( Tabuleta.ID_PLUGIN, "icons/clear.gif"));
		setToolTipText( Tabuleta.getResourceString( "actions.ClearAction.ToolTip" ) );
	}
	/**
	 *  @see org.eclipse.jface.action.IAction#run()
	 */
	public void run()
	{
		if( shouldProceed() )
		{
			Tabuleta.getDefault().resetDirty();
			Tabuleta.getDefault().getConcernModel().reset();
			Tabuleta.getDefault().setDefaultResource( null );
		}
	}
	
	private boolean shouldProceed()
	{
		boolean lReturn = true;
		if( Tabuleta.getDefault().isDirty() )
		{
			lReturn = MessageDialog.openQuestion( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
					Tabuleta.getResourceString( "actions.ClearAction.QuestionDialogTitle" ),
					Tabuleta.getResourceString( "actions.ClearAction.WarningOverwrite"	));
		}
		return lReturn;
	}
	
}
