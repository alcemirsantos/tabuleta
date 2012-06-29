/* ConcernMapper - A concern modeling plug-in for Eclipse
 * Copyright (C) 2006  McGill University (http://www.cs.mcgill.ca/~martin/cm)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Revision: 1.26 $
 */

package br.ufmg.dcc.t2fm.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import br.ufmg.dcc.t2fm.Test2FeatureMapper;
import br.ufmg.dcc.t2fm.model.io.ModelIOException;
import br.ufmg.dcc.t2fm.model.io.ModelReader;
import br.ufmg.dcc.t2fm.ui.ProblemManager;
import br.ufmg.dcc.t2fm.views.MapView;

/**
 * This action class is in charge of loading a concern model in memory
 * and displaying it in the ConcernMapper view.
 */
public class LoadConcernModelAction implements IObjectActionDelegate, IRunnableWithProgress
{
    /** The file to load. */
	private IFile aFile;

	/**
	 * Does nothing.
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 * @param pAction the action proxy that handles presentation portion of the action
     * @param pTargetPart the new part target
	 */
	public void setActivePart(IAction pAction, IWorkbenchPart pTargetPart)
	{}

	/**
	 * Loads the concern model stored in the file wrapped in this object.
	 * @see IActionDelegate#run(IAction)
	 * @param pAction the action proxy that handles the presentation portion of the
     *   action
	 */
	public void run( IAction pAction )
	{
		if( aFile == null )
		{
			return;
		}
		boolean lProceed = shouldProceed();
		if( !lProceed )
		{
			return;
		}
		
		ProgressMonitorDialog lProgressDialog = new ProgressMonitorDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() );	
		try
		{
			lProgressDialog.run( true, false, this );
		}
		catch( InvocationTargetException lException )
		{
			Throwable lCause = lException.getCause();
			if( lCause instanceof Exception )
			{
				ProblemManager.reportException( (Exception)lCause );
			}
			else
			{
				ProblemManager.reportException( lException );
			}
		}
		catch( InterruptedException lException )
		{
			ProblemManager.reportException( lException );
		}
	}
	
	
	/**
	 * Performs the actual loading of the file.
	 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
	 * @param pMonitor the progress monitor to use to display progress and receive
     *   requests for cancelation
	 */
	public void run( IProgressMonitor pMonitor )
	{
		try
		{
			Test2FeatureMapper.getDefault().getConcernModel().startStreaming();
		    Test2FeatureMapper.getDefault().setDefaultResource( aFile );
		    Test2FeatureMapper.getDefault().getConcernModel().reset();
		    ModelReader lReader = new ModelReader( Test2FeatureMapper.getDefault().getConcernModel() );
		        
		    final int lSkipped = lReader.read( aFile, new LoadMonitor( pMonitor, 
		    		Test2FeatureMapper.getResourceString( "actions.LoadConcernModelAction.TaskName" )) );
		    // The statement below was removed because it causes an invalid Thread access.
		    // The consequence is that when loading a concern file using the pop-up menu as opposed to
		    // the drag-and-drop method, the view does not get shown if it isn't already.
		    //PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView( Test2FeatureMapper.ID_VIEW );
		    if( lSkipped > 0 )
		    {
		    	PlatformUI.getWorkbench().getDisplay().asyncExec( new Runnable()
				{
					public void run()
					{
						Shell[] lShells = PlatformUI.getWorkbench().getDisplay().getShells();
						for( Shell lNext : lShells ) 
						{
							MessageDialog.openWarning( lNext, 
									Test2FeatureMapper.getResourceString( "actions.LoadConcernModelAction.ErrorDialogTitle" ),
									lSkipped + " " + Test2FeatureMapper.getResourceString( "actions.LoadConcernModelAction.SkippedMessage" ));
							break;
						}
					}});
		    }
		}
		catch( ModelIOException lException )
		{
			ProblemManager.reportExceptionMessage( lException );
			Test2FeatureMapper.getDefault().getConcernModel().reset();
		}
		finally
		{
			Test2FeatureMapper.getDefault().getConcernModel().stopStreaming();
			Test2FeatureMapper.getDefault().resetDirty();
			
			for( IWorkbenchWindow lWindow : PlatformUI.getWorkbench().getWorkbenchWindows() )
			{
				IWorkbenchPage lPage = lWindow.getActivePage();
				if( lPage != null )
				{
					IViewPart lView = lPage.findView( Test2FeatureMapper.ID_VIEW );
					if( lView != null )
					{
						((MapView)lView).updateActionState();
					}
				}
			}
		}
	}

	/**
	 * Whenever the selection changes, store the currently selected
	 * file, if possible.
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 * @param pAction the action proxy that handles presentation portion of 
     * 		the action
     * @param pSelection the current selection, or <code>null</code> if there
     * 		is no selection.
	 */
	public void selectionChanged(IAction pAction, ISelection pSelection)
	{
		IStructuredSelection lStructuredSelection = (IStructuredSelection) pSelection;
		Object lFile = lStructuredSelection.getFirstElement();
		if( lFile instanceof IFile )
		{
			aFile = (IFile) lFile;
		}
		else if( lFile instanceof IAdaptable )
		{
			Object lAdapter = ((IAdaptable) lFile).getAdapter( IResource.class );
			if( lAdapter instanceof IFile )
			{
				aFile = (IFile) lAdapter;
			}
		}
		else
		{
			aFile = null;
		}
	}
	
	private boolean shouldProceed()
	{
		boolean lReturn = true;
		if( Test2FeatureMapper.getDefault().isDirty() )
		{
		    lReturn = MessageDialog.openQuestion( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
		            Test2FeatureMapper.getResourceString( "actions.LoadConcernModelAction.QuestionDialogTitle" ),
		            Test2FeatureMapper.getResourceString( "actions.LoadConcernModelAction.WarningOverwrite"	));
		}
		return lReturn;
	}
	
	/**
	 * Sets the file to load when this action is executed.
	 * @param pFile The file to load.
	 */
	public void setFile( IFile pFile )
	{
		aFile = pFile;
	}
	
	/**
	 * Adapter for the ca.mcgill.cs.serg.cm.model.io.IProgressMonitor.
	 */
	static class LoadMonitor implements br.ufmg.dcc.t2fm.model.io.IProgressMonitor
    {
    	private IProgressMonitor aMonitor;
    	private String aTaskName;
    	
    	/**
    	 * Creates a new monitor for loading cm files.
    	 * @param pMonitor The internal monitor.
    	 * @param pTaskName The name of the task to report on.
    	 */
    	public LoadMonitor( IProgressMonitor pMonitor, String pTaskName )
    	{
    		aMonitor = pMonitor;
    		aTaskName = pTaskName;
    	}
    	
		/**
		 * @see ca.mcgill.cs.serg.cm.model.io.IProgressMonitor#setTotal(int)
		 * @param pTotal The number of increments to complete.
		 */
		public void setTotal( int pTotal ) 
		{
			aMonitor.beginTask( aTaskName, pTotal );
		}
		/**
		 * @see ca.mcgill.cs.serg.cm.model.io.IProgressMonitor#worked(int)
		 * @param pAmount The number of increments.
		 */
		public void worked( int pAmount )
		{
			aMonitor.worked( pAmount );
		}
    }
}
