package io.github.r1mao;

import io.github.r1mao.algorithm.DescriptorParser;
import io.github.r1mao.algorithm.Graph;
import io.github.r1mao.algorithm.LengauerTarjan;
import io.github.r1mao.algorithm.Node;
import io.github.r1mao.ir.code.Constant;
import io.github.r1mao.ir.code.Temp;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.HashSet;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        /*Graph graph=new Graph();
        Node<Integer> n0=new Node<Integer>(0,graph);
        Node<Integer> n1=new Node<Integer>(1,graph);
        Node<Integer> n2=new Node<Integer>(2,graph);
        Node<Integer> n3=new Node<Integer>(3,graph);
        Node<Integer> n4=new Node<Integer>(4,graph);
        Node<Integer> n5=new Node<Integer>(5,graph);
        Node<Integer> n6=new Node<Integer>(6,graph);
        Node<Integer> n7=new Node<Integer>(7,graph);
        Node<Integer> n8=new Node<Integer>(8,graph);
        Node<Integer> n9=new Node<Integer>(9,graph);
        Node<Integer> n10=new Node<Integer>(10,graph);
        Node<Integer> n11=new Node<Integer>(11,graph);
        Node<Integer> n12=new Node<Integer>(12,graph);
        graph.addNode(n0);
        graph.addNode(n1);
        graph.addNode(n2);
        graph.addNode(n3);
        graph.addNode(n4);
        graph.addNode(n5);
        graph.addNode(n6);
        graph.addNode(n7);
        graph.addNode(n8);
        graph.addNode(n9);
        graph.addNode(n10);
        graph.addNode(n11);
        graph.addNode(n12);
        n0.addEdge(n1,n2,n3);
        n1.addEdge(n4);
        n2.addEdge(n1,n4,n5);
        n3.addEdge(n6,n7);
        n4.addEdge(n12);
        n5.addEdge(n8);
        n6.addEdge(n9);
        n7.addEdge(n9,n10);
        n8.addEdge(n5,n11);
        n9.addEdge(n11);
        n10.addEdge(n9);
        n11.addEdge(n0,n9);
        n12.addEdge(n8);

        LengauerTarjan obj=new LengauerTarjan(graph,n0);
        obj.run();
        for(Node n:graph.getNodes())
            System.out.println(obj.getDominateFrontier(n));*/

        ClassReader reader=new ClassReader("io.github.r1mao.algorithm.LoopChecker");
        ClassWriter writer=new ClassWriter(reader,ClassWriter.COMPUTE_MAXS);
        ClassWalker visitor=new ClassWalker(Opcodes.ASM8,writer);
        reader.accept(visitor,ClassReader.EXPAND_FRAMES);

        HashSet<Integer> set=new HashSet<>(),gg;
        set.add(1);
        set.add(2);
        gg= (HashSet<Integer>) set.clone();
        gg.add(3);
        System.out.println(set);
        System.out.println(gg);
    }
}
