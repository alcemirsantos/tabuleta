/* ConcernMapper - A concern modeling plug-in for Eclipse
 * Copyright (C) 2006  McGill University (http://www.cs.mcgill.ca/~martin/cm)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Revision: 1.8 $
 */

package ca.mcgill.cs.serg.cm.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.PlatformUI;

import ca.mcgill.cs.serg.cm.ConcernMapper;
import ca.mcgill.cs.serg.cm.ui.FilterDialog;

/**
 * Shows the filter dialog box and allows the user to modify the filtering options.
 */
public class FilterAction extends Action
{	
	/** 
	 * Builds a new FilterAction and sets its tooltip text based on current
	 * preference settings for the filtering.
	 */
	public FilterAction()
	{
		setImageDescriptor( ConcernMapper.imageDescriptorFromPlugin( ConcernMapper.ID_PLUGIN, "icons/filter.png") );
		setText( ConcernMapper.getResourceString( "actions.FilterAction.Text" ) );		
	}
	
	/** 
	 * Open a filter options dialog.
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run()
	{
		FilterDialog lDialog = new FilterDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		lDialog.open();
	}
}
