/* ConcernMapper - A concern modeling plug-in for Eclipse
 * Copyright (C) 2006  McGill University (http://www.cs.mcgill.ca/~martin/cm)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Revision: 1.15 $
 */

package br.ufmg.dcc.tabuleta.actions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import br.ufmg.dcc.tabuleta.Tabuleta;

/**
 * An action to delete a list of elements in a model.
 * To delete elements from the concern model, first create an action, then add each element to 
 * delete by using scheduleElementDelete and scheduleConcernDelete, and then run the action.
 */
public class DeleteAction extends Action
{
	private Map<String, HashSet<Object>> aElementsToDelete;
	private Set<String> aConcernsToDelete;
		
	/**
	 * Creates the action with no element to delete.
	 */
	public DeleteAction()
	{
		aElementsToDelete = new HashMap<String, HashSet<Object>>();
		aConcernsToDelete = new HashSet<String>();
		setText( Tabuleta.getResourceString( "actions.DeleteAction.Label") );
		setImageDescriptor( PlatformUI.getWorkbench().getSharedImages().getImageDescriptor( ISharedImages.IMG_TOOL_DELETE)); 
		setToolTipText( Tabuleta.getResourceString( "actions.DeleteAction.ToolTip" ) ); 
	}

	/**
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() 
	{
		Tabuleta.getDefault().getConcernModel().startStreaming();
		try
		{
			deleteConcerns();
			deleteElements();
			aConcernsToDelete.clear();
			aElementsToDelete.clear();
		}
		finally
		{
			Tabuleta.getDefault().getConcernModel().stopStreaming();
		}
	}
	
	/**
	 * Deletes all concerns maked for deletion, and all elements in them.
	 */
	private void deleteConcerns()
	{
		for( String lConcernName : aConcernsToDelete )
		{
			Tabuleta.getDefault().getConcernModel().deleteConcern( lConcernName );
			aElementsToDelete.remove( lConcernName );
		}
	}
	
	/**
	 * Deletes all elements maked for deletion.
	 */
	private void deleteElements()
	{
		for( String lConcernName : aElementsToDelete.keySet() )
		{
			for( Object lElement : aElementsToDelete.get( lConcernName ))
			{
				if( Tabuleta.getDefault().getConcernModel().exists( lConcernName, lElement ))
				{
					Tabuleta.getDefault().getConcernModel().deleteElement( lConcernName, lElement );
				}
			}
		}
	}
	
	/**
	 * Adds an element to be deleted from the concern model the next time this action 
	 * is run.  Running the action clears the set of elements to delete.
	 * If the element is not in the concern, it won't be deleted and will simply be ignored.
	 * @param pConcernName The concern from which to delete the element.
	 * @param pElement The element to delete.
	 */
	public void scheduleElementDelete( String pConcernName, Object pElement )
	{
		HashSet<Object> lElements = aElementsToDelete.get( pConcernName );
		if( lElements == null )
		{
			lElements = new HashSet<Object>();
			aElementsToDelete.put( pConcernName, lElements );
		}
		lElements.add( pElement );
	}
	
	/**
	 * Adds a concern to be deleted from the concern model the next time this action
	 * is run. Running the action clears the set of concerns to delete.
	 * @param pConcernName The name of the concern to delete.
	 */
	public void scheduleConcernDelete( String pConcernName )
	{
		aConcernsToDelete.add( pConcernName );
	}
}
