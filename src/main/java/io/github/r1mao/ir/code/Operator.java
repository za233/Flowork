package io.github.r1mao.ir.code;

import java.util.HashMap;

public class Operator extends Value
{
    private int operateType;
    private Value[] operands;
    protected HashMap<String,String> metaData;
    public static int IARRAY_GET=0,LARRAY_GET=1,FARRAY_GET=2,DARRAY_GET=3,AARRAY_GET=4,BARRAY_GET=5,CARRAY_GET=6,SARRAY_GET=7;
    public static int IADD=8,LADD=9,FADD=10,DADD=11,ISUB=12,LSUB=13,FSUB=14,DSUB=15,IMUL=16,LMUL=17,FMUL=18,DMUL=19,IDIV=20,LDIV=21,FDIV=22,DDIV=23,IREM=24,LREM=25,FREM=26,DREM=27,INEG=28,LNEG=29,FNEG=30,DNEG=31;
    public static int ISHL=32,LSHL=33,ISHR=34,LSHR=35,IUSHR=36,LUSHR=37,IAND=38,LAND=39,IOR=40,LOR=41,IXOR=42,LXOR=43;
    public static int I2L=44,F2L=45,D2L=46,I2F=47,L2F=48,D2F=49,I2D=50,L2D=51,F2D=52,L2I=53,F2I=54,D2I=55,I2B=56,I2C=57,I2S=58;
    public static int LCMP=59,FCMPL=60,FCMPG=61,DCMPL=62,DCMPG=63;
    public static int IEQ=64,INE=65,ILT=66,ILE=67,IGT=68,IGE=69,AEQ=70,ANE=71;
    public static int GETSTATIC=72,GETFIELD=73,PUTSTATIC=74,PUTFIELD=75;
    public static int INVOKESTATIC=76,INVOKEMETHOD=77;
    public static int NEW=78,NEWARRAY=79,ANEWARRAY=80;
    public static int ARRAY_LEN=81;
    public static int CHECKCAST=82,INSTANCEOF=83;
    public static int MULTI_NEWARRAY=84;
    private Operator(int operateType,Value ...values)
    {
        super(Value.OPERATOR);
        this.operateType=operateType;
        this.operands=values;
    }
    public void addMetadata(String key,String value)
    {
        if(this.metaData==null)
            this.metaData=new HashMap<>();
        this.metaData.put(key,value);
    }
    public String getMetadata(String key)
    {
        if(this.metaData==null)
            return null;
        return this.metaData.get(key);
    }
    public static Operator getOp(int type,Value ...values)
    {
        return new Operator(type,values);
    }

