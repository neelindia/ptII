/*
 * $Id$
 *
 * Copyright (c) 1998 The Regents of the University of California.
 * All rights reserved.  See the file COPYRIGHT for details.
 */
package ptolemy.schematic.editor;

import ptolemy.kernel.*;
import ptolemy.kernel.util.*;
import ptolemy.moml.*;
import diva.graph.model.*;
import java.util.*;

/**
 * 
 *
 * @author Steve Neuendorffer
 * @version $Id$
 * @rating red
 */
public class EditorGraphImpl extends BasicGraphImpl {
    /**
     * Add a node to the given graph.
     */
    public void addNode(Node n, Graph parent) {
	super.addNode(n, parent);
	CompositeEntity container = 
	    (CompositeEntity)parent.getSemanticObject();
	NamedObj object = (NamedObj)n.getSemanticObject();
	if((object == null) || (container == null)) 
	    return;	
	if(object instanceof Port) {
	    Port port = (Port)object;
	    try {
		port.setContainer(container);
	    } catch (Exception ex) {
		ex.printStackTrace();
		throw new GraphException(ex.getMessage());
	    }    
	} else if(object instanceof ComponentEntity) {
	    ComponentEntity entity = (ComponentEntity)object;
	    try {
		entity.setContainer(container);
	    } catch (Exception ex) {
		ex.printStackTrace();
		throw new GraphException(ex.getMessage());
	    }
	}    
    }

    /**
     * Return a new instance of a BasicCompositeNode with the given
     * semantic object.
     */
    public CompositeNode createCompositeNode(Object semanticObject) {
	BasicCompositeNode n = 
	    (BasicCompositeNode) super.createCompositeNode(semanticObject);
	if(semanticObject instanceof Entity) {
	    Entity entity = (Entity) semanticObject;
	    Enumeration ports = entity.getPorts();
	    while(ports.hasMoreElements()) {
		Port port = (Port) ports.nextElement();
	        BasicNode portNode = (BasicNode) createNode(port);
		n.add(portNode);
	    }
	}	
        return n;
    }

    /**
     * Set the edge's head to the given node.
     */
    public void setEdgeHead(Edge e, Node head) {
  	Node currentHead = (BasicNode)e.getHead();
	Node currentTail = (BasicNode)e.getTail();
	if(currentHead != null) {
	    _findObjectsAndUnlink(currentHead, currentTail);
	}
	if((currentHead != null || currentTail != null) && head != null) {
	    _findObjectsAndLink(head, currentTail);
	}
	super.setEdgeHead(e, head);
    }

    /**
     * Set the edge's tail to the given node.
     */
    public void setEdgeTail(Edge e, Node tail) {
   	Node currentHead = (BasicNode)e.getHead();
	Node currentTail = (BasicNode)e.getTail();
	if(currentTail != null) {
	    _findObjectsAndUnlink(currentHead, currentTail);
	}
	if((currentHead != null || currentTail != null) && tail != null) {
	    _findObjectsAndLink(currentHead, tail);
	}
	super.setEdgeTail(e, tail);
    }

    public String createLinkName(NamedObj container) {
	String root = "link_";
	int ID = 0;
	String name = null;
	// This is unimportant..  it just gets the loop started.
	Object obj = root;
	while(obj != null) {
	    name = root + ID++;
	    obj = container.getAttribute(name);
	}
	return name;
    }    

    private void _findObjectsAndUnlink(Node head, Node tail) {
	Port port;
	Relation relation;
	Vertex vertex;
	if(tail.getSemanticObject() instanceof Port &&
	   head.getSemanticObject() instanceof Vertex) {
	    vertex = (Vertex)head.getSemanticObject();
	    port = (Port)tail.getSemanticObject();
	    relation = (Relation)vertex.getContainer();
	} else if(tail.getSemanticObject() instanceof Vertex &&
		  head.getSemanticObject() instanceof Port) {
	    vertex = (Vertex)tail.getSemanticObject();
	    port = (Port)head.getSemanticObject();
	    relation = (Relation)vertex.getContainer();
	} else {
	    throw new GraphException("must link Port to Relation");
	}
	port.unlink(relation);
	
    }

    private void _findObjectsAndLink(Node head, Node tail) {
	Port port;
	Relation relation;
	Vertex vertex;
	if(tail.getSemanticObject() instanceof Port &&
	   head.getSemanticObject() instanceof Vertex) {
	    vertex = (Vertex)head.getSemanticObject();
	    port = (Port)tail.getSemanticObject();
	    relation = (Relation)vertex.getContainer();
	} else if(tail.getSemanticObject() instanceof Vertex &&
		  head.getSemanticObject() instanceof Port) {
	    vertex = (Vertex)tail.getSemanticObject();
	    port = (Port)head.getSemanticObject();
	    relation = (Relation)vertex.getContainer();
	} else {
	    throw new GraphException("must link Port to Relation");
	}
	try {
	    port.link(relation);
	}
	catch (IllegalActionException ex) {
	    ex.printStackTrace();
	    throw new GraphException(ex.getMessage());
	}	    
    }
}
