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
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

import easypastry.util.HashHashtable;

import rice.p2p.commonapi.*;
import rice.p2p.scribe.Scribe;
import rice.p2p.scribe.ScribeMultiClient;
import rice.p2p.scribe.ScribeContent;
import rice.p2p.scribe.ScribeImpl;
import rice.p2p.scribe.Topic;
import rice.pastry.commonapi.PastryIdFactory;

public class CastHandler implements ScribeMultiClient, Application, CastService {
  
  private Node node;
  private Scribe scribe;
  private Application app;
  protected Endpoint endpoint;
  
  private Hashtable<String,CastListener> listeners = new Hashtable<String,CastListener>();
  private Set<CastListener> updaters = new HashSet<CastListener>();
  private HashHashtable<String,String,CastFilter> filters = new HashHashtable<String,String, CastFilter>();
  
  private CastDeserialize castDes;

  /**
   * The constructor for this scribe client.  
   * It will construct the ScribeApplication.
   * 
   * @param node the PastryNode
   */
  public CastHandler(Node node) {

	this.node = node;
	
    this.endpoint = node.buildEndpoint(this, "easyPastryInstance");

    ScribeImpl si = new ScribeImpl(node,"easyPastryScribeInstance");    
    this.scribe = si;
    this.app = si;
    
    castDes = new CastDeserialize(endpoint);    
    this.endpoint.setDeserializer(castDes);
    
    this.endpoint.register();
    
  }
  
  /* (non-Javadoc)
 * @see damon.core.kbr.cast.CastService#subscribe(java.lang.String)
 */
  public void subscribe(String subject) {
	  
	Topic topic = new Topic(new PastryIdFactory(node.getEnvironment()), subject); 

    scribe.subscribe(topic, this, null, null); 
  }
  
  public void unsubscribe(String subject) {
	  
		Topic topic = new Topic(new PastryIdFactory(node.getEnvironment()), subject); 

	    scribe.unsubscribe(topic, this); 
  }
  
  /* (non-Javadoc)
 * @see damon.core.kbr.cast.CastService#sendDirect(rice.p2p.commonapi.NodeHandle, damon.core.kbr.cast.CastContent)
 */
  public void sendDirect(NodeHandle destNH, CastContent content) {
	  
	if (content.getSource()==null) content.setSource(endpoint.getLocalNodeHandle());
	DirectMessage msg = new DirectMessage(endpoint.getLocalNodeHandle(), content);
	
 	endpoint.route(null, msg, destNH);
  }
  
  /* (non-Javadoc)
 * @see damon.core.kbr.cast.CastService#sendHopped(java.lang.String, damon.core.kbr.cast.CastContent)
 */
  public void sendHopped(Id id, CastContent content) {
	if (content.getSource()==null) content.setSource(endpoint.getLocalNodeHandle());
	content.setKey(id);
	endpoint.route(id, new HoppedMessage(id, content), null); 
  }
  
  private void resendHopped(CastContent content) {	   
	  endpoint.route(content.getKey(), new HoppedMessage(content.getKey(), content), null);
  }
  
  private boolean isOwner(Id id) {
	  	   
	  NodeHandleSet set = null;
	  try {
	    set = endpoint.replicaSet(id, 1);
	  } catch (Exception e) {}

	  if (set==null) return false;
	  else {			
			Id x = endpoint.getId();
			NodeHandle nh = set.getHandle(0);
			if (nh==null) 	{
				return false;
			}
			return nh.getId().equals(x);
		}  
  }
  
  /* (non-Javadoc)
 * @see damon.core.kbr.cast.CastService#sendAnycast(java.lang.String, damon.core.kbr.cast.CastContent)
 */
  public void sendAnycast(String subject, CastContent content) {
	if (content.getSource()==null) content.setSource(endpoint.getLocalNodeHandle());
	content.setSubject(subject);
	Topic topic = new Topic(new PastryIdFactory(node.getEnvironment()), subject);  
    scribe.anycast(topic, content); 
  }
  
  private void resendAnycast(CastContent content) {			
		Topic topic = new Topic(new PastryIdFactory(node.getEnvironment()), content.getSubject());  
	    scribe.anycast(topic, content); 
  }
  
  
  
