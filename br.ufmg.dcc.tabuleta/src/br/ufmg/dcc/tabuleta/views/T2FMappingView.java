/*************************************************************************
 * Copyright (c) 2012 Federal University of Minas Gerais - UFMG 
 * All rights avaiable. This program and the accompanying materials
 * are made avaiable under the terms of the Eclipse Public Lincense v1.0
 * which accompanies this distribution, and is avaiable at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Alcemir R. Santos - improvements on the ConcernMapper
 * 			architeture. ConcernMapper is available at
 * 			http://www.cs.mcgill.ca/~martin/cm/
 *************************************************************************/
package br.ufmg.dcc.tabuleta.views;


import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.actions.JavaSearchActionGroup;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.navigator.LocalSelectionTransfer;

import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.core.ISessionListener;
import com.mountainminds.eclemma.internal.core.EclEmmaCorePlugin;
import com.mountainminds.eclemma.internal.core.SessionManager;
import com.mountainminds.eclemma.internal.ui.actions.CoverageAsAction;

import br.ufmg.dcc.tabuleta.Tabuleta;
import br.ufmg.dcc.tabuleta.actions.ClearAction;
import br.ufmg.dcc.tabuleta.actions.CollapseAllAction;
import br.ufmg.dcc.tabuleta.actions.DeleteAction;
import br.ufmg.dcc.tabuleta.actions.FilterAction;
import br.ufmg.dcc.tabuleta.actions.GenerateTestSuiteAction;
import br.ufmg.dcc.tabuleta.actions.LoadConcernModelAction;
import br.ufmg.dcc.tabuleta.actions.NewConcernAction;
import br.ufmg.dcc.tabuleta.actions.RenameConcernAction;
import br.ufmg.dcc.tabuleta.actions.SaveAction;
import br.ufmg.dcc.tabuleta.actions.SaveAsAction;
import br.ufmg.dcc.tabuleta.actions.SaveCoverageAsCMAction;
import br.ufmg.dcc.tabuleta.actions.ShowInconsistentElementsAction;
import br.ufmg.dcc.tabuleta.model.ConcernModel;
import br.ufmg.dcc.tabuleta.model.ConcernModelChangeListener;
import br.ufmg.dcc.tabuleta.ui.ConcernMapperPreferencePage;
import br.ufmg.dcc.tabuleta.ui.ProblemManager;
import br.ufmg.dcc.tabuleta.views.components.ConcernMapperFilter;
import br.ufmg.dcc.tabuleta.views.components.ConcernModelContentProvider;
import br.ufmg.dcc.tabuleta.views.components.ConcernModelLabelProvider;
import br.ufmg.dcc.tabuleta.views.components.ConcernNode;
import br.ufmg.dcc.tabuleta.views.components.IConcernMapperViewNode;
import br.ufmg.dcc.tabuleta.views.components.JavaElementNode;
import br.ufmg.dcc.tabuleta.views.components.WrapperNode;

/**
 * Implements a view of the Concern model associated with 
 * the Test2FeatureMapper plug-in. It was build over the Test2FeatureMapper.
 */

public class T2FMappingView extends ViewPart implements ConcernModelChangeListener, IPropertyChangeListener {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "br.ufmg.dcc.tabuleta.views.MapView";
	
	
		private static final int SLIDER_INCREMENT = 10;
		// The slider only goes to 100 in practice.  I don't know why.
		private static final int SLIDER_MAXIMUM = 110;
		private static final int SLIDER_LABEL_CHARACTERS = 3;
		private static final int MAX_DEGREE = 100;
		private static final int COMMENT_BOX_HEIGHT = 45;
			
		private Text aCommentBox;
		private TreeViewer aViewer;
		private Slider aDegreeSlider;
		private Text aDegreeValue;
		private Composite aSliderPanel;
		private SaveAction aSaveAction;
		private SaveCoverageAsCMAction saveCoverageAsCMAction;
		private Action aDoubleClickAction;
		private FilterAction aFilterAction;
		private ISelectionChangedListener aSelectionListener;
		private ModifyListener aModifyListener;
		private JavaSearchActionGroup aJavaSearchActions;
		private ConcernMapperFilter aFilter = new ConcernMapperFilter();
		private String aConcernToReveal = null;
		private HashSet<Object> aElementsToReveal = new HashSet<Object>();
		private Composite aParent;

		
		/**
		 * This is a callback that will allow us
		 * to create the aViewer and initialize it.
		 * @param pParent The parent widget.
		 */
		public void createPartControl( Composite pParent )
		{
			aParent = pParent;
			GridLayout lLayout = new GridLayout();
			lLayout.numColumns = 1;
			lLayout.horizontalSpacing = 0;  // remove if not needed
			lLayout.verticalSpacing = 0;	// remove if not needed
			lLayout.marginHeight = 0;		// remove if not needed
			lLayout.marginWidth = 0;		// remove if not needed
			pParent.setLayout( lLayout );
			pParent.setLayoutData( new GridData( GridData.FILL_BOTH ));
			
			aViewer = new TreeViewer( pParent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL );
			aViewer.setContentProvider( new ConcernModelContentProvider() );
			aViewer.setLabelProvider( new ConcernModelLabelProvider() );
				
			if( !(Tabuleta.getDefault().getPreferenceStore().getBoolean( ConcernMapperPreferencePage.P_FILTER_ENABLED)))
			{
				aViewer.addFilter( new ConcernMapperFilter() );
			}
			
			GridData lGridData = new GridData();
			lGridData.verticalAlignment = GridData.FILL;
	 		lGridData.horizontalAlignment = GridData.FILL;
	 		lGridData.grabExcessHorizontalSpace = true;
	 		lGridData.grabExcessVerticalSpace = true;
	 			
	 		aViewer.getControl().setLayoutData( lGridData );
	 		
	 		initSliderPanel( pParent, Tabuleta.getDefault().getPreferenceStore().getBoolean( ConcernMapperPreferencePage.P_SHOW_SLIDER ));
	 		initCommentBox( pParent, Tabuleta.getDefault().getPreferenceStore().getBoolean( ConcernMapperPreferencePage.P_SHOW_COMMENTS ) );

	 		aViewer.setSorter( new ElementSorter() );
			
			initDragAndDrop();
			hookSelectionListener();
			hookContextMenu();
			getSite().setSelectionProvider( aViewer );
			makeActions();
			contributeToActionBars();
			hookDoubleClickAction();
			
			// Add this view as a listener for model and property change events
			Tabuleta.getDefault().getConcernModel().addListener( this );
			Tabuleta.getDefault().getPreferenceStore().addPropertyChangeListener( this );
			
			modelChanged( ConcernModel.DEFAULT ); // artificial call to refresh the view.
		}
		
