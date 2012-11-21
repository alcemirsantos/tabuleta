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
package br.ufmg.dcc.tabuleta.launching.adapter;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.mapping.ResourceMapping;
import org.eclipse.core.resources.mapping.ResourceMappingContext;
import org.eclipse.core.resources.mapping.ResourceTraversal;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IPackageFragment;

import br.ufmg.dcc.tabuleta.views.components.ConcernNode;

/**
 * @author Alcemir R. Santos
 *
 */
public class JavaPackageResourceMapping extends ResourceMapping {

	private IPackageFragment pakkage;

	/**
	 * @param node
	 */
	public JavaPackageResourceMapping(ConcernNode node) {
		pakkage = (IPackageFragment) node;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.mapping.ResourceMapping#getModelObject()
	 */
	@Override
	public Object getModelObject() {
		return pakkage;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.mapping.ResourceMapping#getModelProviderId()
	 */
	@Override
	public String getModelProviderId() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.mapping.ResourceMapping#getProjects()
	 */
	@Override
	public IProject[] getProjects() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.mapping.ResourceMapping#getTraversals(org.eclipse.core.resources.mapping.ResourceMappingContext, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public ResourceTraversal[] getTraversals(ResourceMappingContext context,
			IProgressMonitor monitor) throws CoreException {
		return new ResourceTraversal[] {
				new ResourceTraversal (
				new IResource[] {pakkage.getCorrespondingResource()}, 
				IResource.DEPTH_ONE	, IResource.NONE)};
	}

}