  /* (non-Javadoc)
 * @see damon.core.kbr.cast.CastService#sendManycast(java.lang.String, damon.core.kbr.cast.CastContent, int)
 */
  public void sendManycast(String subject, CastContent content, int num) {
	if (content.getSource()==null) content.setSource(endpoint.getLocalNodeHandle());
	content.setNum(num);
	content.setSubject(subject);
	Topic topic = new Topic(new PastryIdFactory(node.getEnvironment()), subject);  
    scribe.anycast(topic, content); 
  }
  
  /* (non-Javadoc)
 * @see damon.core.kbr.cast.CastService#sendMulticast(java.lang.String, damon.core.kbr.cast.CastContent)
 */
  public void sendMulticast(String subject, CastContent content) {
	  
	if (content.getSource()==null) content.setSource(endpoint.getLocalNodeHandle());
	content.setSubject(subject);
	Topic topic = new Topic(new PastryIdFactory(node.getEnvironment()), subject);

    scribe.publish(topic, content); 
  }

  /**
   * Called when we receive an anycast.  If we return
   * false, it will be delivered elsewhere.  Returning true
   * stops the message here.
   */
  public boolean anycast(Topic topic, ScribeContent content) {
	
	CastContent cc = (CastContent) content;
	
	if (cc.getSource().equals(endpoint.getLocalNodeHandle())) {

		return false;
	}
	else if (cc.getPath().contains(endpoint.getLocalNodeHandle())) {

		return false;
	}
	
    boolean stop = true;    
	for(CastListener dsl : listeners.values()) {
	  	stop &= dsl.contentAnycasting(cc);
	}
	
	//Manycast?
	if (stop) {
	  cc.addHost(endpoint.getLocalNodeHandle());	
	  if (cc.getNum()>0) {  
	    int num = cc.getNumOfHops();

	    if (num==cc.getNum()) {
	      deliver(topic,content);
	      return true;	  
	    }
	    else {
	      deliver(topic,content);
		  resendAnycast(cc);
		  return true;
	    }
	  }
	  else {
		  deliver(topic,content);
		  return true;
	  }	   
	}   
	else 
	  return false;
    
  }
  
  /**
   * Called whenever we receive a published message.
   */
  public void deliver(Topic topic, ScribeContent content) {	
       
    CastContent cc = (CastContent) content;
    
    CastDeliver deliver = new CastDeliver(listeners,cc);
    deliver.start();
  }
  
  /****************************** Application ********************************************/

  public boolean forward(RouteMessage message) { 
	  
	Message msg = null;
	
	try {
		msg = message.getMessage(endpoint.getDeserializer());

	    if (msg instanceof DirectMessage) {
	    		    	
	    	return true;
	    }
	    else if (msg instanceof HoppedMessage) {
	    		 
	    	HoppedMessage hm = (HoppedMessage) message;
	    	CastContent cc = hm.getContent();

	    	cc.addHost(endpoint.getLocalNodeHandle());
			 if (isOwner(cc.getKey())) {  
			   return true;
			 }  
			 else {				 
				 resendHopped(cc);
				 return false;
			 }					    	
	    }
	    
		else return app.forward(message);
		
	} catch (Exception e) {
		return true; 
	}	
	
	
    
  }
  
  public void deliver(Id id, Message message) {
	
	 if (message instanceof DirectMessage) {
		 
		 CastContent cc = ((DirectMessage) message).getContent();
		 cc.addHost(endpoint.getLocalNodeHandle());		    
		 CastDeliver deliver = new CastDeliver(listeners,cc);
		 deliver.start();   
		 
	 }
	 else if (message instanceof HoppedMessage) {
		 
		 HoppedMessage hm = (HoppedMessage) message;
		 CastContent cc = hm.getContent();		 
		 cc.addHost(endpoint.getLocalNodeHandle());   
		 if (isOwner(cc.getKey())) { 
		   
		   CastDeliver deliver = new CastDeliver(listeners,cc);
		   deliver.start();
		 }  
		 else {			 
			 resendHopped(cc);
		 }
	 }
	    
	 //Scribe deliver
	 else app.deliver(id, message); 
  }
  
