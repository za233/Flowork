package io.github.r1mao.algorithm;

import java.util.ArrayList;

public class Graph {
    private ArrayList<Node> nodes;

    public Graph() {
        this.nodes = new ArrayList<>();
    }

    public Graph(ArrayList<Node> nodes) {
        this.nodes = nodes;
    }

    public void addNode(Node n) {
        assert n.getParent() == this;
        this.nodes.add(n);
    }

    public ArrayList<Node> getNodes() {
        return this.nodes;
    }

    public boolean hasNode(Node n) {
        return this.nodes.contains(n);
    }

    public int getNodeNum() {
        return this.nodes.size();
    }
}