		private void initSliderPanel( Composite pParent, boolean pVisible )
		{
			aSliderPanel  = new Composite( pParent, 0 );
			aSliderPanel.setLayout( new GridLayout(2, false) );
			aSliderPanel.setVisible( pVisible );
			GridData lGridData = new GridData( GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL );
			lGridData.exclude = !pVisible;
			aSliderPanel.setLayoutData( lGridData );
			aDegreeSlider = new Slider( aSliderPanel, SWT.HORIZONTAL );
			aDegreeSlider.setSize( 0, 0 );
			
			lGridData = new GridData();
			lGridData.horizontalAlignment = GridData.FILL;
	 		lGridData.grabExcessHorizontalSpace = true;
	 		lGridData.grabExcessVerticalSpace = false;
	 		
			aDegreeSlider.setLayoutData( lGridData );
			aDegreeSlider.setIncrement( 1 );
			aDegreeSlider.setPageIncrement( SLIDER_INCREMENT  );
			aDegreeSlider.setMinimum( 0 );
			// This only goes to 100 in practice.  I don't know why it subtracts 10.
			aDegreeSlider.setMaximum( SLIDER_MAXIMUM );
			aDegreeSlider.setEnabled( false );
			
			aDegreeValue = new Text( aSliderPanel, 0 );
			aDegreeValue.setSize( 0, 0);
					
			lGridData = new GridData();
			lGridData.verticalAlignment = GridData.END;
	 		lGridData.grabExcessHorizontalSpace = false;
	 		lGridData.grabExcessVerticalSpace = false;
	 		
			aDegreeValue.setLayoutData( lGridData );
			aDegreeValue.setTextLimit( SLIDER_LABEL_CHARACTERS );
			aDegreeValue.setEditable( false );
			aDegreeValue.setEnabled( false );
			aDegreeValue.setText( "" );
			aDegreeValue.setOrientation( SWT.RIGHT );
			
			aSliderPanel.pack();
		}
		
		private void initCommentBox( Composite pParent, boolean pVisible )
		{
			aCommentBox  = new Text( pParent, SWT.LEFT | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL );
			aCommentBox.setVisible( pVisible );
			aCommentBox.setEnabled( false );
							
			GridData lGridData = new GridData();
			lGridData.horizontalAlignment = GridData.FILL;
			lGridData.heightHint = COMMENT_BOX_HEIGHT;
	 		lGridData.grabExcessHorizontalSpace = true;
	 		lGridData.grabExcessVerticalSpace = false;
	 		lGridData.exclude = !pVisible;
	 		aCommentBox.setLayoutData( lGridData );
	 		aCommentBox.setText( "" );
	 				
			aCommentBox.pack();
		}
		
		private void updateVisibilityStatus()
		{
			boolean lRelayout = false;
			boolean lVisible = Tabuleta.getDefault().getPreferenceStore().getBoolean( ConcernMapperPreferencePage.P_SHOW_SLIDER );
			if( lVisible != aSliderPanel.isVisible() )
			{
				GridData lData = (GridData)aSliderPanel.getLayoutData();
				lData.exclude = !lVisible;
				aSliderPanel.setLayoutData( lData );
				aSliderPanel.setVisible( lVisible );
				lRelayout = true;
			}
			
			lVisible = Tabuleta.getDefault().getPreferenceStore().getBoolean( ConcernMapperPreferencePage.P_SHOW_COMMENTS );
			if( lVisible != aCommentBox.isVisible() )
			{
				GridData lData = (GridData)aCommentBox.getLayoutData();
				lData.exclude = !lVisible;
				aCommentBox.setLayoutData( lData );
				aCommentBox.setVisible( lVisible );
				lRelayout = true;
			}
			
			if( lRelayout )
			{
				aParent.layout();
			}
		}
		
		/**
		 * Schedule the concern containing the elements to reveal in the 
		 * next refresh.
		 * @param pConcern The concern to reveal.
		 */
		public void setConcernToReveal( String pConcern )
		{
			aConcernToReveal = pConcern;
		}
		
