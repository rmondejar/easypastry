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

/**
 * Damon Context constants
 * @author Rubén Mondéjar <ruben.mondejar@urv.cat>
 */
	
public interface Context {
	
	//props
	public static final String HOST  = "host";
	public static final String PORT  = "port";
	public static final String DHT_IMPL  = "dht_impl";
	public static final String DHT_CONFIG  = "dht_config";	
	
	public static final String REGISTRY = "REGISTRY";	
		
	public static final int TIMEOUT = 50;
	
	public static final int DELAY = 100;	
	
	
	
	

}
