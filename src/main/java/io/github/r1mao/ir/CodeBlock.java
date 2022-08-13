package io.github.r1mao.ir;

import io.github.r1mao.DataType;
import io.github.r1mao.OpcodeInfo;
import io.github.r1mao.algorithm.DescriptorParser;
import io.github.r1mao.ir.code.*;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class CodeBlock
{
    private ArrayList<BytecodeWrapper> originalCode=new ArrayList<>();
    private LinkedList<IRStatement> irCode=new LinkedList<>();
    private CodeBlock next=null;
    private IRBasicBlock holder=null;
    private HashMap<BytecodeWrapper, java.util.Stack> stackInfo=new HashMap<>();
    private boolean irGenerated=false;
    private boolean irAnalysed=false;
    private HashSet<IRStatement> genSet=new HashSet<>(),killSet=new HashSet<>();
    private HashSet<IRStatement> inSet=new HashSet<>(),outSet=new HashSet<>();
    public void addBytecodeWrapper(BytecodeWrapper wrapper)
    {
        this.originalCode.add(wrapper);
    }
    public void setHolder(IRBasicBlock basicBlock)
    {
        if(this.holder==null)
            this.holder=basicBlock;
    }
    public ArrayList<BytecodeWrapper> getOriginalCode()
    {
        return this.originalCode;
    }
    public IRBasicBlock getHolder()
    {
        return this.holder;
    }
    public void setNextBlock(CodeBlock next)
    {
        assert this.next==null;
        this.next=next;
    }
    public CodeBlock getNextBlock()
    {
        return this.next;
    }
    public BytecodeWrapper getBytecodeByIndex(int index)
    {

        return this.originalCode.get(index);
    }
    public BytecodeWrapper getFirstBytecode()
    {
        return this.getBytecodeByIndex(0);
    }
    public BytecodeWrapper getLastBytecode()
    {
        int len=this.originalCode.size();
        return this.getBytecodeByIndex(len-1);
    }
    public String displayIRCode()
    {
        String code="";
        for(IRStatement ir:this.irCode)
            code+=ir.dump()+"\n";
        return code;
    }
    public void emulateStack(java.util.Stack<DataType> stackContext)
    {
        for(BytecodeWrapper bytecode:this.getOriginalCode())
        {
            if(OpcodeInfo.needStackContext(bytecode.getOpcode()))
                this.stackInfo.put(bytecode,(java.util.Stack) stackContext.clone());
            DataType type0,type1,type2,type3;
            String desc;
            DescriptorParser parser;
            switch(bytecode.getOpcode())
            {
                case Opcodes.ACONST_NULL:
                case Opcodes.ICONST_0:
                case Opcodes.ICONST_1:
                case Opcodes.ICONST_2:
                case Opcodes.ICONST_3:
                case Opcodes.ICONST_4:
                case Opcodes.ICONST_5:
                case Opcodes.ICONST_M1:
                case Opcodes.BIPUSH:
                case Opcodes.SIPUSH:
                case Opcodes.ILOAD:
                    stackContext.push(DataType.TYPE_INTEGER);
                    break;
                case Opcodes.FCONST_0:
                case Opcodes.FCONST_1:
                case Opcodes.FCONST_2:
                case Opcodes.FLOAD:
                    stackContext.push(DataType.TYPE_FLOAT);
                    break;
                case Opcodes.DCONST_0:
                case Opcodes.DCONST_1:
                case Opcodes.DLOAD:
                    stackContext.push(DataType.TYPE_DOUBLE);
                    break;
                case Opcodes.LCONST_0:
                case Opcodes.LCONST_1:
                case Opcodes.LLOAD:
                    stackContext.push(DataType.TYPE_LONG);
                    break;
                case Opcodes.LDC:
                    type0=DataType.getTypeByObject(bytecode.getOperand(0));
                    stackContext.push(type0);
                    break;
                case Opcodes.ALOAD:
                case Opcodes.NEW:
                    stackContext.push(DataType.TYPE_REFERENCE);
                    break;
                case Opcodes.IALOAD:
                case Opcodes.IADD:
                case Opcodes.ISUB:
                case Opcodes.IMUL:
                case Opcodes.IDIV:
                case Opcodes.IREM:
                case Opcodes.IXOR:
                case Opcodes.IOR:
                case Opcodes.ISHL:
                case Opcodes.ISHR:
                case Opcodes.IAND:
                case Opcodes.IUSHR:
                case Opcodes.BALOAD:
                case Opcodes.CALOAD:
                case Opcodes.SALOAD:
                case Opcodes.LCMP:
                case Opcodes.FCMPL:
                case Opcodes.FCMPG:
                case Opcodes.DCMPL:
                case Opcodes.DCMPG:
                    stackContext.pop();
                    stackContext.pop();
                    stackContext.push(DataType.TYPE_INTEGER);
                    break;
                case Opcodes.INEG:
                case Opcodes.L2I:
                case Opcodes.F2I:
                case Opcodes.D2I:
                case Opcodes.ARRAYLENGTH:
                case Opcodes.INSTANCEOF:
                    stackContext.pop();
                    stackContext.push(DataType.TYPE_INTEGER);
                    break;
                case Opcodes.FALOAD:
                case Opcodes.FADD:
                case Opcodes.FSUB:
                case Opcodes.FMUL:
                case Opcodes.FDIV:
                case Opcodes.FREM:
                    stackContext.pop();
                    stackContext.pop();
                    stackContext.push(DataType.TYPE_FLOAT);
                    break;
                case Opcodes.FNEG:
                case Opcodes.I2F:
                case Opcodes.L2F:
                case Opcodes.D2F:
                    stackContext.pop();
                    stackContext.push(DataType.TYPE_FLOAT);
                    break;
                case Opcodes.DALOAD:
                case Opcodes.DADD:
                case Opcodes.DSUB:
                case Opcodes.DMUL:
                case Opcodes.DDIV:
                case Opcodes.DREM:
                    stackContext.pop();
                    stackContext.pop();
                    stackContext.push(DataType.TYPE_DOUBLE);
                    break;
                case Opcodes.DNEG:
                case Opcodes.F2D:
                case Opcodes.I2D:
                case Opcodes.L2D:
                    stackContext.pop();
                    stackContext.push(DataType.TYPE_DOUBLE);
                    break;
                case Opcodes.LNEG:
                case Opcodes.I2L:
                case Opcodes.F2L:
                case Opcodes.D2L:
                    stackContext.pop();
                    stackContext.push(DataType.TYPE_LONG);
                    break;
                case Opcodes.LALOAD:
                case Opcodes.LADD:
                case Opcodes.LSUB:
                case Opcodes.LMUL:
                case Opcodes.LDIV:
                case Opcodes.LREM:
                case Opcodes.LXOR:
                case Opcodes.LOR:
                case Opcodes.LSHL:
                case Opcodes.LSHR:
                case Opcodes.LAND:
                case Opcodes.LUSHR:
                    stackContext.pop();
                    stackContext.pop();
                    stackContext.push(DataType.TYPE_LONG);
                    break;
                case Opcodes.AALOAD:
                    stackContext.pop();
                    stackContext.pop();
                    stackContext.push(DataType.TYPE_REFERENCE);
                    break;
                case Opcodes.ISTORE:
                case Opcodes.LSTORE:
                case Opcodes.FSTORE:
                case Opcodes.DSTORE:
                case Opcodes.ASTORE:
                case Opcodes.IFEQ:
                case Opcodes.IFNE:
                case Opcodes.IFLT:
                case Opcodes.IFGE:
                case Opcodes.IFGT:
                case Opcodes.IFLE:
                case Opcodes.TABLESWITCH:
                case Opcodes.LOOKUPSWITCH:
                case Opcodes.IRETURN:
                case Opcodes.LRETURN:
                case Opcodes.FRETURN:
                case Opcodes.DRETURN:
                case Opcodes.ARETURN:
                case Opcodes.PUTSTATIC:
                case Opcodes.MONITORENTER:
                case Opcodes.MONITOREXIT:
                case Opcodes.IFNULL:
                case Opcodes.IFNONNULL:
                    stackContext.pop();
                    break;
                case Opcodes.IASTORE:
                case Opcodes.LASTORE:
                case Opcodes.FASTORE:
                case Opcodes.DASTORE:
                case Opcodes.AASTORE:
                case Opcodes.BASTORE:
                case Opcodes.CASTORE:
                case Opcodes.SASTORE:
                    stackContext.pop();
                    stackContext.pop();
                    stackContext.pop();
                    break;
                case Opcodes.POP:
                    type0=stackContext.pop();
                    assert type0.getSizeInSlot()==4;
                    break;
                case Opcodes.POP2:
                    type0=stackContext.pop();
                    if(type0.getSizeInSlot()==4)
                    {
                        type1=stackContext.pop();
                        assert type1.getSizeInSlot()==4;
                    }
                    break;
                case Opcodes.DUP:
                    type0=stackContext.pop();
                    assert type0.getSizeInSlot()==4;
                    stackContext.push(type0);
                    stackContext.push(type0);
                    break;
                case Opcodes.DUP_X1:
                    type0=stackContext.pop();
                    type1=stackContext.pop();
                    assert type0.getSizeInSlot()==4 && type1.getSizeInSlot()==4;
                    stackContext.push(type0);
                    stackContext.push(type1);
                    stackContext.push(type0);
                    break;
                case Opcodes.DUP_X2:
                    type0=stackContext.pop();
                    assert type0.getSizeInSlot()==4;
                    type1=stackContext.pop();
                    if(type1.getSizeInSlot()==4)
                    {
                        type2=stackContext.pop();
                        assert type2.getSizeInSlot()==4;
                        stackContext.push(type0);
                        stackContext.push(type2);
                    }
                    else
                    {

                        stackContext.push(type0);
                    }
                    stackContext.push(type1);
                    stackContext.push(type0);
                    break;
                case Opcodes.DUP2:
                    type0=stackContext.pop();
                    if(type0.getSizeInSlot()==4)
                    {
                        type1=stackContext.pop();
                        assert type1.getSizeInSlot()==4;
                        stackContext.push(type1);
                        stackContext.push(type0);
                        stackContext.push(type1);
                        stackContext.push(type0);
                    }
                    else
                    {
                        stackContext.push(type0);
                        stackContext.push(type0);
                    }
                    break;
                case Opcodes.DUP2_X1:
                    type0=stackContext.pop();
                    if(type0.getSizeInSlot()==4)
                    {
                        type1=stackContext.pop();
                        type2=stackContext.pop();
                        assert type1.getSizeInSlot()==4 && type2.getSizeInSlot()==4;
                        stackContext.push(type1);
                        stackContext.push(type0);
                        stackContext.push(type2);
                        stackContext.push(type1);
                        stackContext.push(type0);
                    }
                    else
                    {
                        type1=stackContext.pop();
                        assert type1.getSizeInSlot()==4;
                        stackContext.push(type0);
                        stackContext.push(type1);
                        stackContext.push(type0);
                    }
                    break;
                case Opcodes.DUP2_X2:
                    type0=stackContext.pop();
                    if(type0.getSizeInSlot()==4)
                    {
                        type1=stackContext.pop();
                        assert type1.getSizeInSlot()==4;
                        type2=stackContext.pop();
                        if(type2.getSizeInSlot()==4)
                        {
                            type3=stackContext.pop();
                            assert type3.getTypeId()==4;
                            stackContext.push(type1);
                            stackContext.push(type0);
                            stackContext.push(type3);
                        }
                        else
                        {
                            stackContext.push(type1);
                            stackContext.push(type0);
                        }
                        stackContext.push(type2);
                        stackContext.push(type1);
                        stackContext.push(type0);
                    }
                    else
                    {
                        type1=stackContext.pop();
                        if(type1.getSizeInSlot()==4)
                        {
                            type2=stackContext.pop();
                            assert type2.getSizeInSlot()==4;
                            stackContext.push(type0);
                            stackContext.push(type2);
                        }
                        else
                        {
                            stackContext.push(type0);
                        }
                        stackContext.push(type1);
                        stackContext.push(type0);
                    }
                    break;
                case Opcodes.SWAP:
                    type0=stackContext.pop();
                    type1=stackContext.pop();
                    assert type0.getSizeInSlot()==4 && type1.getSizeInSlot()==4;
                    stackContext.push(type0);
                    stackContext.push(type1);
                    break;
                case Opcodes.IF_ICMPEQ:
                case Opcodes.IF_ICMPNE:
                case Opcodes.IF_ICMPGE:
                case Opcodes.IF_ICMPGT:
                case Opcodes.IF_ICMPLE:
                case Opcodes.IF_ICMPLT:
                case Opcodes.IF_ACMPEQ:
                case Opcodes.IF_ACMPNE:
                case Opcodes.PUTFIELD:
                    stackContext.pop();
                    stackContext.pop();
                    break;
                case Opcodes.GETSTATIC:
                    desc= (String) bytecode.getOperand(2);
                    stackContext.push(new DescriptorParser(desc,false).getType());
                    break;
                case Opcodes.GETFIELD:
                    stackContext.pop();
                    desc= (String) bytecode.getOperand(2);
                    stackContext.push(new DescriptorParser(desc,false).getType());
                    break;
                case Opcodes.INVOKESTATIC:
                    desc=(String) bytecode.getOperand(2);
                    parser=new DescriptorParser(desc,true);
                    for(int i=0;i<parser.getTypes().size();i++)
                        stackContext.pop();
                    type0=parser.getReturnType();
                    if(type0.getTypeId()!=DataType.VOID)
                        stackContext.push(type0);
                    break;
                case Opcodes.INVOKEVIRTUAL:
                case Opcodes.INVOKESPECIAL:
                case Opcodes.INVOKEINTERFACE:
                    stackContext.pop();
                    desc=(String) bytecode.getOperand(2);
                    parser=new DescriptorParser(desc,true);
                    for(int i=0;i<parser.getTypes().size();i++)
                        stackContext.pop();
                    type0=parser.getReturnType();
                    if(type0.getTypeId()!=DataType.VOID)
                        stackContext.push(type0);
                    break;
                case Opcodes.NEWARRAY:
                case Opcodes.ANEWARRAY:
                    stackContext.pop();
                    stackContext.push(DataType.TYPE_REFERENCE);
                    break;
                case Opcodes.MULTIANEWARRAY:
                    int count= (int) bytecode.getOperand(1);
                    for(int i=0;i<count;i++)
                        stackContext.pop();
                    stackContext.push(DataType.TYPE_REFERENCE);
                    break;

            }
        }

    }

    public void makeIRCode()
    {
        this.irCode.clear();
        int stackPtr=this.getHolder().getStackAddress();
        for(BytecodeWrapper bytecode:this.originalCode)
        {
            java.util.Stack<DataType> stackContext=null;
            if(OpcodeInfo.needStackContext(bytecode.getOpcode()))
            {
                stackContext=stackInfo.get(bytecode);
                stackContext= (java.util.Stack<DataType>) stackContext.clone();
            }
            Object value;
            DataType type0,type1,type2;
            int index,num,min,max;
            IRBasicBlock trueBlock,falseBlock;
            StmtSwitch stmtSwitch;
            Operator op;
            DescriptorParser parser;
            Value[] args;
            assert stackPtr>=0;
            switch(bytecode.getOpcode())
            {
                case Opcodes.ACONST_NULL:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr,4), Constant.getConstant(null)));
                    stackPtr+=4;
                    break;
                case Opcodes.ICONST_M1:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr,4), Constant.getConstant(-1)));
                    stackPtr+=4;
                    break;
                case Opcodes.ICONST_0:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr,4), Constant.getConstant(0)));
                    stackPtr+=4;
                    break;
                case Opcodes.ICONST_1:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr,4), Constant.getConstant(1)));
                    stackPtr+=4;
                    break;
                case Opcodes.ICONST_2:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr,4), Constant.getConstant(2)));
                    stackPtr+=4;
                    break;
                case Opcodes.ICONST_3:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr,4), Constant.getConstant(3)));
                    stackPtr+=4;
                    break;
                case Opcodes.ICONST_4:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr,4), Constant.getConstant(4)));
                    stackPtr+=4;
                    break;
                case Opcodes.ICONST_5:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr,4), Constant.getConstant(5)));
                    stackPtr+=4;
                    break;
                case Opcodes.LCONST_0:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr,8), Constant.getConstant((long)0)));
                    stackPtr+=8;
                    break;
                case Opcodes.LCONST_1:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr,8), Constant.getConstant((long)1)));
                    stackPtr+=8;
                    break;
                case Opcodes.FCONST_0:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr,4), Constant.getConstant((float)0)));
                    stackPtr+=4;
                    break;
                case Opcodes.FCONST_1:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr,4), Constant.getConstant((float)1)));
                    stackPtr+=4;
                    break;
                case Opcodes.FCONST_2:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr,4), Constant.getConstant((float)2)));
                    stackPtr+=4;
                    break;
                case Opcodes.DCONST_0:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr,8), Constant.getConstant((double)0)));
                    stackPtr+=8;
                    break;
                case Opcodes.DCONST_1:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr,8), Constant.getConstant((double)1)));
                    stackPtr+=8;
                    break;
                case Opcodes.SIPUSH:
                case Opcodes.BIPUSH:
                    value=bytecode.getOperand(0);
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr,4), Constant.getConstant(value)));
                    stackPtr+=4;
                    break;
                case Opcodes.LDC:
                    value=bytecode.getOperand(0);
                    type0=DataType.getTypeByObject(value);
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr,type0.getSizeInSlot()), Constant.getConstant(value)));
                    stackPtr+=type0.getSizeInSlot();
                    break;
                case Opcodes.ILOAD:
                    index= (int) bytecode.getOperand(0);
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr,4), Local.getVariable(bytecode,index,DataType.TYPE_INTEGER)));
                    stackPtr+=4;
                    break;
                case Opcodes.ALOAD:
                    index= (int) bytecode.getOperand(0);
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr,4), Local.getVariable(bytecode,index,DataType.TYPE_REFERENCE)));
                    stackPtr+=4;
                    break;
                case Opcodes.LLOAD:
                    index= (int) bytecode.getOperand(0);
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr,8), Local.getVariable(bytecode,index,DataType.TYPE_LONG)));
                    stackPtr+=8;
                    break;
                case Opcodes.FLOAD:
                    index= (int) bytecode.getOperand(0);
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr,4), Local.getVariable(bytecode,index,DataType.TYPE_FLOAT)));
                    stackPtr+=4;
                    break;
                case Opcodes.DLOAD:
                    index= (int) bytecode.getOperand(0);
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr,8), Local.getVariable(bytecode,index,DataType.TYPE_DOUBLE)));
                    stackPtr+=8;
                    break;
                case Opcodes.IALOAD:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-8,4), Operator.getOp(Operator.IARRAY_GET, Stack.getRegion(stackPtr-8,4), Stack.getRegion(stackPtr-4,4))));
                    stackPtr-=4;
                    break;
                case Opcodes.LALOAD:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-8,8), Operator.getOp(Operator.LARRAY_GET, Stack.getRegion(stackPtr-8,4), Stack.getRegion(stackPtr-4,4))));
                    break;
                case Opcodes.FALOAD:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-8,4), Operator.getOp(Operator.FARRAY_GET, Stack.getRegion(stackPtr-8,4), Stack.getRegion(stackPtr-4,4))));
                    stackPtr-=4;
                    break;
                case Opcodes.DALOAD:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-8,8), Operator.getOp(Operator.DARRAY_GET, Stack.getRegion(stackPtr-8,4), Stack.getRegion(stackPtr-4,4))));
                    break;
                case Opcodes.AALOAD:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-8,4), Operator.getOp(Operator.AARRAY_GET, Stack.getRegion(stackPtr-8,4), Stack.getRegion(stackPtr-4,4))));
                    stackPtr-=4;
                    break;
                case Opcodes.BALOAD:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-8,4), Operator.getOp(Operator.BARRAY_GET, Stack.getRegion(stackPtr-8,4), Stack.getRegion(stackPtr-4,4))));
                    stackPtr-=4;
                    break;
                case Opcodes.CALOAD:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-8,4), Operator.getOp(Operator.CARRAY_GET, Stack.getRegion(stackPtr-8,4), Stack.getRegion(stackPtr-4,4))));
                    stackPtr-=4;
                    break;
                case Opcodes.SALOAD:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-8,4), Operator.getOp(Operator.SARRAY_GET, Stack.getRegion(stackPtr-8,4), Stack.getRegion(stackPtr-4,4))));
                    stackPtr-=4;
                    break;
                case Opcodes.ISTORE:
                    index= (int) bytecode.getOperand(0);
                    this.irCode.add(new StmtAssignment(Local.getVariable(bytecode,index,DataType.TYPE_INTEGER), Stack.getRegion(stackPtr-4,4)));
                    stackPtr-=4;
                    break;
                case Opcodes.LSTORE:
                    index= (int) bytecode.getOperand(0);
                    this.irCode.add(new StmtAssignment(Local.getVariable(bytecode,index,DataType.TYPE_LONG), Stack.getRegion(stackPtr-8,8)));
                    stackPtr-=8;
                    break;
                case Opcodes.FSTORE:
                    index= (int) bytecode.getOperand(0);
                    this.irCode.add(new StmtAssignment(Local.getVariable(bytecode,index,DataType.TYPE_FLOAT), Stack.getRegion(stackPtr-4,4)));
                    stackPtr-=4;
                    break;
                case Opcodes.DSTORE:
                    index= (int) bytecode.getOperand(0);
                    this.irCode.add(new StmtAssignment(Local.getVariable(bytecode,index,DataType.TYPE_DOUBLE), Stack.getRegion(stackPtr-8,8)));
                    stackPtr-=8;
                    break;
                case Opcodes.ASTORE:
                    index= (int) bytecode.getOperand(0);
                    this.irCode.add(new StmtAssignment(Local.getVariable(bytecode,index,DataType.TYPE_REFERENCE), Stack.getRegion(stackPtr-4,4)));
                    stackPtr-=4;
                    break;
                case Opcodes.IASTORE:
                    this.irCode.add(new StmtAssignment(Operator.getOp(Operator.IARRAY_GET, Stack.getRegion(stackPtr-12,4), Stack.getRegion(stackPtr-8,4)), Stack.getRegion(stackPtr-4,4)));
                    stackPtr-=12;
                    break;
                case Opcodes.LASTORE:
                    this.irCode.add(new StmtAssignment(Operator.getOp(Operator.LARRAY_GET, Stack.getRegion(stackPtr-16,4), Stack.getRegion(stackPtr-12,4)), Stack.getRegion(stackPtr-8,8)));
                    stackPtr-=16;
                    break;
                case Opcodes.FASTORE:
                    this.irCode.add(new StmtAssignment(Operator.getOp(Operator.FARRAY_GET, Stack.getRegion(stackPtr-12,4), Stack.getRegion(stackPtr-8,4)), Stack.getRegion(stackPtr-4,4)));
                    stackPtr-=12;
                    break;
                case Opcodes.DASTORE:
                    this.irCode.add(new StmtAssignment(Operator.getOp(Operator.DARRAY_GET, Stack.getRegion(stackPtr-16,4), Stack.getRegion(stackPtr-12,4)), Stack.getRegion(stackPtr-8,8)));
                    stackPtr-=16;
                    break;
                case Opcodes.AASTORE:
                    this.irCode.add(new StmtAssignment(Operator.getOp(Operator.AARRAY_GET, Stack.getRegion(stackPtr-12,4), Stack.getRegion(stackPtr-8,4)), Stack.getRegion(stackPtr-4,4)));
                    stackPtr-=12;
                    break;
                case Opcodes.BASTORE:
                    this.irCode.add(new StmtAssignment(Operator.getOp(Operator.BARRAY_GET, Stack.getRegion(stackPtr-12,4), Stack.getRegion(stackPtr-8,4)), Stack.getRegion(stackPtr-4,4)));
                    stackPtr-=12;
                    break;
                case Opcodes.CASTORE:
                    this.irCode.add(new StmtAssignment(Operator.getOp(Operator.CARRAY_GET, Stack.getRegion(stackPtr-12,4), Stack.getRegion(stackPtr-8,4)), Stack.getRegion(stackPtr-4,4)));
                    stackPtr-=12;
                    break;
                case Opcodes.SASTORE:
                    this.irCode.add(new StmtAssignment(Operator.getOp(Operator.SARRAY_GET, Stack.getRegion(stackPtr-12,4), Stack.getRegion(stackPtr-8,4)), Stack.getRegion(stackPtr-4,4)));
                    stackPtr-=12;
                    break;
                case Opcodes.POP:
                    stackPtr-=4;
                    break;
                case Opcodes.POP2:
                    stackPtr-=8;
                    break;
                case Opcodes.DUP:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr,4), Stack.getRegion(stackPtr-4,4)));
                    stackPtr+=4;
                    break;
                case Opcodes.DUP_X1:
                    this.irCode.add(new StmtAssignment(Temp.getTemp(0),Stack.getRegion(stackPtr-8,4)));
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-8,4), Stack.getRegion(stackPtr-4,4)));
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-4,4),Temp.getTemp(0)));
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr,4),Stack.getRegion(stackPtr-8,4)));
                    stackPtr+=4;
                    break;
                case Opcodes.DUP_X2:
                    stackContext.pop();
                    type1=stackContext.pop();
                    if(type1.getSizeInSlot()==4)
                    {
                        this.irCode.add(new StmtAssignment(Temp.getTemp(0),Stack.getRegion(stackPtr-12,4)));
                        this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-12,4),Stack.getRegion(stackPtr-4,4)));
                        this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-4,4),Stack.getRegion(stackPtr-8,4)));
                        this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-8,4),Temp.getTemp(0)));
                        this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr,4),Stack.getRegion(stackPtr-12,4)));
                    }
                    else
                    {
                        this.irCode.add(new StmtAssignment(Temp.getTemp(0),Stack.getRegion(stackPtr-12,8)));
                        this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-12,4), Stack.getRegion(stackPtr-4,4)));
                        this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-8,8),Temp.getTemp(0)));
                        this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr,4),Stack.getRegion(stackPtr-12,4)));
                    }
                    stackPtr+=4;
                    break;
                case Opcodes.DUP2:
                    type0=stackContext.pop();
                    if(type0.getSizeInSlot()==4)
                    {
                        this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr,4),Stack.getRegion(stackPtr-8,4)));
                        this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr+4,4),Stack.getRegion(stackPtr-4,4)));

                    }
                    else
                    {
                        this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr,8),Stack.getRegion(stackPtr-8,8)));
                    }
                    stackPtr+=8;
                    break;
                case Opcodes.DUP2_X1:
                    type0=stackContext.pop();
                    if(type0.getSizeInSlot()==4)
                    {
                        this.irCode.add(new StmtAssignment(Temp.getTemp(0),Stack.getRegion(stackPtr-12,4)));
                        this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-12,4),Stack.getRegion(stackPtr-8,4)));
                        this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-8,4),Stack.getRegion(stackPtr-4,4)));
                        this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-4,4),Temp.getTemp(0)));
                        this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr,4),Stack.getRegion(stackPtr-12,4)));
                        this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr+4,4),Stack.getRegion(stackPtr-8,4)));
                    }
                    else
                    {
                        this.irCode.add(new StmtAssignment(Temp.getTemp(0),Stack.getRegion(stackPtr-12,4)));
                        this.irCode.add(new StmtAssignment(Temp.getTemp(1),Stack.getRegion(stackPtr-8,8)));
                        this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-12,8),Temp.getTemp(1)));
                        this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-4,4),Temp.getTemp(0)));
                        this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr,8),Stack.getRegion(stackPtr-12,8)));
                    }
                    stackPtr+=8;
                    break;
                case Opcodes.DUP2_X2:
                    type0=stackContext.pop();
                    if(type0.getSizeInSlot()==4)
                    {
                        type1=stackContext.pop();
                        assert type1.getSizeInSlot()==4;
                        type2=stackContext.pop();
                        if(type2.getSizeInSlot()==4)
                        {
                            this.irCode.add(new StmtAssignment(Temp.getTemp(0),Stack.getRegion(stackPtr-16,4)));
                            this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-16,4),Stack.getRegion(stackPtr-8,4)));
                            this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-8,4),Temp.getTemp(0)));
                            this.irCode.add(new StmtAssignment(Temp.getTemp(0),Stack.getRegion(stackPtr-12,4)));
                            this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-12,4),Stack.getRegion(stackPtr-4,4)));
                            this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-4,4),Temp.getTemp(0)));
                            this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr,4),Stack.getRegion(stackPtr-16,4)));
                            this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr+4,4),Stack.getRegion(stackPtr-12,4)));
                        }
                        else
                        {
                            this.irCode.add(new StmtAssignment(Temp.getTemp(0),Stack.getRegion(stackPtr-16,8)));
                            this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-16,4),Stack.getRegion(stackPtr-8,4)));
                            this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-12,4),Stack.getRegion(stackPtr-4,4)));
                            this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-8,8),Temp.getTemp(0)));
                            this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr,4),Stack.getRegion(stackPtr-16,4)));
                            this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr+4,4),Stack.getRegion(stackPtr-12,4)));
                        }

                    }
                    else
                    {
                        type1=stackContext.pop();
                        if(type1.getSizeInSlot()==4)
                        {
                            this.irCode.add(new StmtAssignment(Temp.getTemp(0),Stack.getRegion(stackPtr-16,4)));
                            this.irCode.add(new StmtAssignment(Temp.getTemp(1),Stack.getRegion(stackPtr-12,4)));
                            this.irCode.add(new StmtAssignment(Temp.getTemp(2),Stack.getRegion(stackPtr-8,8)));
                            this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-16,8),Temp.getTemp(2)));
                            this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-8,4),Temp.getTemp(0)));
                            this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-4,4),Temp.getTemp(1)));
                            this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr,8),Stack.getRegion(stackPtr-16,8)));
                        }
                        else
                        {
                            this.irCode.add(new StmtAssignment(Temp.getTemp(0),Stack.getRegion(stackPtr-16,8)));
                            this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-16,8),Stack.getRegion(stackPtr-8,8)));
                            this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-8,8),Temp.getTemp(0)));
                            this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr,8),Stack.getRegion(stackPtr-16,8)));
                        }
                    }
                    stackPtr+=8;
                    break;
                case Opcodes.SWAP:
                    this.irCode.add(new StmtAssignment(Temp.getTemp(0),Stack.getRegion(stackPtr-8,4)));
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-8,4),Stack.getRegion(stackPtr-4,4)));
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-4,4),Temp.getTemp(0)));
                    break;

                case Opcodes.IADD:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-8,4),Operator.getOp(Operator.IADD,Stack.getRegion(stackPtr-8,4),Stack.getRegion(stackPtr-4,4))));
                    stackPtr-=4;
                    break;
                case Opcodes.LADD:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-16,8),Operator.getOp(Operator.LADD,Stack.getRegion(stackPtr-16,8),Stack.getRegion(stackPtr-8,8))));
                    stackPtr-=8;
                    break;
                case Opcodes.FADD:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-8,4),Operator.getOp(Operator.FADD,Stack.getRegion(stackPtr-8,4),Stack.getRegion(stackPtr-4,4))));
                    stackPtr-=4;
                    break;
                case Opcodes.DADD:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-16,8),Operator.getOp(Operator.DADD,Stack.getRegion(stackPtr-16,8),Stack.getRegion(stackPtr-8,8))));
                    stackPtr-=8;
                    break;
                case Opcodes.ISUB:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-8,4),Operator.getOp(Operator.ISUB,Stack.getRegion(stackPtr-8,4),Stack.getRegion(stackPtr-4,4))));
                    stackPtr-=4;
                    break;
                case Opcodes.LSUB:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-16,8),Operator.getOp(Operator.LSUB,Stack.getRegion(stackPtr-16,8),Stack.getRegion(stackPtr-8,8))));
                    stackPtr-=8;
                    break;
                case Opcodes.FSUB:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-8,4),Operator.getOp(Operator.FSUB,Stack.getRegion(stackPtr-8,4),Stack.getRegion(stackPtr-4,4))));
                    stackPtr-=4;
                    break;
                case Opcodes.DSUB:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-16,8),Operator.getOp(Operator.DSUB,Stack.getRegion(stackPtr-16,8),Stack.getRegion(stackPtr-8,8))));
                    stackPtr-=8;
                    break;
                case Opcodes.IMUL:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-8,4),Operator.getOp(Operator.IMUL,Stack.getRegion(stackPtr-8,4),Stack.getRegion(stackPtr-4,4))));
                    stackPtr-=4;
                    break;
                case Opcodes.LMUL:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-16,8),Operator.getOp(Operator.LMUL,Stack.getRegion(stackPtr-16,8),Stack.getRegion(stackPtr-8,8))));
                    stackPtr-=8;
                    break;
                case Opcodes.FMUL:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-8,4),Operator.getOp(Operator.FMUL,Stack.getRegion(stackPtr-8,4),Stack.getRegion(stackPtr-4,4))));
                    stackPtr-=4;
                    break;
                case Opcodes.DMUL:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-16,8),Operator.getOp(Operator.DMUL,Stack.getRegion(stackPtr-16,8),Stack.getRegion(stackPtr-8,8))));
                    stackPtr-=8;
                    break;
                case Opcodes.IDIV:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-8,4),Operator.getOp(Operator.IDIV,Stack.getRegion(stackPtr-8,4),Stack.getRegion(stackPtr-4,4))));
                    stackPtr-=4;
                    break;
                case Opcodes.LDIV:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-16,8),Operator.getOp(Operator.LDIV,Stack.getRegion(stackPtr-16,8),Stack.getRegion(stackPtr-8,8))));
                    stackPtr-=8;
                    break;
                case Opcodes.FDIV:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-8,4),Operator.getOp(Operator.FDIV,Stack.getRegion(stackPtr-8,4),Stack.getRegion(stackPtr-4,4))));
                    stackPtr-=4;
                    break;
                case Opcodes.DDIV:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-16,8),Operator.getOp(Operator.DDIV,Stack.getRegion(stackPtr-16,8),Stack.getRegion(stackPtr-8,8))));
                    stackPtr-=8;
                    break;
                case Opcodes.IREM:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-8,4),Operator.getOp(Operator.IREM,Stack.getRegion(stackPtr-8,4),Stack.getRegion(stackPtr-4,4))));
                    stackPtr-=4;
                    break;
                case Opcodes.LREM:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-16,8),Operator.getOp(Operator.LREM,Stack.getRegion(stackPtr-16,8),Stack.getRegion(stackPtr-8,8))));
                    stackPtr-=8;
                    break;
                case Opcodes.FREM:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-8,4),Operator.getOp(Operator.FREM,Stack.getRegion(stackPtr-8,4),Stack.getRegion(stackPtr-4,4))));
                    stackPtr-=4;
                    break;
                case Opcodes.DREM:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-16,8),Operator.getOp(Operator.DREM,Stack.getRegion(stackPtr-16,8),Stack.getRegion(stackPtr-8,8))));
                    stackPtr-=8;
                    break;
                case Opcodes.INEG:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-8,4),Operator.getOp(Operator.INEG,Stack.getRegion(stackPtr-8,4),Stack.getRegion(stackPtr-4,4))));
                    stackPtr-=4;
                    break;
                case Opcodes.LNEG:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-16,8),Operator.getOp(Operator.LNEG,Stack.getRegion(stackPtr-16,8),Stack.getRegion(stackPtr-8,8))));
                    stackPtr-=8;
                    break;
                case Opcodes.FNEG:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-8,4),Operator.getOp(Operator.FNEG,Stack.getRegion(stackPtr-8,4),Stack.getRegion(stackPtr-4,4))));
                    stackPtr-=4;
                    break;
                case Opcodes.DNEG:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-16,8),Operator.getOp(Operator.DNEG,Stack.getRegion(stackPtr-16,8),Stack.getRegion(stackPtr-8,8))));
                    stackPtr-=8;
                    break;
                case Opcodes.ISHL:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-8,4),Operator.getOp(Operator.ISHL,Stack.getRegion(stackPtr-8,4),Stack.getRegion(stackPtr-4,4))));
                    stackPtr-=4;
                    break;
                case Opcodes.LSHL:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-16,8),Operator.getOp(Operator.LSHL,Stack.getRegion(stackPtr-16,8),Stack.getRegion(stackPtr-8,8))));
                    stackPtr-=8;
                    break;
                case Opcodes.ISHR:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-8,4),Operator.getOp(Operator.ISHR,Stack.getRegion(stackPtr-8,4),Stack.getRegion(stackPtr-4,4))));
                    stackPtr-=4;
                    break;
                case Opcodes.LSHR:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-16,8),Operator.getOp(Operator.LSHR,Stack.getRegion(stackPtr-16,8),Stack.getRegion(stackPtr-8,8))));
                    stackPtr-=8;
                    break;
                case Opcodes.IUSHR:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-8,4),Operator.getOp(Operator.IUSHR,Stack.getRegion(stackPtr-8,4),Stack.getRegion(stackPtr-4,4))));
                    stackPtr-=4;
                    break;
                case Opcodes.LUSHR:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-16,8),Operator.getOp(Operator.LUSHR,Stack.getRegion(stackPtr-16,8),Stack.getRegion(stackPtr-8,8))));
                    stackPtr-=8;
                    break;
                case Opcodes.IAND:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-8,4),Operator.getOp(Operator.IAND,Stack.getRegion(stackPtr-8,4),Stack.getRegion(stackPtr-4,4))));
                    stackPtr-=4;
                    break;
                case Opcodes.LAND:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-16,8),Operator.getOp(Operator.LAND,Stack.getRegion(stackPtr-16,8),Stack.getRegion(stackPtr-8,8))));
                    stackPtr-=8;
                    break;
                case Opcodes.IOR:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-8,4),Operator.getOp(Operator.IOR,Stack.getRegion(stackPtr-8,4),Stack.getRegion(stackPtr-4,4))));
                    stackPtr-=4;
                    break;
                case Opcodes.LOR:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-16,8),Operator.getOp(Operator.LOR,Stack.getRegion(stackPtr-16,8),Stack.getRegion(stackPtr-8,8))));
                    stackPtr-=8;
                    break;
                case Opcodes.IXOR:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-8,4),Operator.getOp(Operator.IXOR,Stack.getRegion(stackPtr-8,4),Stack.getRegion(stackPtr-4,4))));
                    stackPtr-=4;
                    break;
                case Opcodes.LXOR:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-16,8),Operator.getOp(Operator.LXOR,Stack.getRegion(stackPtr-16,8),Stack.getRegion(stackPtr-8,8))));
                    stackPtr-=8;
                    break;
                case Opcodes.IINC:
                    index= (int) bytecode.getOperand(0);
                    num= (int) bytecode.getOperand(1);
                    this.irCode.add(new StmtAssignment(Local.getVariable(bytecode,index,DataType.TYPE_INTEGER),Operator.getOp(Operator.IADD,Local.getVariable(bytecode,index,DataType.TYPE_INTEGER),Constant.getConstant(num))));
                    break;
                case Opcodes.I2L:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-4,8),Operator.getOp(Operator.I2L,Stack.getRegion(stackPtr-4,4))));
                    stackPtr+=4;
                    break;
                case Opcodes.I2F:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-4,4),Operator.getOp(Operator.I2F,Stack.getRegion(stackPtr-4,4))));
                    break;
                case Opcodes.I2D:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-4,8),Operator.getOp(Operator.I2D,Stack.getRegion(stackPtr-4,4))));
                    stackPtr+=4;
                    break;
                case Opcodes.L2I:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-8,4),Operator.getOp(Operator.L2I,Stack.getRegion(stackPtr-8,8))));
                    stackPtr-=4;
                    break;
                case Opcodes.L2F:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-8,4),Operator.getOp(Operator.L2F,Stack.getRegion(stackPtr-8,8))));
                    stackPtr-=4;
                    break;
                case Opcodes.L2D:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-8,8),Operator.getOp(Operator.L2D,Stack.getRegion(stackPtr-8,8))));
                    break;
                case Opcodes.F2I:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-4,4),Operator.getOp(Operator.F2I,Stack.getRegion(stackPtr-4,4))));
                    break;
                case Opcodes.F2L:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-4,8),Operator.getOp(Operator.F2L,Stack.getRegion(stackPtr-4,4))));
                    stackPtr+=4;
                    break;
                case Opcodes.F2D:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-4,8),Operator.getOp(Operator.F2D,Stack.getRegion(stackPtr-4,4))));
                    stackPtr+=4;
                    break;
                case Opcodes.D2I:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-8,4),Operator.getOp(Operator.D2I,Stack.getRegion(stackPtr-8,8))));
                    stackPtr-=4;
                    break;
                case Opcodes.D2L:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-8,8),Operator.getOp(Operator.D2L,Stack.getRegion(stackPtr-8,8))));
                    break;
                case Opcodes.D2F:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-8,4),Operator.getOp(Operator.D2F,Stack.getRegion(stackPtr-8,8))));
                    stackPtr-=4;
                    break;
                case Opcodes.I2B:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-4,4),Operator.getOp(Operator.I2B,Stack.getRegion(stackPtr-4,4))));
                    break;
                case Opcodes.I2C:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-4,4),Operator.getOp(Operator.I2C,Stack.getRegion(stackPtr-4,4))));
                    break;
                case Opcodes.I2S:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-4,4),Operator.getOp(Operator.I2S,Stack.getRegion(stackPtr-4,4))));
                    break;

                case Opcodes.LCMP:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-16,4),Operator.getOp(Operator.LCMP,Stack.getRegion(stackPtr-16,8),Stack.getRegion(stackPtr-8,8))));
                    stackPtr-=12;
                    break;
                case Opcodes.FCMPL:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-8,4),Operator.getOp(Operator.FCMPL,Stack.getRegion(stackPtr-8,4),Stack.getRegion(stackPtr-4,4))));
                    stackPtr-=4;
                    break;
                case Opcodes.FCMPG:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-8,4),Operator.getOp(Operator.FCMPG,Stack.getRegion(stackPtr-8,4),Stack.getRegion(stackPtr-4,4))));
                    stackPtr-=4;
                    break;
                case Opcodes.DCMPL:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-16,4),Operator.getOp(Operator.DCMPL,Stack.getRegion(stackPtr-16,8),Stack.getRegion(stackPtr-8,8))));
                    stackPtr-=12;
                    break;
                case Opcodes.DCMPG:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-16,4),Operator.getOp(Operator.DCMPG,Stack.getRegion(stackPtr-16,8),Stack.getRegion(stackPtr-8,8))));
                    stackPtr-=12;
                    break;
                case Opcodes.IFEQ:
                    trueBlock=(IRBasicBlock) this.holder.getSuccessors().get(0);
                    falseBlock=(IRBasicBlock) this.holder.getSuccessors().get(1);
                    this.irCode.add(new StmtBranch(Operator.getOp(Operator.IEQ,Stack.getRegion(stackPtr-4,4),Constant.getConstant(0)),trueBlock,falseBlock));
                    stackPtr-=4;
                    break;
                case Opcodes.IFNE:
                    trueBlock=(IRBasicBlock) this.holder.getSuccessors().get(0);
                    falseBlock=(IRBasicBlock) this.holder.getSuccessors().get(1);
                    this.irCode.add(new StmtBranch(Operator.getOp(Operator.INE,Stack.getRegion(stackPtr-4,4),Constant.getConstant(0)),trueBlock,falseBlock));
                    stackPtr-=4;
                    break;
                case Opcodes.IFLT:
                    trueBlock=(IRBasicBlock) this.holder.getSuccessors().get(0);
                    falseBlock=(IRBasicBlock) this.holder.getSuccessors().get(1);
                    this.irCode.add(new StmtBranch(Operator.getOp(Operator.ILT,Stack.getRegion(stackPtr-4,4),Constant.getConstant(0)),trueBlock,falseBlock));
                    stackPtr-=4;
                    break;
                case Opcodes.IFGE:
                    trueBlock=(IRBasicBlock) this.holder.getSuccessors().get(0);
                    falseBlock=(IRBasicBlock) this.holder.getSuccessors().get(1);
                    this.irCode.add(new StmtBranch(Operator.getOp(Operator.IGE,Stack.getRegion(stackPtr-4,4),Constant.getConstant(0)),trueBlock,falseBlock));
                    stackPtr-=4;
                    break;
                case Opcodes.IFGT:
                    trueBlock=(IRBasicBlock) this.holder.getSuccessors().get(0);
                    falseBlock=(IRBasicBlock) this.holder.getSuccessors().get(1);
                    this.irCode.add(new StmtBranch(Operator.getOp(Operator.IGT,Stack.getRegion(stackPtr-4,4),Constant.getConstant(0)),trueBlock,falseBlock));
                    stackPtr-=4;
                    break;
                case Opcodes.IFLE:
                    trueBlock=(IRBasicBlock) this.holder.getSuccessors().get(0);
                    falseBlock=(IRBasicBlock) this.holder.getSuccessors().get(1);
                    this.irCode.add(new StmtBranch(Operator.getOp(Operator.ILE,Stack.getRegion(stackPtr-4,4),Constant.getConstant(0)),trueBlock,falseBlock));
                    stackPtr-=4;
                    break;
                case Opcodes.IF_ICMPEQ:
                    trueBlock=(IRBasicBlock) this.holder.getSuccessors().get(0);
                    falseBlock=(IRBasicBlock) this.holder.getSuccessors().get(1);
                    this.irCode.add(new StmtBranch(Operator.getOp(Operator.IEQ,Stack.getRegion(stackPtr-8,4),Stack.getRegion(stackPtr-4,4)),trueBlock,falseBlock));
                    stackPtr-=8;
                    break;
                case Opcodes.IF_ICMPNE:
                    trueBlock=(IRBasicBlock) this.holder.getSuccessors().get(0);
                    falseBlock=(IRBasicBlock) this.holder.getSuccessors().get(1);
                    this.irCode.add(new StmtBranch(Operator.getOp(Operator.INE,Stack.getRegion(stackPtr-8,4),Stack.getRegion(stackPtr-4,4)),trueBlock,falseBlock));
                    stackPtr-=8;
                    break;
                case Opcodes.IF_ICMPLT:
                    trueBlock=(IRBasicBlock) this.holder.getSuccessors().get(0);
                    falseBlock=(IRBasicBlock) this.holder.getSuccessors().get(1);
                    this.irCode.add(new StmtBranch(Operator.getOp(Operator.ILT,Stack.getRegion(stackPtr-8,4),Stack.getRegion(stackPtr-4,4)),trueBlock,falseBlock));
                    stackPtr-=8;
                    break;
                case Opcodes.IF_ICMPGE:
                    trueBlock=(IRBasicBlock) this.holder.getSuccessors().get(0);
                    falseBlock=(IRBasicBlock) this.holder.getSuccessors().get(1);
                    this.irCode.add(new StmtBranch(Operator.getOp(Operator.IGE,Stack.getRegion(stackPtr-8,4),Stack.getRegion(stackPtr-4,4)),trueBlock,falseBlock));
                    stackPtr-=8;
                    break;
                case Opcodes.IF_ICMPGT:
                    trueBlock=(IRBasicBlock) this.holder.getSuccessors().get(0);
                    falseBlock=(IRBasicBlock) this.holder.getSuccessors().get(1);
                    this.irCode.add(new StmtBranch(Operator.getOp(Operator.IGT,Stack.getRegion(stackPtr-8,4),Stack.getRegion(stackPtr-4,4)),trueBlock,falseBlock));
                    stackPtr-=8;
                    break;
                case Opcodes.IF_ICMPLE:
                    trueBlock=(IRBasicBlock) this.holder.getSuccessors().get(0);
                    falseBlock=(IRBasicBlock) this.holder.getSuccessors().get(1);
                    this.irCode.add(new StmtBranch(Operator.getOp(Operator.ILE,Stack.getRegion(stackPtr-8,4),Stack.getRegion(stackPtr-4,4)),trueBlock,falseBlock));
                    stackPtr-=8;
                    break;
                case Opcodes.IF_ACMPEQ:
                    trueBlock=(IRBasicBlock) this.holder.getSuccessors().get(0);
                    falseBlock=(IRBasicBlock) this.holder.getSuccessors().get(1);
                    this.irCode.add(new StmtBranch(Operator.getOp(Operator.AEQ,Stack.getRegion(stackPtr-8,4),Stack.getRegion(stackPtr-4,4)),trueBlock,falseBlock));
                    stackPtr-=8;
                    break;
                case Opcodes.IF_ACMPNE:
                    trueBlock=(IRBasicBlock) this.holder.getSuccessors().get(0);
                    falseBlock=(IRBasicBlock) this.holder.getSuccessors().get(1);
                    this.irCode.add(new StmtBranch(Operator.getOp(Operator.ANE,Stack.getRegion(stackPtr-8,4),Stack.getRegion(stackPtr-4,4)),trueBlock,falseBlock));
                    stackPtr-=8;
                    break;
                case Opcodes.GOTO:
                    trueBlock=(IRBasicBlock) this.holder.getSuccessors().get(0);
                    this.irCode.add(new StmtBranch(trueBlock));
                    break;

                case Opcodes.TABLESWITCH:
                    min= (int) bytecode.getOperand(0);
                    max= (int) bytecode.getOperand(1);
                    stmtSwitch=new StmtSwitch(Stack.getRegion(stackPtr-4,4),(IRBasicBlock) this.holder.getSuccessors().get(0));
                    index=0;
                    for(int i=min;i<=max;i++)
                    {
                        stmtSwitch.addCase(i,(IRBasicBlock) this.holder.getSuccessors().get(index+1));
                        index++;
                    }
                    this.irCode.add(stmtSwitch);
                    stackPtr-=4;
                    break;
                case Opcodes.LOOKUPSWITCH:
                    stmtSwitch=new StmtSwitch(Stack.getRegion(stackPtr-4,4),(IRBasicBlock) this.holder.getSuccessors().get(0));
                    index=0;
                    int key[]=(int[])bytecode.getOperand(1);
                    for(int i=0;i<key.length;i++)
                    {
                        stmtSwitch.addCase(key[i],(IRBasicBlock) this.holder.getSuccessors().get(index+1));
                        index++;
                    }
                    this.irCode.add(stmtSwitch);
                    stackPtr-=4;
                    break;
                case Opcodes.IRETURN:
                    this.irCode.add(new StmtReturn(Stack.getRegion(stackPtr-4,4),DataType.TYPE_INTEGER));
                    stackPtr-=4;
                    break;
                case Opcodes.LRETURN:
                    this.irCode.add(new StmtReturn(Stack.getRegion(stackPtr-8,8),DataType.TYPE_LONG));
                    stackPtr-=8;
                    break;
                case Opcodes.FRETURN:
                    this.irCode.add(new StmtReturn(Stack.getRegion(stackPtr-4,4),DataType.TYPE_FLOAT));
                    stackPtr-=4;
                    break;
                case Opcodes.DRETURN:
                    this.irCode.add(new StmtReturn(Stack.getRegion(stackPtr-8,8),DataType.TYPE_DOUBLE));
                    stackPtr-=8;
                    break;
                case Opcodes.ARETURN:
                    this.irCode.add(new StmtReturn(Stack.getRegion(stackPtr-4,4),DataType.TYPE_REFERENCE));
                    stackPtr-=4;
                    break;
                case Opcodes.RETURN:
                    this.irCode.add(new StmtReturn(null,DataType.TYPE_VOID));
                    break;

                case Opcodes.GETSTATIC:
                    type0=new DescriptorParser((String)bytecode.getOperand(2),false).getType();
                    op=Operator.getOp(Operator.GETSTATIC);
                    op.addMetadata("owner",(String)bytecode.getOperand(0));
                    op.addMetadata("name",(String)bytecode.getOperand(1));
                    op.addMetadata("descriptor",(String)bytecode.getOperand(2));
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr,type0.getSizeInSlot()),op));
                    stackPtr+=type0.getSizeInSlot();
                    break;
                case Opcodes.PUTSTATIC:
                    type0=new DescriptorParser((String)bytecode.getOperand(2),false).getType();
                    op=Operator.getOp(Operator.PUTSTATIC);
                    op.addMetadata("owner",(String)bytecode.getOperand(0));
                    op.addMetadata("name",(String)bytecode.getOperand(1));
                    op.addMetadata("descriptor",(String)bytecode.getOperand(2));
                    this.irCode.add(new StmtAssignment(op,Stack.getRegion(stackPtr-type0.getSizeInSlot(),type0.getSizeInSlot())));
                    stackPtr-=type0.getSizeInSlot();
                    break;
                case Opcodes.GETFIELD:
                    type0=new DescriptorParser((String)bytecode.getOperand(2),false).getType();
                    op=Operator.getOp(Operator.GETFIELD,Stack.getRegion(stackPtr-4,4));
                    op.addMetadata("owner",(String)bytecode.getOperand(0));
                    op.addMetadata("name",(String)bytecode.getOperand(1));
                    op.addMetadata("descriptor",(String)bytecode.getOperand(2));
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-4,type0.getSizeInSlot()),op));
                    stackPtr-=4;
                    stackPtr+=type0.getSizeInSlot();
                    break;
                case Opcodes.PUTFIELD:
                    type0=new DescriptorParser((String)bytecode.getOperand(2),false).getType();
                    op=Operator.getOp(Operator.PUTFIELD,Stack.getRegion(stackPtr-type0.getSizeInSlot()-4,4));
                    op.addMetadata("owner",(String)bytecode.getOperand(0));
                    op.addMetadata("name",(String)bytecode.getOperand(1));
                    op.addMetadata("descriptor",(String)bytecode.getOperand(2));
                    this.irCode.add(new StmtAssignment(op,Stack.getRegion(stackPtr-type0.getSizeInSlot(),type0.getSizeInSlot())));
                    stackPtr-=4;
                    stackPtr-=type0.getSizeInSlot();
                    break;
                case Opcodes.INVOKESTATIC:
                    parser=new DescriptorParser((String) bytecode.getOperand(2),true);
                    args=new Value[parser.getArgumentNum()];
                    index=0;
                    num=stackPtr-parser.getArgumentSizeInSlot();
                    for(DataType type:parser.getTypes())
                    {
                        args[index]=Stack.getRegion(num,type.getSizeInSlot());
                        num+=type.getSizeInSlot();
                        index++;
                    }

                    stackPtr-=parser.getArgumentSizeInSlot();
                    op=Operator.getOp(Operator.INVOKESTATIC,args);
                    op.addMetadata("owner",(String)bytecode.getOperand(0));
                    op.addMetadata("name",(String)bytecode.getOperand(1));
                    op.addMetadata("descriptor",(String)bytecode.getOperand(2));
                    type0=parser.getReturnType();
                    if(type0==DataType.TYPE_VOID)
                        this.irCode.add(new StmtAssignment(null,op));
                    else
                    {
                        this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr,type0.getSizeInSlot()),op));
                        stackPtr+=type0.getSizeInSlot();
                    }
                    break;
                case Opcodes.INVOKEVIRTUAL:
                case Opcodes.INVOKESPECIAL:
                case Opcodes.INVOKEINTERFACE:
                    parser=new DescriptorParser((String) bytecode.getOperand(2),true);
                    args=new Value[parser.getArgumentNum()+1];
                    index=1;
                    num=stackPtr-parser.getArgumentSizeInSlot();
                    args[0]=Stack.getRegion(num-4,4);
                    for(DataType type:parser.getTypes())
                    {
                        args[index]=Stack.getRegion(num,type.getSizeInSlot());
                        num+=type.getSizeInSlot();
                        index++;
                    }
                    stackPtr-=parser.getArgumentSizeInSlot();
                    stackPtr-=4;
                    op=Operator.getOp(Operator.INVOKEMETHOD,args);
                    op.addMetadata("owner",(String)bytecode.getOperand(0));
                    op.addMetadata("name",(String)bytecode.getOperand(1));
                    op.addMetadata("descriptor",(String)bytecode.getOperand(2));
                    type0=parser.getReturnType();
                    if(type0==DataType.TYPE_VOID)
                        this.irCode.add(new StmtAssignment(null,op));
                    else
                    {
                        this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr,type0.getSizeInSlot()),op));
                        stackPtr+=type0.getSizeInSlot();
                    }
                    break;

                case Opcodes.NEW:
                    op=Operator.getOp(Operator.NEW);
                    op.addMetadata("type",(String)bytecode.getOperand(0));
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr,4),op));
                    stackPtr+=4;
                    break;
                case Opcodes.NEWARRAY:
                    num= (int) bytecode.getOperand(0);
                    op=Operator.getOp(Operator.NEWARRAY,Stack.getRegion(stackPtr-4,4));
                    stackPtr-=4;
                    op.addMetadata("rawType",(String)DataType.getTypeName(DataType.getTypeByArrayTypeID(num)));
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr,4),op));
                    stackPtr+=4;
                    break;
                case Opcodes.ANEWARRAY:
                    op=Operator.getOp(Operator.ANEWARRAY,Stack.getRegion(stackPtr-4,4));
                    stackPtr-=4;
                    op.addMetadata("type",(String)bytecode.getOperand(0));
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr,4),op));
                    stackPtr+=4;
                    break;
                case Opcodes.ARRAYLENGTH:
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-4,4),Operator.getOp(Operator.ARRAY_LEN,Stack.getRegion(stackPtr-4,4))));
                    break;
                case Opcodes.ATHROW:
                    this.irCode.add(new StmtThrow(Stack.getRegion(stackPtr-4,4)));
                    break;
                case Opcodes.CHECKCAST:
                    op=Operator.getOp(Operator.CHECKCAST,Stack.getRegion(stackPtr-4,4));
                    op.addMetadata("type",(String)bytecode.getOperand(0));
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-4,4),op));
                    break;
                case Opcodes.INSTANCEOF:
                    op=Operator.getOp(Operator.INSTANCEOF,Stack.getRegion(stackPtr-4,4));
                    op.addMetadata("type",(String)bytecode.getOperand(0));
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr-4,4),op));
                    break;
                case Opcodes.MONITORENTER:
                    this.irCode.add(new StmtMonitor(Stack.getRegion(stackPtr-4,4),true));
                    stackPtr-=4;
                    break;
                case Opcodes.MONITOREXIT:
                    this.irCode.add(new StmtMonitor(Stack.getRegion(stackPtr-4,4),false));
                    stackPtr-=4;
                    break;
                case Opcodes.MULTIANEWARRAY:
                    num= (int) bytecode.getOperand(1);
                    args=new Value[num];
                    for(int i=0;i<args.length;i++)
                        args[i]=Stack.getRegion(stackPtr-4*num+4*i,4);
                    stackPtr-=4*num;
                    op=Operator.getOp(Operator.MULTI_NEWARRAY,args);
                    op.addMetadata("descriptor",(String)bytecode.getOperand(0));
                    this.irCode.add(new StmtAssignment(Stack.getRegion(stackPtr,4),op));
                    stackPtr+=4;
                    break;
                case Opcodes.IFNULL:
                    trueBlock=(IRBasicBlock) this.holder.getSuccessors().get(0);
                    falseBlock=(IRBasicBlock) this.holder.getSuccessors().get(1);
                    this.irCode.add(new StmtBranch(Operator.getOp(Operator.AEQ,Stack.getRegion(stackPtr-4,4),Constant.getConstant(null)),trueBlock,falseBlock));
                    stackPtr-=4;
                    break;
                case Opcodes.IFNONNULL:
                    trueBlock=(IRBasicBlock) this.holder.getSuccessors().get(0);
                    falseBlock=(IRBasicBlock) this.holder.getSuccessors().get(1);
                    this.irCode.add(new StmtBranch(Operator.getOp(Operator.ANE,Stack.getRegion(stackPtr-4,4),Constant.getConstant(null)),trueBlock,falseBlock));
                    stackPtr-=4;
                    break;
                case Opcodes.NOP:
                    break;
                case Opcodes.INVOKEDYNAMIC:
                case Opcodes.JSR:
                case Opcodes.RET:
                default:
                    assert false;
                    break;
            }
        }
        for(IRStatement s:this.irCode)
            s.setHolder(this);
        this.irGenerated=true;

    }
    public IRMethod getMethod()
    {
        return (IRMethod) this.holder.getParent();
    }
    private HashSet<IRStatement> getKillByStmt(IRStatement stmt)
    {
        HashSet<IRStatement> kill=new HashSet<>();
        for(Value var:stmt.getWriteVariable())
        {
            HashSet<IRStatement> set=(HashSet<IRStatement>) this.getMethod().getDefineMap().get(var).clone();
            set.remove(stmt);
            kill.addAll(set);
        }
        return kill;
    }
    private HashSet<IRStatement> getGenByStmt(IRStatement stmt)
    {
        HashSet<IRStatement> gen=new HashSet<>();
        if(stmt.getWriteVariable().size()!=0)
            gen.add(stmt);
        return gen;
    }
    public HashSet<IRStatement> getKillSet()
    {
        return this.killSet;
    }
    public HashSet<IRStatement> getGenSet()
    {
        return this.genSet;
    }
    public void getAliveDefine(HashSet<IRStatement> initialDef,int index)
    {
        for(int i=0;i<index;i++)
        {
            IRStatement stmt=this.irCode.get(i);
            initialDef.removeAll(this.getKillByStmt(stmt));
            initialDef.addAll(this.getGenByStmt(stmt));
        }
    }
    public void analyseIRCodeDataflow()
    {
        assert this.irGenerated==true;
        this.irAnalysed=true;
        this.genSet.clear();
        this.killSet.clear();
        for(IRStatement stmt:this.irCode)
            this.killSet.addAll(this.getKillByStmt(stmt));
        for(int i=this.irCode.size()-1;i>=0;i--)
        {
            IRStatement stmt=this.irCode.get(i);
            HashSet<IRStatement> littleGen=this.getGenByStmt(stmt);
            for(int j=i+1;j<irCode.size();j++)
            {
                IRStatement tmp=this.irCode.get(j);
                HashSet<IRStatement> littleKill=this.getKillByStmt(tmp);
                littleGen.removeAll(littleKill);
                if(littleGen.size()==0)
                    break;
            }
            this.genSet.addAll(littleGen);
        }
    }
    public LinkedList<IRStatement> getIRCode()
    {
        assert this.irGenerated==true;
        return this.irCode;
    }
}
