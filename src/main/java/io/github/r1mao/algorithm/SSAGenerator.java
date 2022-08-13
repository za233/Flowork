package io.github.r1mao.algorithm;

import io.github.r1mao.ir.CodeBlock;
import io.github.r1mao.ir.IRBasicBlock;
import io.github.r1mao.ir.IRMethod;
import io.github.r1mao.ir.IRStatement;
import io.github.r1mao.ir.code.*;
import io.github.r1mao.ir.code.ssa.StaticValue;
import io.github.r1mao.ir.code.ssa.StmtPhiNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;


public class SSAGenerator
{
    private IRMethod method;
    private HashSet<Value> globalVar;
    private HashMap<Value,HashSet<IRBasicBlock>> blocks;
    private HashSet<IRBasicBlock> visited=new HashSet<>(),path=new HashSet<>();
    private Stack<HashMap<Value,StaticValue>> context=new Stack<>();

    public SSAGenerator(IRMethod method)
    {
        this.method=method;
        this.getGlobalName();
    }
    private void getGlobalName()
    {
        ArrayList<Node> nodeSet=this.method.getNodes();
        this.globalVar=new HashSet();
        this.blocks=new HashMap<>();
        for(Node n:nodeSet)
        {
            IRBasicBlock bb= (IRBasicBlock) n;
            HashSet<Value> kill=new HashSet<>();
            for(IRStatement stmt:bb.getData().getIRCode())
            {
                ArrayList<Value> readVars=stmt.getReadVariable(),writeVar=stmt.getWriteVariable();
                for(Value v:readVars)
                {
                    if(kill.contains(v))
                        continue;
                    this.globalVar.add(v);
                }
                for(Value v:writeVar)
                {
                    kill.add(v);
                    HashSet<IRBasicBlock> set=this.blocks.get(v);
                    if(set==null)
                        this.blocks.put(v,new HashSet<>());
                    set=this.blocks.get(v);
                    set.add(bb);
                }
            }
        }
    }
    private HashMap<IRBasicBlock,HashSet<Value>> getPhiMap(IRBasicBlock entry)
    {
        LengauerTarjan obj=new LengauerTarjan(method,entry);
        obj.run();
        HashMap<IRBasicBlock,HashSet<Value>> phiMap=new HashMap<>();
        for(Value g:globalVar)
        {
            if(blocks.get(g)==null)
                blocks.put(g,new HashSet<>());
            LinkedList<IRBasicBlock> workList=new LinkedList(blocks.get(g));
            while(!workList.isEmpty())
            {
                IRBasicBlock bb=workList.removeFirst();
                for(Node n:obj.getDominateFrontier(bb))
                {
                    IRBasicBlock df= (IRBasicBlock) n;
                    HashSet<Value> phiNodes=phiMap.get(df);
                    if(phiNodes==null)
                        phiMap.put(df,new HashSet<>());
                    phiNodes=phiMap.get(df);
                    if(!phiNodes.contains(g))
                    {
                        phiNodes.add(g);
                        workList.addLast(df);
                    }
                }
            }
        }
        return phiMap;

    }
    private void translate(IRBasicBlock bb)
    {
        this.visited.add(bb);
        HashMap<Value,StaticValue> mapping= (HashMap<Value, StaticValue>) this.context.lastElement().clone();
        for(IRStatement stmt:bb.getCode().getIRCode())
        {
            if(stmt instanceof StmtAssignment)
            {

                StmtAssignment assignment= (StmtAssignment) stmt;
                Value def=assignment.getDefValue();
                for(Value v:stmt.getReadVariable())
                {
                    StaticValue staticValue=mapping.get(v);
                    if(staticValue==null)
                    {
                        staticValue=StaticValue.newValue(v);
                        staticValue.setUndefineValue(true);
                        mapping.put(v,staticValue);
                    }
                    if(def!=null && !def.isLeafValue())
                        assignment.replaceUseOf(v,staticValue);
                    else
                        assignment.replaceUseOfValue(v,staticValue);
                    staticValue.addUse(stmt);
                }
                if(def!=null && def.isVariableValue())
                {
                    StaticValue value=StaticValue.newValue(def);
                    assignment.replaceUseOfTo(def,value);
                    mapping.put(def,value);
                    //System.out.println("add "+def.dump());
                }
            }
            else if(stmt instanceof StmtPhiNode)
            {
                StmtPhiNode node= (StmtPhiNode) stmt;
                StaticValue value=StaticValue.newValue(node.getOriginalValue());
                mapping.put(node.getValue(), value);
                node.replaceUseOf(node.getValue(),value);

            }
            else
            {
                for(Value v:stmt.getReadVariable())
                {
                    StaticValue staticValue=mapping.get(v);
                    if(staticValue==null)
                    {
                        staticValue=StaticValue.newValue(v);
                        staticValue.setUndefineValue(true);
                        mapping.put(v,staticValue);
                    }
                    stmt.replaceUseOf(v,staticValue);
                    staticValue.addUse(stmt);
                }
            }
        }
        this.method.dump();
        for(Node n:bb.getSuccessors())
        {
            IRBasicBlock scc= (IRBasicBlock) n;
            for(IRStatement stmt:scc.getCode().getIRCode())
            {
                if(!(stmt instanceof StmtPhiNode))
                    break;
                StmtPhiNode phiNode= (StmtPhiNode) stmt;
                Value var=phiNode.getOriginalValue();
                StaticValue staticValue=mapping.get(var);
                if(staticValue!=null)
                    staticValue.addUse(stmt);
                phiNode.addEdge(bb,staticValue);
            }
        }
        for(Node n:bb.getSuccessors())
        {
            IRBasicBlock scc= (IRBasicBlock) n;
            if(this.visited.contains(scc))
                continue;
            this.context.push(mapping);
            translate(scc);
            this.context.pop();
        }
    }
    public void run()
    {
        ArrayList<IRBasicBlock> entryBlocks=new ArrayList<>();
        HashMap<IRBasicBlock,HashSet<Value>> phiMap=this.getPhiMap(this.method.getEntryBlock()),tmp;
        entryBlocks.add(this.method.getEntryBlock());
        for(IRBasicBlock b:this.method.getTryCatchHandlerBlocks())
        {
            entryBlocks.add(b);
            tmp=this.getPhiMap(b);
            for(IRBasicBlock t:tmp.keySet())
            {
                if(phiMap.get(t)!=null)
                    phiMap.get(t).addAll(tmp.get(t));
                else
                    phiMap.put(t,tmp.get(t));
            }
        }
        for(IRBasicBlock bb:phiMap.keySet())
        {
            for(Value v:phiMap.get(bb))
            {
                StmtPhiNode phi=new StmtPhiNode(v);
                bb.getCode().getIRCode().addFirst(phi);
                phi.setHolder(bb.getCode());
            }
        }

        //this.method.dump();
        this.context.push(new HashMap<>());
        for(IRBasicBlock bb:entryBlocks)
            translate(bb);
        /*for(StaticValue v:StaticValue.getAllValues())
        {
            Value x=v.getTarget();
            System.out.println("-----------------");
            System.out.println(v.dump());
            System.out.println(x);
            System.out.println(x.dump());
        }*/
        deleteRubbishCode();


    }
    public void deleteRubbishCode()
    {
        boolean result;
        do
        {
            result=false;
            for(IRBasicBlock bb:this.method.getBasicBlocks())
            {
                ArrayList<IRStatement> rubbishCode=new ArrayList<>();
                for(IRStatement stmt:bb.getCode().getIRCode())
                {
                    if(stmt instanceof StmtAssignment)
                    {
                        StmtAssignment assign= (StmtAssignment) stmt;
                        Value def=assign.getDefValue();
                        if(def!=null && def.getValueType()==Value.STATIC_VALUE)
                        {
                            StaticValue staticValue= (StaticValue) def;
                            if(staticValue.getUsers().size()==0)
                            {
                                rubbishCode.add(assign);
                                result=true;
                                StaticValue.releaseValue(staticValue);
                            }
                        }
                    }
                    else if(stmt instanceof StmtPhiNode)
                    {
                        StmtPhiNode phiNode= (StmtPhiNode) stmt;
                        Value def=phiNode.getValue();
                        if(def.getValueType()==Value.STATIC_VALUE)
                        {
                            StaticValue staticValue= (StaticValue) def;
                            if(staticValue.getUsers().size()==0)
                            {
                                rubbishCode.add(phiNode);
                                result=true;
                                StaticValue.releaseValue(staticValue);
                            }

                        }
                    }
                }
                for(IRStatement stmt:rubbishCode)
                    bb.getCode().getIRCode().remove(stmt);

            }
        }while(result);

    }

}
