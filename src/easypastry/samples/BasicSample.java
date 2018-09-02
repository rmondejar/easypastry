/*****************************************************************************************
 * EasyPastry
 * Copyright (C) 2008 Ruben Mondejar
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *****************************************************************************************/
package easypastry.samples;

import easypastry.cast.CastHandler;
import easypastry.core.PastryConnection;
import easypastry.core.PastryKernel;
import easypastry.dht.DHTException;
import easypastry.dht.DHTHandler;

public class BasicSample {

	protected PastryConnection conn;
	protected DHTHandler dht;
	protected CastHandler cast;
	
	public void initKBR(String filename) throws Exception {
		PastryKernel.init(filename);
		conn = PastryKernel.getPastryConnection();
	}
	
	public void initDHT(String context) throws DHTException {
		dht = PastryKernel.getDHTHandler(context);
	}

	public void initCast(String subject) {		
		cast = PastryKernel.getCastHandler();
		cast.subscribe(subject);
		cast.addDeliverListener(subject, new BasicCastListener());		
	}
	
	public void start() throws Exception {
	    conn.bootNode();	
	}
	
	public DHTHandler getDHTHandler() {
		return dht;
	}
	
	public CastHandler getCastHandler() {
		return cast;
	}
	
	private void testDHT() throws DHTException {
		
		DHTHandler dht = getDHTHandler();
		System.out.println("DHT | Inserting : <key1, value1>");
		dht.put("key1", "value1");
		System.out.println("DHT | Inserting : <key1, value2>");
		dht.put("key1", "value2");
		System.out.println("DHT | Retrieving : <key1>");
		String value = (String) dht.get("key1");
		System.out.println("DHT | Current value of <key1> : <"+value+">");		
	}
	
	private void testCast(String subject) {
	
		CastHandler cast = getCastHandler();
		System.out.println("Cast | Direct : text message 1");
		cast.sendDirect(cast.getLocalNodeHandle(), new BasicCastContent(subject, "text message 1"));
		System.out.println("Cast | Muticast : text message 2");
		cast.sendMulticast("p2p://test", new BasicCastContent(subject, "text message 2"));	
				
	}
	
	public static void main(String[] args) {
	  try {		  
	  
		BasicSample app = new BasicSample();
		app.initKBR("easypastry-config.xml");
		app.initDHT("test");
		app.initCast("p2p://test");
		app.start();
		
        app.testDHT();
		
		app.testCast("p2p://test");		
		
		System.exit(0);
	  }
	  catch(Exception ex) {
		  ex.printStackTrace();
	  }

	}
	
}