    public boolean isArrayGet()
    {
        return this.operateType>=IARRAY_GET && this.operateType<=SARRAY_GET;
    }
    public boolean isAdd()
    {
        return this.operateType>=IADD && this.operateType<=DADD;
    }
    public boolean isSub()
    {
        return this.operateType>=ISUB && this.operateType<=DSUB;
    }
    public boolean isMul()
    {
        return this.operateType>=IMUL && this.operateType<=DMUL;
    }
    public boolean isDiv()
    {
        return this.operateType>=IDIV && this.operateType<=DDIV;
    }
    public boolean isRem()
    {
        return this.operateType>=IREM && this.operateType<=DREM;
    }
    public boolean isNeg()
    {
        return this.operateType>=INEG && this.operateType<=DNEG;
    }
    public boolean isShl()
    {
        return this.operateType>=ISHL && this.operateType<=LSHL;
    }
    public boolean isShr()
    {
        return this.operateType>=ISHR && this.operateType<=LSHR;
    }
    public boolean isUShr()
    {
        return this.operateType>=IUSHR && this.operateType<=LUSHR;
    }
    public boolean isAnd()
    {
        return this.operateType>=IAND && this.operateType<=LAND;
    }
    public boolean isOr()
    {
        return this.operateType>=IOR && this.operateType<=LOR;
    }
    public boolean isXor()
    {
        return this.operateType>=IXOR && this.operateType<=LXOR;
    }
    public boolean isCastToInt()
    {
        return this.operateType>=L2I && this.operateType<=D2I;
    }
    public boolean isCastToLong()
    {
        return this.operateType>=I2L && this.operateType<=D2L;
    }
    public boolean isCastToFloat()
    {
        return this.operateType>=I2F && this.operateType<=D2F;
    }
    public boolean isCastToDouble()
    {
        return this.operateType>=I2D && this.operateType<=F2D;
    }
    public boolean isCastToByte()
    {
        return this.operateType==I2B;
    }
    public boolean isCastToChar()
    {
        return this.operateType==I2C;
    }
    public boolean isCastToShort()
    {
        return this.operateType==I2S;
    }
    public boolean isCmp1()
    {
        return this.operateType>=LCMP && this.operateType<=DCMPG;
    }
    public boolean isCmp2()
    {
        return this.operateType>=IEQ && this.operateType<=ANE;
    }
    public boolean isPutStatic()
    {
        return this.operateType==PUTSTATIC;
    }
    public boolean isPutField()
    {
        return this.operateType==PUTFIELD;
    }
    public boolean isGetStatic()
    {
        return this.operateType==GETSTATIC;
    }
    public boolean isGetField()
    {
        return this.operateType==GETFIELD;
    }
    public boolean isInvokeStatic()
    {
        return this.operateType==INVOKESTATIC;
    }
    public boolean isInvokeMethod()
    {
        return this.operateType==INVOKEMETHOD;
    }
    public boolean isNew()
    {
        return this.operateType==NEW;
    }
    public boolean isNewArray()
    {
        return this.operateType==NEWARRAY;
    }
    public boolean isANewArray()
    {
        return this.operateType==ANEWARRAY;
    }
    public boolean isArrayLen()
    {
        return this.operateType==ARRAY_LEN;
    }
    public boolean isCheckCast()
    {
        return this.operateType==CHECKCAST;
    }
    public boolean isInstanceOf()
    {
        return this.operateType==INSTANCEOF;
    }
    public boolean isMultiANewArray()
    {
        return this.operateType==MULTI_NEWARRAY;
    }
    @Override
    public String dump()
    {
        if(this.isArrayGet())
        {
            return operands[0].dump()+"["+operands[1].dump()+"]";
        }
        else if(this.isAdd())
        {
            return operands[0].dump()+" + "+operands[1].dump();
        }
        else if(this.isSub())
        {
            return operands[0].dump()+" - "+operands[1].dump();
        }
        else if(this.isMul())
        {
            return operands[0].dump()+" * "+operands[1].dump();
        }
        else if(this.isDiv())
        {
            return operands[0].dump()+" / "+operands[1].dump();
        }
        else if(this.isRem())
        {
            return operands[0].dump()+" % "+operands[1].dump();
        }
        else if(this.isNeg())
        {
            return "-"+operands[0].dump();
        }
        else if(this.isAnd())
        {
            return operands[0].dump()+" & "+operands[1].dump();
        }
        else if(this.isOr())
        {
            return operands[0].dump()+" | "+operands[1].dump();
        }
        else if(this.isXor())
        {
            return operands[0].dump()+" ^ "+operands[1].dump();
        }
        else if(this.isShl())
        {
            return operands[0].dump()+" >> "+operands[1].dump();
        }
        else if(this.isShr())
        {
            return operands[0].dump()+" << "+operands[1].dump();
        }
        else if(this.isUShr())
        {
            return operands[0].dump()+" >>> "+operands[1].dump();
        }
        else if(this.isCastToByte())
        {
            return "(byte) "+operands[0].dump();
        }
        else if(this.isCastToChar())
        {
            return "(char) "+operands[0].dump();
        }
        else if(this.isCastToShort())
        {
            return "(short) "+operands[0].dump();
        }
        else if(this.isCastToInt())
        {
            return "(int) "+operands[0].dump();
        }
        else if(this.isCastToFloat())
        {
            return "(float) "+operands[0].dump();
        }
        else if(this.isCastToLong())
        {
            return "(long) "+operands[0].dump();
        }
        else if(this.isCastToDouble())
        {
            return "(double) "+operands[0].dump();
        }
        else if(this.isCmp1())
        {
            return "cmp("+operands[0].dump()+", "+operands[1].dump()+")";
        }
        else if(this.isCmp2())
        {
            if(this.operateType==IEQ || this.operateType==AEQ)
                return operands[0].dump()+" == "+operands[1].dump();
            else if(this.operateType==INE || this.operateType==ANE)
                return operands[0].dump()+" != "+operands[1].dump();
            else if(this.operateType==ILT)
                return operands[0].dump()+" < "+operands[1].dump();
            else if(this.operateType==ILE)
                return operands[0].dump()+" <= "+operands[1].dump();
            else if(this.operateType==IGT)
                return operands[0].dump()+" > "+operands[1].dump();
            else if(this.operateType==IGE)
                return operands[0].dump()+" >= "+operands[1].dump();
            else
                assert false;
        }
        else if(this.isGetStatic() || this.isPutStatic())
        {
            return "("+this.getMetadata("owner").replaceAll("/",".")+")."+this.getMetadata("name");
        }
        else if(this.isGetField() || this.isPutField())
        {
            return "(("+this.getMetadata("owner").replaceAll("/",".")+")"+operands[0].dump()+")."+this.getMetadata("name");
        }
        else if(this.isInvokeStatic())
        {
            String result="("+this.getMetadata("owner").replaceAll("/",".")+")."+this.getMetadata("name")+"(";
            for(int i=0;i<this.operands.length;i++)
            {
                result+=operands[i].dump();
                if(i!=this.operands.length-1)
                    result+=", ";
            }
            result+=")";
            return result;
        }
        else if(this.isInvokeMethod())
        {
            String result="(("+this.getMetadata("owner").replaceAll("/",".")+")"+operands[0].dump()+")."+this.getMetadata("name")+"(";
            for(int i=1;i<this.operands.length;i++)
            {
                result+=operands[i].dump();
                if(i!=this.operands.length-1)
                    result+=", ";
            }
            result+=")";
            return result;
        }
        else if(this.isNew())
        {
            return "new "+this.getMetadata("type").replaceAll("/",".")+"()";
        }
        else if(this.isNewArray())
        {
            return "new "+this.getMetadata("rawType")+"["+operands[0].dump()+"]";
        }
        else if(this.isANewArray())
        {
            return "new "+this.getMetadata("type").replaceAll("/",".")+"["+operands[0].dump()+"]";
        }
        else if(this.isArrayLen())
        {
            return operands[0].dump()+".length";
        }
        else if(this.isCheckCast())
        {
            return "("+this.getMetadata("type").replaceAll("/",".")+") "+operands[0].dump();
        }
        else if(this.isInstanceOf())
        {
            return operands[0].dump()+" instanceof "+this.getMetadata("type").replaceAll("/",".");
        }
        else if(this.isMultiANewArray())
        {
            String result="new array<"+this.getMetadata("descriptor")+"> ";
            for(int i=0;i<this.operands.length;i++)
                result+="["+this.operands[i].dump()+"]";
            return result;
        }
        return null;
    }
}
