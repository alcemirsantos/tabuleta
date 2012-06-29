/* ConcernMapper - A concern modeling plug-in for Eclipse
 * Copyright (C) 2006  McGill University (http://www.cs.mcgill.ca/~martin/cm)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Revision: 1.10 $
 */

package br.ufmg.dcc.t2fm.actions;

import java.text.Collator;
import java.util.Arrays;
import java.util.Iterator;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.actions.SelectionConverter;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.IEditorStatusLine;

import br.ufmg.dcc.t2fm.Test2FeatureMapper;
import br.ufmg.dcc.t2fm.model.ConcernModelChangeListener;
import br.ufmg.dcc.t2fm.ui.ProblemManager;
import br.ufmg.dcc.t2fm.views.MapView;

/**
 * Generates the menu and corresponding actions required to add an element to
 * the concern model through a popup menu in JDT views or editors.
 */
public class AddToConcernAction implements IObjectActionDelegate, IEditorActionDelegate, IMenuCreator, ConcernModelChangeListener
{
	private static final String DEFAULT_CONCERN_NAME = "New Concern";
	private static final int MAX_DEGREE = 100;

	private ISelection aSelection;
	private Menu aMenu;
	private IEditorPart aJavaEditor;
	
	/**
	 * Creates a new action and adds the action as a listener to 
	 * the model.
	 */
	public AddToConcernAction()
	{
		Test2FeatureMapper.getDefault().getConcernModel().addListener( this );
	}

	/** 
	 * Never called because the action becomes a menu.
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 * @param pAction See above.
	 */
	public void run( IAction pAction )
	{ }

	/** 
	 * Does nothing.
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
	 * @param pAction See above.
	 * @param pTargetPart See above.
	 */
	public void setActivePart( IAction pAction, IWorkbenchPart pTargetPart )
	{ }

	/**
	 * Records the active editor that triggered a selection.
	 * @see org.eclipse.ui.IEditorActionDelegate#setActiveEditor(org.eclipse.jface.action.IAction, org.eclipse.ui.IEditorPart)
	 * @param pAction See above.
	 * @param pTargetEditor See Above.
	 */
	public void setActiveEditor( IAction pAction, IEditorPart pTargetEditor )
	{
		aJavaEditor = pTargetEditor;
	}

	/**
	 * Sets this class as a menu creator of pAction.
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 * @param pAction See above.
	 * @param pSelection See above.
	 */
	public void selectionChanged( IAction pAction, ISelection pSelection )
	{
		pAction.setMenuCreator( this );
		aSelection = pSelection;
	}

	/**
	 * @see org.eclipse.jface.action.IMenuCreator#dispose()
	 */
	public void dispose()
	{
		Test2FeatureMapper.getDefault().getConcernModel().removeListener( this );
		for( MenuItem lItem : aMenu.getItems() )
		{
			lItem.dispose();
		}
		aMenu.dispose();
	}

	/**
	 * @see org.eclipse.jface.action.IMenuCreator#getMenu(org.eclipse.swt.widgets.Control)
	 * @param pParent See above.
	 * @return See above.
	 */
	public Menu getMenu( Control pParent )
	{
		return null;
	}

	/**
	 * @see org.eclipse.jface.action.IMenuCreator#getMenu(org.eclipse.swt.widgets.Menu)
	 * @param pParent See above.
	 * @return See above.
	 */
	public Menu getMenu( Menu pParent )
	{
		aMenu = new Menu( pParent );
		fillMenu();
		return aMenu;
	}