		/**
		 * Adds an element to be revealed in the next refresh.
		 * @param pElement The element to reveal.
		 */
		public void addElementToReveal( Object pElement )
		{
			aElementsToReveal.add( pElement );
		}
		
		/**
		 * Sets the selection to a specified concern.
		 * @param pConcern The name of the concern to select
		 */
		public void setConcernSelection( String pConcern )
		{
			aViewer.setSelection( new StructuredSelection( pConcern ));
		}
		
		/**
		 * Collapses the element tree.
		 */
		public void collapseAll()
		{
			aViewer.collapseAll();
		}
		
		/**
		 * @see ca.mcgill.cs.serg.cm.model.ConcernModelChangeListener#modelChanged(int)
		 * @param pType The type of change to the model. See the 
		 * constants in ConcernModel
		 */
		public void modelChanged( int pType ) 
		{
			if (!aViewer.getControl().isDisposed())
			{
				Display lDisplay = aViewer.getControl().getDisplay();
				
				if ( !lDisplay.isDisposed())
				{
					if( pType != ConcernModel.COMMENT)
					{
						// Setting the input must be done asynchronously.
						// see: http://docs.jboss.org/jbosside/cookbook/build/en/html/Example6.html#d0e996
						lDisplay.asyncExec( new Runnable()
								{
							
							public void run()
							{
								//make sure the tree still exists
								if (aViewer != null && aViewer.getControl().isDisposed())
								{
									return;
								}
								else
								{
									Object[] lExpanded = aViewer.getExpandedElements();
									aViewer.setInput( Tabuleta.getDefault().getConcernModel() );
									aViewer.setExpandedElements( lExpanded );
									for( Object lObject : aElementsToReveal )
									{
										IConcernMapperViewNode lNode = ((ConcernModelContentProvider)aViewer.getContentProvider()).
											getNodeObject( lObject, aConcernToReveal );
										if( lNode != null )
										{
											Object lTempNode = lNode.getParent();
											while( !(lTempNode instanceof ConcernModel) )
											{
												aViewer.setExpandedState( lTempNode, true );
												lTempNode = ((IConcernMapperViewNode)lTempNode).getParent();
											}
										}
									}
									aElementsToReveal.clear();
									aViewer.refresh();
									updateFilters();
								}
							}
							
							
								});
					}
				}
			}
			updateActionState();
		}
		
		/**
		 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
		 * @param pEvent the property change event object describing which property
	     * changed and how
		 */
		public void propertyChange(PropertyChangeEvent pEvent)
		{
			if( pEvent.getProperty().equals( ConcernMapperPreferencePage.P_SHOW_INCONSISTENT_ELEMENTS ))
			{
				modelChanged( ConcernModel.DEFAULT );
			}
			else if( pEvent.getProperty().equals( ConcernMapperPreferencePage.P_SHOW_SLIDER ) || 
					 pEvent.getProperty().equals( ConcernMapperPreferencePage.P_SHOW_COMMENTS ))
			{
				updateVisibilityStatus();
			}
			else if( pEvent.getProperty().equals( ConcernMapperPreferencePage.P_FILTER_ENABLED) ||
					pEvent.getProperty().equals( ConcernMapperPreferencePage.P_FILTER_THRESHOLD))
			{
				updateFilters();
			}
		}
		
		private void updateFilters()
		{
			aViewer.resetFilters();
			if(Tabuleta.getDefault().getPreferenceStore().getBoolean(
					ConcernMapperPreferencePage.P_FILTER_ENABLED))	
			{
				aViewer.addFilter( aFilter );
			}
		}

		/**
		 * Updates the action buttons to reflect
		 * the state of the plugin.
		 */
		public void updateActionState()
		{
			aSaveAction.setEnabled( Tabuleta.getDefault().isDirty() );
		    getViewSite().getActionBars().updateActionBars();
		}
		
		/**
		 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
		 */
		public void setFocus() 
		{}
		
		/**
		 * Adds the elements in the action bar.
		 */
		private void contributeToActionBars() 
		{
			IActionBars lBars = getViewSite().getActionBars();
			fillLocalToolBar( lBars.getToolBarManager() );
			fillToolBarMenu( lBars.getMenuManager() );
		}
		
		/**
		 * Initializes the tree viewer to support drop actions.
		 */
		private void initDragAndDrop() 
		{
			int lOps = DND.DROP_COPY | DND.DROP_MOVE;
			Transfer[] lTransfers = new Transfer[] {
				LocalSelectionTransfer.getInstance() };
			aViewer.addDropSupport( lOps, lTransfers, new ConcernMapperViewDropAdapter());
			
			aViewer.addDragSupport( lOps, lTransfers, new  DragSourceAdapter() );
		}
		
		private void makeActions() 
		{
		    aSaveAction = new SaveAction( this );
		    aSaveAction.setEnabled( Tabuleta.getDefault().isDirty() );
		    
		    saveCoverageAsCMAction = new SaveCoverageAsCMAction(this);
		    saveCoverageAsCMAction.setEnabled(false);
		    
		    aDoubleClickAction = new Action() 
			{
				public void run() 
				{
					ISelection lSelection = aViewer.getSelection();
					Object lObject = ((IStructuredSelection)lSelection).getFirstElement();
					if( lObject instanceof JavaElementNode )
					{
					    IJavaElement lElement = ((JavaElementNode)lObject).getElement();
					    if( lElement.exists() )
					    {
					    	IWorkbenchPage lPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					    	IResource lResource = lElement.getResource();
					    	if( lResource instanceof IFile )
					    	{
					    		try
					    		{
					    			JavaUI.revealInEditor( IDE.openEditor( lPage, (IFile)lResource), lElement );
					    		}
					    		catch( PartInitException lException )
					    		{
					    			ProblemManager.reportException( lException );
					    		}
					    	}
					    }
					}
				}
			};
			
			aFilterAction = new FilterAction();
			aJavaSearchActions = new JavaSearchActionGroup( this );

		}
		
