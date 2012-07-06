/* ConcernMapper - A concern modeling plug-in for Eclipse
 * Copyright (C) 2006  McGill University (http://www.cs.mcgill.ca/~martin/cm)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Revision: 1.14 $
 */

package ca.mcgill.cs.serg.cm.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import ca.mcgill.cs.serg.cm.ConcernMapper;

/**
 * A general utility class to report exceptions to the UI.
 */
public final class ProblemManager
{
	private ProblemManager()
	{}
	
	/**
	 * Reports an exception in a dialog and logs the exception.  The 
	 * exception dialog contains the stack trace details.
	 * @param pException The exception to report.
	 */
	public static void reportException( Exception pException )
	{
		StackTraceElement[] lStackElements = pException.getStackTrace();
		IStatus[] lStatuses = new IStatus[ lStackElements.length + 1 ];
		lStatuses[0] = new Status( IStatus.ERROR, ConcernMapper.ID_PLUGIN, IStatus.OK, pException.getClass().getName(), pException );
		for( int lI = 0 ; lI < lStackElements.length; lI++ )
		{
			lStatuses[lI+1] = new Status( IStatus.ERROR, ConcernMapper.ID_PLUGIN, IStatus.OK, "     " + lStackElements[lI].toString(), pException );
		}
		
		String lMessage = ConcernMapper.getResourceString( "ui.ProblemManager.DefaultMessage" );
		String lTitle = ConcernMapper.getResourceString( "ui.ProblemManager.DialogTitle" );
		if( pException.getMessage() != null )
		{
			lMessage = pException.getMessage();
		}
		MultiStatus lStatus = new MultiStatus( ConcernMapper.ID_PLUGIN, IStatus.OK, lStatuses, lMessage, pException );
		ErrorDialog.openError( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), lTitle, lMessage, lStatus, IStatus.ERROR );
		ConcernMapper.getDefault().getLog().log( lStatus );
	}
	
	/**
	 * Reports an exception in a dialog and logs the exception.  The 
	 * exception dialog does not contain the stack strace details.
	 * @param pException The exception to report.
	 */
	public static void reportExceptionMessage( Exception pException )
	{
		StackTraceElement[] lStackElements = pException.getStackTrace();
		IStatus[] lStatuses = new IStatus[ lStackElements.length + 1 ];
		lStatuses[0] = new Status( IStatus.ERROR, ConcernMapper.ID_PLUGIN, IStatus.OK, pException.getClass().getName(), pException );
		for( int lI = 0 ; lI < lStackElements.length; lI++ )
		{
			lStatuses[lI+1] = new Status( IStatus.ERROR, ConcernMapper.ID_PLUGIN, IStatus.OK, "     " + lStackElements[lI].toString(), pException );
		}
		
		String lMessage = ConcernMapper.getResourceString( "ui.ProblemManager.DefaultMessage" );
		final String lTitle = ConcernMapper.getResourceString( "ui.ProblemManager.DialogTitle" );
		if( pException.getMessage() != null )
		{
			lMessage = pException.getMessage();
		}
		final String lMessage2 = lMessage; 
		MultiStatus lStatus = new MultiStatus( ConcernMapper.ID_PLUGIN, IStatus.OK, lStatuses, lMessage2, pException );
		
		PlatformUI.getWorkbench().getDisplay().asyncExec( new Runnable()
			{
				public void run()
				{
					Shell[] lShells = PlatformUI.getWorkbench().getDisplay().getShells();
					for( Shell lNext : lShells ) 
					{
						MessageDialog.openError( lNext, lTitle, lMessage2 );
						break;
					}
				}});
		
		ConcernMapper.getDefault().getLog().log( lStatus );
	}
}

