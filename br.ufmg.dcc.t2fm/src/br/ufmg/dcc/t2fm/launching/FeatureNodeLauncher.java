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
package br.ufmg.dcc.t2fm.launching;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IPackageFragmentRoot;

import com.mountainminds.eclemma.core.launching.CoverageLauncher;

/**
 * @author Alcemir R. Santos
 *
 */
public class FeatureNodeLauncher extends CoverageLauncher {

	/**
	 * @see com.mountainminds.eclemma.core.launching.ICoverageLauncher#getOverallScope(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	@Override
	public Set<IPackageFragmentRoot> getOverallScope(
			ILaunchConfiguration configuration) throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

}
