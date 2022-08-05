package io.github.r1mao.algorithm;

import java.util.*;

public class LengauerTarjan
{
    private Node startPoint;
    private Graph graph;
    private int visitTime;
    private HashMap<Node,Integer> dfn;
    private HashMap<Node,Node> semiDom,iDom,sameDom,parent,ancestor;
    private ArrayList<Node> vertex;
    public LengauerTarjan(Graph graph,Node start)
    {
        this.graph=graph;
        this.startPoint=start;
        assert this.graph.hasNode(this.startPoint);
        this.visitTime=0;
        this.vertex=new ArrayList<>();
        this.semiDom=new HashMap<>();
        this.iDom=new HashMap<>();
        this.sameDom=new HashMap<>();
        this.parent=new HashMap<>();
        this.ancestor=new HashMap<>();
        this.dfn=new HashMap<>();
    }
    private void dfs(Node last,Node now)
    {
        if(!this.dfn.containsKey(now))
        {
            this.dfn.put(now,this.visitTime);
            this.visitTime+=1;
            this.parent.put(now,last);
            this.vertex.add(now);
            List<Node> successors=now.getSuccessors();
            for(int i=0;i<successors.size();i++)
            {
                Node s=successors.get(i);
                dfs(now,s);
            }
        }
    }
    private void link(Node p,Node n)
    {
        this.ancestor.put(n,p);
    }
    private Node find(Node n)
    {
        Node v=n,u=n;
        while(this.ancestor.containsKey(v))
        {
            if(this.dfn.get(this.semiDom.get(v))<this.dfn.get(this.semiDom.get(u)))
                u=v;
            v=this.ancestor.get(v);
        }
        return u;
    }
    public void run()
    {
        this.dfs(null,this.startPoint);
        HashMap<Node,Set<Node>> bucket=new HashMap<>();
        for(Node n:this.graph.getNodes())
            bucket.put(n, new HashSet<Node>());
        for(int i=graph.getNodeNum()-1;i>=1;i--)
        {
            Node n=this.vertex.get(i),p=this.parent.get(n);
            Node s=p;
            List<Node> list=n.getPredecessors();
            for(int j=0;j<list.size();j++)
            {
                Node tmp=null,v=list.get(j);
                if(this.dfn.get(v)<=this.dfn.get(n))
                    tmp=v;
                else
                    tmp=this.semiDom.get(this.find(v));
                if(this.dfn.get(tmp)<this.dfn.get(s))
                    s=tmp;
            }
            this.semiDom.put(n,s);
            bucket.get(s).add(n);
            this.link(p,n);
            for(Node v:bucket.get(p))
            {
                Node y=this.find(v);
                if(this.semiDom.get(y)==this.semiDom.get(v))
                    this.iDom.put(v,p);
                else
                    this.sameDom.put(v,y);
            }
            bucket.get(p).clear();
        }
        for(int i=1;i<graph.getNodeNum();i++)
        {
            Node n=this.vertex.get(i);
            if(this.sameDom.containsKey(n))
                this.iDom.put(n,this.iDom.get(this.sameDom.get(n)));
        }
    }
    public Node getIDom(Node n)
    {
        return this.iDom.get(n);
    }
}