		private void hookDoubleClickAction() 
		{
			aViewer.addDoubleClickListener( new IDoubleClickListener() 
					{
				public void doubleClick( DoubleClickEvent pEvent) 
				{
					aDoubleClickAction.run();
				}
					});
		}
		
		/**
		 * Registers the context menu on the view.
		 */
		private void hookContextMenu() 
		{
			MenuManager lMenuManager = new MenuManager("#PopupMenu");
			lMenuManager.setRemoveAllWhenShown( true );
			lMenuManager.addMenuListener( new IMenuListener() 
					{
				public void menuAboutToShow( IMenuManager pManager ) 
				{
					T2FMappingView.this.fillContextMenu( pManager );
				}
					});
			Menu lMenu = lMenuManager.createContextMenu( aViewer.getControl() );
			aViewer.getControl().setMenu( lMenu );
			getSite().registerContextMenu( lMenuManager, aViewer );
		}
		
		private void hookSelectionListener()
		{
			aSelectionListener =  new ConcernMapperSelectionChangedListener();
			
			aViewer.addSelectionChangedListener( aSelectionListener );
			
			CoverageSessionChangedListener aCoverageSessionChangedListener = new CoverageSessionChangedListener();
			EclEmmaCorePlugin.getInstance().getSessionManager().addSessionListener(aCoverageSessionChangedListener);
			
			aDegreeSlider.addSelectionListener( new SelectionListener() {
				
				public void widgetSelected( SelectionEvent pEvent )
				{
					aDegreeValue.setText( new Integer(aDegreeSlider.getSelection()).toString() );
				}
				
				public void widgetDefaultSelected( SelectionEvent pEvent ) {}	
			});
			
			aDegreeSlider.addMouseListener( new MouseListener() {
				
				public void mouseDoubleClick(MouseEvent pEvent) {}
				
				public void mouseDown(MouseEvent pEvent) {}
				
				public void mouseUp(MouseEvent pEvent) 
				{
					aViewer.removeSelectionChangedListener( aSelectionListener );
					ISelection lSelection = aViewer.getSelection();
					if( lSelection instanceof IStructuredSelection )
					{
						Object[] lStructuredSelectionArray = ((IStructuredSelection)lSelection).toArray();
						for( int lI = 0; lI<lStructuredSelectionArray.length; lI++ )
						{
							Object lElement = lStructuredSelectionArray[lI];
							if( lElement instanceof WrapperNode )
							{
								WrapperNode lNode = (WrapperNode)lElement;
								if( Tabuleta.getDefault().getConcernModel().exists( lNode.getConcern(), lNode.getElement() ))
								{
									Tabuleta.getDefault().getConcernModel().
									setDegree( lNode.getConcern(), lNode.getElement(), aDegreeSlider.getSelection());
								}
							}
						}	
					}
					aViewer.addSelectionChangedListener( aSelectionListener );
				}
			});
			
			aModifyListener = new ModifyListener(){

				public void modifyText( ModifyEvent pEvent )
				{
					aViewer.removeSelectionChangedListener( aSelectionListener );
					ISelection lSelection = aViewer.getSelection();
					if( lSelection instanceof IStructuredSelection )
					{
						IStructuredSelection lStructuredSelection = (IStructuredSelection)lSelection;
						
						if( lStructuredSelection.size() == 1 )
						{
							Object lElement = lStructuredSelection.getFirstElement();
							if( lElement instanceof ConcernNode )
							{
								ConcernNode lNode = (ConcernNode)lElement;
								if( Tabuleta.getDefault().getConcernModel().exists( lNode.getConcern() ))
								{
									if( !Tabuleta.getDefault().getConcernModel().getConcernComment(
											lNode.getConcern()).equals( aCommentBox.getText()))
									{
										Tabuleta.getDefault().getConcernModel().setConcernComment( lNode.getConcern(), aCommentBox.getText());
									}
								}
							}
							else if( lElement instanceof WrapperNode )
							{
								WrapperNode lNode = (WrapperNode)lElement;
								if( Tabuleta.getDefault().getConcernModel().exists( lNode.getConcern(), lNode.getElement() ))
								{
									if( !Tabuleta.getDefault().getConcernModel().getElementComment(
											lNode.getConcern(), lNode.getElement()).equals( aCommentBox.getText()))
									{
										Tabuleta.getDefault().getConcernModel().setElementComment(
												lNode.getConcern(), lNode.getElement(), aCommentBox.getText());
									}
								}
							}
						}	
					}
					aViewer.addSelectionChangedListener( aSelectionListener );
					
				}};
			
			aCommentBox.addModifyListener( aModifyListener );
		}
		
		
		
