package com.company;

import java.util.*;
import java.util.stream.Collectors;


interface LabeledVertex<LabelType> {

    LabelType getLabel();

    void setLabel(LabelType label);

}

interface LabeledEdge<LabelType, NodeLabelType> {

    LabeledVertex<NodeLabelType> getFrom();

    LabeledVertex<NodeLabelType> getTo();

    LabelType getLabel();

    void setLabel(LabelType label);

}

interface LabeledGraph<NodeLabelType, EdgeLabelType> {

    Iterator<? extends LabeledVertex<NodeLabelType>> vertexIterator();

    Iterator<? extends LabeledEdge<EdgeLabelType, NodeLabelType>> edgeIterator(LabeledVertex<NodeLabelType> vertex);

    LabeledEdge<EdgeLabelType, NodeLabelType> getEdge(LabeledVertex<NodeLabelType> from, LabeledVertex<NodeLabelType> to);

}

interface EditableGraph<NodeLabelType, EdgeLabelType> extends LabeledGraph<NodeLabelType, EdgeLabelType> {

    LabeledVertex<NodeLabelType> addVertex(NodeLabelType label);

    LabeledEdge<EdgeLabelType, NodeLabelType> addEdge(LabeledVertex<NodeLabelType> from, LabeledVertex<NodeLabelType> to, EdgeLabelType label);

    void removeVertex(LabeledVertex<NodeLabelType> v);

    LabeledEdge<EdgeLabelType, NodeLabelType> removeEdge(LabeledVertex<NodeLabelType> from, LabeledVertex<NodeLabelType> to);

}

class Vertex<VertexType, EdgeType> implements LabeledVertex<VertexType> {

    /**
     * remove map of neighboors and move all the operations to Graph map
     */

    LinkedHashMap<LabeledVertex<VertexType>, LabeledEdge<EdgeType, VertexType>> neighboors;
    private VertexType label;

    Vertex(VertexType label) {
        neighboors = new LinkedHashMap<>();
        this.label = label;
    }

    @Override
    public void setLabel(VertexType label) {
        this.label = label;
    }

    @Override
    public VertexType getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label.toString();
    }


}

class Edge<EdgeType, VertexType> implements LabeledEdge<EdgeType, VertexType> {

    private EdgeType label;
    private LabeledVertex<VertexType> from;
    private LabeledVertex<VertexType> to;

    /**
     * make constructor to receive hashmap of vertices
     * put neighbors and edge to the value map of from vertex
     */
    Edge(LabeledVertex<VertexType> from, LabeledVertex<VertexType> to, EdgeType label,
         LinkedHashMap<LabeledVertex<VertexType>, LabeledEdge<EdgeType, VertexType>> neighborsMap) {
        this.label = label;
        this.from = from;
        this.to = to;
        neighborsMap.put(to, this);

    }

    @Override
    public String toString() {
        return from.toString() + "-" + label + "->" + to.toString();
    }

    @Override
    public LabeledVertex<VertexType> getFrom() {
        return from;
    }

    @Override
    public LabeledVertex<VertexType> getTo() {
        return to;
    }

    @Override
    public EdgeType getLabel() {
        return label;
    }

    @Override
    public void setLabel(EdgeType label) {
        this.label = label;
    }
}


class NeighbourhoodMapGraph<VertexType, EdgeType> implements EditableGraph<VertexType, EdgeType> {

    private LinkedHashMap<LabeledVertex<VertexType>, LinkedHashMap<LabeledVertex<VertexType>, LabeledEdge<EdgeType, VertexType>>> vertexTab;

    public LinkedHashMap<LabeledVertex<VertexType>, LinkedHashMap<LabeledVertex<VertexType>, LabeledEdge<EdgeType, VertexType>>>
    getVertexTab() {
        return vertexTab;
    }

    NeighbourhoodMapGraph() {
        vertexTab = new LinkedHashMap<>();
    }

    @Override
    public Iterator<? extends LabeledVertex<VertexType>> vertexIterator() {
        LinkedList<LabeledVertex<VertexType>> list =
                vertexTab.entrySet().stream()
                        .map(Map.Entry::getKey)
                        .collect(Collectors
                                .toCollection(LinkedList::new));

        return list.iterator();
    }

    @Override
    public Iterator<? extends LabeledEdge<EdgeType, VertexType>> edgeIterator(LabeledVertex<VertexType> vertex) {
        LinkedList<LabeledEdge<EdgeType, VertexType>> list =
                vertexTab.get(vertex).entrySet().stream()
                        .map(Map.Entry::getValue)
                        .collect(Collectors
                                .toCollection(LinkedList::new));

        return list.iterator();
    }

    @Override
    public LabeledEdge<EdgeType, VertexType> getEdge(LabeledVertex<VertexType> from, LabeledVertex<VertexType> to) {
        return vertexTab.get(from).get(to);
    }

    @Override
    public LabeledVertex<VertexType> addVertex(VertexType label) {
        Vertex<VertexType, EdgeType> v = new Vertex<>(label);
        vertexTab.put(v, new LinkedHashMap<>());
        return v;
    }

    @Override
    public LabeledEdge<EdgeType, VertexType> addEdge(LabeledVertex<VertexType> from, LabeledVertex<VertexType> to, EdgeType label) {
        return new Edge<>(from, to, label, vertexTab.get(from));
    }

    @Override
    public void removeVertex(LabeledVertex<VertexType> v) {
        Iterator<? extends LabeledVertex<VertexType>> vertexIterator = vertexIterator();
        while (vertexIterator.hasNext()) {
            vertexTab.get(vertexIterator.next()).remove(v);
        }
        vertexTab.remove(v);

    }

    @Override
    public LabeledEdge<EdgeType, VertexType> removeEdge(LabeledVertex<VertexType> from, LabeledVertex<VertexType> to) {
        return vertexTab.get(from).remove(to);
    }

    @Override
    public String toString() {
        return vertexTab.toString();
    }
}

class LabeledGraphDescriptor<Graph> {
    Graph graph;

    LabeledGraphDescriptor(Graph g, String s) {
        graph = g;
    }

    @Override
    public String toString() {
        return "";//todo
    }
}

public class Main {

    public static void main(String[] args) {
        NeighbourhoodMapGraph<String, Double> g = new NeighbourhoodMapGraph<>();

        @SuppressWarnings("unchecked")

        LabeledVertex<String>[] vs = new LabeledVertex[]{

                g.addVertex("pierwszy"),

                g.addVertex("nastÄ™pny"),

                g.addVertex("kolejny"),

                g.addVertex("ostatni")

        };

        g.addEdge(vs[0], vs[1], 35.0);

        g.addEdge(vs[0], vs[3], 15.1);

        g.addEdge(vs[1], vs[2], 12.9);

        g.addEdge(vs[3], vs[2], -1.3);

        g.addEdge(vs[3], vs[0], 125.0);

        g.addEdge(vs[2], vs[0], 5.5);

        System.out.println(g);

        System.out.println("-----");


        vs[2].setLabel("zmieniony");


        g.getEdge(vs[2], vs[0]).setLabel(34.0);

        g.removeEdge(vs[3], vs[0]);

        g.removeVertex(vs[1]);


        System.out.println(g);
    }
}
