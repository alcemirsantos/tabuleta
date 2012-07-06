/* ConcernMapper - A concern modeling plug-in for Eclipse
 * Copyright (C) 2006  McGill University (http://www.cs.mcgill.ca/~martin/cm)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Revision: 1.14 $
 */

package ca.mcgill.cs.serg.cm.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

import ca.mcgill.cs.serg.cm.ConcernMapper;
import ca.mcgill.cs.serg.cm.model.io.ModelIOException;
import ca.mcgill.cs.serg.cm.model.io.ModelWriter;
import ca.mcgill.cs.serg.cm.ui.ProblemManager;
import ca.mcgill.cs.serg.cm.views.ConcernMapperView;

/**
 * Saves the concern model to a file.
 */
public class SaveAction extends Action
{
    private ConcernMapperView aView;
    
	/**
	 * @param pView The view containing the action.
	 */
	public SaveAction( ConcernMapperView pView )
	{
	    aView = pView;
		setText( ConcernMapper.getResourceString( "actions.SaveAction.Label") );
		setImageDescriptor( ConcernMapper.imageDescriptorFromPlugin( ConcernMapper.ID_PLUGIN, "icons/save.gif")); 
		setDisabledImageDescriptor( ConcernMapper.imageDescriptorFromPlugin( ConcernMapper.ID_PLUGIN, "icons/saved.gif")); 
		setToolTipText( ConcernMapper.getResourceString( "actions.SaveAction.ToolTip" ) ); 
	}
	
	/**
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run()
	{
		IFile lFile = ConcernMapper.getDefault().getDefaultResource();
		if( lFile == null)
		{
			new SaveAsAction( aView ).run();
			return;
		}
		try
		{
			lFile.getParent().refreshLocal( IResource.DEPTH_ONE, null);
		}
		catch(CoreException lException)
		{
			ProblemManager.reportException( lException );
		}
		catch(OperationCanceledException lException)
		{
			ProblemManager.reportException( lException );
		}
		if( !lFile.exists() )
		{
			new SaveAsAction( aView ).run();
			return;
		}
		try
		{
			ModelWriter lWriter = new ModelWriter( ConcernMapper.getDefault().getConcernModel() );
			lWriter.write( lFile );
		}
		catch( ModelIOException lException )
		{
		    MessageDialog.openError( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
					ConcernMapper.getResourceString( "actions.SaveAction.ErrorLabel"),
					ConcernMapper.getResourceString( "actions.SaveAction.ErrorMessage") + " " + lException.getMessage());
			return;
		}
		
		// TODO: Clean this up.  The save actions should not know about the view.
		ConcernMapper.getDefault().resetDirty();
		if(aView != null)
		{
			aView.updateActionState();
		}
	}
}
