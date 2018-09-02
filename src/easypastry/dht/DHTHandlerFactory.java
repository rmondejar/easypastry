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
package easypastry.dht;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import rice.p2p.commonapi.Application;
import rice.p2p.commonapi.Endpoint;
import rice.p2p.commonapi.Node;

import bunshin.Bunshin;
import bunshin.BunshinImpl;
import bunshin.listeners.BunshinMergeClient;
import bunshin.messaging.BunshinDeserialize;
import bunshin.storage.StorageManager;

public class DHTHandlerFactory {

	private static Bunshin bunshinApp;

	public static DHTHandler createDHTHandler(Node node,
			String dhtName, Properties dhtProps, String dhtContext)
			throws DHTException {
		
		return createDHTHandler(node,dhtName,dhtProps,dhtContext,null, false);
	}
	
	public static DHTHandler createDHTHandler(Node node,
			String dhtName, Properties dhtProps, String dhtContext, Object mergeClient, boolean test)
			throws DHTException {

		DHTHandler dht = null;

		if (dhtName.toLowerCase().equals("bunshin")) {
			try {
							
				if (!test) dht = createBunshinDHTHandlerSingleton(node, dhtProps, dhtContext, (BunshinMergeClient) mergeClient);
				else dht = createBunshinDHTHandlerTest(node, dhtProps, dhtContext, (BunshinMergeClient) mergeClient);
			} catch (Exception e) {
				e.printStackTrace();
				throw new DHTException("Error creating a new " + dhtName + " DHT instance");
			}		
		} else
			throw new DHTException("DHT " + dhtName + " is not recognized currently");

		return dht;
	}


	private static BunshinDHTHandler createBunshinDHTHandlerSingleton(
			Node node, Properties props, String context, BunshinMergeClient mergeClient)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, UnknownHostException {

		if (bunshinApp == null) {
			bunshinApp = initBunshin(node, props);
		}

		BunshinDHTHandler bph = new BunshinDHTHandler();
		bph.setBunshinApp(bunshinApp);
		bph.setContext(context, mergeClient);
		return bph;
	}
	
	private static BunshinDHTHandler createBunshinDHTHandlerTest(
			Node node, Properties props, String context, BunshinMergeClient mergeClient)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, UnknownHostException {

		Bunshin bunshinTestApp = initBunshin(node, props);

		BunshinDHTHandler bph = new BunshinDHTHandler();
		bph.setBunshinApp(bunshinTestApp);
		bph.setContext(context, mergeClient);
			
		return bph;
	}

	private static Bunshin initBunshin(Node node, Properties props)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, UnknownHostException {

		String id = (String) props.get(bunshin.Context.ID_APPLICATION);

		StorageManager manager = (StorageManager) Class.forName(
				(String) props.get(bunshin.Context.STORAGE_MANAGER))
				.newInstance();
		
		String storage_dir = (String) props.get("BUNSHIN_STORAGE_ROOT_DIR");
		if (storage_dir==null) storage_dir = ".";
		storage_dir += File.separator+InetAddress.getLocalHost().getHostName().replace(':', '_') + File.separatorChar;
		props.put("BUNSHIN_STORAGE_ROOT_DIR", storage_dir);
				
		manager.init(props);

		int replicaFactor = Integer.parseInt((String) props
				.get(bunshin.Context.REPLICA_FACTOR));
		boolean cache = bunshin.Context.TRUE.equals((String) props
				.get(bunshin.Context.CACHE));
		boolean debug = bunshin.Context.TRUE.equals((String) props
				.get(bunshin.Context.DEBUG));

		BunshinDeserialize bd = new BunshinDeserialize();
		bunshinApp = new BunshinImpl(id, bd);
		bunshinApp.setStorageManager(manager);
		bunshinApp.setReplicationFactor(replicaFactor);
		if (cache)
			bunshinApp.activateCache();
		if (debug)
			bunshinApp.activateDebug();

		// We are only going to use one instance of this application on each
		// PastryNode
		Endpoint endPoint = node.buildEndpoint((Application) bunshinApp, id);
		bunshinApp.setEndPoint(endPoint);
		endPoint.setDeserializer(bd);

		// now we can receive messages
		endPoint.register();
		
		return bunshinApp;
	}

}
