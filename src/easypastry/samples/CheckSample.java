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

import rice.p2p.commonapi.NodeHandle;

public class CheckSample extends BasicSample {
	
  private void testNodes() throws Exception {
		
		String[] nodeNames = new String[] { "node1" , "node2" };
		
		System.out.println ("Test Node Start");
		
		NodeHandle localNH = cast.getLocalNodeHandle();
	
		int numNode = 0;
		try {
			NodeHandle nh = (NodeHandle) dht.get( nodeNames[0] );
			if (nh==null) numNode = 0;
			else numNode = 1;
		} catch(Exception e) {
			numNode = 0;
		}
		
		System.out.println("PUT NH ("+localNH+") of "+nodeNames[numNode]);
		dht.put(nodeNames[numNode], localNH);        
		
		System.out.println("WAITING .. press any key to continue");
		System.in.read();
		
		for (int i=0 ; i<nodeNames.length ; i++){

			NodeHandle nh = (NodeHandle) getDHTHandler().get( nodeNames[i] );
						
			System.out.println ( "Trying " + nodeNames[i] + "..." + nh);
			
			if ( nh != null ) {			
				
				if (conn.isAlive(nh)) {
					
					System.out.println ( nodeNames[i] + " is online " );

				} else {
					
					System.out.println ( nodeNames[i] + " is offline" );
					
				}
			}
		}
		System.out.println ("END");
	}

    public static void main(String[] args) {
	  try {		  
	  
		CheckSample sample = new CheckSample();
		sample.initKBR("easypastry-config.xml");
		sample.initDHT("test");
		sample.initCast("p2p://test");
		sample.start();
		
		sample.testNodes();
		
		System.exit(0);
	
	  }
	  catch(Exception ex) {
		  ex.printStackTrace();
	  }

	}

}
