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
package br.ufmg.dcc.t2fm.actions;


import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
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
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SaveAsDialog;

import br.ufmg.dcc.t2fm.Test2FeatureMapper;
import br.ufmg.dcc.t2fm.actions.SaveAsAction.ConcernModelSaveAsDialog;
import br.ufmg.dcc.t2fm.model.io.JavaFileWriter;
import br.ufmg.dcc.t2fm.model.io.ModelIOException;
import br.ufmg.dcc.t2fm.model.io.ModelWriter;
import br.ufmg.dcc.t2fm.ui.ConcernMapperPreferencePage;
import br.ufmg.dcc.t2fm.views.MapView;

/**
 * @author Alcemir R. Santos
 *
 */
public class GenerateTestSuiteAction extends Action {

	MapView aView;
	
	public GenerateTestSuiteAction(MapView pView){
		aView = pView;
		setText("Generate Test Suite");
		setToolTipText("Generate Test Suite Action");
		setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
	}
	
	public void run()  {
		IFile lFile = null;
		
		SaveAsDialog lDialog = new TestSuiteSaveAsDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() );
		
		lDialog.open();
		
		if( lDialog.getReturnCode() == Window.CANCEL )
		{
			return;
		}
		
		IPath lPath = lDialog.getResult();
		
		String src = lPath.segment(0);
		String pakkage = buildPackage(lPath);
		String className = lPath.lastSegment();

		lPath = addJavaFileExtension( lPath );
		
		IWorkspace lWorkspace = ResourcesPlugin.getWorkspace();
		lFile = lWorkspace.getRoot().getFile( lPath );
		
		IPath s = lWorkspace.getRoot().getLocation();
		src = buildSource(s)+File.separator+src;
		if( !lFile.exists() )
		{
//			try {
//				lFile.create( null, true, null );
//			} catch (CoreException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			JavaFileWriter lWriter = new JavaFileWriter(src, pakkage, className, Test2FeatureMapper.getDefault().getConcernModel() );
			lWriter.write( lFile );
		}
		
		//TODO escrever o arquivo Java da suite de teste

		showMessage("Generate Test Suite Action executed.");
	}

	private String buildPackage(IPath lPath){
		String pakkage=lPath.segment(1);
		for (int i=2; i<lPath.segments().length-1; i++) {
			pakkage += "."+lPath.segment(i);
		}
		return pakkage;
	}
	
	private String buildSource(IPath lPath){
		String pakkage=lPath.segment(0);
		for (int i=1; i<lPath.segments().length; i++) {
			pakkage += File.separator+lPath.segment(i);
		}
		return pakkage;
	}
	private void showMessage(String message) {
		MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Test2FeatureMapper View", message);
		
	}
	
	private static IPath addJavaFileExtension( IPath pPath )
	{
		IPath lReturn = pPath;
		if ( lReturn.getFileExtension() == null )
		{
			lReturn = lReturn.addFileExtension( "java" );
		}
		else if ( !lReturn.getFileExtension().equals( "java" ) )
		{
			lReturn = lReturn.removeFileExtension();
			lReturn = lReturn.addFileExtension( "java" );
		}
		return lReturn;
	}

	/**
	 * We have to define this class simply to be able to change the title
	 * of the dialog.  This is necessary because when the save as functionality
	 * kicks in as the result of an autosave the default title is too generic and 
	 * users may not directly see that they are saving their test suite.
	 * 
	 * @author Alcemir Santos
	 */
	class TestSuiteSaveAsDialog extends SaveAsDialog
	{
		/**
		 * Creates a new SaveAsDialog for saving concern models.
		 * @param pShell The parent shell.
		 */
		public TestSuiteSaveAsDialog( Shell pShell )
		{
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

	        setTitle( Test2FeatureMapper.getResourceString( "actions.GenerateTestSuiteAction.DialogTitle") );
	        setMessage( Test2FeatureMapper.getResourceString( "actions.GenerateTestSuiteAction.DialogMessage") );

	        return lContents;
	    }
	}
	
}
