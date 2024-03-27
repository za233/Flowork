package io.github.r1mao.ir;

import io.github.r1mao.ir.code.Value;

import java.util.ArrayList;

public abstract class IRStatement {
    private CodeBlock holder;

    public String dump() {
        return "";
    }

    public void setHolder(CodeBlock holder) {
        if (this.holder == null)
            this.holder = holder;
        else
            assert false;
    }

    public CodeBlock getHolder() {
        return this.holder;
    }

    public ArrayList<Value> getReadVariable() {
        return new ArrayList<>();
    }

    public ArrayList<Value> getWriteVariable() {
        return new ArrayList<>();
    }

    public void replaceUseOf(Value v1, Value v2) //fix
    {

    }

    public ArrayList<Value> getAllValues() {
        return new ArrayList<>();
    }

}
