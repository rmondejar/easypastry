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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import rice.p2p.commonapi.*;
import rice.p2p.commonapi.rawserialization.InputBuffer;
import rice.p2p.commonapi.rawserialization.OutputBuffer;
import rice.p2p.commonapi.rawserialization.RawMessage;

import rice.p2p.util.rawserialization.JavaDeserializer;
import rice.p2p.util.rawserialization.JavaSerializationException;

public class DirectMessage implements RawMessage {
  
  private static final long serialVersionUID = -4187625620200525235L;

  public static final short TYPE = 1;

  // the content of this message
  private CastContent content;
  private NodeHandle source;

  /**
  * Constructor which takes a unique integer Id
   *
   * @param id The unique id
   * @param source The source address
   * @param dest The destination address
   */
  public DirectMessage(NodeHandle source, CastContent content) {
	  this.source = source;
	  this.content = content;     
  }
  
  public String toString() {
	  return "DirectMessage "+TYPE+" source : "+source+" content : "+content;
  }

  public NodeHandle getSource() {
	  return source;
  }

  /**
   * Returns the content
   *
   * @return The content
   */
  public CastContent getContent() {
    return content;
  }


  public short getType() {
    return TYPE;
  }


  @Override
  public int getPriority() {	
	return Message.MEDIUM_HIGH_PRIORITY;
  }

  public void serialize(OutputBuffer buf) throws IOException {
	  
	   try {
	      ByteArrayOutputStream baos = new ByteArrayOutputStream();
	      ObjectOutputStream oos = new ObjectOutputStream(baos);   
	      
	      oos.writeObject(content);
	      oos.writeObject(source);
	      oos.close();
	      
	      byte[] temp = baos.toByteArray();
	      buf.writeInt(temp.length);
	      buf.write(temp, 0, temp.length);
	      	      
	   } catch (IOException ioe) {
		  ioe.printStackTrace();
	      throw new JavaSerializationException(content, ioe);
	    }
	  
     
}

/**
 * Deserializing constructor.  This does the "real" deserialization.
 * 
 * @param buf
 * @param endpoint
 * @throws IOException
 */

  public DirectMessage(InputBuffer buf, Endpoint endpoint) {
    
	try {
    
		int length = buf.readInt();
		
		byte[] array = new byte[length];
	    buf.read(array);
	    
	    ObjectInputStream ois = new JavaDeserializer(new ByteArrayInputStream(array), endpoint);
    
	  try {
	    Object o = ois.readObject();
	    content = (CastContent) o;
	  }catch(Exception e) {
		System.out.println("Unknown content");
	  }	  
	  
	    Object o = ois.readObject();
	    source = (NodeHandle) o;
	    
	    
	}catch(Exception e) {
		e.printStackTrace();
	}
  	
  }

}