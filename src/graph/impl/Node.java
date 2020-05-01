package graph.impl;

import java.util.Collection;

import graph.INode;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

/**
 * Class to represent a single node (or vertex) of a graph.
 * 
 * Node can be used for either directed or undirected graphs, as well as
 * for weighted or unweighted graphs. For unweighted graphs, use something like 1 for all 
 * of the weights. For undirected graphs, add a directed edge in both directions.
 * 
 * You want to make as many operations O(1) as possible, which means you will
 * probably use a lot of Maps.
 * 
 * Side note: You can tell that I come from a networking background and not a mathematical
 * background because I almost always use the term "node" instead of "vertex".
 * 
 * @author jspacco
 *
 */
public class Node implements INode
{
    private String node;
    private HashMap<INode, Integer> directedNode;
    /**
     * Create a new node with the given name. The newly created node should
     * have no edges.
     * 
     * @param name
     */
    public Node(String name) {
        this.node = name;
        this.directedNode = new HashMap<>();
    }
    
    
    
    /**
     * Return the name of the node, which is a String.
     * 
     * @return
     */
    public String getName() {
        return node;
    }

    /**
     * Return a collection of nodes that the current node is connected to by an edge.
     * 
     * @return
     */
    public Collection<INode> getNeighbors() {
        Set<INode> ans = directedNode.keySet();
        return ans;
    }
    
    /**
     * Add a directed edge to the given node using the given weight.
     * 
     * @param n
     * @param weight
     */
    public void addDirectedEdgeToNode(INode n, int weight) {
        directedNode.put(n, weight);
    }
    
    /**
     * Add an undirected edge to the given node using the given weight.
     * Remember than an undirected edge can be implemented using two directed edges.
     * 
     * @param n
     * @param weight
     */
    public void addUndirectedEdgeToNode(INode n, int weight) {
        this.addDirectedEdgeToNode(n, weight);
        n.addDirectedEdgeToNode(this, weight);
    }

    /**
     * Remove the directed edge to the given node.
     * 
     * If there is no edge to the given node, throw
     * IllegalStateException (which is a type of runtime exception).
     * 
     * @param n
     * @throws IllegalStateException
     */
    public void removeDirectedEdgeToNode(INode n) {
        directedNode.remove(n);
    }
    
    /**
     * Remove an undirected edge to the given node. This means removing
     * the edge to the given node, but also any edge from the given
     * node back to this node.
     * 
     * Throw a IllegalStateException if there is no edge to the given node.
     * 
     * @param n
     * @throws IllegalStateException
     */
    public void removeUndirectedEdgeToNode(INode n) {
        directedNode.remove(n);
        n.removeDirectedEdgeToNode(this);
    }
    
    /**
     * Return true if there is an edge to the given node from this node,
     * and false otherwise.
     * 
     * @param other
     * @return
     */
    public boolean hasEdge(INode other) {
        return this.getNeighbors().contains(other);
    }
    
    /**
     * Get the weight of the edge to the given node.
     * 
     * If no such edge exists, throw {@link IllegalStateException}
     * 
     * @param n
     * @return
     * @throws IllegalStateException
     */
    public int getWeight(INode n) {
        return directedNode.get(n);
    }
}
