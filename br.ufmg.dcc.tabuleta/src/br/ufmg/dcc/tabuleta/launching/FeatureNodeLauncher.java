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
package br.ufmg.dcc.tabuleta.launching;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.launching.JavaRuntime;

import br.ufmg.dcc.tabuleta.Tabuleta;

import com.mountainminds.eclemma.core.ScopeUtils;
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
				return null;
//		 final IJavaProject project = JavaRuntime.getJavaProject(configuration);
//		    
//		 Test2FeatureMapper.getDefault().getConcernModel().getElements(configuration.)
//		 
//		    if (project == null) {
//		      return Collections.emptySet();
//		    } else {
//		      return ScopeUtils.filterJREEntries(Arrays.asList(project.getAllPackageFragmentRoots()));
//		    }
	}

}
