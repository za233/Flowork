package io.github.r1mao.ir.code;

import io.github.r1mao.DataType;
import io.github.r1mao.ir.IRStatement;

import java.util.ArrayList;

public class StmtReturn extends IRStatement
{
    private Value value;
    private DataType returnType;
    public StmtReturn(Value value, DataType type)
    {
        this.value=value;
        this.returnType=type;
    }
    public Value getReturnValue()
    {
        return value;
    }
    @Override
    public String dump()
    {
        if(value!=null)
            return "return "+value.dump()+";";
        else
            return "return;";
    }
    @Override
    public ArrayList<Value> getReadVariable()
    {
        ArrayList<Value> arr=new ArrayList<>();
        arr.addAll(Value.getVariableValue(this.value));
        return arr;
    }
    @Override
    public ArrayList<Value> getAllValues()
    {
        ArrayList<Value> a=new ArrayList<>();
        a.add(value);
        return a;
    }
    @Override
    public void replaceUseOf(Value v1,Value v2)
    {
        if(!value.isLeafValue() && v1!=value)
        {
            Value.replaceUseOf(value,v1,v2);
        }
        else
        {
            if(v1==value)
                value=v2;
        }
    }
}
