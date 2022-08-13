package io.github.r1mao.ir.code;

import io.github.r1mao.ir.IRBasicBlock;
import io.github.r1mao.ir.IRStatement;

import java.util.ArrayList;

public class StmtBranch extends IRStatement
{
    private Value condition;
    private IRBasicBlock trueTarget,falseTarget;
    public StmtBranch(Value condition, IRBasicBlock trueTarget,IRBasicBlock falseTarget)
    {
        this.condition=condition;
        this.trueTarget=trueTarget;
        this.falseTarget=falseTarget;
    }
    public StmtBranch(IRBasicBlock target)
    {
        this.trueTarget=target;
    }
    public Value getCondition()
    {
        return condition;
    }
    public String dump()
    {
        if(condition!=null)
            return "if("+condition.dump()+") goto "+trueTarget.getName()+"; else goto "+falseTarget.getName()+";";
        else
            return "goto "+trueTarget.getName()+";";
    }
    @Override
    public ArrayList<Value> getReadVariable()
    {
        ArrayList<Value> arr=new ArrayList<>();
        arr.addAll(Value.getVariableValue(this.condition));
        return arr;
    }
    @Override
    public ArrayList<Value> getAllValues()
    {
        ArrayList<Value> a=new ArrayList<>();
        a.add(condition);
        return a;
    }
    @Override
    public void replaceUseOf(Value v1,Value v2)
    {
        if(!condition.isLeafValue() && v1!=condition)
        {
            Value.replaceUseOf(condition,v1,v2);
        }
        else
        {
            if(v1==condition)
                condition=v2;
        }
    }

}