	// Populate the dynamic menu
	private void fillMenu()
	{
		if(aMenu.isDisposed())
		{
			return;
		}
		
		// Remove previous items
		for( MenuItem lItem : aMenu.getItems() )
		{
			lItem.dispose();
		}
		
		// Add an item for each concern in the model
		String[] lConcerns = Test2FeatureMapper.getDefault().getConcernModel().getConcernNames();
		Arrays.sort( lConcerns, Collator.getInstance() );
		for( String lConcern : lConcerns )
		{
			MenuItem lMenuItem = new MenuItem( aMenu, SWT.PUSH );
			lMenuItem.setText( lConcern );
			lMenuItem.addSelectionListener( new AddToConcernMenuItemListener() );
		}
		
		// If there were concerns in the model, add a separator before the New
		// Concern item
		if( lConcerns.length != 0 )
		{
			new MenuItem( aMenu, SWT.SEPARATOR );
		}
		
		// Add the New Concern item
		MenuItem lNewConcernItem = new MenuItem( aMenu, SWT.PUSH );
		lNewConcernItem.addSelectionListener( new AddToConcernMenuItemListener() );
		if( !Test2FeatureMapper.getDefault().getConcernModel().exists( DEFAULT_CONCERN_NAME ) )
		{
			lNewConcernItem.setText( DEFAULT_CONCERN_NAME );
		}
		else
		{
			for( int lIndex = 2;; lIndex++ )
			{
				if( !Test2FeatureMapper.getDefault().getConcernModel().exists( DEFAULT_CONCERN_NAME + " " + lIndex ) )
				{
					lNewConcernItem.setText( DEFAULT_CONCERN_NAME + " " + lIndex );
					break;
				}
			}
		}
	}

	/**
	 * @see ca.mcgill.cs.serg.cm.model.ConcernModelChangeListener#modelChanged(int)
	 * @param pType See above.
	 */
	public void modelChanged( int pType )
	{
		if( aMenu != null )
		{
			// This needs to run in a non-UI thread since it can
			// be called from the UI.
			aMenu.getDisplay().asyncExec( new Runnable() 
			{
				public void run()
				{
					fillMenu();
				}
			});
		}
	}

	/**
	 * Carries out the action.
	 */
	private class AddToConcernMenuItemListener extends SelectionAdapter
	{
		public void widgetSelected( SelectionEvent pEvent )
		{
			String lConcern = ( (MenuItem) pEvent.widget ).getText();

			IEditorStatusLine lEditorStatusLine = null;
			IStatusLineManager lStatusLineManager = null;
			if( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor() != null )
			{
				lEditorStatusLine = (IEditorStatusLine) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						.getActivePage().getActiveEditor().getAdapter( IEditorStatusLine.class );
			}
			else
			{
				lStatusLineManager = ( (ViewPart) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.getActivePart() ).getViewSite().getActionBars().getStatusLineManager();
			}
			IStructuredSelection lSelection;
			if( aSelection instanceof IStructuredSelection )
			{
				lSelection = (IStructuredSelection) aSelection;
			}
			else
			{
				try
				{
					lSelection = SelectionConverter.getStructuredSelection( aJavaEditor );
				}
				catch( JavaModelException lException )
				{
					lSelection = null;
				}
			}
			if( lSelection != null )
			{
				if( lSelection.isEmpty() )
				{
					// The selection does not resolve to an IJavaElement
					if( lEditorStatusLine != null )
					{
						lEditorStatusLine.setMessage( true, Test2FeatureMapper.getResourceString( 
										"actions.AddToConcernAction.SelectionNotResolvable" ), null );
					}
				}
				for( Iterator<IJavaElement> lJavaElementIterator = lSelection.iterator(); lJavaElementIterator.hasNext(); )
				{
					IJavaElement lNext = lJavaElementIterator.next();
					if( supportedElement( lNext ) )
					{
						addToConcern( lNext, lConcern );
					}
						// if it is a class or interface, get its members and add them to the concern
					else if( supportedType( lNext ) ) 
					{
						final IField[] lFields = returnFields( (IType) lNext );
						final IMethod[] lMethods = returnMethods( (IType) lNext );
						for ( IField lField : lFields )
						{
							addToConcern( lField, lConcern );
						}
						for ( IMethod lMethod : lMethods)
						{
							addToConcern( lMethod, lConcern );
						}
					}
					else
					{
						// The element is not supported by ConcernMapper
						String lMessage = Test2FeatureMapper.getResourceString( "actions.AddToConcernAction.ElementNotSupported" );
						if( lEditorStatusLine != null )
						{
							lEditorStatusLine.setMessage( true, lMessage, null );
						}
						else
						{
							lStatusLineManager.setErrorMessage( lMessage );
						}
					}
				}
			}
		}

