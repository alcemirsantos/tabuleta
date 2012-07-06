/* ConcernMapper - A concern modelling plug-in for Eclipse
 * Copyright (C) 2006  McGill University (http://www.cs.mcgill.ca/~martin/cm)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Revision: 1.26 $
 */

package ca.mcgill.cs.serg.cm.ui;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ca.mcgill.cs.serg.cm.ConcernMapper;

/**
 * Implements the preference page for ConcernMapper.
 */
public class ConcernMapperPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{
	public static final String P_FILTER_ENABLED 			= "FilterEnabledPreference";
	public static final String P_FILTER_THRESHOLD 			= "FilterThresholdPreference";
	public static final String P_BOLD_ENABLED 				= "BoldEnabledPreference";
	public static final String P_DECORATION_LIMIT 			= "DecorationLimitPreference";
	public static final String P_SUFFIX_ENABLED 			= "SuffixEnabledPreference";
	public static final String P_CM_FILE_EXT 				= "CmFileExtensionPreference";
	public static final String P_AUTO_SAVE					= "AutosaveOnClosePreference";
	public static final String P_AUTO_LOAD					= "AutoLoadPreference";
	public static final String P_AUTO_LOAD_RESOURCE			= "AutoLoadResource";
	public static final String NO_LOADED_RESOURCE			= "NoLoadedResource";
	public static final String P_SHOW_INCONSISTENT_ELEMENTS = "ShowInconsistentElementsPreference";
	public static final String P_SHOW_SLIDER				= "ShowSlider";
	public static final String P_SHOW_COMMENTS				= "ShowComments";
	
	private static final int MAX_FILTER_THRESHOLD = 99;
	
