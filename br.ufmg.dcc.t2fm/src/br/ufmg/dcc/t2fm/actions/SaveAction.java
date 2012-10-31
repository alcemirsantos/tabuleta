/* ConcernMapper - A concern modeling plug-in for Eclipse
 * Copyright (C) 2006  McGill University (http://www.cs.mcgill.ca/~martin/cm)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Revision: 1.14 $
 */

package br.ufmg.dcc.t2fm.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

import br.ufmg.dcc.t2fm.Test2FeatureMapper;
import br.ufmg.dcc.t2fm.model.io.ModelIOException;
import br.ufmg.dcc.t2fm.model.io.ModelWriter;
import br.ufmg.dcc.t2fm.ui.ProblemManager;
import br.ufmg.dcc.t2fm.views.MapView;

/**
 * Saves the concern model to a file.
 */
public class SaveAction extends Action
{
    private MapView aView;
    
	/**
	 * @param pView The view containing the action.
	 */
	public SaveAction( MapView pView )
	{
	    aView = pView;
		setText( Test2FeatureMapper.getResourceString( "actions.SaveAction.Label") );
		setImageDescriptor( Test2FeatureMapper.imageDescriptorFromPlugin( Test2FeatureMapper.ID_PLUGIN, "icons/save.gif")); 
		setDisabledImageDescriptor( Test2FeatureMapper.imageDescriptorFromPlugin( Test2FeatureMapper.ID_PLUGIN, "icons/saved.gif")); 
		setToolTipText( Test2FeatureMapper.getResourceString( "actions.SaveAction.ToolTip" ) ); 
	}
	
	/**
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run()
	{
		IFile lFile = Test2FeatureMapper.getDefault().getDefaultResource();
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
			ModelWriter lWriter = new ModelWriter( Test2FeatureMapper.getDefault().getConcernModel() );
			lWriter.write( lFile );
		}
		catch( ModelIOException lException )
		{
		    MessageDialog.openError( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
					Test2FeatureMapper.getResourceString( "actions.SaveAction.ErrorLabel"),
					Test2FeatureMapper.getResourceString( "actions.SaveAction.ErrorMessage") + " " + lException.getMessage());
			return;
		}
		
		// TODO: Clean this up.  The save actions should not know about the view.
		Test2FeatureMapper.getDefault().resetDirty();
		if(aView != null)
		{
			aView.updateActionState();
		}
	}
}