		/*
		 * Takes a selection and extracts all the elements that should be deleted. Extracts the elements 
		 * from the element nodes and gather all the descendants of classes, etc.
		 */
		@SuppressWarnings("unchecked")
		private DeleteAction getDeleteActionFromSelection( IStructuredSelection pSelection )
		{
			DeleteAction lReturn = new DeleteAction();
			if( pSelection.size() >= 1 )
			{
				for( Iterator lI = pSelection.iterator(); lI.hasNext(); )
				{
					Object lNext = lI.next();
					if( lNext instanceof ConcernNode )
					{
						lReturn.scheduleConcernDelete( ((ConcernNode)lNext).getConcernName() );
					}
					else if( lNext instanceof WrapperNode )
					{
						Set<IConcernMapperViewNode> lDescendants = ((IConcernMapperViewNode)lNext).getAllDescendants();
						for( IConcernMapperViewNode lNode : lDescendants )
						{
							lReturn.scheduleElementDelete( lNode.getConcern(), ((WrapperNode)lNode).getElement() );
						}
					}
				}
			}
			return lReturn;
		}
		
		/**
		 * Adds the action to the toolbar.
		 * @param pManager The toolbar manager.
		 */
		private void fillLocalToolBar( IToolBarManager pManager ) 
		{
			pManager.add( new ClearAction());
			pManager.add( saveCoverageAsCMAction);
			pManager.add( new GenerateTestSuiteAction(this) );
			pManager.add( aSaveAction );
			pManager.add( new SaveAsAction( this ));
			pManager.add( new NewConcernAction( this ));
			pManager.add( new Separator() );
			pManager.add( new CollapseAllAction( this ));
			
		}
		
		/**
		 * Fills the context menu based on the type of selection.
		 * @param pManager
		 */
		private void fillContextMenu( IMenuManager pManager )
		{
			ISelection lSelection = aViewer.getSelection();
			if( lSelection instanceof IStructuredSelection )
			{
				IStructuredSelection lStructuredSelection = (IStructuredSelection)lSelection;
				if( lStructuredSelection.size() == 1 )
				{
					Object lNext = lStructuredSelection.iterator().next();
					if( lNext instanceof ConcernNode )
					{
						pManager.add( new RenameConcernAction( this, ((ConcernNode)lNext).getConcernName() ));
					}
					else
					{
						// We provide the context menu for searching
						if( lNext instanceof JavaElementNode )
						{
							IJavaElement lElement = ((JavaElementNode)lNext).getElement();
							if( lElement.exists() )
							{
								aJavaSearchActions.setContext( new ActionContext( new StructuredSelection( lElement )));
								GroupMarker lSearchGroup = new GroupMarker("group.search");
								pManager.add( lSearchGroup );
								aJavaSearchActions.fillContextMenu( pManager );
								aJavaSearchActions.setContext( null );
								pManager.remove( lSearchGroup );
							}
						}
					}
				}
				pManager.add( getDeleteActionFromSelection( lStructuredSelection ));
			}
			pManager.add( new Separator( IWorkbenchActionConstants.MB_ADDITIONS ));
		}
		
		/**
		 * Adds the actions to the menu.
		 * @param pManager the menu manager.
		 */
		private void fillToolBarMenu( IMenuManager pManager )
		{
			pManager.add( new ShowInconsistentElementsAction( "", IAction.AS_CHECK_BOX ));
			pManager.add( aFilterAction );
		}
		
		/**
		 * Exports the tree view selection.
		 * @return The current selection of the tree viewer.
		 */
		public ISelection getCurrentSelection()
		{
			return aViewer.getSelection();
		}
		
		/**
		 * Called when the view is closed.  Deregister the view
		 * as a listener to the model.
		 */
		public void dispose() 
		{
			Tabuleta.getDefault().getConcernModel().removeListener( this );
			Tabuleta.getDefault().getPreferenceStore().removePropertyChangeListener( this );
			aJavaSearchActions.dispose();
			super.dispose();
		}
		
		/**
		 * The sorter class for elements in the tree viewer
	     * This class only needs to spit the elements into categories.
	     * The default behavior of the class takes care of the sorting
	     */
	    class ElementSorter extends ViewerSorter
	    {        
	        private static final int OTHER = 0;
	        private static final int FIELD = 1;
	        private static final int METHOD = 2;
	        private static final int TYPE = 4;
	        private static final int CONCERN = 5;
	        
	        /**
	         * @see org.eclipse.jface.viewers.ViewerSorter#category(java.lang.Object)
	         * @param pElement the element
	         * @return the category
	         */
	        public int category( Object pElement )
	        {
	            int lReturn = OTHER;
	            if( pElement instanceof ConcernNode )
	            {
	                return CONCERN;
	            }
	            else if( pElement instanceof WrapperNode )
	            {
	                Object lElement = ((WrapperNode)pElement).getElement();
	                if( lElement instanceof IType )
	                {
	                    lReturn = TYPE;
	                }
	                else if( lElement instanceof IField )
	                {
	                    lReturn = FIELD;
	                }
	                else if( lElement instanceof IMethod )
	                {
	                    lReturn = METHOD;
	                }
	            }
	            
	            return lReturn;
	        }
	    }
	    
	    /**
	     * Allows the dropping of elements into this view.
	     */
	    class ConcernMapperViewDropAdapter extends ViewerDropAdapter
	    {
	    	private boolean aCopy = false;
	               
	        /**
	         * Creates a new adapter.
	         */
	        public ConcernMapperViewDropAdapter()
	        {
	        	super( aViewer );
	            setFeedbackEnabled( false );
	        }
	        
