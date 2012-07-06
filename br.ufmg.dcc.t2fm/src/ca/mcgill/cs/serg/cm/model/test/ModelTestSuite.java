/* ConcernMapper - A concern modeling plug-in for Eclipse
 * Copyright (C) 2006  McGill University (http://www.cs.mcgill.ca/~martin/cm)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Revision: 1.6 $
 */

package ca.mcgill.cs.serg.cm.model.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test suite for the model package.
 */
public class ModelTestSuite extends TestSuite 
{
	/**
	 * @return A test suite for the concern model package.
	 */
	public static Test suite()
	{
      TestSuite lSuite = new TestSuite( "Test suite for the model package" );
      lSuite.addTestSuite( TestConcernModel.class );

      return lSuite;
  	}

}
