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
package easypastry.core;

import java.io.IOException;
import java.net.*;
import java.nio.channels.ServerSocketChannel;

import rice.environment.Environment;
import rice.p2p.commonapi.Endpoint;
import rice.p2p.commonapi.Node;
import rice.p2p.commonapi.NodeHandle;
import rice.pastry.NodeIdFactory;
import rice.pastry.PastryNode;
import rice.pastry.PastryNodeFactory;
import rice.pastry.socket.SocketPastryNodeFactory;
import rice.pastry.standard.RandomNodeIdFactory;

public class PastryConnection {
	
	  // Loads pastry settings
	  private Environment env = new Environment();

	  // the port to begin creating nodes on
	  private int PORT = 5019;

	  // the port on the bootstrap to contact
	  private int BOOTSTRAP_PORT = 5019;

	  // the host to boot the first node off of
	  private String LOCALHOST_HOSTNAME = "localhost";
	  private String BOOTSTRAP_HOST = "localhost";

	  private String hostname;
	  
	  // Reference to the Pastry node
	  private PastryNode node;

	  //boot
	  protected InetSocketAddress bootaddress;

	
	 
	  /**
	   * Creates a node and stablishs the connection with the network
	   * @param String host
	   * @param int port   
	   *
	   */
	  public PastryConnection(String bootHost, int bootPort) {
		  
		  // ----- ATTEMPT TO LOAD LOCAL HOSTNAME -----
		  try {
		      LOCALHOST_HOSTNAME = InetAddress.getLocalHost().getHostName();		      
		    } catch (UnknownHostException e) {
		    }
		  
	    try {
			createNode(bootHost,bootPort);			
		} catch (Exception e) {
			e.printStackTrace();
		}
	  }
	  
	  /**
	   * This method creates a new node which will serve as the underlying p2p layer
	   *
	   * @param args Constructor arguments: 0 - String bootstrap host; 1 - int bootstrap port; 2 - PastryNodeFactory factory; 3 - int protocol
	 * @throws Exception 
	   */
	  public void createNode (String bootHost, int bootPort) throws Exception {

		// disable the UPnP setting (in case you are testing this on a NATted LAN)
		env.getParameters().setString("nat_search_policy","never");

		    
	    if (!bootHost.equalsIgnoreCase ("auto")) {
	      BOOTSTRAP_HOST = bootHost;
	    }
	    BOOTSTRAP_PORT = bootPort;
	    PORT = bootPort;
	 
	    //  Generate the NodeIds Randomly
	    NodeIdFactory nidFactory = new RandomNodeIdFactory(env);

	    PastryNodeFactory factory = null;
	        
	    int newPort = changeBoundPort(BOOTSTRAP_HOST, BOOTSTRAP_PORT);
	    
	    while(node==null) {
	    	
	      //construct the PastryNodeFactory, this is how we use rice.pastry.socket
	      factory = new SocketPastryNodeFactory(nidFactory, newPort, env);
	      
	      // Get bootstrap reference
	      bootaddress = new InetSocketAddress (BOOTSTRAP_HOST, BOOTSTRAP_PORT);     

	      try {
	    	
	    	// construct a node, but this does not cause it to boot
	      	node = factory.newNode();	    	
	      } catch(Exception e) {
	    	newPort+=100;  
	        System.out.println ("Port " + (newPort - 100) + " already bound. Trying " + newPort + "...");
	      }	      
	    }    
	  }
	  

	  /**
	   * This method boots node previously created
	   * 
	 * @throws Exception 
	   */
	  public void bootNode () throws Exception {
	    
	    node.boot(bootaddress);
	   
	    // the node may require sending several messages to fully boot into the ring
	    synchronized(node) {
	       while(!node.isReady() && !node.joinFailed()) {
	       // delay so we don't busy-wait
	       node.wait(500);
	            
	       // abort if can't join
	       if (node.joinFailed()) {
	          throw new IOException("Could not join the FreePastry ring.  Reason:"+node.joinFailedReason()); 
	       }
	     }       
	    }
	    
	    System.out.println("Finished creating new node "+node);
	    
	    // wait 10 seconds
	    env.getTimeSource().sleep(3000);
	  } 

	  
	  private int changeBoundPort(String boothost, int bootport) throws IOException{

		  int newPort = bootport;
		  		  
		// If working in remote mode, check if port is already bound 
	      while (true) {
	    	ServerSocketChannel channel = null;        
			try {
	    	    // Create a new non-blocking server socket channel
	    		channel = ServerSocketChannel.open();
	    		channel.configureBlocking(false);	    		    		
	    		InetSocketAddress isa = new InetSocketAddress(boothost, newPort);    		
	    		channel.socket().bind(isa);
	    		
	          channel.socket().close();
	          channel.close();
	          break;
	        } catch (BindException e) {
	          if (e.getMessage().contains("Address already in use")) {
	            newPort += 100;
	            System.out.println ("Port " + (newPort - 100) + " already bound. Trying " + newPort + "...");
	          }
	          else break;
	        }
	        finally {	          
	          try {
	        	channel.socket().close();
	            channel.close();
	          }
	          catch (IOException ex) {}
	        }
	      }      
	      if (bootport!=newPort) System.out.println("Port changed: "+bootport+" --> "+newPort);
	      
		    return newPort;
	     }

	public Node getNode() {
		return node;
	}
	
	public boolean isAlive(NodeHandle nh) {		
		return node.isAlive((rice.pastry.NodeHandle) nh);
	}
	
	public int proximity(NodeHandle nh) {		
		return node.proximity((rice.pastry.NodeHandle) nh);
	}
	
	public String getHostname() {
		return hostname;
	}
	
	public Environment getEnvironment() {
		return env;
	}
	

	public void close() {
		node.destroy();		
	}

	/******************* Environment **********************/
	
	public double random() {
		return env.getRandomSource().nextDouble();
	}
	
	public long getTime() {
	  return env.getTimeSource().currentTimeMillis();    	  
	}
	  
	public void sleep(long millis) {
	  try {
		env.getTimeSource().sleep(millis);
	  } catch (InterruptedException e) {
		e.printStackTrace();
	  }	  
	}


	
}