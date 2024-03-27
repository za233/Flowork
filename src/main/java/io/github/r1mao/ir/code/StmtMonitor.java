package io.github.r1mao.ir.code;

import io.github.r1mao.ir.IRStatement;

import java.util.ArrayList;

public class StmtMonitor extends IRStatement {
    private Value value;
    private boolean isEnter;

    public StmtMonitor(Value value, boolean isEnter) {
        this.value = value;
        this.isEnter = isEnter;
    }

    public Value getVariable() {
        return value;
    }

    @Override
    public String dump() {
        if (this.isEnter)
            return "monitor_enter(" + this.value.dump() + ");";
        else
            return "monitor_exit(" + this.value.dump() + ");";
    }

    @Override
    public ArrayList<Value> getReadVariable() {
        ArrayList<Value> arr = new ArrayList<>();
        arr.addAll(Value.getVariableValue(this.value));
        return arr;
    }

    @Override
    public ArrayList<Value> getAllValues() {
        ArrayList<Value> a = new ArrayList<>();
        a.add(value);
        return a;
    }

    @Override
    public void replaceUseOf(Value v1, Value v2) {
        if (!value.isLeafValue() && v1 != value) {
            Value.replaceUseOf(value, v1, v2);
        } else {
            if (v1 == value)
                value = v2;
        }
    }
}
