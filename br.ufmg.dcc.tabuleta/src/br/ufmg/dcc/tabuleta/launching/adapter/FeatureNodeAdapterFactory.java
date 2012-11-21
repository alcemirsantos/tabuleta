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

import org.eclipse.core.resources.mapping.ResourceMapping;
import org.eclipse.core.runtime.IAdapterFactory;

import br.ufmg.dcc.tabuleta.views.components.ConcernNode;

/**
 * @author Alcemir R. Santos
 *
 */
public class FeatureNodeAdapterFactory implements IAdapterFactory{

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
	 */
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if ((adaptableObject instanceof ConcernNode) && adapterType == ResourceMapping.class) {
			return new JavaPackageResourceMapping((ConcernNode) adaptableObject);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
	 */
	@Override
	public Class[] getAdapterList() {
		// TODO Auto-generated method stub
		return null;
	}
	

}
