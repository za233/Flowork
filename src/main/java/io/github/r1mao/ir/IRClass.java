package io.github.r1mao.ir;

import java.util.ArrayList;

public class IRClass {
    private final int access;
    private final String name;
    private final String signature;
    private final String superName;
    private final String[] interfaces;
    private ArrayList<IRMethod> methods = new ArrayList<>();

    public IRClass(int access, String name, String signature, String superName, String[] interfaces) {
        this.access = access;
        this.name = name;
        this.signature = signature;
        this.superName = superName;
        this.interfaces = interfaces;
    }

    public void addMethod(IRMethod method) {
        this.methods.add(method);
    }

    public String getName() {
        return this.name;
    }
}