	/**
	 * Creates a new preference page for ConcernMapper.
	 */
	public ConcernMapperPreferencePage() 
	{
		super( FieldEditorPreferencePage.GRID );
		IPreferenceStore lStore = ConcernMapper.getDefault().getPreferenceStore();
		setPreferenceStore( lStore );
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	public void createFieldEditors()
	{
		//ConcernMapperFilter
		addField( new LabelFieldEditor( 
				ConcernMapper.getResourceString( "ui.ConcernMapperPreferencePage.HeaderFilter" ), getFieldEditorParent() ) );
		addField( new BooleanFieldEditor(P_FILTER_ENABLED,
				ConcernMapper.getResourceString( "ui.ConcernMapperPreferencePage.FilterEnabled" ),
				getFieldEditorParent()) );
		IntegerFieldEditor lIntegerFieldEditor = new IntegerFieldEditor(P_FILTER_THRESHOLD,
				ConcernMapper.getResourceString( "ui.ConcernMapperPreferencePage.FilterThreshold" ),
				getFieldEditorParent());
		lIntegerFieldEditor.setValidRange( 0, MAX_FILTER_THRESHOLD );
		addField( lIntegerFieldEditor );
		addField( new LabelFieldEditor( "", getFieldEditorParent() ) );
		
		//Saving
		addField( new LabelFieldEditor( 
				ConcernMapper.getResourceString( "ui.ConcernMapperPreferencePage.HeaderSaving" ), getFieldEditorParent() ) );
		addField( new BooleanFieldEditor( P_CM_FILE_EXT,
				ConcernMapper.getResourceString( "ui.ConcernMapperPreferencePage.CmFileExt" ),
				getFieldEditorParent() ) );
		addField( new BooleanFieldEditor( P_AUTO_SAVE,
				ConcernMapper.getResourceString( "ui.ConcernMapperPreferencePage.AutoSave" ),
				getFieldEditorParent() ) );
		addField( new BooleanFieldEditor( P_AUTO_LOAD,
				ConcernMapper.getResourceString( "ui.ConcernMapperPreferencePage.AutoLoad" ),
				getFieldEditorParent() ) );
		addField( new LabelFieldEditor( "", getFieldEditorParent() ) );
		
		//UI
		addField( new LabelFieldEditor( 
				ConcernMapper.getResourceString( "ui.ConcernMapperPreferencePage.HeaderUI" ), getFieldEditorParent() ) );
		
		addField( new BooleanFieldEditor(ConcernMapperPreferencePage.P_SHOW_SLIDER,
				ConcernMapper.getResourceString( "ui.ConcernMapperPreferencePage.ShowSlider" ),
				getFieldEditorParent()) );
		addField( new BooleanFieldEditor(ConcernMapperPreferencePage.P_SHOW_COMMENTS,
				ConcernMapper.getResourceString( "ui.ConcernMapperPreferencePage.ShowComments" ),
				getFieldEditorParent()) );
		addField( new BooleanFieldEditor(ConcernMapperPreferencePage.P_SUFFIX_ENABLED,
				ConcernMapper.getResourceString( "ui.ConcernMapperPreferencePage.SuffixEnabled" ),
				getFieldEditorParent()) );
		addField( new BooleanFieldEditor(ConcernMapperPreferencePage.P_BOLD_ENABLED,
				ConcernMapper.getResourceString( "ui.ConcernMapperPreferencePage.BoldEnabled" ),
				getFieldEditorParent()) );
		
		String[][] lRadioGroupValues = {
				{ConcernMapper.getResourceString( "ui.ConcernMapperPreferencePage.Parent1" ), "1"},
				{ConcernMapper.getResourceString( "ui.ConcernMapperPreferencePage.Parent2" ), "2"},
				{ConcernMapper.getResourceString( "ui.ConcernMapperPreferencePage.Parent3" ), "3"},
				{ConcernMapper.getResourceString( "ui.ConcernMapperPreferencePage.Parent4" ), "4"},
				{ConcernMapper.getResourceString( "ui.ConcernMapperPreferencePage.Parent5" ), "5"},
				{ConcernMapper.getResourceString( "ui.ConcernMapperPreferencePage.Parent6" ), "7"}};
		addField( new RadioGroupFieldEditor(
				ConcernMapperPreferencePage.P_DECORATION_LIMIT, ConcernMapper.
				getResourceString( "ui.ConcernMapperPreferencePage.ParentDecoration"), 1,
	            lRadioGroupValues, getFieldEditorParent()) );
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 * @param pWorkbench the workbench
	 */
	public void init(IWorkbench pWorkbench) {}

	/**
	 * A field editor for displaying labels not associated with other widgets.
	 */
	class LabelFieldEditor extends FieldEditor
	{
		private Label aLabel;

		/**
		 * All labels can use the same preference name since they don't
		 * store any preference.
		 * @param pValue The value for the label.
		 * @param pParent The parent widget.
		 */
		public LabelFieldEditor(String pValue, Composite pParent)
		{
			super( "label", pValue, pParent );
		}

		/**
		 * Adjusts the field editor to be displayed correctly
		 * for the given number of columns.
		 * @see org.eclipse.jface.preference.FieldEditor#adjustForNumColumns(int)
		 * @param pNumColumns the number of columns
		 */
		protected void adjustForNumColumns(int pNumColumns)
		{
			((GridData) aLabel.getLayoutData()).horizontalSpan = pNumColumns;
		}

		/**
		 * Fills the field editor's controls into the given parent.
		 * @see org.eclipse.jface.preference.FieldEditor#doFillIntoGrid(org.eclipse.swt.widgets.Composite, int)
		 * @param pParent the composite used as a parent for the basic controls;
		 *	the parent's layout must be a <code>GridLayout</code>
		 * @param pNumColumns the number of columns
		 * 
		 */
		protected void doFillIntoGrid( Composite pParent, int pNumColumns )
		{
			aLabel = getLabelControl( pParent );
			
			GridData lGridData = new GridData();
			lGridData.horizontalSpan = pNumColumns;
			lGridData.horizontalAlignment = GridData.FILL;
			lGridData.grabExcessHorizontalSpace = false;
			lGridData.verticalAlignment = GridData.CENTER;
			lGridData.grabExcessVerticalSpace = false;
			
			aLabel.setLayoutData( lGridData );
		}

		/**
		 * @see org.eclipse.jface.preference.FieldEditor#getNumberOfControls()
		 * @return the number of controls
		 */
		public int getNumberOfControls() 
		{ return 1; }
		
		/**
		 * @see org.eclipse.jface.preference.FieldEditor#doLoad()
		 */
		protected void doLoad() {}
		
		/**
		 * @see org.eclipse.jface.preference.FieldEditor#doLoadDefault()
		 */
		protected void doLoadDefault() {}
		
		/**
		 * @see org.eclipse.jface.preference.FieldEditor#doStore()
		 */
		protected void doStore() {}
	}
}
