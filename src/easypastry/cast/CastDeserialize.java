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

import rice.p2p.commonapi.Endpoint;
import rice.p2p.commonapi.Message;
import rice.p2p.commonapi.NodeHandle;
import rice.p2p.commonapi.rawserialization.InputBuffer;
import rice.p2p.commonapi.rawserialization.MessageDeserializer;

import java.io.IOException;

public class CastDeserialize implements MessageDeserializer {

	private Endpoint endpoint;
	
	public CastDeserialize(Endpoint endpoint) {
		this.endpoint = endpoint;
	}
	
	public Message deserialize(InputBuffer buf, short type, int priority, NodeHandle sender) throws IOException {

        switch (type) {
            case 1:
        		return new DirectMessage(buf,endpoint);     
            
            case 2:
        		return new HoppedMessage(buf,endpoint);     
            

        }
		return null;    
}

	
}
