package easypastry.utest;

import java.util.Vector;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import easypastry.core.PastryConnection;
import easypastry.core.PastryKernel;
import easypastry.dht.DHTHandler;
import easypastry.util.UID;

public class DHTTestCase extends TestCase {
	
	private PastryConnection conn = null;
	private DHTHandler dht = null;
	
	@Before
	public void setUp() {
		System.out.println("[DHTTest] - Setting >>>>>>>>> UP <<<<<<<<<<<");
		try {
			PastryKernel.init("easypastry-config-test.xml");
			conn = PastryKernel.getPastryConnection();			
			dht = PastryKernel.getDHTHandler("test");
			conn.bootNode();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	@After
	public void tearDown() {
		System.out.println("[DHTTest] - Tearing >>>>>>>>> DOWN <<<<<<<<<<\n");
		conn.close();
	}
	
	@Test
	public void testRecovery() {

		System.out.println("Testing put and get : ");		 
	    
		Vector<String> keys = new Vector<String>();
	      
	    for(int i=0;i<10;i++) {
	    	
	      try {
	          Thread.sleep(100);
	          String key = UID.getUID();
	          dht.put(key,"value "+i);
	          keys.add(key);
	          
	          System.out.println("Insert key "+key+", value "+i);
	           
	        } catch (Exception ex) {
	          ex.printStackTrace();
	        }
	    }
	    
	    int lookups = 0;
	    for (String key : keys) {
	    	
	    	try {
	    	  String value = (String) dht.get(key);
	    	  
	    	  System.out.println("Recovering key "+key+" -> "+value);
	    	} catch(Exception e) {
	    	  lookups--;
	    	}  
	    	lookups++;
	    }
	      		
	    assertEquals (keys.size(), lookups);
	}	

}
