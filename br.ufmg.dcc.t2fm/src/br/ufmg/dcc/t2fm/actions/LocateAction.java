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

import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.mountainminds.eclemma.core.launching.CoverageLauncher;

import br.ufmg.dcc.t2fm.Test2FeatureMapper;
import br.ufmg.dcc.t2fm.model.io.ModelIOException;
import br.ufmg.dcc.t2fm.model.io.ModelWriter;
import br.ufmg.dcc.t2fm.ui.ProblemManager;
import br.ufmg.dcc.t2fm.views.MapView;
import br.ufmg.dcc.t2fm.views.components.JavaElementNode;
import br.ufmg.dcc.t2fm.views.components.WrapperNode;

/**
 * @author Alcemir R. Santos
 *
 */
public class LocateAction extends CoverageLauncher { // Action{

	private MapView aView;
    
//	/**
//	 * @param pView The view containing the action.
//	 */
//	public LocateAction( MapView pView ){
//	    aView = pView;
//		setText( Test2FeatureMapper.getResourceString( "actions.LocateAction.Label") );
//		setImageDescriptor( Test2FeatureMapper.imageDescriptorFromPlugin( Test2FeatureMapper.ID_PLUGIN, "icons/save.gif")); 
//		setDisabledImageDescriptor( Test2FeatureMapper.imageDescriptorFromPlugin( Test2FeatureMapper.ID_PLUGIN, "icons/saved.gif")); 
//		setToolTipText( Test2FeatureMapper.getResourceString( "actions.LocateAction.ToolTip" ) ); 
//	}
//	
//	/**
//	 * @see org.eclipse.jface.action.IAction#run()
//	 */
//	public void run()	{	}
//
	/* (non-Javadoc)
	 * @see com.mountainminds.eclemma.core.launching.ICoverageLauncher#getOverallScope(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	@Override
	public Set<IPackageFragmentRoot> getOverallScope(ILaunchConfiguration configuration) throws CoreException {

		Set<IPackageFragmentRoot> result = null;
	
		ISelection lSelection = aView.getCurrentSelection();
		if( lSelection instanceof IStructuredSelection )
		{
			Object[] lStructuredSelectionArray = ((IStructuredSelection)lSelection).toArray();
			for( int lI = 0; lI<lStructuredSelectionArray.length; lI++ )
			{
				Object lElement = lStructuredSelectionArray[lI];
				if( lElement instanceof WrapperNode )
				{
					WrapperNode lNode = (WrapperNode)lElement;
					result = Test2FeatureMapper.getDefault().getConcernModel().getAllElements(lNode.getConcern());
				}
			}	
		}
	   
	    return result;
	}
}
