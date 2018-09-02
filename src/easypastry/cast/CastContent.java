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

import java.util.Vector;

import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.NodeHandle;
import rice.p2p.scribe.ScribeContent;

public class CastContent implements ScribeContent {

  private static final long serialVersionUID = -8157412604280393531L;

  protected String subject = "#default";
  protected int num = 0;
  
  private NodeHandle source; 
  private Vector<NodeHandle> path = new Vector<NodeHandle>();  
  
  private Id key;
   
  public CastContent(String subject) {
	  this.subject = subject;
  }
	  
  public void setSubject(String subject) {
	   this.subject = subject;
  }
  
  public void setSource(NodeHandle source) {
	this.source = source;
  }  
  
  public void setKey(Id key) {
	  this.key = key;
  }
  
  public void addHost(NodeHandle nh) {
	path.add(nh);
  }
  
  public NodeHandle getSource() {
	  return source;
  }
  
  public int getNumOfHops() {
	  return path.size();
  }
  
  public String getSubject() {
	  return subject;
  }
  
  public Vector<NodeHandle> getPath() {
	  return path;
  }
  
  public int getNum() {
	  return num;
  }

  public Id getKey() {	
	return key;  
  }

  public void setNum(int num) {
	this.num = num;	
  }
  
}
