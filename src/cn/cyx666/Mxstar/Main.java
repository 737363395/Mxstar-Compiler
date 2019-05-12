package cn.cyx666.Mxstar;

import cn.cyx666.Mxstar.Compiler.Compiler;

import java.io.*;


public class Main {


    public static void main(String[] args) throws Exception{
        InputStream inputStream = new FileInputStream("test/program.mx");
        OutputStream outputStream = System.out;
        Compiler compiler = new Compiler(inputStream, outputStream);
        try {
            compiler.run();
        } catch (Error error) {
            System.err.println(error.getMessage());
            System.exit(1);
        }
    }
}
