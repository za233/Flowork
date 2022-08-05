package io.github.r1mao;

import io.github.r1mao.algorithm.Node;
import io.github.r1mao.ir.BytecodeWrapper;
import io.github.r1mao.ir.CodeBlock;
import io.github.r1mao.ir.IRBasicBlock;
import io.github.r1mao.ir.IRMethod;
import javafx.util.Pair;
import org.objectweb.asm.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ClassWalker extends ClassVisitor
{
    protected ClassWalker(int api,ClassVisitor v)
    {
        super(api,v);
    }
    class MethodWalker extends MethodVisitor
    {
        class VariableInfo
        {
            public int index;
            public String name,desc;
            public VariableInfo(int index,String name,String desc)
            {
                this.name=name;
                this.desc=desc;
                this.index=index;
            }
        }
        private ArrayList<BytecodeWrapper> wrappers=new ArrayList<>();
        private HashMap<Label,BytecodeWrapper> labelMap=new HashMap<>();
        private Label cur;
        private IRMethod method;
        private HashSet<Label> bucket=new HashSet<>();
        private HashSet<BytecodeWrapper> splitPoint=new HashSet<>();
        private boolean shouldSplit=false;
        private ArrayList<Pair<Label,VariableInfo>> startVar=new ArrayList<>(),endVar=new ArrayList<>();
        protected MethodWalker(int api, MethodVisitor methodVisitor)
        {
            super(api,methodVisitor);
        }
        public void visitParameter(final String name, final int access)
        {
            super.visitParameter(name,access);
        }
        public void init(int access, String name, String desc, String signature, String[] exceptions)
        {
            System.out.println("working method: "+name+" "+desc);
            /*DescriptorParser parser=new DescriptorParser(desc,true);
            ArrayList<String> bucket=new ArrayList<>();
            try
            {
                String o="";
                for(DataType type:parser.getTypes())
                    o+=DataType.getTypeName(type)+" ";
                System.out.println(o);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
*/
            this.method=new IRMethod(access,name,desc,signature,exceptions);
        }
        public IRMethod getMethod()
        {
            return null;
        }
        public BytecodeWrapper wrap(final int opcode,final Object ...operand)
        {
            //System.out.println(opcode);
            BytecodeWrapper wrapper=new BytecodeWrapper(opcode,operand);
            if(this.cur!=null)
            {
                this.labelMap.put(cur,wrapper);
                if(this.bucket.contains(this.cur))
                {
                    this.bucket.remove(this.cur);
                    this.splitPoint.add(wrapper);
                }
                this.cur=null;
            }
            if(this.shouldSplit)
            {
                this.splitPoint.add(wrapper);
                this.shouldSplit=false;
            }
            this.wrappers.add(wrapper);
            return wrapper;
        }
        public void pushSplitPointOrLater(Label label)
        {
            if(this.labelMap.containsKey(label))
                this.splitPoint.add(this.labelMap.get(label));
            else
                this.bucket.add(label);
        }
        public void visitLocalVariable(final String name, final String descriptor, final String signature, final Label start, final Label end, final int index)
        {
            assert start!=null && end!=null;
            super.visitLocalVariable(name,descriptor,signature,start,end,index);
            VariableInfo info=new VariableInfo(index,name,descriptor);
            this.startVar.add(new Pair(start,info));
            this.endVar.add(new Pair(end,info));
        }
        public void visitIntInsn(final int opcode, final int operand)
        {
            super.visitIntInsn(opcode,operand);
            wrap(opcode,operand);
        }
        public void visitInsn(final int opcode)
        {

            super.visitInsn(opcode);
            wrap(opcode);
            if(OpcodeInfo.isReturn(opcode))
                this.shouldSplit=true;
        }
        public void visitVarInsn(final int opcode, final int varIndex)
        {

            super.visitVarInsn(opcode,varIndex);
            wrap(opcode,varIndex);
            if(OpcodeInfo.isReturn(opcode))
                this.shouldSplit=true;
        }
        public void visitTypeInsn(final int opcode, final String type)
        {

            super.visitTypeInsn(opcode,type);
            wrap(opcode,type);
        }
        public void visitFieldInsn(final int opcode, final String owner, final String name, final String descriptor)
        {

            super.visitFieldInsn(opcode, owner, name, descriptor);
            wrap(opcode, owner, name, descriptor);
        }
        public void visitMethodInsn(final int opcode, final String owner, final String name, final String descriptor, final boolean isInterface)
        {
            super.visitMethodInsn(opcode,owner,name,descriptor,isInterface);
            wrap(opcode,owner,name,descriptor,isInterface);
        }
        public void visitInvokeDynamicInsn(final String name, final String descriptor, final Handle bootstrapMethodHandle, final Object... bootstrapMethodArguments)
        {
            System.out.println("InvokeDynamic currently not supported ");
            assert false;
            super.visitInvokeDynamicInsn(name,descriptor,bootstrapMethodHandle,bootstrapMethodArguments);
            wrap(Opcodes.INVOKEDYNAMIC,name,descriptor,bootstrapMethodHandle,bootstrapMethodArguments);
        }
        public void visitJumpInsn(final int opcode, final Label label)
        {

            super.visitJumpInsn(opcode,label);
            wrap(opcode,label);
            pushSplitPointOrLater(label);
            this.shouldSplit=true;
        }
        public void visitLabel(final Label label)
        {

            super.visitLabel(label);
            this.cur=label;
        }
        public void visitLdcInsn(final Object value)
        {

            super.visitLdcInsn(value);
            wrap(Opcodes.LDC,value);
        }
        public void visitIincInsn(final int varIndex, final int increment)
        {

            super.visitIincInsn(varIndex,increment);
            wrap(Opcodes.IINC,varIndex,increment);
        }
        public void visitTableSwitchInsn(final int min, final int max, final Label dflt, final Label[] labels)
        {

            super.visitTableSwitchInsn(min,max,dflt,labels);
            wrap(Opcodes.TABLESWITCH,min,max,dflt,labels);
            for(Label l:labels)
                pushSplitPointOrLater(l);
            this.shouldSplit=true;
        }
        public void visitLookupSwitchInsn(final Label dflt, final int[] keys, final Label[] labels)
        {

            super.visitLookupSwitchInsn(dflt,keys,labels);
            wrap(Opcodes.LOOKUPSWITCH,dflt,keys,labels);
            for(Label l:labels)
                pushSplitPointOrLater(l);
            this.shouldSplit=true;
        }
        public void visitMultiANewArrayInsn(final String descriptor, final int numDimensions)
        {

            super.visitMultiANewArrayInsn(descriptor,numDimensions);
            wrap(Opcodes.MULTIANEWARRAY,descriptor,numDimensions);
        }
        public void visitTryCatchBlock(final Label start, final Label end, final Label handler, final String type)
        {

            this.pushSplitPointOrLater(handler);
            super.visitTryCatchBlock(start,end,handler,type);
        }
        public void buildCfg()
        {
            CodeBlock code=new CodeBlock();
            ArrayList<CodeBlock> codes=new ArrayList<>();

            // 此处开始根据字节码序列和分割点构造CodeBlock
            for(BytecodeWrapper w:this.wrappers)
            {
                if(this.splitPoint.contains(w))
                {
                    codes.add(code);
                    code=new CodeBlock();
                }
                code.addBytecodeWrapper(w);
            }
            if(!codes.contains(code))
                codes.add(code);
            code=null;
            HashMap<BytecodeWrapper,IRBasicBlock> entryMapping=new HashMap<>();
            // 此处通过CodeBlock构造IRBasicBlock,并统计每个基本块的起始指令
            for(CodeBlock c:codes)
            {
                IRBasicBlock bb=new IRBasicBlock(c,this.method);
                entryMapping.put(c.getFirstBytecode(),bb);
                this.method.addNode(bb);
                if(code!=null)
                    code.setNextBlock(c);
                code=c;
            }
            // 此处开始处理基本块的后继，分析控制流
            for(int i=0;i<this.method.getNodeNum();i++)
            {
                IRBasicBlock bb=(IRBasicBlock) this.method.getNodes().get(i);
                BytecodeWrapper bytecode=bb.getCode().getLastBytecode();
                int opcode=bytecode.getOpcode();
                if(OpcodeInfo.isJump(opcode))                   // 如果是强制跳转,则后继为跳转目标，若为条件跳转，则可能到达下一基本块
                {
                    Label jumpTarget=(Label) bytecode.getOperand(0);
                    BytecodeWrapper entry=this.labelMap.get(jumpTarget);
                    try
                    {
                        bb.addEdge(entryMapping.get(entry));
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    if(OpcodeInfo.isConditionalJump(opcode))
                    {
                        CodeBlock cb=bb.getCode().getNextBlock();
                        if(cb!=null)
                        {
                            try
                            {
                                bb.addEdge(cb.getHolder());
                            } catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }

                }
                else if(OpcodeInfo.isReturn(opcode))                   // 如果为返回指令，则没有后继
                    continue;
                else if(OpcodeInfo.isSwitch(opcode))                   // 如果为switch指令，则后继为各个跳转目标
                {
                    int dfltIndex=-1,jumpIndex=-1;
                    if(opcode==Opcodes.LOOKUPSWITCH)
                    {
                        dfltIndex=0;
                        jumpIndex=2;
                    }
                    else
                    {
                        dfltIndex=2;
                        jumpIndex=3;
                    }
                    Label[] jump=(Label[])bytecode.getOperand(jumpIndex);
                    ArrayList<Label> labelLists=new ArrayList<>();
                    labelLists.add((Label)bytecode.getOperand(dfltIndex));
                    for(int x=0;x<jump.length;x++)
                        labelLists.add(jump[x]);
                    for(Label l:labelLists)
                    {
                        BytecodeWrapper entry=this.labelMap.get(l);
                        try
                        {
                            bb.addEdge(entryMapping.get(entry));
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
                else                   // 如果是其他指令，则控制流将直接跳转到下一个基本块。
                {
                    CodeBlock cb=bb.getCode().getNextBlock();
                    if(cb!=null)
                    {
                        try
                        {
                            bb.addEdge(cb.getHolder());
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
            for(IRBasicBlock bb:this.method.getBasicBlocks())
            {
                for(int i=0;i<bb.getCode().getOriginalCode().size()-1;i++)
                {
                    BytecodeWrapper bytecode=bb.getCode().getOriginalCode().get(i);
                    assert (!OpcodeInfo.isJump(bytecode.getOpcode())) && (!OpcodeInfo.isReturn(bytecode.getOpcode()));
                }
            }
        }
        public void analysisLocalVariables()
        {
            Label finishLabel=this.cur;
            HashMap<BytecodeWrapper,ArrayList<VariableInfo>> start=new HashMap<>(),end=new HashMap<>();
            for(Pair<Label,VariableInfo> p:startVar)
            {
                BytecodeWrapper bytecode=this.labelMap.get(p.getKey());
                int x=this.wrappers.indexOf(bytecode);
                if(x>=1)
                    bytecode=this.wrappers.get(x-1);
                assert bytecode!=null;
                if(!start.containsKey(bytecode))
                    start.put(bytecode,new ArrayList<>());

                ArrayList<VariableInfo> list=start.get(bytecode);
                list.add(p.getValue());
            }
            for(Pair<Label,VariableInfo> p:endVar)
            {
                if(p.getKey()==finishLabel)
                    continue;
                BytecodeWrapper bytecode=this.labelMap.get(p.getKey());
                assert bytecode!=null;
                if(!end.containsKey(bytecode))
                    end.put(bytecode,new ArrayList<>());
                ArrayList<VariableInfo> list=end.get(bytecode);
                list.add(p.getValue());
            }

            HashSet<VariableInfo> aliveVar=new HashSet<>();
            for(BytecodeWrapper bytecode:this.wrappers)
            {
                ArrayList<VariableInfo> list=start.get(bytecode);
                if(list!=null)
                {
                    for(VariableInfo info:list)
                        aliveVar.add(info);
                }
                list=end.get(bytecode);
                if(list!=null)
                {
                    for(VariableInfo info:list)
                        aliveVar.remove(info);
                }
                for(VariableInfo info:aliveVar)
                    bytecode.addVariable(info.index,info.name,info.desc);

            }
        }
        public void visitEnd()
        {
            this.buildCfg();
            this.analysisLocalVariables();
            this.method.getUnreachableBlocks();
            this.method.emulateStack();
            this.method.analysisStack();
            this.method.generateIRCode();
            //System.out.println("calculate stack offset finished");

            /*for(Node n:this.method.getNodes())
            {
                IRBasicBlock bb=(IRBasicBlock) n;
                System.out.println("Stack offset "+bb.getStackOffset()+"  Stack address:"+bb.getStackAddress());
            }*/


            super.visitEnd();
            //this.method.addNode();
        }
    }
    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
    {

        super.visit(version,access,name,signature, superName,interfaces);
    }
    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value)
    {
        return super.visitField(access,name, descriptor,signature,value);
    }
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
    {
        MethodVisitor mv=super.visitMethod(access,name, desc,signature,exceptions);
        MethodWalker mw=new MethodWalker(this.api,mv);
        mw.init(access,name, desc,signature,exceptions);
        return mw;
    }
    public void visitEnd()
    {
        super.visitEnd();
    }

}
