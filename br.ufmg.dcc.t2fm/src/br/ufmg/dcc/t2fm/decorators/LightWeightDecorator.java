/* ConcernMapper - A concern modeling plug-in for Eclipse
 * Copyright (C) 2006  McGill University (http://www.cs.mcgill.ca/~martin/cm)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Revision: 1.26 $
 */

package br.ufmg.dcc.t2fm.decorators;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.PlatformUI;

import br.ufmg.dcc.t2fm.Test2FeatureMapper;
import br.ufmg.dcc.t2fm.model.ConcernModel;
import br.ufmg.dcc.t2fm.model.ConcernModelChangeListener;
import br.ufmg.dcc.t2fm.ui.ConcernMapperPreferencePage;

/**
 * Decorates elements in Package explorer, Outline and Type Hierarchy view.
 */
public class LightWeightDecorator extends LabelProvider implements ConcernModelChangeListener, ILightweightLabelDecorator, IPropertyChangeListener
{
	/**
	 * Creates the new label decorator.
	 */
	public LightWeightDecorator() 
	{
		Test2FeatureMapper.getDefault().getConcernModel().addListener( this );
		Test2FeatureMapper.getDefault().getPreferenceStore().addPropertyChangeListener( this );
	}
	
	/**
	 * Decorates elements belonging to the concern model in the JDT views.
	 * @param pElement The element being decorated
	 * @param pDecoration The decoration to add to the element's label
	 * @see org.eclipse.jface.viewers.ILightweightLabelDecorator#decorate(java.lang.Object, org.eclipse.jface.viewers.IDecoration)
	 */
	public void decorate( Object pElement, IDecoration pDecoration ) 
	{
		boolean lDoDecorateSuffix = false;
		boolean lDoDecorateFont = false;
		List<String> lConcerns = new ArrayList<String>();
		
		//Get the names of the concerns from the concern model
		String[] lConcernNames = Test2FeatureMapper.getDefault().getConcernModel().getConcernNames();
		
		//For each concern, get the elements it contains
		for( String lConcernName : lConcernNames )
		{
			Set<Object> lCurrentConcernElements = Test2FeatureMapper.getDefault().getConcernModel().getElements( lConcernName );
			//if pElement is an element of the current concern
			//add the name of the concern to the list of concerns pElement belongs to
			if( lCurrentConcernElements.contains( pElement ))
			{
				int lDegree = Test2FeatureMapper.getDefault().getConcernModel().getDegree( lConcernName, pElement );
				int lThreshold = Test2FeatureMapper.getDefault().getPreferenceStore().getInt( ConcernMapperPreferencePage.P_FILTER_THRESHOLD );
				boolean lFilter = Test2FeatureMapper.getDefault().getPreferenceStore().getBoolean( ConcernMapperPreferencePage.P_FILTER_ENABLED );
				if(  (lDegree >= lThreshold) || !lFilter )
				{
					lConcerns.add( lConcernName );
					lDoDecorateSuffix = true;
					lDoDecorateFont = true;
				}
			}
			else
			{
				if( getAllParents( lCurrentConcernElements, lConcernName ).contains( pElement ))
				{
					lDoDecorateFont = true;
				}
			}
		}
		//sort the concerns in alphabetical order
		Collections.sort( lConcerns );
		
		//add the decorations
		if( lDoDecorateSuffix && Test2FeatureMapper.getDefault().getPreferenceStore().getBoolean( ConcernMapperPreferencePage.P_SUFFIX_ENABLED ))
		{
			pDecoration.addSuffix( " "+lConcerns.toString() );
		}
		if( lDoDecorateFont && Test2FeatureMapper.getDefault().getPreferenceStore().getBoolean( ConcernMapperPreferencePage.P_BOLD_ENABLED ))
		{
			pDecoration.setFont( PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getFontRegistry().getBold( "Text Font" ));
		}
	}
	
