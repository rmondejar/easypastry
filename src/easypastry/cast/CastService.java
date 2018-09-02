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

import java.util.Collection;

import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.IdRange;
import rice.p2p.commonapi.NodeHandle;

public interface CastService {

	public abstract void close();
	
	/**
	 * Subscribes to id.
	 */
	public void subscribe(String subject);
	
	/**
	 * Unsubscribes to id.
	 */
	public void unsubscribe(String subject);

	/**
	 * Sends a direct message.
	 */
	public void sendDirect(NodeHandle destNH, CastContent content);

	/**
	 * Sends a hopped message.
	 */
	public void sendHopped(Id key, CastContent content);

	/**
	 * Sends an anycast message.
	 */
	public void sendAnycast(String subject, CastContent content);

	/**
	 * Sends a manycast message.
	 */
	public void sendManycast(String subject, CastContent content,
			int num);

	/**
	 * Sends a multicast message.
	 */
	public void sendMulticast(String subject, CastContent content);

	/****************** Listeners & Filters ****************************/

	public void addDeliverListener(String id, CastListener dsl);

	public void removeDeliverListener(String id);

	public void addForwardFilter(String id, String name, CastFilter dsf);

	public void removeForwardFiter(String id, String name);
	
	public Collection<CastFilter> getForwardFilters(String id);

	/************ Some passthrough accessors *************/

	public boolean isRoot(String subject);

	public NodeHandle getParent(String subject);

	public Collection<NodeHandle> getChildren(String subject);
	
	public NodeHandle getLocalNodeHandle();
	
	public Collection<NodeHandle> getNeighbours(int num, boolean ordered);
	
	public Collection<NodeHandle> getReplicaSet(Id id, int num);

	public IdRange getRange(NodeHandle handle, int rank, Id lkey);

}