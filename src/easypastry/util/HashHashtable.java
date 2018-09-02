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
package easypastry.util;

import java.util.Hashtable;

public class HashHashtable<K,N,V> extends Hashtable<K, Hashtable<N,V>> {
	
	private static final long serialVersionUID = -1633487045054321656L;

	public synchronized void put(K key, N name, V value) {
		 
	    Hashtable<N,V> htn = super.get(key);
		if (htn==null) {		  		  
		  htn = new Hashtable<N,V>();	      	
		}
		htn.put(name,value);
		super.put(key, htn);
	}
	
	public V get(K key, N name) {
		 
	    Hashtable<N,V> htn = super.get(key);
		if (htn!=null) {		  		  
		  return htn.get(name);	      	
		}
		else return null;	  	
	}
	
	public V remove(K key, N name) {
		
	    Hashtable<N,V> htn = super.get(key);
		if (htn!=null) {		  		  
		  return htn.remove(name);	      	
		}
		else return null;	  
	}
	
}