  public void update(NodeHandle handle, boolean joined) {
	  app.update(handle, joined);
	  UpdateDeliver deliver = new UpdateDeliver(updaters,handle,joined);
	  deliver.start();  
  }   


  /* (non-Javadoc)
 * @see damon.core.kbr.cast.CastService#addDeliverListener(java.lang.String, damon.core.kbr.cast.CastListener)
 */
  
  public void addDeliverListener(String id, CastListener dsl) {
 	  listeners.put(id, dsl);
 	  updaters.add(dsl);
  }
  
  /* (non-Javadoc)
 * @see damon.core.kbr.cast.CastService#removeDeliverListener(java.lang.String)
 */
public void removeDeliverListener(String id) {
	  CastListener dsl = listeners.remove(id);
	  if (dsl!=null) updaters.remove(dsl);
  }
  
  /* (non-Javadoc)
 * @see damon.core.kbr.cast.CastService#addForwardFilter(java.lang.String, damon.core.kbr.cast.CastFilter)
 */
public void addForwardFilter(String id, String name, CastFilter dsf) {
    filters.put(id, name, dsf);
  }
  
  /* (non-Javadoc)
 * @see damon.core.kbr.cast.CastService#removeForwardFiter(java.lang.String)
 */
public void removeForwardFiter(String id, String name) {
	filters.remove(id, name);  
  }

public Collection<CastFilter> getForwardFilters(String id) {
	if (filters.containsKey(id)) return filters.get(id).values();
	return new Vector<CastFilter>();
}
  
/***************************** More Scribe Methods ************************************/
  
  public void childAdded(Topic topic, NodeHandle child) {

  }

  public void childRemoved(Topic topic, NodeHandle child) {

  }

  @Override
  public void subscribeFailed(Topic topic) {  	
	  scribe.subscribe(topic, this, null, null);
  	
  }

  @Override
  public void subscribeFailed(Collection<Topic> topics) {
  	  scribe.subscribe(topics, this, null, null);
  	
  }

  @Override
  public void subscribeSuccess(Collection<Topic> topics) {
  	
  }

  
  /* (non-Javadoc)
 * @see damon.core.kbr.cast.CastService#isRoot(java.lang.String)
 */
  
  public boolean isRoot(String subject) {
	Topic topic = new Topic(new PastryIdFactory(node.getEnvironment()), subject);
    return scribe.isRoot(topic);
  }
  
  /* (non-Javadoc)
 * @see damon.core.kbr.cast.CastService#getParent(java.lang.String)
 */
public NodeHandle getParent(String subject) {
	Topic topic = new Topic(new PastryIdFactory(node.getEnvironment()), subject);	  
    return scribe.getParent(topic);     
  }
  
  /* (non-Javadoc)
 * @see damon.core.kbr.cast.CastService#getChildren(java.lang.String)
 */
public Collection<NodeHandle> getChildren(String subject) {
	Topic topic = new Topic(new PastryIdFactory(node.getEnvironment()), subject);
    return scribe.getChildrenOfTopic(topic); 
  }

public NodeHandle getLocalNodeHandle() {
	return node.getLocalNodeHandle();
}

public Collection<NodeHandle> getNeighbours(int num, boolean ordered) {
	Collection<NodeHandle> c = new Vector<NodeHandle>();
	if (ordered) {
		for(Object obj : endpoint.networkNeighbors(num)) {
			c.add((NodeHandle) obj); 
		}
	}
	else {
		NodeHandleSet nhs = endpoint.neighborSet(num);
		if (nhs!=null) {
		  for(int i=0;i<nhs.size();i++) {
			c.add(nhs.getHandle(i));
		  }
		}  
	}
	return c;		
  }

  public Collection<NodeHandle> getReplicaSet(Id id, int num) {
	
	Collection<NodeHandle> c = bunshin.util.Utilities.convert(endpoint.replicaSet(id,num));	
	return c;		
  }

  public IdRange getRange(NodeHandle handle, int rank, Id lkey) {	
	return endpoint.range(handle, rank, lkey);
  }



@Override
public void close() {
	scribe.destroy();	
}





}