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
package easypastry.cast;

import java.util.Set;

import rice.p2p.commonapi.NodeHandle;

public class UpdateDeliver extends Thread {
	
	private Set<CastListener> updaters;
	private NodeHandle handle;
	private boolean joined;

	public UpdateDeliver(Set<CastListener> updaters,
			NodeHandle handle, boolean joined) {
	
		this.updaters = updaters;
		this.handle = handle;
		this.joined = joined;
	}

	public void run() {		
						 
		for(CastListener dsl : updaters) {
		  dsl.hostUpdate(handle, joined);
		}
		
	}
}
