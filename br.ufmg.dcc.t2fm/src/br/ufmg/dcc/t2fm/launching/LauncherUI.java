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

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTabGroup;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaJRETab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaMainTab;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Alcemir R. Santos
 *
 */
public class LauncherUI implements ILaunchConfigurationTabGroup {

	private ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[3];
		
	public LauncherUI(){
		tabs[0] = new JavaMainTab();
		tabs[1] = new JavaJRETab();
		tabs[2] = new CommonTab();
				
	}
	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		dialog.setActiveTab(tabs[0]);
	}

	@Override
	public void dispose() {	}

	@Override
	public ILaunchConfigurationTab[] getTabs() {
		return tabs;
	}


	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		tabs[0].setDefaults(configuration);
		tabs[1].setDefaults(configuration);
		tabs[2].setDefaults(configuration);
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		tabs[0].initializeFrom(configuration);
		tabs[1].initializeFrom(configuration);
		tabs[2].initializeFrom(configuration);

	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		tabs[0].performApply(configuration);
		tabs[1].performApply(configuration);
		tabs[2].performApply(configuration);

	}

	@Override
	public void launched(ILaunch launch) {

	}
}
