package io.github.r1mao.ir.code;

import io.github.r1mao.DataType;
import io.github.r1mao.ir.code.ssa.StaticValue;

import java.util.ArrayList;

public class Value
{
    public static int LOCAL=0,TEMP=1,OPERATOR=2,CONSTANT=3,STACK=4,STATIC_VALUE=5;
    protected int valueType;

    protected Value(int valueType)
    {
        this.valueType=valueType;
    }

    public static void resetCache()
    {
        Temp.getAllocatedTempValue().clear();
        Local.getAllLocals().clear();
        Stack.getAllocatedStackRegion().clear();
        Constant.getAllConstant().clear();
        StaticValue.getAllValues().clear();
    }
    private static void getLeafValues(Value in,ArrayList<Value> result)
    {
        if(in==null)
            return;
        if(in.isLeafValue())
            result.add(in);
        else
        {
            Operator op= (Operator) in;
            for(int i=0;i<op.getOperands().length;i++)
                getLeafValues(op.getOperands()[i],result);
        }

    }
    public static void replaceUseOf(Value target,Value from,Value to)
    {
        assert !target.isLeafValue();
        Operator op= (Operator) target;
        for(int i=0;i<op.getOperands().length;i++)
        {
            Value operand=op.getOperands()[i];
            if(operand==from)
                op.getOperands()[i]=to;
            else
            {
                if(!operand.isLeafValue())
                    replaceUseOf(operand,from,to);
            }
        }


    }
    public static ArrayList<Value> getVariableValue(Value in)
    {
        ArrayList<Value> tmp=new ArrayList<>(),result=new ArrayList<>();
        getLeafValues(in,tmp);
        for(Value v:tmp)
        {
            if(v.getValueType()==CONSTANT)
                continue;
            result.add(v);
        }
        return result;
    }
    public static ArrayList<Value> getAllVariableValues()
    {
        ArrayList<Value> result=new ArrayList<>();
        result.addAll(Temp.getAllocatedTempValue());
        result.addAll(Local.getAllLocals());
        result.addAll(Stack.getAllocatedStackRegion());
        return result;
    }
    public static ArrayList<Value> getAllLeafValues()
    {
        ArrayList<Value> result=getAllVariableValues();
        result.addAll(Constant.getAllConstant());
        return result;
    }
    public boolean isLeafValue()
    {
        return this.getValueType()!=Value.OPERATOR;
    }
    public boolean isVariableValue()
    {
        return this.getValueType()!=Value.OPERATOR && this.getValueType()!=Value.CONSTANT;
    }
    public static void dumpLeafValue()
    {
        System.out.println("\nTemp Value:");
        for(Temp t:Temp.getAllocatedTempValue())
            System.out.print(t.dump()+", ");
        System.out.println("\nLocal Variable:");
        for(Local t:Local.getAllLocals())
            System.out.print(t.dump()+", ");
        System.out.println("\nStack Region:");
        for(Stack t:Stack.getAllocatedStackRegion())
            System.out.print(t.dump()+", ");
        System.out.println("");
    }
    public int getValueType()
    {
        return this.valueType;
    }

    public String dump()
    {
        return "???";
    }

}
