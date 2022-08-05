package io.github.r1mao.ir.code;

import io.github.r1mao.ir.IRBasicBlock;
import io.github.r1mao.ir.IRStatement;

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
    public String dump()
    {
        if(condition!=null)
            return "if("+condition.dump()+") goto "+trueTarget.getName()+"; else goto "+falseTarget.getName()+";";
        else
            return "goto "+trueTarget.getName()+";";
    }
}
