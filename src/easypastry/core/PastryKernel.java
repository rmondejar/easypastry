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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import rice.p2p.commonapi.Node;
import rice.p2p.commonapi.NodeHandle;

import easypastry.cast.CastHandler;
import easypastry.dht.DHTException;
import easypastry.dht.DHTHandler;
import easypastry.dht.DHTHandlerFactory;
import easypastry.util.Context;

public class PastryKernel {
	
	//dht data
	protected static String hostname;
	protected static PastryConnection conn;
	protected static Node node;	
	protected static String dhtName;
	protected static Properties dhtProps;
	protected static CastHandler cast;
		
	public static void init(String configPath) throws Exception {
		init("",-1,configPath);
	}
	
	public static void init(String bhost, String configPath) throws Exception {
		init(bhost,-1,configPath);
	}		

	public static void init(String bhost, int bport, String configPath) throws Exception {
		
		// load config
		Properties config = null;
		try {
		 config = loadProps(configPath);
		} catch(Exception e) {
		  throw new Exception ("Config file not found : "+configPath, e);
		}

		// init network
		String host = bhost;
		if (host.length()<1) host=  config.getProperty(Context.HOST);
		
		int port = bport; 
		if (port<1) port = Integer.parseInt(config.getProperty(Context.PORT));
		
		conn = new PastryConnection(host, port);
		hostname = conn.getHostname();
		node = conn.getNode();
		
		// init cast service
		cast = new CastHandler(node);

		// init dht layer
		int end = configPath.lastIndexOf(File.separator);
		String folderPath = "";
		if (end>0)  folderPath = configPath.substring(0, end) + File.separator;
						
		dhtName = config.getProperty(Context.DHT_IMPL);
		String dhtPath = folderPath +config.getProperty(Context.DHT_CONFIG);
		System.out.println("loading DHT : "+dhtPath);
		dhtProps = loadProps(dhtPath);

	}
	
	public static Properties loadProps(String path) throws IOException {
		FileInputStream fis = new FileInputStream(path);
		String ext = path.substring(path.lastIndexOf('.')+1);
		Properties prop = new Properties();
		if (ext.equals("xml")) {
			prop.loadFromXML(fis);
		}
		else prop.load(fis);
		fis.close();
		return prop;
	}
	
	public static String getHostName() {
		return hostname;
	}

	
	public static DHTHandler getDHTHandler(String dhtPropsName, String context) throws DHTException, IOException {
		Properties props =  loadProps(dhtPropsName);
		return DHTHandlerFactory.createDHTHandler(node,dhtName,props,context);
	}
	
	public static DHTHandler getDHTHandler(Node node, String context) throws DHTException {
		return DHTHandlerFactory.createDHTHandler(node,dhtName,dhtProps,context);
	}
	
	public static DHTHandler getDHTHandler(String context) throws DHTException {
		return DHTHandlerFactory.createDHTHandler(node,dhtName,dhtProps,context);
	}
	
	public static DHTHandler getDHTHandler(String dhtPropsName, String context, Object mergeClient) throws DHTException, IOException {
		Properties props =  loadProps(dhtPropsName);
		return DHTHandlerFactory.createDHTHandler(node,dhtName,props,context, mergeClient,false);
	}
	
	public static DHTHandler getDHTHandler(String context, Object mergeClient) throws DHTException {
		return DHTHandlerFactory.createDHTHandler(node,dhtName,dhtProps,context, mergeClient,false);
	}
	
	public static PastryConnection getPastryConnection() {
		return conn;
	}


	
	public static CastHandler getCastHandler() {
		return cast;
	}

	public static NodeHandle getNodeHandle() {		
		return node.getLocalNodeHandle();
	}
	
	public static void close() {
		conn.close();
	}

	
	
}
