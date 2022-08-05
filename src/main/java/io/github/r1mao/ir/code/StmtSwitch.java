package io.github.r1mao.ir.code;

import io.github.r1mao.ir.IRBasicBlock;
import io.github.r1mao.ir.IRStatement;

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

    public String dump()
    {
        String result="switch("+var.dump()+") {\n";
        for(int k:this.blockMap.keySet())
            result+="\tcase "+k+":  goto "+this.blockMap.get(k).getName()+";\n";
        result+="\tdefault:  goto "+this.defaultBlock.getName()+";\n}";
        return result;
    }
}
