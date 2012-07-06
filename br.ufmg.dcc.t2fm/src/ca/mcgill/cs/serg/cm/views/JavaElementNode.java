/* ConcernMapper - A concern modeling plug-in for Eclipse
 * Copyright (C) 2006  McGill University (http://www.cs.mcgill.ca/~martin/cm)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Revision: 1.3 $
 */

package ca.mcgill.cs.serg.cm.views;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IJavaElement;

/**
 * A bundle to facilitate the handling of elements in the 
 * ConcernModelContentProvider.
 */
class JavaElementNode extends WrapperNode implements IAdaptable
{
	/**
	 * Creates a new ElementNode.
	 * @param pElement The element wrapped by this element node.
	 */
	public JavaElementNode( IJavaElement pElement )
	{
		super( pElement );
	}
	
	/**
	 * @return The Java element wrapped by this node.
	 */
	public IJavaElement getElement()
	{
		return (IJavaElement)super.getElement();
	}
	
	/**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 * @param pAdapter the adapter class to look up
	 * @return a object castable to the given class, 
	 *    or <code>null</code> if this object does not
	 *    have an adapter for the given class
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter( Class pAdapter ) 
	{
		if( pAdapter == IJavaElement.class )
		{
			return getElement();
		}
		else
		{
			return null;
		}
	}
}
