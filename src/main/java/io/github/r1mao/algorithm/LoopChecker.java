package io.github.r1mao.algorithm;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class LoopChecker
{
    private Graph graph;
    private Node startPoint;
    private HashMap<Node,Integer> weight;
    public HashMap<Node, ArrayList<Integer>> valueMap;
    public static int MAX_TIMES=10000;
    public LoopChecker(Graph graph, HashMap<Node,Integer> weight, Node start)
    {
        this.graph=graph;
        this.startPoint=start;
        this.weight=weight;
        this.valueMap=new HashMap<>();
        assert this.graph.hasNode(this.startPoint);
    }
    public HashMap<Node, ArrayList<Integer>> getResult()
    {
        return this.valueMap;
    }
    public boolean run()
    {
        ArrayList<Node> list=graph.getNodes();
        for(int i=0;i<graph.getNodeNum();i++)
            this.valueMap.put(list.get(i),new ArrayList<Integer>());
        LinkedList<Pair<Node,Integer>> queue=new LinkedList<Pair<Node, Integer>>();
        queue.add(new Pair<>(startPoint, 0));
        this.valueMap.get(startPoint).add(0);
        int times=0;
        while(!queue.isEmpty())
        {
            Pair<Node,Integer> data=queue.removeFirst();
            Node n=data.getKey();
            int value=data.getValue()+this.weight.get(n);
            list=n.getSuccessors();
            times++;
            if(times>MAX_TIMES)
                return false;
            for(int i=0;i<list.size();i++)
            {
                Node s=list.get(i);
                ArrayList<Integer> values=this.valueMap.get(s);
                if(values.contains(value))
                    continue;
                values.add(value);
                queue.addLast(new Pair<>(s, value));
            }
        }
        return true;
    }
}
