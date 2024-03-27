package io.github.r1mao.ir.code.ssa;

import io.github.r1mao.ir.IRStatement;
import io.github.r1mao.ir.code.Value;

import java.util.ArrayList;

public class StaticValue extends Value {
    private Value target;
    private static ArrayList<StaticValue> allStaticValue = new ArrayList<StaticValue>();
    private ArrayList<IRStatement> users = new ArrayList<>();
    private boolean undefine = false;

    protected StaticValue(Value target) {
        super(STATIC_VALUE);
        assert target.isVariableValue();
        this.target = target;
    }

    public void setUndefineValue(boolean flag) {
        this.undefine = true;
    }

    public boolean isUndefineValue() {
        return this.undefine;
    }

    public static StaticValue newValue(Value target) {
        StaticValue v = new StaticValue(target);
        allStaticValue.add(v);
        return v;
    }

    public Value getTarget() {
        return this.target;
    }

    public void addUse(IRStatement user) {
        this.users.add(user);
    }

    public ArrayList<IRStatement> getUsers() {
        return this.users;
    }

    public static ArrayList<StaticValue> getAllValues() {
        return allStaticValue;
    }

    public String dump() {
        return "%" + allStaticValue.indexOf(this);
    }

    public static void releaseValue(StaticValue value) {
        allStaticValue.remove(value);
    }
}
