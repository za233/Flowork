package io.github.r1mao.ir.code;

import io.github.r1mao.ir.IRBasicBlock;
import io.github.r1mao.ir.IRStatement;

import java.util.ArrayList;
import java.util.HashMap;

public class StmtSwitch extends IRStatement
{
    private Value var;
    private IRBasicBlock defaultBlock;
    private final HashMap<Integer, IRBasicBlock> blockMap=new HashMap<>();
    public StmtSwitch(Value var,IRBasicBlock defaultBlock)
    {
        this.var=var;
        this.defaultBlock=defaultBlock;
    }
    public void addCase(int value,IRBasicBlock target)
    {
        this.blockMap.put(value,target);
    }

    public Value getSwitchVariable()
    {
        return var;
    }
    public String dump()
    {
        String result="switch("+var.dump()+") {\n";
        for(int k:this.blockMap.keySet())
            result+="\tcase "+k+":  goto "+this.blockMap.get(k).getName()+";\n";
        result+="\tdefault:  goto "+this.defaultBlock.getName()+";\n\t}";
        return result;
    }
    @Override
    public ArrayList<Value> getReadVariable()
    {
        ArrayList<Value> arr=new ArrayList<>();
        arr.addAll(Value.getVariableValue(this.var));
        return arr;
    }
    @Override
    public ArrayList<Value> getAllValues()
    {
        ArrayList<Value> a=new ArrayList<>();
        a.add(var);
        return a;
    }
    @Override
    public void replaceUseOf(Value v1,Value v2)
    {
        if(!var.isLeafValue() && v1!=var)
        {
            Value.replaceUseOf(var,v1,v2);
        }
        else
        {
            if(v1==var)
                var=v2;
        }
    }
}
