package io.github.r1mao.ir;

import io.github.r1mao.DataType;
import io.github.r1mao.algorithm.LoopChecker;
import io.github.r1mao.algorithm.Graph;
import io.github.r1mao.algorithm.Node;
import io.github.r1mao.ir.code.Value;
import javafx.util.Pair;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class IRMethod extends Graph
{
    private int access;
    private String name,desc,signature;
    private IRClass parent;
    private String[] exceptions;
    private ArrayList<IRBasicBlock> unreachableBlock;
    private ArrayList<IRBasicBlock> tryCatchHandlerBlock=new ArrayList<>();
    private ArrayList<Pair<BytecodeWrapper,BytecodeWrapper>> tryCatchBound=new ArrayList<>();
    private ArrayList<String> tryCatchBlockType=new ArrayList<>();
    private HashMap<Value,HashSet<IRStatement>> defineMap;
    public IRMethod(int access, String name, String desc, String signature, String[] exceptions)
    {
        this.access=access;
        this.name=name;
        this.desc=desc;
        this.signature=signature;
        this.exceptions=exceptions;
    }
    public void setParent(IRClass parent)
    {
        this.parent=parent;
    }
    public String getName()
    {
        return this.parent.getName().replaceAll("/","_")+"$"+this.name;
    }
    public ArrayList<IRBasicBlock> getBasicBlocks()
    {
        ArrayList<IRBasicBlock> arr=new ArrayList<>();
        for(Node n:this.getNodes())
            arr.add((IRBasicBlock) n);
        return arr;
    }
    public void addTryCatchBlock(BytecodeWrapper start,BytecodeWrapper end,IRBasicBlock handler,String type)
    {
        assert start!=null;
        this.tryCatchBound.add(new Pair(start,end));
        this.tryCatchHandlerBlock.add(handler);
        this.tryCatchBlockType.add(type);
    }
    public ArrayList<IRBasicBlock> getTryCatchHandlerBlocks()
    {
        return this.tryCatchHandlerBlock;
    }
    public IRBasicBlock getEntryBlock()
    {
        return this.getBasicBlocks().get(0);
    }
    public void emulateStack(Stack<DataType> context,IRBasicBlock startBlock)
    {
        IRBasicBlock block=startBlock;
        HashSet<IRBasicBlock> visited=new HashSet<>();
        visited.add(block);
        LinkedList<Pair<IRBasicBlock,Stack<DataType>>> queue=new LinkedList<>();
        queue.add(new Pair<>(block, context));
        while(!queue.isEmpty())
        {
            Pair<IRBasicBlock,Stack<DataType>> record=queue.removeFirst();
            block=record.getKey();
            context=record.getValue();
            ArrayList<Node> successors=block.getSuccessors();
            int delta=0;
            for(DataType d:context)
                delta+=d.getSizeInSlot();
            block.getCode().emulateStack(context);
            for(DataType d:context)
                delta-=d.getSizeInSlot();
            delta=-delta;

            block.setStackOffset(delta);
            for(int i=0;i<successors.size();i++)
            {
                IRBasicBlock bb=(IRBasicBlock) successors.get(i);
                if(visited.contains(bb))
                    continue;
                Stack<DataType> forked= (Stack<DataType>) context.clone();
                queue.addLast(new Pair<>(bb, forked));
                visited.add(bb);
            }
        }

    }
    public ArrayList<IRBasicBlock> getUnreachableBlocks()
    {
        if(this.unreachableBlock==null)
        {
            this.unreachableBlock=new ArrayList<>();
            for(Node n:this.getNodes())
            {
                if(n.getPredecessors().size()==0 && this.getNodes().indexOf(n)!=0 && !this.tryCatchHandlerBlock.contains(n))
                    this.unreachableBlock.add((IRBasicBlock) n);
            }
        }
        return this.unreachableBlock;
    }
    public HashMap<Value,HashSet<IRStatement>> getDefineMap()
    {
        return defineMap;
    }
    public void analysisStack(int base,IRBasicBlock block)
    {
        HashMap<Node,Integer> weight=new HashMap<>();
        for(Node n:this.getNodes())
        {
            IRBasicBlock bb=(IRBasicBlock) n;
            weight.put(n,bb.getStackOffset());
        }
        LoopChecker f=new LoopChecker(this,weight,block);
        f.run(base);
        HashMap<Node,ArrayList<Integer>> valueMap=f.getResult();
        for(Node n:valueMap.keySet())
        {
            ArrayList<Integer> values=valueMap.get(n);
            if(this.unreachableBlock.contains(n) || values.size()==0)
                continue;
            assert values.size()<=1;
            IRBasicBlock bb=(IRBasicBlock) n;
            bb.setStackAddress(values.get(0));
        }
    }
    public void generateIRCode()
    {
        assert this.unreachableBlock.size()==0;
        for(Node n:this.getNodes())
        {
            IRBasicBlock bb=(IRBasicBlock) n;
            bb.getCode().makeIRCode();

        }

    }
    public void analyseIRCode()
    {
        this.defineMap=new HashMap<>();
        for(Node n:this.getNodes())
        {
            IRBasicBlock bb=(IRBasicBlock) n;
            for(IRStatement stmt:bb.getCode().getIRCode())
            {
                for(Value v:stmt.getWriteVariable())
                {
                    if(this.defineMap.get(v)==null)
                        this.defineMap.put(v,new HashSet<>());
                    HashSet<IRStatement> set=this.defineMap.get(v);
                    set.add(stmt);
                }
            }
        }
        for(Node n:this.getNodes())
        {
            IRBasicBlock bb=(IRBasicBlock) n;
            bb.getCode().analyseIRCodeDataflow();
        }
        this.reachDefineAnalyse();
    }
    public void dump()
    {
        for(IRBasicBlock bb:this.getBasicBlocks())
        {
            System.out.println(bb.getName()+":\n"+bb.getCode().displayIRCode()+"\n");
        }
    }
    public void buildGvFile(String filename)
    {
        String content="digraph "+this.name+" {\n\tlabeljust=l\n";
        int i=0;
        for(IRBasicBlock bb:this.getBasicBlocks())
        {
            content+="\t"+i+" [label=\""+bb.getName()+":\n"+bb.getCode().displayIRCode()+"\"]";
            i++;
        }

        for(Node n:this.getNodes())
        {
            for(Object s:n.getSuccessors())
                content+="\t"+this.getNodes().indexOf(n)+" -> "+this.getNodes().indexOf(s)+"\n";
        }
        content+="}";
        FileWriter fileWriter= null;
        try
        {
            File file =new File(filename);
            if(!file.exists())
                file.createNewFile();
            fileWriter=new FileWriter(filename);
            fileWriter.write(content);
            fileWriter.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return;
    }
    private void reachDefineAnalyse()
    {
        for(Node n:this.getNodes())
        {
            IRBasicBlock bb=(IRBasicBlock) n;
            bb.getInSet().clear();
            bb.getOutSet().clear();
        }
        boolean flag=true;
        while(flag)
        {
            flag=false;
            for(Node n:this.getNodes())
            {

                IRBasicBlock bb=(IRBasicBlock) n;
                int size=bb.getInSet().size();
                for(Node p:bb.getPredecessors())
                {
                    IRBasicBlock bb1= (IRBasicBlock) p;
                    bb.getInSet().addAll(bb1.getOutSet());
                }
                if(bb.getInSet().size()>size)
                    flag=true;
                bb.getOutSet().clear();
                bb.getOutSet().addAll(bb.getCode().getGenSet());
                HashSet<IRStatement> tmp= (HashSet<IRStatement>) bb.getInSet().clone();
                tmp.removeAll(bb.getCode().getKillSet());
                bb.getOutSet().addAll(tmp);

            }
        }
        /*for(Node n:this.getNodes())
        {
            IRBasicBlock bb = (IRBasicBlock) n;

            System.out.println("----------------------------------------\nin set for "+bb.getName());
            for(IRStatement statement:bb.getInSet())
                System.out.println("at "+statement.getHolder().getHolder().getName()+" -> "+statement.dump());
            System.out.println("\n");

            System.out.println("\nout set for "+bb.getName());
            for(IRStatement statement:bb.getOutSet())
                System.out.println("at "+statement.getHolder().getHolder().getName()+" -> "+statement.dump());
            System.out.println("\n");
        }*/
    }
}
