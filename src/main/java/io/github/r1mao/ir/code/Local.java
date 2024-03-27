package io.github.r1mao.ir.code;

import io.github.r1mao.DataType;
import io.github.r1mao.algorithm.DescriptorParser;
import io.github.r1mao.ir.BytecodeWrapper;

import java.util.ArrayList;

public class Local extends Value {
    private DataType type;
    private String desc;
    private String name;
    private int slotIndex;
    private static ArrayList<Local> cache = new ArrayList<>();

    private Local(String name, String desc, DataType type) {
        super(Value.LOCAL);
        this.name = name;
        this.desc = desc;
        this.type = type;
    }

    private Local(int slotIndex, DataType type) {
        super(Value.LOCAL);
        this.slotIndex = slotIndex;
        this.type = type;

    }

    public static Local getVariable(BytecodeWrapper bytecode, int index, DataType type) {
        if (bytecode.getVariableTable().containsKey(index))
            return getVariable(bytecode.getVariableName(index), bytecode.getVariableDesc(index));
        else
            return getVariable(index, type);
    }

    private static Local getVariable(String name, String desc) {
        for (Local var : cache) {
            if (var.name == name && var.desc == desc)
                return var;
        }
        Local l = new Local(name, desc, new DescriptorParser(desc, false).getType());
        cache.add(l);
        return l;
    }

    private static Local getVariable(int slot, DataType type) {
        for (Local var : cache) {
            if (var.name == null && var.slotIndex == slot && var.type == type)
                return var;
        }
        Local l = new Local(slot, type);
        cache.add(l);
        return l;
    }

    @Override
    public String dump() {
        if (name != null) {
            return name;
        }
        return "var" + slotIndex;
    }

    public static ArrayList<Local> getAllLocals() {
        return cache;
    }

}
