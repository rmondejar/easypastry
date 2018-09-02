package easypastry.utest;

import java.util.Properties;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.NodeHandle;

import easypastry.cast.CastContent;
import easypastry.cast.CastHandler;
import easypastry.cast.CastListener;
import easypastry.core.PastryConnection;
import easypastry.core.PastryKernel;
import easypastry.util.Context;

public class CastTestCase extends TestCase implements CastListener {
	
	private PastryConnection[] conn;
	private CastHandler[] cast;
	
	
	private final int MAX = 4;
	private final String url = "p2p://test";
	private int num;
	private CastContent content;

	public CastTestCase() {
		cast = new CastHandler[MAX];	
		conn = new PastryConnection[MAX];
	}
	
	@Before
	public void setUp() {
		System.out.println("[CastTest] - Setting >>>>>>>>> UP <<<<<<<<<<<");
		try {
			Properties config = PastryKernel.loadProps("easypastry-config-test.xml");			
			String host = config.getProperty(Context.HOST);
			int port = Integer.parseInt(config.getProperty(Context.PORT));

			for (int i = 0; i < MAX; i++) {				
				conn[i] = new PastryConnection(host, port);
				cast[i] = new CastHandler(conn[i].getNode());
				conn[i].bootNode();
				cast[i].subscribe(url);
				cast[i].addDeliverListener(url, this);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	@After
	public void tearDown() {
		System.out.println("[CastTest] - Tearing >>>>>>>>> DOWN <<<<<<<<<<\n");
		for (int i = 0; i < MAX; i++) {
			try {
				cast[i].close();
				conn[i].close();
				Thread.sleep(100);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Test
	public void testDirect() {

		System.out.print("Testing direct : ");
		num = 0;
		
		content = new CastContent(url);		
		cast[0].sendDirect(cast[MAX-1].getLocalNodeHandle(),content); 
		sleep(3000);
		
		if (num==1) System.out.println("OK");
		else System.out.println("ERROR");
		assertEquals (1, num);
	}	
	
	@Test
	public void testHopped() {	
		System.out.print("Testing hopped :");
		num = 0;

		content = new CastContent(url);		
		
		Id id = easypastry.util.Utilities.generateHash("p2p://random");
		cast[0].sendHopped(id,content);
		sleep(3000);
		
		if (num==1) System.out.println("OK");
		else System.out.println("ERROR");
		assertEquals (1, num);
	}
	
	@Test
	public void testAny() {	
		System.out.print("Testing any : ");
		num = 0;
		
		content = new CastContent(url);		
		
		cast[0].sendAnycast(url,content);
		sleep(3000);
		
		if (num==1) System.out.println("OK");
		else System.out.println("ERROR");
		assertEquals (1, num);
	}	

	@Test
	public void testMany() {	
		
		System.out.print("Testing many : ");
		num = 0;
		
		content = new CastContent(url);
		
		cast[0].sendManycast(url,content,2);
		sleep(6000);
	
		if (num==2) System.out.println("OK");
		else System.out.println("ERROR");
		assertEquals (2, num);
	}	
	
	@Test
	public void testMulti() {
		
		System.out.print("Testing multi : ");
		num = 0;
		content = new CastContent(url);
		
		cast[0].sendMulticast(url, content);
		
		sleep(2000*MAX);	
		if (num==MAX) System.out.println("OK");
		else System.out.println("ERROR");
		assertEquals (MAX, num);
	}


	@Override
	public boolean contentAnycasting(CastContent content) {
		return true;
	}

	@Override
	public void contentDelivery(CastContent content) {
		num++;
	}

	@Override
	public void hostUpdate(NodeHandle nh, boolean joined) {
	}
	
	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch(Exception e) {}
	}


}
