/* ConcernMapper - A concern modeling plug-in for Eclipse
 * Copyright (C) 2006  McGill University (http://www.cs.mcgill.ca/~martin/cm)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Revision: 1.23 $
 */

package br.ufmg.dcc.t2fm.views.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import br.ufmg.dcc.t2fm.Test2FeatureMapper;
import br.ufmg.dcc.t2fm.model.ConcernModel;
import br.ufmg.dcc.t2fm.ui.ConcernMapperPreferencePage;


/**
 * Content provider for a Concern model.  This class understands the internal structure
 * of a concern model that contains Java Elements.  It organizes the element in a concern
 * model in a forest structure.  The roots of the trees are concerns in a concern model.  The
 * direct children of these elements are non-inner types.  For types, the tree hierarchy
 * is the same as the declarative hierarchy, with elements having as children the elements
 * they declare.
 */
public class ConcernModelContentProvider implements IStructuredContentProvider, ITreeContentProvider 
{
    private ConcernModel aConcernModel;
    private Map<String, Map<Object, IConcernMapperViewNode>> aNodeMap = new HashMap<String, Map<Object, IConcernMapperViewNode>>();
	private boolean aInputChanged;
	private Object[] aElements;
    
    /**
     * Return the object managed by the ContentProvider that corresponds to the 
     * domain object pObject, in the context of the concern pConcern.  For example, if
     * a Java method A.m() is part of two concerns, calling this method with A.m() and 
     * "concern1" will return the JavaElementNode corresponding to the method in concern1.
     * @param pObject The object to search for.
     * @param pConcern The name of the concern containing the object.
     * @return The corresponding IConcernMapperViewNode, or null if none are found.
     */
    public IConcernMapperViewNode getNodeObject( Object pObject, String pConcern )
    {
    	IConcernMapperViewNode lReturn = null;
    	Map<Object, IConcernMapperViewNode> lMap = aNodeMap.get( pConcern );
    	if( lMap != null )
    	{
    		lReturn = lMap.get( pObject );
    	}
    	return lReturn;
    }
    
    /** 
     * Returns the elements to display in the viewer 
     * when its input is set to the given element. 
     * These elements can be presented as rows in a table, items in a list, etc.
     * The result is not modified by the viewer.
     * 
     * This method expects as input a concern model.
     * @param pInput the concern model to view.
     * @return The objects.s
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements( Object pInput )
    {
    	if ( !aInputChanged )
    	{
    		return aElements;
    	}
    	aElements = new Object[0];
        if( pInput instanceof ConcernModel )
        {
           aConcernModel = (ConcernModel)pInput;
           aNodeMap.clear();
           aElements = buildDynamicStructure();
           aInputChanged = false;
        }
        return aElements;
    }
    
    private Object[] buildDynamicStructure()
    {
    	List<ConcernNode> lReturn = new ArrayList<ConcernNode>();
    	for( String lConcernName : aConcernModel.getConcernNames() )
    	{
    		Map<Object, IConcernMapperViewNode> lMap = new HashMap<Object, IConcernMapperViewNode>();
    		aNodeMap.put( lConcernName, lMap );
    		ConcernNode lConcernNode = new ConcernNode( lConcernName );
    		lConcernNode.setParent( aConcernModel );
    		lReturn.add( lConcernNode );
    		for( Object lConcernElement : aConcernModel.getAllElements( lConcernName ))
    		{
    			if( lConcernElement instanceof IJavaElement )
    			{
    				IJavaElement lJavaElement = (IJavaElement) lConcernElement;
    				if( lJavaElement.exists() )
    				{
    					addToMap( lMap, lJavaElement, lConcernNode );
    				}
    				else
    				{
    					if( Test2FeatureMapper.getDefault().getPreferenceStore().getBoolean( ConcernMapperPreferencePage.P_SHOW_INCONSISTENT_ELEMENTS ))
    					{
    						addToMap( lMap, lJavaElement, lConcernNode );
    					}
    				}
    			}
    		}
    	}
    	return lReturn.toArray();
    }
       
     /**
     * Returns ElementNode objects describing the children of this node.
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     * @param pParent the parent element
     * @return an array of child elements
     */
    public Object[] getChildren( Object pParent ) 
    {
        Object[] lReturn = new Object[0];
        if( pParent instanceof IConcernMapperViewNode )
        {
            lReturn = ((IConcernMapperViewNode)pParent).getChildren();
        }
        return lReturn;
    }
    
    /*
     * Builds a map of root nodes and their children.
     */
    private void addToMap( Map<Object, IConcernMapperViewNode> pMap, IJavaElement pElement, ConcernNode pConcernNode )
	{
		assert pElement instanceof IMember;
		
		if( pMap.get( pElement ) != null )
		{
			return;
		}
		
		JavaElementNode lNewNode = new JavaElementNode( pElement );
		pMap.put( pElement, lNewNode );
		
		IType lParent = ((IMember)pElement).getDeclaringType();
		if( lParent != null )
		{
			IConcernMapperViewNode lParentNode = pMap.get( lParent );
			if( lParentNode == null )
			{
				addToMap( pMap, lParent, pConcernNode );
				lParentNode = pMap.get( lParent );
			}
			lNewNode.setParent( lParentNode );
			lParentNode.addChild( lNewNode );
		}
		else
		{
			lNewNode.setParent( pConcernNode );
			pConcernNode.addChild( lNewNode );
		}
	}
	
	/**
	 * Returns the parent of this node.
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 * @param pElement the element
     * @return the parent element, or <code>null</code> if it
     *   has none or if the parent cannot be computed
	 */
    public Object getParent( Object pElement )
    { 
    	if( pElement instanceof IConcernMapperViewNode )
    	{
    		return ((IConcernMapperViewNode)pElement).getParent();
    	}
    	else 
    	{
    		return null;
    	}
    }

    /**
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     * @param pElement the element
     * @return <code>true</code> if the given element has children,
     *  and <code>false</code> if it has no children
     */
    public boolean hasChildren( Object pElement )
    {
        boolean lReturn = false;
        if( pElement instanceof IConcernMapperViewNode )
        {
            lReturn = ((IConcernMapperViewNode)pElement).hasChildren();
        }
        return lReturn;
    }

    /**
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose() {}
    
    /**
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     * @param pViewer the viewer
     * @param pOldInput the old input element, or <code>null</code> if the viewer
     *   did not previously have an input
     * @param pNewInput the new input element, or <code>null</code> if the viewer
     *   does not have an input
     */
    public void inputChanged(Viewer pViewer, Object pOldInput, Object pNewInput)
    {
    	aInputChanged = true;
    }
}