	        /**
	         * We consider to be dragging from within the model if
	         * any of the local selection is an element node.
	         * @return
	         */
	        @SuppressWarnings("unchecked")
			private boolean dragFromModel()
	        {
	        	boolean lReturn = false;
	        	Object lSelection = LocalSelectionTransfer.getInstance().getSelection();
	    		if( lSelection instanceof IStructuredSelection )
	            {
	                for( Iterator lI = ((IStructuredSelection)lSelection).iterator(); lI.hasNext(); )
	                {
	                    Object lNext = lI.next();
	                    if (lNext instanceof IConcernMapperViewNode )
	                    {
	                    	lReturn = true;
	                    	break;
	                    }
	                }
	            }
	    		return lReturn;
	        }
	        
	        /**
	         * When a user has dragged a elements into the tree viewer, take
	         * the steps necessary to include these elements into the model. 
	         *
	         * @param pData the drop data
	         * @return <code>true</code> if the drop was successful, and 
	         *   <code>false</code> otherwise
	         */
	        @SuppressWarnings("unchecked")
			public boolean performDrop( Object pData ) 
	        {
	        	
	        	boolean lReturn = false;
	            
	            if( getCurrentTarget() != null )
	            {
	            	ConcernNode lTargetNode = (ConcernNode)getCurrentTarget();
	            	String lTarget = lTargetNode.getConcernName();
	            	aConcernToReveal = lTarget;
	            	assert Tabuleta.getDefault().getConcernModel().exists( lTarget );
	                Object lSelection = LocalSelectionTransfer.getInstance().getSelection();
	                if( lSelection instanceof IStructuredSelection )
	                {
	                	Tabuleta.getDefault().getConcernModel().startStreaming();
	                	try
	                	{
	                		for( Iterator lI = ((IStructuredSelection)lSelection).iterator(); lI.hasNext(); )
	                		{
	                			Object lNext = lI.next();
	                			IJavaElement lElement = null;
	                			if( lNext instanceof IJavaElement )
	                			{
	                				lElement = (IJavaElement)lNext;
	                			}
	                			else if( lNext instanceof WrapperNode )
	                			{
	                				lElement = (IJavaElement)((WrapperNode)lNext).getElement();
	                			}
	                			else if( lNext instanceof IAdaptable )
	                			{
	                				lElement = (IJavaElement)((IAdaptable)lNext).getAdapter( IJavaElement.class );
	                			}
	                			if( lElement == null )
	                			{
	                				continue;
	                			}
	                			// if it is a class or interface, get its members and add them to the concern
	                			if( supportedType( lElement ) )
	                			{

	        						final IField[] lFields = returnFields( (IType) lElement );
	        						final IMethod[] lMethods = returnMethods( (IType) lElement );
	        						for ( IField lField : lFields )
	        						{
	        							if( !Tabuleta.getDefault().getConcernModel().exists( lTarget, lField ))
	                        			{
	                        				if( lNext instanceof WrapperNode ) // we're dealing with within-model dnd
	                        				{
	                        					int lDegree = Tabuleta.getDefault().getConcernModel().
	                        						getDegree( ((WrapperNode)lNext).getConcern(), lField );
	                        					deleteExistingElement( (WrapperNode)lNext, lField);
	                        					Tabuleta.getDefault().getConcernModel().addElement( lTarget, lField, lDegree );
	                        					lReturn = true;
	                        				}
	                        				else 
	                        				{
	                        					Tabuleta.getDefault().getConcernModel().addElement( lTarget, lField, MAX_DEGREE );
	                        					lReturn = true;
	                        				} 
	                        			}
	        						}
	        						for( IMethod lMethod : lMethods )
	        						{
	        							if( !Tabuleta.getDefault().getConcernModel().exists( lTarget, lMethod ))
	                        			{
	                        				if( lNext instanceof WrapperNode ) // we're dealing with within-model dnd
	                        				{
	                        					int lDegree = Tabuleta.getDefault().getConcernModel().
	                        						getDegree( ((WrapperNode)lNext).getConcern(), lMethod );
	                        					deleteExistingElement( (WrapperNode)lNext, lMethod);
	                        					Tabuleta.getDefault().getConcernModel().addElement( lTarget, lMethod, lDegree );
	                        					lReturn = true;
	                        				}
	                        				else 
	                        				{
	                        					Tabuleta.getDefault().getConcernModel().addElement( lTarget, lMethod, MAX_DEGREE );
	                        					lReturn = true;
	                        				} 
	                        			}
	        						}
	                			}
	                			else
	                			{
	                				if( !Tabuleta.getDefault().getConcernModel().exists( lTarget, lElement ))
	                				{
	                					if( lNext instanceof WrapperNode ) // we're dealing with within-model dnd
	                					{
	                						int lDegree = Tabuleta.getDefault().getConcernModel().
	                						getDegree( ((WrapperNode)lNext).getConcern(), lElement );
	                						deleteExistingElement( (WrapperNode)lNext, lElement);
	                						Tabuleta.getDefault().getConcernModel().addElement( lTarget, lElement, lDegree );
	                						lReturn = true;
	                					}
	                					else 
	                					{
	                						Tabuleta.getDefault().getConcernModel().addElement( lTarget, lElement, MAX_DEGREE );
	                						lReturn = true;
	                					} 
	                				}
	                				aElementsToReveal.add( lElement );
	                			}
	                		}
	                	}
	                	finally
	                	{
	                		Tabuleta.getDefault().getConcernModel().stopStreaming();
	                	}
	                }
	            }
	            else
	            {
	            	lReturn = loadConcernFile();
	            }
	            return lReturn;
	        }
	        
