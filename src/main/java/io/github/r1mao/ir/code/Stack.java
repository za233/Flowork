package io.github.r1mao.ir.code;

import java.util.ArrayList;

public class Stack extends Value
{
    private final int size;
    private int stackIndex;
    private static ArrayList<Stack> cache=new ArrayList<>();
    private Stack(int stackIndex, int size)
    {
        super(Value.STACK);
        this.stackIndex=stackIndex;
        this.size=size;
    }
    public static Stack getRegion(int stackIndex, int size)
    {
        for(Stack t:cache)
        {
            if(t.stackIndex==stackIndex && t.size==size)
                return t;
        }
        Stack r=new Stack(stackIndex,size);
        cache.add(r);
        return r;
    }
    @Override
    public String dump()
    {
        return "s"+stackIndex+"_"+size;
    }
    public static ArrayList<Stack> getAllocatedStackRegion()
    {
        return cache;
    }
}
