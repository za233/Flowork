package io.github.r1mao.ir;

import io.github.r1mao.algorithm.Node;
import javafx.util.Pair;

import java.util.HashMap;


public class IRBasicBlock extends Node<CodeBlock>
{
    private HashMap<Integer, Pair<String,String>> localVariable=new HashMap<>();
    private int stackOffset=0;
    private int stackAddress=0;
    public IRBasicBlock(CodeBlock data, IRMethod parent)
    {
        super(data, parent);
        data.setHolder(this);
    }
    public String getName()
    {
        return "block_"+this.getParent().getNodes().indexOf(this);
    }
    public CodeBlock getCode()
    {
        return this.getData();
    }
    public void setStackOffset(int offset)
    {
        this.stackOffset=offset;
    }
    public int getStackOffset()
    {
        return this.stackOffset;
    }
    public void setStackAddress(int offset)
    {
        this.stackAddress=offset;
    }
    public int getStackAddress()
    {
        return this.stackAddress;
    }
}