	        /*
	         * Load the file which is the local selection as the concern model.
	         */
	        private boolean loadConcernFile()
	        {

	        	boolean lReturn = false;
	        	LoadConcernModelAction lLoadConcernModel = new LoadConcernModelAction();
	        	Object lSelection = LocalSelectionTransfer.getInstance().getSelection();
	        	if( lSelection instanceof IStructuredSelection )
	        	{
	        		Object lFirstSelection = ((IStructuredSelection)lSelection).getFirstElement();
	        		if (lFirstSelection instanceof IFile)
	        		{
	        			IFile lFile = (IFile)lFirstSelection;
	        			lLoadConcernModel.setFile( lFile );
	        			lLoadConcernModel.run( (IAction)null );
	        			lReturn = true;
	        		}
	        	}
	        	return lReturn;
	        }

	        /**
	         * @return True is the object currently being dragged is a single .cm file.
	         */
	        private boolean isCMFileDragged()
	        {
	        	boolean lReturn = false;
	        	Object lSelection = LocalSelectionTransfer.getInstance().getSelection();
	        	assert lSelection != null;
	    		if( (lSelection instanceof IStructuredSelection ) && (((IStructuredSelection)lSelection).size() == 1 ))
	            {
	    			Object lObject = ((IStructuredSelection)lSelection).iterator().next();
	    			if( (lObject instanceof IFile ) && ((IFile)lObject).getName().endsWith( ".cm"))
	    			{
	    				lReturn = true;
	                }
	            }
	    		

	        	return lReturn;
	        }
	        
	        /**
	         * Converts pElement to a Java Element if possible.  If not, return null.
	         * Return the element itself if it is itself a Java element.
	         * @param pElement
	         * @return A Java element or null.
	         */
	        private IJavaElement convertToIJavaElement( Object pElement )
	        {
	        	IJavaElement lReturn = null;
	        	if( pElement instanceof IJavaElement )
	        	{
	        		lReturn = (IJavaElement)pElement;
	        	}
	        	else if( pElement instanceof JavaElementNode )
	        	{
	        		lReturn = ((JavaElementNode)pElement).getElement();
	        	}
	        	else if( pElement instanceof IAdaptable )
	        	{
	        		lReturn = (IJavaElement)((IAdaptable)pElement).getAdapter( IJavaElement.class );
	        	}
	        	else
	        	{
	        		// lReturn remains null
	        		lReturn = null;
	        	}
	        	
	        	return lReturn;
	        }
	        
