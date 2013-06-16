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
package br.ufmg.dcc.tabuleta.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SaveAsDialog;

import prefuse.data.io.DataIOException;
import prefuse.data.io.GraphMLWriter;
import br.ufmg.dcc.tabuleta.Tabuleta;
import br.ufmg.dcc.tabuleta.actions.util.CmFilesOperations;
import br.ufmg.dcc.tabuleta.ui.ProblemManager;
import br.ufmg.dcc.tabuleta.views.components.GraphManager;

/**
 * @author Alcemir R. Santos
 *
 */
public class SaveAsGraphMLAction extends Action {
	
	/**
	 * 
	 */
	public SaveAsGraphMLAction() {
		setText( Tabuleta.getResourceString( "actions.SaveAsGraphMLAction.Label") );
		setImageDescriptor( Tabuleta.imageDescriptorFromPlugin( Tabuleta.ID_PLUGIN, "icons/saveas.gif")); 
		setToolTipText( Tabuleta.getResourceString( "actions.SaveAsGraphMLAction.ToolTip" ) ); 
	
	}
	
	/**
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run(){
		CmFilesOperations.showMessage(Tabuleta.getResourceString("actions.SaveAsGraphMLAction.Label"), "Sorry, this action is under development.");
//		IFile lFile = null;
//		
//		SaveAsDialog saveAsDialog = new GraphMLSaveAsDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() );
//				
//		IFile lCurrentFile = Tabuleta.getDefault().getDefaultResource();
//		if( lCurrentFile != null )
//		{
//			saveAsDialog.setOriginalFile( lCurrentFile );
//		}
//		saveAsDialog.open();
//		
//		if( saveAsDialog.getReturnCode() == Window.CANCEL )	return;
//		
//		IPath lPath = saveAsDialog.getResult();		
//		lPath = addGraphMLFileExtension( lPath );
//		
//		IWorkspace lWorkspace = ResourcesPlugin.getWorkspace();
//		lFile = lWorkspace.getRoot().getFile( lPath );
//		
//		if( !lFile.exists() ){
//			try{
//				lFile.create( null, true, null );
//			}catch( CoreException lException ){
//				MessageDialog.openError( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
//					Tabuleta.getResourceString( "actions.SaveAsGraphMLAction.ErrorLabel"),
//					Tabuleta.getResourceString( "actions.SaveAsGraphMLAction.ErrorMessage") + " " + lException.getMessage());
//				return;
//			}
//		}
//		
//		GraphMLWriter writer = new GraphMLWriter();
//		try {
//			writer.writeGraph(GraphManager.getInstance().getActiveGraph(), lFile.getRawLocation().toFile());
//		} catch (DataIOException e) {
//			ProblemManager.reportException(e);
//		}
		
	}

	private static IPath addGraphMLFileExtension( IPath pPath )
	{
		IPath lReturn = pPath;
		String extension = "graphml";
		if ( lReturn.getFileExtension() == null ){
			lReturn = lReturn.addFileExtension( extension );
		}else if ( !lReturn.getFileExtension().equals( extension ) )	{
			lReturn = lReturn.removeFileExtension();
			lReturn = lReturn.addFileExtension( extension );
		}
		return lReturn;
	}
	/**
	 * We have to define this class simply to be able to change the title
	 * of the dialog.  This is necessary because when the save as functionality
	 * kicks in as the result of an autosave the default title is too generic and 
	 * users may not directly see that they are saving their coverage graphs.
	 * 
	 * @author Alcemir Santos
	 */
	class GraphMLSaveAsDialog extends SaveAsDialog	{
		/**
		 * Creates a new SaveAsDialog for saving the coverage graphs.
		 * @param pShell The parent shell.
		 */
		public GraphMLSaveAsDialog( Shell pShell ){
			super( pShell );
		}
		
	    /**
	     * @see org.eclipse.jface.window.Window#createContents(org.eclipse.swt.widgets.Composite)
	     * @param pParent the parent composite for the controls in this window. The type
	     * 					of layout used is determined by getLayout()
	 	 * @return the control that will be returned by subsequent calls to getControl()
	     */
	    protected Control createContents( Composite pParent )
	    {
	        Control lContents = super.createContents( pParent );

	        setTitle( Tabuleta.getResourceString( "actions.SaveAsGraphMLAction.DialogTitle") );
	        setMessage( Tabuleta.getResourceString( "actions.SaveAsGraphMLAction.DialogMessage") );

	        return lContents;
	    }
	}
	
}
