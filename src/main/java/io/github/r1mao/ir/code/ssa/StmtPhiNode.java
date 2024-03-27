package io.github.r1mao.ir.code.ssa;

import io.github.r1mao.ir.IRBasicBlock;
import io.github.r1mao.ir.IRStatement;
import io.github.r1mao.ir.code.Value;

import java.util.ArrayList;
import java.util.HashMap;


public class StmtPhiNode extends IRStatement {
    private final HashMap<IRBasicBlock, Value> map;
    private Value var;
    private final Value originalVar;

    public StmtPhiNode(Value var) {
        this.map = new HashMap<>();
        this.var = var;
        this.originalVar = var;
        assert var.isVariableValue();
    }

    public Value getOriginalValue() {
        return this.originalVar;
    }

    public void setValue(Value value) {
        assert value.getValueType() == Value.STATIC_VALUE;
        this.var = value;
    }

    public Value getValue() {
        return this.var;
    }

    public void addEdge(IRBasicBlock bb, Value v) {
        assert this.getHolder().getHolder().getPredecessors().contains(bb);
        this.map.put(bb, v);
    }

    public String dump() {
        if (this.map.size() == 0) {

            if (this.var.getValueType() == Value.STATIC_VALUE) {
                StaticValue v = (StaticValue) this.var;
                return this.var.dump() + " = phi(" + v.getTarget().dump() + ")";
            }
            return this.var.dump() + " = phi(" + this.var.dump() + ")";

        }
        String result = this.var.dump() + " = phi(";
        int index = 0;
        for (Value v : this.map.values()) {
            if (v == null)
                result += "null";
            else
                result += v.dump();
            if (index != this.map.size() - 1)
                result += ", ";
            index++;
        }
        return result + ");";
    }

    @Override
    public ArrayList<Value> getAllValues() {
        ArrayList<Value> a = new ArrayList<>();
        a.add(var);
        for (Value v : this.map.values())
            a.add(v);
        return a;
    }

    @Override
    public void replaceUseOf(Value v1, Value v2) //fix
    {
        if (var == v1)
            var = v2;
        for (IRBasicBlock bb : this.map.keySet()) {

            if (this.map.get(bb) == null)
                continue;
            assert this.map.get(bb).isVariableValue();
            if (this.map.get(bb) == v1)
                this.map.put(bb, v2);
        }
    }
}