	        /**
	         * Determines if pElement can be included in a concern model.
	         * @param pElement The element to test
	         * @return true if pElement is of a type that is supported by the concern model.
	         */
	        private boolean supportedElement( IJavaElement pElement )
	        {
	        	
	        	boolean lReturn = false;
	        	if( (pElement instanceof IField) || (pElement instanceof IMethod ) )
	        	{
	              	try
	              	{
	              		if( ((IMember)pElement).getDeclaringType().isAnonymous() ||
	              			((IMember)pElement).getDeclaringType().isLocal() )
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
	      	 * Deletes the element from its concern if a copy of it already exists.
	      	 * 
	      	 * @param pNode
	      	 * 			A node that wraps a concern whose element we want to remove
	      	 * 		  pElement
	      	 * 			A must-existing element in the pNode that we want to remove
	      	 */
	      	private void deleteExistingElement( WrapperNode pNode, IJavaElement pElement  )
	      	{
	      		if (!aCopy)
	      		{
	      			Tabuleta.getDefault().getConcernModel().deleteElement( pNode.getConcern(), pElement );
	      		}
	      	}

	        
	        /**
	         * Determines whether it is possible to drop a certain type of object in the tree viewer.
	         * It is only possible to drop field and method objects into concerns, and concern files
	         * within the general space (null target).
	         *
	         * @param pTarget the object that the mouse is currently hovering over, or
	         *   <code>null</code> if the mouse is hovering over empty space
	         * @param pOperation the current drag operation (copy, move, etc.)
	         * @param pTransferType the current transfer type
	         * @return <code>true</code> if the drop is valid, and <code>false</code>
	         *   otherwise
	         */
	        @SuppressWarnings("unchecked")
			public boolean validateDrop( Object pTarget, int pOperation, TransferData pTransferType )
	        {	 
	       
	        	aCopy = pOperation == DND.DROP_COPY;
	        	boolean lReturn = false;
	        	
	        	if( pTarget == null )
	        	{
	        		lReturn = isCMFileDragged();
	        	}
	        	else if( pTarget instanceof ConcernNode )
	        	{
	        		Object lSelection = LocalSelectionTransfer.getInstance().getSelection();
	        		if( lSelection instanceof IStructuredSelection )
	        		{
	        			lReturn = true;
	        			for( Iterator lI = ((IStructuredSelection)lSelection).iterator(); lI.hasNext(); )
	        			{
	        				Object lNext = lI.next();
	        				IJavaElement lElement = convertToIJavaElement( lNext );
	        				if( lElement == null )
	        				{
	        					lReturn = false;
	        					break;
	        				}
	        				else if( !supportedElement( lElement ) && !supportedType( lElement ) )
	        				{
	        					lReturn = false;
	        					break;
	        				} 
	        			}
	                }
	        	}
	        	return lReturn;
	        }

			/** 
			 * @see org.eclipse.swt.dnd.DropTargetListener#dragEnter(org.eclipse.swt.dnd.DropTargetEvent)
			 * @param pEvent  the information associated with the drag enter event
			 */
			public void dragEnter( DropTargetEvent pEvent )
			{
				super.dragEnter( pEvent );
				if( !dragFromModel() )
				{
					pEvent.detail = DND.DROP_COPY;
				}
			}
	    }
	    
	    class CoverageSessionChangedListener implements ISessionListener{

			/* (non-Javadoc)
			 * @see com.mountainminds.eclemma.core.ISessionListener#sessionAdded(com.mountainminds.eclemma.core.ICoverageSession)
			 */
			@Override
			public void sessionAdded(ICoverageSession addedSession) {
				// TODO Auto-generated method stub
			}

			/* (non-Javadoc)
			 * @see com.mountainminds.eclemma.core.ISessionListener#sessionRemoved(com.mountainminds.eclemma.core.ICoverageSession)
			 */
			@Override
			public void sessionRemoved(ICoverageSession removedSession) {
				// TODO Auto-generated method stub
			}

			/**
			 * Sempre que uma sessão do EclEmma for ativada este método é chamado. 
			 */
			/* (non-Javadoc)
			 * @see com.mountainminds.eclemma.core.ISessionListener#sessionActivated(com.mountainminds.eclemma.core.ICoverageSession)
			 */
			@Override
			public void sessionActivated(ICoverageSession session) {
				saveCoverageAsCMAction.setEnabled(true);				
			}
	    	
	    }
	    /**
	     * Handles selection changes in the ConcernMapper view.
	     */
	    class ConcernMapperSelectionChangedListener implements ISelectionChangedListener
	    {
	    	private JavaElementLabelProvider aStatusLineLabelProvider = new JavaElementLabelProvider(
	    			JavaElementLabelProvider.SHOW_QUALIFIED | 
	    			JavaElementLabelProvider.SHOW_RETURN_TYPE | 
	    			JavaElementLabelProvider.SHOW_ROOT | 
	    			JavaElementLabelProvider.SHOW_TYPE );
	    	
	    	/**
	    	 * {@inheritDoc}
	    	 */
	    	public void selectionChanged( SelectionChangedEvent pEvent )
			{	
				LocalSelectionTransfer.getInstance().setSelection( aViewer.getSelection() );
				aDegreeSlider.setEnabled( false );
				aCommentBox.setEnabled( false );
				aCommentBox.setVisible( false );
				aDegreeValue.setText( "" );
				getViewSite().getActionBars().getStatusLineManager().setMessage( null );
				ISelection lSelection = aViewer.getSelection();
				if( lSelection instanceof IStructuredSelection )
				{
					IStructuredSelection lStructuredSelection = (IStructuredSelection)lSelection;
					if( lStructuredSelection.size() == 1 )
					{
						Object lElement = lStructuredSelection.getFirstElement();
						if( lElement instanceof ConcernNode )
						{
							getViewSite().getActionBars().getStatusLineManager().setMessage( ((ConcernNode)lElement).getConcernName() + " (" + 
									Tabuleta.getDefault().getConcernModel().getAllElements( ((ConcernNode)lElement).getConcernName()).size() + 
									" elements)");
							ConcernNode lNode = (ConcernNode)lElement;
							aCommentBox.setEnabled( true );
							aCommentBox.setVisible( true );
							aCommentBox.removeModifyListener( aModifyListener );
							aCommentBox.setText( Tabuleta.getDefault().getConcernModel().getConcernComment( lNode.getConcern() ));
							aCommentBox.addModifyListener( aModifyListener );
						}
						if( lElement instanceof WrapperNode )
						{
							WrapperNode lNode = (WrapperNode)lElement;
							if( Tabuleta.getDefault().getConcernModel().exists( lNode.getConcern(), lNode.getElement() ))
							{
								aDegreeSlider.setEnabled( true );
								aCommentBox.setEnabled( true );
								aCommentBox.setVisible( true );
								int lDegree = Tabuleta.getDefault().getConcernModel().getDegree( lNode.getConcern(), lNode.getElement() );
								aDegreeSlider.setSelection( lDegree );
								aDegreeValue.setText( new Integer( lDegree ).toString() );
								aCommentBox.setText(
										Tabuleta.getDefault().getConcernModel().getElementComment( lNode.getConcern(), lNode.getElement() ) );
							}
							getViewSite().getActionBars().getStatusLineManager().setMessage(
									aStatusLineLabelProvider.getText( lNode.getElement() ));
						}
					}
					else if( lStructuredSelection.size() > 1 )
					{
						getViewSite().getActionBars().getStatusLineManager().setMessage(
								lStructuredSelection.size() + " " + Tabuleta.getResourceString( "views.MapView.itemsSelected"));
						aDegreeSlider.setEnabled( true );
						aDegreeSlider.setSelection( MAX_DEGREE );
						aDegreeValue.setText( "100" );
						for( int lI = 0; lI < lStructuredSelection.toArray().length; lI++ )
						{
							if (lStructuredSelection.toArray()[lI] instanceof WrapperNode)
							{
								WrapperNode lNode = (WrapperNode)lStructuredSelection.toArray()[lI];
								if ( !Tabuleta.getDefault().getConcernModel().exists( lNode.getConcern(), lNode.getElement()))
								{
									aDegreeSlider.setEnabled( false );
								}
							}
							else
							{
								aDegreeSlider.setEnabled( false );
							}
						}
					}
				}
			}
	    }
}