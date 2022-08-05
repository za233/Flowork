package io.github.r1mao.ir.code;

import io.github.r1mao.DataType;
import io.github.r1mao.algorithm.DescriptorParser;
import io.github.r1mao.ir.BytecodeWrapper;

public class Local extends Value
{
    private DataType type;
    private String desc;
    private String name;
    private int slotIndex;
    private Local(String name, String desc, DataType type)
    {
        super(Value.LOCAL);
        this.name=name;
        this.desc=desc;
        this.type=type;
    }
    private Local(int slotIndex,DataType type)
    {
        super(Value.LOCAL);
        this.slotIndex=slotIndex;
        this.type=type;

    }
    public static Local getVariable(BytecodeWrapper bytecode,int index,DataType type)
    {
        if(bytecode.getVariableTable().containsKey(index))
            return getVariable(bytecode.getVariableName(index),bytecode.getVariableDesc(index));
        else
            return getVariable(index,type);
    }
    private static Local getVariable(String name, String desc)
    {
        return new Local(name,desc,new DescriptorParser(desc,false).getType());
    }
    private static Local getVariable(int slot,DataType type)
    {
        return new Local(slot,type);
    }
    @Override
    public String dump()
    {
        if(name!=null)
        {
            return name;
        }
        return "var"+slotIndex;
    }
}
