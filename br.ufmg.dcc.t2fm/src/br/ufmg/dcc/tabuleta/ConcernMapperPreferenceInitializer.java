/* ConcernMapper - A concern modeling plug-in for Eclipse
 * Copyright (C) 2006  McGill University (http://www.cs.mcgill.ca/~martin/cm)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Revision: 1.11 $
 */

package br.ufmg.dcc.tabuleta;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import br.ufmg.dcc.tabuleta.ui.ConcernMapperPreferencePage;

/**
 * Initializes the default preferences for the plug-in.
 */
public class ConcernMapperPreferenceInitializer extends	AbstractPreferenceInitializer 
{
	/**
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences()
	{
		final IEclipsePreferences lNode = new DefaultScope().getNode( Tabuleta.ID_PLUGIN );
		
		lNode.put( ConcernMapperPreferencePage.P_BOLD_ENABLED, 
				Tabuleta.getResourceString( "ConcernMapperPreferenceInitializer.DefaultBoldEnabled" ));
		lNode.put( ConcernMapperPreferencePage.P_SUFFIX_ENABLED, 
				Tabuleta.getResourceString( "ConcernMapperPreferenceInitializer.DefaultSuffixEnabled" ));
		lNode.put( ConcernMapperPreferencePage.P_FILTER_ENABLED, 
				Tabuleta.getResourceString( "ConcernMapperPreferenceInitializer.DefaultFilterEnabled" ));
		lNode.put( ConcernMapperPreferencePage.P_FILTER_THRESHOLD, 
				Tabuleta.getResourceString( "ConcernMapperPreferenceInitializer.DefaultFilterTreshold" ));
		lNode.put( ConcernMapperPreferencePage.P_CM_FILE_EXT, 
				Tabuleta.getResourceString( "ConcernMapperPreferenceInitializer.DefaultFileExtension" ));
		lNode.put( ConcernMapperPreferencePage.P_DECORATION_LIMIT, 
				Tabuleta.getResourceString( "ConcernMapperPreferenceInitializer.DefaultDecorationLimit" ));
		lNode.put( ConcernMapperPreferencePage.P_AUTO_SAVE, 
				Tabuleta.getResourceString( "ConcernMapperPreferenceInitializer.DefaultAutoSave" ));
		lNode.put( ConcernMapperPreferencePage.P_AUTO_LOAD, 
				Tabuleta.getResourceString( "ConcernMapperPreferenceInitializer.DefaultAutoLoad" ));
		lNode.put( ConcernMapperPreferencePage.P_SHOW_INCONSISTENT_ELEMENTS, 
				Tabuleta.getResourceString( "ConcernMapperPreferenceInitializer.DefaultShowInconsistentElements" ));
		lNode.put( ConcernMapperPreferencePage.P_AUTO_LOAD_RESOURCE, ConcernMapperPreferencePage.NO_LOADED_RESOURCE);
		lNode.put( ConcernMapperPreferencePage.P_SHOW_SLIDER, 
				Tabuleta.getResourceString( "ConcernMapperPreferenceInitializer.DefaultShowSlider" ));
		lNode.put( ConcernMapperPreferencePage.P_SHOW_COMMENTS, 
				Tabuleta.getResourceString( "ConcernMapperPreferenceInitializer.DefaultShowComments" ));

	}
}
