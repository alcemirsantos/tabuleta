/* ConcernMapper - A concern modeling plug-in for Eclipse
 * Copyright (C) 2006  McGill University (http://www.cs.mcgill.ca/~martin/cm)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Revision: 1.13 $
 */

package br.ufmg.dcc.tabuleta.ui;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Text;

import br.ufmg.dcc.tabuleta.Tabuleta;

/**
 * Dialog appearing when a user selects the filter action.
 */
public class FilterDialog extends MessageDialogWithToggle 
{
	private static final int SLIDER_INCREMENT = 10;
	private static final int SLIDER_MAXIMUM = 109; // Only goes to 99 in practice.  Go figure.
	
	private Slider aThresholdSlider;
	private Text aThresholdValue;

	/**
	 * Creates the filter dialog.
	 * @param pParentShell The parent shell for this dialog
	 */
	public FilterDialog( Shell pParentShell ) 
	{
		super( pParentShell, 
			   Tabuleta.getResourceString( "views.ConcernMapperView.FilterDialogTitle" ), 
			   null, 
			   Tabuleta.getResourceString( "ui.ConcernMapperPreferencePage.FilterThreshold" ), 
			   0,
			   new String[]{ IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL }, 
			   0, 
			   Tabuleta.getResourceString( "ui.ConcernMapperPreferencePage.FilterEnabled" ),
			   Tabuleta.getDefault().getPreferenceStore().getBoolean( ConcernMapperPreferencePage.P_FILTER_ENABLED ));
	}

	/**
	 * Modifies the preferences on buttons pressed.
	 * @param pButtonID The id for the button pressed.
	 */
	protected void buttonPressed( int pButtonID ) 
	{
		if( pButtonID == 0 )
		{
			Tabuleta.getDefault().getPreferenceStore().
				setValue( ConcernMapperPreferencePage.P_FILTER_ENABLED, getToggleState());
			Tabuleta.getDefault().getPreferenceStore().
				setValue( ConcernMapperPreferencePage.P_FILTER_THRESHOLD, new Integer( aThresholdSlider.getSelection()).toString() );
		}
		close();
	}

	/**
	 * Builds the custom layout for this dialog.
	 * @param pParent The parent widget.
	 * @return The control.
	 */
	protected Control createCustomArea( Composite pParent )
	{
		aThresholdSlider = new Slider( pParent, SWT.HORIZONTAL );
		GridData lGridData = new GridData();
		lGridData.horizontalAlignment = GridData.FILL;
 		lGridData.grabExcessHorizontalSpace = true;
 		lGridData.grabExcessVerticalSpace = false;
 		
		aThresholdSlider.setLayoutData( lGridData );
		aThresholdSlider.setIncrement( 1 );
		aThresholdSlider.setPageIncrement( SLIDER_INCREMENT );
		aThresholdSlider.setMinimum( 0 );
		aThresholdSlider.setMaximum( SLIDER_MAXIMUM ); // This only goes to 99 in practice.
		aThresholdSlider.setEnabled( true );
		aThresholdSlider.setSelection(
				Tabuleta.getDefault().getPreferenceStore().getInt(
						ConcernMapperPreferencePage.P_FILTER_THRESHOLD));
		
		aThresholdValue = new Text( pParent, 0 );
		lGridData = new GridData();
		lGridData.verticalAlignment = GridData.END;
 		lGridData.grabExcessHorizontalSpace = false;
 		lGridData.grabExcessVerticalSpace = false;
 		
 		aThresholdValue.setLayoutData( lGridData );
 		aThresholdValue.setEditable( false );
 		aThresholdValue.setEnabled( false );
 		aThresholdValue.setText( new Integer(
 				Tabuleta.getDefault().getPreferenceStore().getInt(
 						ConcernMapperPreferencePage.P_FILTER_THRESHOLD)).toString() + "   ");
 		aThresholdValue.setOrientation( SWT.RIGHT );
 		aThresholdSlider.addSelectionListener( new SelectionListener(){
 			
 			public void widgetDefaultSelected(SelectionEvent pEvent) {}

			public void widgetSelected(SelectionEvent pEvent) 
			{
				aThresholdValue.setText( new Integer(aThresholdSlider.getSelection()).toString());
				setToggleState( true );
			}
			
		});
		return super.createCustomArea( pParent );
	}

}
