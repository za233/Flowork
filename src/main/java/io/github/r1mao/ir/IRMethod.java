package io.github.r1mao.ir;

import io.github.r1mao.DataType;
import io.github.r1mao.algorithm.LoopChecker;
import io.github.r1mao.algorithm.Graph;
import io.github.r1mao.algorithm.Node;
import javafx.util.Pair;

import java.util.*;

public class IRMethod extends Graph
{
    private int access;
    private String name,desc,signature;
    private String[] exceptions;
    private ArrayList<Node> unreachableBlock;
    private ArrayList<Node> tryCatchHandlerBlock=new ArrayList<>();
    private ArrayList<Pair<BytecodeWrapper,BytecodeWrapper>> tryCatchBound=new ArrayList<>();
    private ArrayList<String> tryCatchBlockType=new ArrayList<>();
    public IRMethod(int access, String name, String desc, String signature, String[] exceptions)
    {
        this.access=access;
        this.name=name;
        this.desc=desc;
        this.signature=signature;
        this.exceptions=exceptions;
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
    public ArrayList<Node> getTryCatchHandlerBlocks()
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
    public ArrayList<Node> getUnreachableBlocks()
    {
        if(this.unreachableBlock==null)
        {
            this.unreachableBlock=new ArrayList<>();
            for(Node n:this.getNodes())
            {
                if(n.getPredecessors().size()==0 && this.getNodes().indexOf(n)!=0 && !this.tryCatchHandlerBlock.contains(n))
                    this.unreachableBlock.add(n);
            }
        }
        return this.unreachableBlock;
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
        for(Node n:this.getNodes())
        {
            IRBasicBlock bb=(IRBasicBlock) n;
            if(!this.unreachableBlock.contains(bb))
                bb.getCode().makeIRCode();
            System.out.println("\n"+bb.getName()+":");
            bb.getCode().displayIRCode();
        }
    }

}
