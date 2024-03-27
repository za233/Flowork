package io.github.r1mao.ir.code;

import io.github.r1mao.ir.IRStatement;

import java.util.ArrayList;

public class StmtAssignment extends IRStatement {
    private Value to, value;

    public StmtAssignment(Value to, Value value) {
        this.to = to;
        this.value = value;
    }

    @Override
    public String dump() {
        if (to != null)
            return to.dump() + " = " + value.dump() + ";";
        else
            return value.dump() + ";";


    }

    public Value getDefValue() {
        return this.to;
    }

    public Value getValue() {
        return this.value;
    }

    public void setDefValue(Value def) {
        this.to = def;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    @Override
    public ArrayList<Value> getReadVariable() {
        ArrayList<Value> arr = new ArrayList<>();
        arr.addAll(Value.getVariableValue(this.value));
        if (this.to != null && !this.to.isLeafValue())
            arr.addAll(Value.getVariableValue(this.to));
        return arr;
    }

    @Override
    public ArrayList<Value> getWriteVariable() {
        ArrayList<Value> arr = new ArrayList<>();
        if (this.to != null && this.to.isVariableValue())
            arr.add(this.to);
        return arr;
    }

    @Override
    public ArrayList<Value> getAllValues() {
        ArrayList<Value> a = new ArrayList<>();
        a.add(to);
        a.add(value);
        return a;
    }

    public void replaceUseOfTo(Value v1, Value v2) {
        if (to != null) {
            if (!to.isLeafValue() && v1 != to) {
                Value.replaceUseOf(to, v1, v2);
            } else {
                if (v1 == to)
                    to = v2;
            }
        }
    }

    public void replaceUseOfValue(Value v1, Value v2) {
        if (!value.isLeafValue() && v1 != value) {
            Value.replaceUseOf(value, v1, v2);
        } else {
            if (v1 == value)
                value = v2;
        }
    }

    @Override
    public void replaceUseOf(Value v1, Value v2) {
        replaceUseOfTo(v1, v2);
        replaceUseOfValue(v1, v2);


    }
}
