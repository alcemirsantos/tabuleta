/* ConcernMapper - A concern modeling plug-in for Eclipse
 * Copyright (C) 2006  McGill University (http://www.cs.mcgill.ca/~martin/cm)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Revision: 1.4 $
 */

package br.ufmg.dcc.tabuleta.actions;

import org.eclipse.jface.action.Action;

import br.ufmg.dcc.tabuleta.Tabuleta;
import br.ufmg.dcc.tabuleta.ui.ConcernMapperPreferencePage;

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
		setText( Tabuleta.getResourceString( "actions.ShowInconsistentElementsAction.Text" ) );
		setToolTipText( Tabuleta.getResourceString( "actions.ShowInconsistentElementsAction.Text" ) );
		setChecked( Tabuleta.getDefault().getPreferenceStore().getBoolean( ConcernMapperPreferencePage.P_SHOW_INCONSISTENT_ELEMENTS ));
	}

	/**
	 * Is called when the action is run. Sets the show inconsistent elements preference.
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run()
	{
		if( isChecked() )
		{
			Tabuleta.getDefault().getPreferenceStore().setValue( ConcernMapperPreferencePage.P_SHOW_INCONSISTENT_ELEMENTS, true );
		}
		else
		{
			Tabuleta.getDefault().getPreferenceStore().setValue( ConcernMapperPreferencePage.P_SHOW_INCONSISTENT_ELEMENTS, false );
		}
	}
}
