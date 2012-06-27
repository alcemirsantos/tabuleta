/**
 * Copyleft ) - Test2Feature Mapper
 * Federal University of Minas Gerais - UFMG 
 */
package br.ufmg.dcc.t2fm.actions;

import org.eclipse.jface.action.Action;

import br.ufmg.dcc.t2fm.views.MapView;

/**
 * @author alcemir
 *
 */
public class RunAction extends Action {
	
	private MapView  aView;
	
	public RunAction(MapView pView){
		aView = pView;
	}
	/** 
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run(){
		// TODO executar o eclema com os testes associados a uma feature;
	}

}
