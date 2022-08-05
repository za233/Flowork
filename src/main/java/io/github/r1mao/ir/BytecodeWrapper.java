package io.github.r1mao.ir;

import io.github.r1mao.DataType;
import io.github.r1mao.OpcodeInfo;
import io.github.r1mao.algorithm.DescriptorParser;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BytecodeWrapper
{
    private int opcode;
    private List<Object> operands;
    private HashMap<Integer, Pair<String,String>> variableTable=new HashMap<>();
    public BytecodeWrapper(final int opcode,Object ...operands)
    {
        this.operands=List.of(operands);
        this.opcode=opcode;
    }
    public Object getOperand(int index)
    {
        return this.operands.get(index);
    }
    public int getOpcode()
    {
        return this.opcode;
    }
    public void addVariable(int index,String name,String desc)
    {
        assert !this.variableTable.containsKey(index);

        this.variableTable.put(index,new Pair(name,desc));
    }
    public HashMap<Integer, Pair<String,String>> getVariableTable()
    {
        return this.variableTable;
    }
    public String getVariableName(int slotIndex)
    {
        return this.variableTable.get(slotIndex).getKey();
    }
    public String getVariableDesc(int slotIndex)
    {
        return this.variableTable.get(slotIndex).getValue();
    }
    public DataType getVariableType(int slotIndex)
    {
        String desc=this.variableTable.get(slotIndex).getValue();
        return new DescriptorParser(desc,false).getType();
    }
}
