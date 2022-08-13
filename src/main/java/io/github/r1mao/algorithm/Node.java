package io.github.r1mao.algorithm;

import java.util.ArrayList;

public class Node<T>
{
    private T data;
    private ArrayList<Node> successors=new ArrayList<>();
    private ArrayList<Node> predecessors=new ArrayList<>();
    private Graph parent;
    public Node(T data,Graph parent)
    {
        this.data=data;
        this.parent=parent;
    }
    public Graph getParent()
    {
        return this.parent;
    }
    public void addSuccessor(Node successor) throws Exception
    {
        if(!this.parent.hasNode(successor))
            throw new Exception("Nodes not in the same graph");
        this.successors.add(successor);
    }
    public void addPredecessor(Node predecessor) throws Exception
    {
        if(!this.parent.hasNode(predecessor))
            throw new Exception("Nodes not in the same graph");
        this.predecessors.add(predecessor);
    }
    public void addEdge(Node ...targets) throws Exception
    {
        for(int i=0;i<targets.length;i++)
        {
            this.addSuccessor(targets[i]);
            targets[i].addPredecessor(this);
        }

    }
    public T getData()
    {
        return this.data;
    }
    public ArrayList<Node> getSuccessors()
    {
        return this.successors;
    }
    public ArrayList<Node> getPredecessors()
    {
        return this.predecessors;
    }
}
