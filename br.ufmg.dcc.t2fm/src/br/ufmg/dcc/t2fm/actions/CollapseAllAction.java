/* ConcernMapper - A concern modeling plug-in for Eclipse
 * Copyright (C) 2006  McGill University (http://www.cs.mcgill.ca/~martin/cm)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Revision: 1.7 $
 */

package br.ufmg.dcc.t2fm.actions;

import org.eclipse.jface.action.Action;

import br.ufmg.dcc.t2fm.Test2FeatureMapper;
import br.ufmg.dcc.t2fm.views.MapView;

/**
 * An action to collapse all trees in the ConcernMapper View.
 */
public class CollapseAllAction extends Action
{
	private MapView aViewer;

	/**
	 * Creates the action.
	 * @param pViewer The viewer controlling this action.
	 */
	public CollapseAllAction( MapView pViewer )
	{
		aViewer = pViewer;
		setText( Test2FeatureMapper.getResourceString( "actions.CollapseAllAction.Label") );
		setImageDescriptor( Test2FeatureMapper.imageDescriptorFromPlugin( Test2FeatureMapper.ID_PLUGIN, "icons/collapseall.gif")); 
		setToolTipText( Test2FeatureMapper.getResourceString( "actions.CollapseAllAction.ToolTip" ) ); 
	}

	/**
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() 
	{
		aViewer.collapseAll();
	}
}
