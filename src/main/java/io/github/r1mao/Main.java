package io.github.r1mao;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

public class Main {
    public static void main(String[] args) throws Exception {
        ClassReader reader = new ClassReader("io.github.r1mao.algorithm.SSAGenerator");
        ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);
        ClassWalker visitor = new ClassWalker(Opcodes.ASM8, writer, false);
        reader.accept(visitor, ClassReader.EXPAND_FRAMES);

    }
}
