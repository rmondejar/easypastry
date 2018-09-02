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

import java.io.Serializable;
import java.util.Collection;
import java.util.Hashtable;

import easypastry.util.Context;

import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.NodeHandle;

import bunshin.Bunshin;
import bunshin.listeners.BunshinMergeClient;
import bunshin.util.Utilities;

public class BunshinDHTHandler implements DHTHandler {

	private Bunshin bunshinApp;
	private String context;	
	
	public static Hashtable<String,Boolean> lookupResultArrived = new Hashtable<String,Boolean>();
	public static Hashtable<String,Boolean> insertAckArrived = new Hashtable<String,Boolean>();

	public void setBunshinApp(Bunshin bunshinApp) {
		this.bunshinApp = bunshinApp;		
	}
	
	public void setContext(String context, BunshinMergeClient bmc) {
		this.context = context;
		bunshinApp.createContext(context, bmc);
	}
			
	public Serializable get(String key) throws DHTException {

		Serializable value = null;
		boolean isNull = false;
		boolean isTimeOut = false;
		
		Id id = Utilities.generateHash(key);
		String code = id.toStringFull() + System.currentTimeMillis();

		BunshinGetClient client = new BunshinGetClient(code);

		bunshinApp.get(context, id, client);

		try {
		Thread.sleep(1000);
		} catch(Exception e) {}
		
		int timeout = 0;

		synchronized (client) {
			
			while ((!lookupResultArrived.containsKey(code)) && timeout < Context.TIMEOUT) {
				try {
					Thread.sleep(Context.DELAY);
				} catch (InterruptedException ex) {
				}
				timeout++;
			}
			
			value = client.getValue();
			if (lookupResultArrived.containsKey(code)) {
				isNull = !lookupResultArrived.remove(code);
				isTimeOut = false;
			}
			else {
				isNull = false;
				isTimeOut = true;
			}
		}

		if (isTimeOut)
			throw new DHTException("Error in context ("+context+"), the ("+code+") is not bound for the key (" + key+")");

		return value;

	}

	@Override
	public void put(String key, Serializable value) throws DHTException {
		
		boolean result;
		
		Id id = Utilities.generateHash(key);
		String code = id.toStringFull() + System.currentTimeMillis();
		BunshinPutClient client = new BunshinPutClient(code);

		bunshinApp.put(context, id, value, client);

		try {
			Thread.sleep(1000);
			} catch(Exception e) {}
			
			int timeout = 0;

			synchronized (client) {
				
				while ((!insertAckArrived.containsKey(code)) && timeout < Context.TIMEOUT) {
					try {
						Thread.sleep(Context.DELAY);
						//System.out.print(".");
					} catch (InterruptedException ex) {
					}
					timeout++;
				}
				if (insertAckArrived.containsKey(code)) result = insertAckArrived.remove(code);
				else result = true;
			}

		if (timeout>=10)
			throw new DHTException("Insertion ack for ("+key+","+value+") does not arrive");

		else if (!result)
			throw new DHTException("Negative value in insertion ack for ("+key+","+value+")");

	}

	@Override
	public void remove(String key) {
		
		Id id = Utilities.generateHash(key);
		
		bunshinApp.remove(context, id);
	}
	
	@Override
	public Hashtable<String, Integer> getState() {
		Hashtable<String, Integer> state = new Hashtable<String, Integer>();
		Collection<String> c = bunshinApp.getStorageManager().getContexts();
		for (String context : c) {
			state.put(context, bunshinApp.getStorageManager().getContextSize(context));
		}
		return state;
	}

	private class BunshinPutClient implements
			bunshin.listeners.BunshinPutClient {

		private String code;
		
		public BunshinPutClient(String code) {
			this.code = code;
		}
		
		public void put(boolean result, NodeHandle source) {
			synchronized (this) {				
				BunshinDHTHandler.insertAckArrived.put(code,result);				
			}
		}
	}

	private class BunshinGetClient implements
			bunshin.listeners.BunshinGetClient {

		private String code;
		private Serializable value;
		
		public BunshinGetClient(String code) {
			this.code = code;
		}

		public void get(Serializable result) {
		   
			synchronized (this) {
				BunshinDHTHandler.lookupResultArrived.put(code,(result!=null));
				value = result;
					

				try { Thread.sleep(300); } catch(Exception e){} 			
			}
		}
		
		public Serializable getValue() {
			return value;
		}
	}

}
