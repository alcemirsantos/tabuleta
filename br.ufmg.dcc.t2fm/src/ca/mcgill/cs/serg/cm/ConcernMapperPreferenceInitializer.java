/* ConcernMapper - A concern modeling plug-in for Eclipse
 * Copyright (C) 2006  McGill University (http://www.cs.mcgill.ca/~martin/cm)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Revision: 1.11 $
 */

package ca.mcgill.cs.serg.cm;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import ca.mcgill.cs.serg.cm.ui.ConcernMapperPreferencePage;

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
		final IEclipsePreferences lNode = new DefaultScope().getNode( ConcernMapper.ID_PLUGIN );
		
		lNode.put( ConcernMapperPreferencePage.P_BOLD_ENABLED, 
				ConcernMapper.getResourceString( "ConcernMapperPreferenceInitializer.DefaultBoldEnabled" ));
		lNode.put( ConcernMapperPreferencePage.P_SUFFIX_ENABLED, 
				ConcernMapper.getResourceString( "ConcernMapperPreferenceInitializer.DefaultSuffixEnabled" ));
		lNode.put( ConcernMapperPreferencePage.P_FILTER_ENABLED, 
				ConcernMapper.getResourceString( "ConcernMapperPreferenceInitializer.DefaultFilterEnabled" ));
		lNode.put( ConcernMapperPreferencePage.P_FILTER_THRESHOLD, 
				ConcernMapper.getResourceString( "ConcernMapperPreferenceInitializer.DefaultFilterTreshold" ));
		lNode.put( ConcernMapperPreferencePage.P_CM_FILE_EXT, 
				ConcernMapper.getResourceString( "ConcernMapperPreferenceInitializer.DefaultFileExtension" ));
		lNode.put( ConcernMapperPreferencePage.P_DECORATION_LIMIT, 
				ConcernMapper.getResourceString( "ConcernMapperPreferenceInitializer.DefaultDecorationLimit" ));
		lNode.put( ConcernMapperPreferencePage.P_AUTO_SAVE, 
				ConcernMapper.getResourceString( "ConcernMapperPreferenceInitializer.DefaultAutoSave" ));
		lNode.put( ConcernMapperPreferencePage.P_AUTO_LOAD, 
				ConcernMapper.getResourceString( "ConcernMapperPreferenceInitializer.DefaultAutoLoad" ));
		lNode.put( ConcernMapperPreferencePage.P_SHOW_INCONSISTENT_ELEMENTS, 
				ConcernMapper.getResourceString( "ConcernMapperPreferenceInitializer.DefaultShowInconsistentElements" ));
		lNode.put( ConcernMapperPreferencePage.P_AUTO_LOAD_RESOURCE, ConcernMapperPreferencePage.NO_LOADED_RESOURCE);
		lNode.put( ConcernMapperPreferencePage.P_SHOW_SLIDER, 
				ConcernMapper.getResourceString( "ConcernMapperPreferenceInitializer.DefaultShowSlider" ));
		lNode.put( ConcernMapperPreferencePage.P_SHOW_COMMENTS, 
				ConcernMapper.getResourceString( "ConcernMapperPreferenceInitializer.DefaultShowComments" ));

	}
}