	private Set<Object> getAllParents( Set<Object> pElements, String pConcern)
	{
		Set<Object> lReturn = new HashSet<Object>();
		Set<Object> lNotFilteredElements = new HashSet<Object>();
		lNotFilteredElements.addAll( pElements );
		
		//If the filter is enabled, remove the elements of pElements that have a degree lower than the threshold
		if(Test2FeatureMapper.getDefault().getPreferenceStore().getBoolean( ConcernMapperPreferencePage.P_FILTER_ENABLED ))
		{	
			Set<Object> lFilteredElements = new HashSet<Object>();
			for( Object lNext : lNotFilteredElements )
			{
				if ( Test2FeatureMapper.getDefault().getConcernModel().getDegree( pConcern, lNext ) <
						Test2FeatureMapper.getDefault().getPreferenceStore().getInt( ConcernMapperPreferencePage.P_FILTER_THRESHOLD ))
				{
					lFilteredElements.add( lNext );
				}
			}
			for( Object lNext : lFilteredElements )
			{
				lNotFilteredElements.remove( lNext );
			}
		}
		//Get the top parent to decorate from the preference store
		int lTopParent = IJavaElement.JAVA_PROJECT;
		if( !Test2FeatureMapper.getDefault().getPreferenceStore().getString( ConcernMapperPreferencePage.P_DECORATION_LIMIT ).equals( "" ))
		{
			lTopParent = Integer.parseInt( Test2FeatureMapper.getDefault().getPreferenceStore().
					getString( ConcernMapperPreferencePage.P_DECORATION_LIMIT ));
		}
		// Get the parents
		for( Object lNext : lNotFilteredElements )
		{
			if( lNext != null)
			{
				if( lNext instanceof IJavaElement )
				{
					IJavaElement lParent = ((IJavaElement)lNext).getParent();
					assert lParent != null;
					while( lParent.getElementType() != lTopParent )
					{
						lReturn.add( lParent );
						lParent = lParent.getParent();
						assert lParent != null;
					}
				}
			}
			
		}
		return lReturn;
	}
	
	/**
	 * Gets the ConcernMapper decorator.
	 * @return The decorator.
	 */
	public static LightWeightDecorator getDecorator()
	{
		IDecoratorManager lDecoratorManager =
			Test2FeatureMapper.getDefault().getWorkbench().getDecoratorManager();
		
		if ( lDecoratorManager.getEnabled( "ca.mcgill.cs.serg.cm.decorator" ))
		{
			return (LightWeightDecorator) lDecoratorManager.getBaseLabelProvider( "ca.mcgill.cs.serg.cm.decorator" );
		}
		return null;
	}
	
	private void fireLabelEvent(final LabelProviderChangedEvent pEvent)
	{
		Display.getDefault().asyncExec( new Runnable()
				{
			public void run()
			{
				fireLabelProviderChanged( pEvent );
			}
				});
	}
	
	
	/**
	 * Refreshes decorations when a change in the Concern Model is reported.
	 * @param pType
	 * @see ca.mcgill.cs.serg.cm.model.ConcernModelChangeListener#modelChanged(int)
	 * @param pType The type of change to the model. See the 
	 * constants in ConcernModel
	 */
	public void modelChanged( int pType ) 
	{
		if ( !(pType == ConcernModel.COMMENT))
		{
			LightWeightDecorator lDecorator = getDecorator();
			if(lDecorator != null)
			{
				fireLabelEvent( new LabelProviderChangedEvent( lDecorator ));
			}
		}
		
	}
	
	/**
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 * @param pEvent the property change event object describing which property
     * changed and how
	 */
	public void propertyChange( PropertyChangeEvent pEvent )
	{
		LightWeightDecorator lDecorator = getDecorator();
		if(lDecorator != null)
		{
			fireLabelEvent( new LabelProviderChangedEvent( lDecorator ));
		}
	}
	
}



