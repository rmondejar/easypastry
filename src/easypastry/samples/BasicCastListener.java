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
import easypastry.cast.CastContent;
import easypastry.cast.CastListener;

public class BasicCastListener implements CastListener {

	@Override
	public boolean contentAnycasting(CastContent content) {
		System.out.println("Anycasting : "+content);
		return true;
	}

	@Override
	public void contentDelivery(CastContent content) {
		System.out.println("Delivery : "+content);

	}

	@Override
	public void hostUpdate(NodeHandle nh, boolean joined) {
		if (joined) System.out.println("Node join : "+nh);
		else System.out.println("Node leave : "+nh);
	}

}
