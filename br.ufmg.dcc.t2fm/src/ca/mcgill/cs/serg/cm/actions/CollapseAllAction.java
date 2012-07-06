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

import ca.mcgill.cs.serg.cm.ConcernMapper;
import ca.mcgill.cs.serg.cm.views.ConcernMapperView;

/**
 * An action to collapse all trees in the ConcernMapper View.
 */
public class CollapseAllAction extends Action
{
	private ConcernMapperView aViewer;

	/**
	 * Creates the action.
	 * @param pViewer The viewer controlling this action.
	 */
	public CollapseAllAction( ConcernMapperView pViewer )
	{
		aViewer = pViewer;
		setText( ConcernMapper.getResourceString( "actions.CollapseAllAction.Label") );
		setImageDescriptor( ConcernMapper.imageDescriptorFromPlugin( ConcernMapper.ID_PLUGIN, "icons/collapseall.gif")); 
		setToolTipText( ConcernMapper.getResourceString( "actions.CollapseAllAction.ToolTip" ) ); 
	}

	/**
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() 
	{
		aViewer.collapseAll();
	}
}