		/**
		 * Determines if pElement can be included in a concern model.
		 * 
		 * @param pElement
		 *            The element to test
		 * @return true if pElement is of a type that is supported by the
		 *         concern model.
		 */
		
		private boolean supportedElement( IJavaElement pElement )
		{
			boolean lReturn = false;
			if( ( pElement instanceof IField ) || ( pElement instanceof IMethod ))
			{
				try
				{
					if( ( (IMember) pElement ).getDeclaringType().isAnonymous() || ( (IMember) pElement ).getDeclaringType().isLocal())
					{
						lReturn = false;
					}
					else
					{
						lReturn = true;
					}
				}
				catch( JavaModelException lException )
				{
					ProblemManager.reportException( lException );
					lReturn = false;
				}
			}
			return lReturn;
		}
		
		/**
		 * Determines if pElement is a class or an interface which elements are to be put into the concern model.
		 *   
		 * @param pElement
		 * 				The element to test
		 * @return true if pElement is a class or an interface
		 * 
		 */
		
		private boolean supportedType( IJavaElement pElement )
		{
			boolean lReturn = false;
			if ( pElement instanceof IType )
			{
				lReturn = true;
				try 
				{
					if ( ((IType) pElement).isAnonymous() || ((IType) pElement).isLocal() )
					{
						lReturn = false;
					}
					else
					{
						lReturn = true;
					}
				} 
				catch ( JavaModelException lException ) 
				{
					ProblemManager.reportException( lException );
					lReturn = false;
				}
			}
			return lReturn;
		}
		
      	/**
      	 * Parses and returns the fields of IType element.
      	 * 
      	 * @param pElement
      	 * 				The element whose fields are to be obtained
      	 */
      	private IField[] returnFields( IType pElement )
      	{
      		IField[] lReturn = null;
			try 
			{
				lReturn = pElement.getFields();
			} 
			catch( JavaModelException lException )
			{
				ProblemManager.reportException( lException );
			}
      		
      		return lReturn;
      		
      	}
      	
      	/**
      	 * Parses and returns the methods of IType element.
      	 * 
      	 * @param pElement
      	 * 				The element whose methods are to be obtained
      	 */
      	private IMethod[] returnMethods( IType pElement )
      	{
      		IMethod[] lReturn = null;
			try 
			{
				lReturn = pElement.getMethods();
			} 
			catch ( JavaModelException lException ) 
			{
				ProblemManager.reportException( lException );
			}
      		
      		return lReturn;
      		
      	}
      	
      	/**
      	 * Adds the element to an identified concern.
      	 * 
      	 * @param pElement
      	 * 				The element we want to add into the concern model
      	 * 		  pConcern
      	 * 				The string-identified concern we want to augment
      	 */
      	private void addToConcern( IJavaElement pElement, String pConcern)
      	{
      		if( !Test2FeatureMapper.getDefault().getConcernModel().exists( pConcern ) )
			{
				Test2FeatureMapper.getDefault().getConcernModel().newConcern( pConcern );
			}
			if( !Test2FeatureMapper.getDefault().getConcernModel().exists( pConcern, pElement ) )
			{
				Test2FeatureMapper.getDefault().getConcernModel().addElement( pConcern, pElement, MAX_DEGREE );
				MapView lView = (MapView)PlatformUI.getWorkbench().
					getActiveWorkbenchWindow().getActivePage().findView( Test2FeatureMapper.ID_VIEW );
				lView.setConcernToReveal( pConcern );
				lView.addElementToReveal( pElement );
			}
      	}
      	
	}

}
