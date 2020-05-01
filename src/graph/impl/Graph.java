package graph.impl;

import java.util.Comparator;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.ArrayDeque;
import java.util.Deque;
import graph.IGraph;
import graph.INode;
import graph.NodeVisitor;

/**
 * A basic representation of a graph that can perform BFS, DFS, Dijkstra,
 * and Prim-Jarnik's algorithm for a minimum spanning tree.
 * 
 * @author jspacco
 *
 */
public class Graph implements IGraph
{
    HashMap<String, INode> container;
    public Graph(){
        container = new HashMap<>();
    }
    /**
     * Return the {@link Node} with the given name.
     * 
     * If no {@link Node} with the given name exists, create
     * a new node with the given name and return it. Subsequent
     * calls to this method with the same name should
     * then return the node just created.
     * 
     * @param name
     * @return
     */
    public INode getOrCreateNode(String name) {
        if(!container.containsKey(name)) container.put(name, new Node(name));
        return container.get(name);
    }

    /**
     * Return true if the graph contains a node with the given name,
     * and false otherwise.
     * 
     * @param name
     * @return
     */
    public boolean containsNode(String name) {
        return container.containsKey(name);
    }

    /**
     * Return a collection of all of the nodes in the graph.
     * 
     * @return
     */
    public Collection<INode> getAllNodes() {
        return container.values();
    }
    
    /**
     * Perform a breadth-first search on the graph, starting at the node
     * with the given name. The visit method of the {@link NodeVisitor} should
     * be called on each node the first time we visit the node.
     * 
     * 
     * @param startNodeName
     * @param v
     */
    public void breadthFirstSearch(String startNodeName, NodeVisitor v)
    {
        Deque<INode> queue = new ArrayDeque<>();
        HashSet<INode> set = new HashSet<>();
        INode start = this.getOrCreateNode(startNodeName);
        queue.add(start);
        v.visit(start);
        set.add(start);
        while(!queue.isEmpty()){
            int size = queue.size();
            for(int i =0 ; i< size; i++){
                INode temp = queue.poll();
                for(INode y : temp.getNeighbors()){
                    if(!set.contains(y)){
                        queue.add(y);
                        v.visit(y);
                        set.add(y);
                    }
                }
            }
        }
    }

    /**
     * Perform a depth-first search on the graph, starting at the node
     * with the given name. The visit method of the {@link NodeVisitor} should
     * be called on each node the first time we visit the node.
     * 
     * 
     * @param startNodeName
     * @param v
     */
    public void depthFirstSearch(String startNodeName, NodeVisitor v)
    {
       Deque<INode> stack =  new ArrayDeque<>();
       HashSet<INode> set = new HashSet<>();
       INode start = this.getOrCreateNode(startNodeName);
       stack.add(start);
       set.add(start);
       while(!stack.isEmpty()){
            start = stack.removeLast();
            v.visit(start);
            for(INode x: start.getNeighbors()){
                if(!set.contains(x)){
                    stack.addLast(x);
                    set.add(x);
                }
            }
       }
    }

    /**
     * Perform Dijkstra's algorithm for computing the cost of the shortest path
     * to every node in the graph starting at the node with the given name.
     * Return a mapping from every node in the graph to the total minimum cost of reaching
     * that node from the given start node.
     * 
     * <b>Hint:</b> Creating a helper class called Path, which stores a destination
     * (String) and a cost (Integer), and making it implement Comparable, can be
     * helpful. Well, either than or repeated linear scans.
     * 
     * @param startName
     * @return
     */
    public Map<INode,Integer> dijkstra(String startName) {
        HashMap<INode, Integer> map = new HashMap<>();
        PriorityQueue<Path> pq = new PriorityQueue<>(new PathComparator());
        HashSet<INode> set = new HashSet<>();
        Path start = new Path(this.getOrCreateNode(startName), 0);
        map.put(start.returnPath(), start.returnCost());
        pq.add(start);
        while(!pq.isEmpty()){
            Path path = pq.poll();
            INode node = path.returnPath();
            set.add(node);
            for(INode x: node.getNeighbors()){
                if(set.contains(x)) continue;

                int newCost =  Math.min(x.getWeight(node) + map.get(node), map.getOrDefault(x, Integer.MAX_VALUE)) ;//original node + cost of lowest priority node
                map.put(x, newCost);
                pq.add(new Path(x, newCost));
            }
        }
        
        return map;
       
    }
    
    /**
     * Perform Prim-Jarnik's algorithm to compute a Minimum Spanning Tree (MST).
     * 
     * The MST is itself a graph containing the same nodes and a subset of the edges 
     * from the original graph.
     * 
     * @return
     */
    public IGraph primJarnik() {
        /*
        1/start from first node, call this MST, add neighbor to a pqueue if neighbor is not already in MST
        2/ dequeue -> check neighbor, start from smallest, if the node is in MST, connect
        3/ Repeat until none is left 
         */
        HashSet<String> set = new HashSet<>(); // this set shall contains node in Prim tree
        INode random = random();
        PriorityQueue<Path> pq = new PriorityQueue<>(new PathComparator());
        Path start = new Path(random, 0);
        IGraph ans = new Graph();
        //set up the first node
        
        String placebo  = start.returnPath().getName();
        pq.add(start);
        for(INode x: this.getAllNodes()){
            ans.getOrCreateNode(x.getName());
        }

        for(int i = 0 ; i < this.getAllNodes().size(); i++){//start from the chosen node 
            boolean chosen = true;
            while(chosen && !pq.isEmpty()){//chose the next node that is not include in MST
                start = pq.poll(); // choose the next minimum cost
                INode temp1 = start.returnPath();
                if(temp1.getName().equals(placebo)){//this is the first node
                    set.add(temp1.getName());
                }
                if(set.contains(temp1.getName())){
                    break;
                }
                //MST doesnt contain this node, add temp to this node
                set.add(temp1.getName());
                //connect temp to parent
                for(INode x: temp1.getNeighbors()){
                    int o = x.getWeight(temp1);
                    if(set.contains(x.getName()) && o == start.returnCost()){
                        INode ans1 = ans.getOrCreateNode(temp1.getName());
                        ans1.addUndirectedEdgeToNode(ans.getOrCreateNode(x.getName()), o);
                        chosen = false;
                        break;
                    }
                }
            }
            for(INode x: start.returnPath().getNeighbors()){//get neighbors
                if(!set.contains(x.getName()) && !x.getName().equals("start")){
                    pq.add(new Path(x, x.getWeight(start.returnPath())));
                }
            }
        }
        return ans;
       
    }


    public INode random(){
        int length = this.getAllNodes().size();
        INode[] temp = new INode[length];
        int i = 0;
        for(INode x : this.getAllNodes()){
            temp[i++] = x;
        }
        int random = (int)(Math.random()*length);

        return temp[random];
    }
}

class PathComparator implements Comparator<Path>{
    
    public int compare(Path path1, Path path2){
        if(path1.returnCost() > path2.returnCost()){
            return 1;
        } else if (path1.returnCost() < path2.returnCost()){
            return -1;
        }
        return 0;
    }
}

class Path {
    private INode path;
    private int cost;

    public Path(INode path, int cost){
        this.path = path;
        this.cost = cost;
    }

    public int returnCost(){
        return this.cost;
    }
    public INode returnPath(){
        return this.path;
    }
}