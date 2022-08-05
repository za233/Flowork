package io.github.r1mao;

import io.github.r1mao.algorithm.DescriptorParser;
import io.github.r1mao.ir.BytecodeWrapper;
import org.objectweb.asm.Opcodes;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

public class OpcodeInfo
{

    public static boolean isJump(int opcode)
    {
        return jumpInsn.contains(opcode);
    }
    public static boolean isReturn(int opcode)
    {
        return  opcode==Opcodes.ATHROW || (opcode>=Opcodes.IRETURN && opcode<=Opcodes.RETURN) || opcode==Opcodes.RET;
    }
    public static boolean isConditionalJump(int opcode)
    {
        if(!isJump(opcode))
            return false;
        if(opcode==Opcodes.GOTO || opcode==Opcodes.JSR)
            return false;
        return true;
    }
    public static boolean needStackContext(int opcode)
    {
        return opcode==Opcodes.DUP_X2 || opcode==Opcodes.DUP2 || opcode==Opcodes.DUP2_X1 || opcode==Opcodes.DUP2_X2;
    }

    private static HashSet<Integer> jumpInsn=new HashSet<>();
    static
    {

        jumpInsn.add(Opcodes.IFEQ);
        jumpInsn.add(Opcodes.IFNE);
        jumpInsn.add(Opcodes.IFLT);
        jumpInsn.add(Opcodes.IFGE);
        jumpInsn.add(Opcodes.IFGT);
        jumpInsn.add(Opcodes.IFLE);
        jumpInsn.add(Opcodes.IF_ICMPEQ);
        jumpInsn.add(Opcodes.IF_ICMPNE);
        jumpInsn.add(Opcodes.IF_ICMPLT);
        jumpInsn.add(Opcodes.IF_ICMPGE);
        jumpInsn.add(Opcodes.IF_ICMPGT);
        jumpInsn.add(Opcodes.IF_ICMPLE);
        jumpInsn.add(Opcodes.IF_ACMPEQ);
        jumpInsn.add(Opcodes.IF_ACMPNE);
        jumpInsn.add(Opcodes.GOTO);
        jumpInsn.add(Opcodes.IFNULL);
        jumpInsn.add(Opcodes.IFNONNULL);
    }

    public static boolean isSwitch(int opcode)
    {
        return opcode==Opcodes.TABLESWITCH || opcode==Opcodes.LOOKUPSWITCH;
    }
}
