/* ConcernMapper - A concern modeling plug-in for Eclipse
 * Copyright (C) 2006  McGill University (http://www.cs.mcgill.ca/~martin/cm)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Revision: 1.4 $
 */

package br.ufmg.dcc.t2fm.actions;

import org.eclipse.jface.action.Action;

import br.ufmg.dcc.t2fm.Test2FeatureMapper;
import br.ufmg.dcc.t2fm.ui.ConcernMapperPreferencePage;

/**
 * Allows to toggle between showing or hiding inconsistent elements in the view.
 * @see org.eclipse.jface.action.Action
 */
public class ShowInconsistentElementsAction extends Action
{
	/**
	 * The constructor. Sets the text, image and tooltip text.
	 * @param pText the text to use for the action. Not used.
	 * @param pStyle the style of the action. Should be Action.AS_CHECK_BOX
	 */
	public ShowInconsistentElementsAction(String pText, int pStyle)
	{
		setText( Test2FeatureMapper.getResourceString( "actions.ShowInconsistentElementsAction.Text" ) );
		setToolTipText( Test2FeatureMapper.getResourceString( "actions.ShowInconsistentElementsAction.Text" ) );
		setChecked( Test2FeatureMapper.getDefault().getPreferenceStore().getBoolean( ConcernMapperPreferencePage.P_SHOW_INCONSISTENT_ELEMENTS ));
	}

	/**
	 * Is called when the action is run. Sets the show inconsistent elements preference.
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run()
	{
		if( isChecked() )
		{
			Test2FeatureMapper.getDefault().getPreferenceStore().setValue( ConcernMapperPreferencePage.P_SHOW_INCONSISTENT_ELEMENTS, true );
		}
		else
		{
			Test2FeatureMapper.getDefault().getPreferenceStore().setValue( ConcernMapperPreferencePage.P_SHOW_INCONSISTENT_ELEMENTS, false );
		}
	}
}
