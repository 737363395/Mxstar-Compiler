package cn.cyx666.Mxstar.Configuration;

public class Configuration {
    private static int registerSize = 8;

    public static void setRegisterSize(int registerSize) {
        Configuration.registerSize = registerSize;
    }

    public static int getRegisterSize() {
        return registerSize;
    }
}
