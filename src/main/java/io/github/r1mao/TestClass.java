package io.github.r1mao;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

public class TestClass
{
    public static int xas=0;
    public double value=1;
    public static Object test(int s,double y) throws Exception
    {
        DataType op[][]=new DataType[3][2];
        op[0][0]=DataType.TYPE_REFERENCE;
        op[1][1]=DataType.TYPE_INTEGER;
        op[2][1]=DataType.TYPE_DOUBLE;
        for(int i=0;i<op.length;i++)
        {
            System.out.println(op[i][i].getTypeName());
        }
        Double x=0.123;
        try
        {
            xas=10;

            x+=y;
            x=x*y-y*x*y;
            long xz=9;
            long yz=xz=2;
            xz=~1;
            System.out.println("yes");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            switch(s)
            {
                case 1:
                    return y;
                case 2:
                    return op[0];
                default:
                    return xxa(x,y);
            }
        }


    }
    public static double xxa(double a,double b)
    {
        return a+b;
    }

    public int getValue()
    {
        value+=1;
        if(value>10)
            return 1;
        else
            return 2;
    }
    public void x()
    {
        int a=1;
        a=~a;
        Object xl = null;
        if(xl==null)
            System.out.println("yes");
        getValue();
        Object x=new Double(0.1);
        System.out.println(a);
        System.out.println(x instanceof Double);
        int b=a+1;
        System.out.println(b);
    }
}
