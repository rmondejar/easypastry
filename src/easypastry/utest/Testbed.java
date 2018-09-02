package easypastry.utest;

import junit.framework.Test;
import junit.framework.TestSuite;

public class Testbed {

	public static Test suite() {
		TestSuite suite = new TestSuite(Testbed.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(DHTTestCase.class);
		suite.addTestSuite(CastTestCase.class);
		//$JUnit-END$
		return suite;
	}

}
