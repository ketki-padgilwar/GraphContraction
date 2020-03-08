package gcontraction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
 
public class Gcontraction
{
    private static class Graph
    {
        private final Map<Integer, Vertex> vertices = new TreeMap<Integer, Vertex>(
                new Comparator<Integer>()
                {
                    @Override
                    public int compare(Integer arg0, Integer arg1)
                    {
                        return arg0.compareTo(arg1);
                    }
                });
        private final List<Edge> edges = new ArrayList<Edge>();
 
        public void addVertex(Vertex v)
        {
            vertices.put(v.lbl, v);
        }
 
        public Vertex getVertex(int lbl)
        {
            Vertex v;
            if ((v = vertices.get(lbl)) == null)
            {
                v = new Vertex(lbl);
                addVertex(v);
            }
            return v;
        }
    }
 
    private static class Vertex
    {
        private final int lbl;
        private final Set<Edge> edges = new HashSet<Edge>();
 
        public Vertex(int lbl)
        {
            this.lbl = lbl;
        }
 
        public void addEdge(Edge edge)
        {
            edges.add(edge);
        }
 
        public Edge getEdgeTo(Vertex v2)
        {
            for (Edge edge : edges)
            {
                if (edge.contains(this, v2))
                    return edge;
            }
            return null;
        }
    }
 
    private static class Edge
    {
        private final List<Vertex> ends = new ArrayList<Vertex>();
 
        public Edge(Vertex fst, Vertex snd)
        {
            if (fst == null || snd == null)
            {
                throw new IllegalArgumentException("Both vertices are required");
            }
            ends.add(fst);
            ends.add(snd);
        }
 
        public boolean contains(Vertex v1, Vertex v2)
        {
            return ends.contains(v1) && ends.contains(v2);
        }
 
        public Vertex getOppositeVertex(Vertex v)
        {
            if (!ends.contains(v))
            {
                throw new IllegalArgumentException("Vertex " + v.lbl);
            }
            return ends.get(1 - ends.indexOf(v));
        }
 
        public void replaceVertex(Vertex oldV, Vertex newV)
        {
            if (!ends.contains(oldV))
            {
                throw new IllegalArgumentException("Vertex " + oldV.lbl);
            }
            ends.remove(oldV);
            ends.add(newV);
            
        }
    }
    
    private static void printvertex(Vertex oldV, Vertex newV){
         
          System.out.println("Merging vertex:"+oldV.lbl+"&"+newV.lbl);
          System.out.println("Adding new mergedvertex as :"+newV.lbl);
          System.out.println("");
     }
    
 
    private static void graphcontraction(Graph gr)
    {
        Random rnd = new Random();
        int r;
        while (gr.vertices.size() > 2)
        {
             r=rnd.nextInt(gr.edges.size());
            Edge edge = gr.edges.remove(r);
            
            Vertex v1 =  removevertex(gr, edge.ends.get(0), edge);
            Vertex v2 =  removevertex(gr, edge.ends.get(1), edge);
            // contract
            Vertex mergedVertex = new Vertex(v1.lbl);
            redirectEdges(gr, v1, mergedVertex);
            redirectEdges(gr, v2, mergedVertex);
             gr.addVertex(mergedVertex);
             printvertex(v2,mergedVertex);
             printGraph(gr);
            System.out.println("");
        }
        
    }
 
    private static Vertex removevertex(Graph gr, Vertex v, Edge e)
    {
        gr.vertices.remove(v.lbl);
        v.edges.remove(e);
        return v;
    }
 
    private static void redirectEdges(Graph gr, Vertex fromV, Vertex toV)
    {
         Edge e = null;
        for (Iterator<Edge> it = fromV.edges.iterator(); it.hasNext();)
        {
            Edge edge = it.next();
            it.remove();
            if (edge.getOppositeVertex(fromV) == toV)
            {
                // removing self-loop
                toV.edges.remove(edge);
                gr.edges.remove(edge);
            }
            else
            {
                edge.replaceVertex(fromV, toV);
                toV.addEdge(edge);
            }
        }
    }
 
   
    private static Graph createGraph(int[][] array)
    {
        Graph gr = new Graph();
        for (int i =0; i <array.length; i++)
        {
            Vertex v = gr.getVertex(i);
           // System.out.println(v.lbl);
            for (int edgeTo : array[i])
            {
                //System.out.println(edgeTo);
                Vertex v2 = gr.getVertex(edgeTo);
                 //System.out.println(v2.lbl);
                Edge e;
                  if ((e = v2.getEdgeTo(v)) == null)
                {
                    e = new Edge(v, v2);
                    gr.edges.add(e);
                    v.addEdge(e);
                    v2.addEdge(e);
                }
                
            }
        }
        return gr;
    }
 
   
    public static void main(String[] args)
    {
        //int[][] arr =getArray();
        
        int[][] arr = {{1,4}, 
                       {0,1,2,5,6}, 
                       {1,2,3,5,6}, 
                       {2,3,7},
                       {0,4,5},
                       {1,2,4,5,6},
                       {1,2,5,6,7},
                       {3,6,7}};
      
        Graph g = createGraph(arr);
        printGraph(g);
        System.out.println("");
        graphcontraction(g);
            
     
    }
    private static void printGraph(Graph gr)
    {
        System.out.println("Graph:");
        for(Vertex v : gr.vertices.values())
        {
            System.out.print(v.lbl + ":");
            for(Edge edge : v.edges)
            {
                System.out.print(" " + edge.getOppositeVertex(v).lbl);
            }
            System.out.println();
        }
    }
}
