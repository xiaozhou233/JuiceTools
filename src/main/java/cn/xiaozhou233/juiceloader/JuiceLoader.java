package cn.xiaozhou233.juiceloader;


public class JuiceLoader {
    private static JuiceLoaderNative loaderNative;
    public static void init(String path){
        System.out.println("JuiceLoader init!");
        System.out.println("param: " + path);

        System.load(path+"/libjuiceloader.dll");
        loaderNative = new JuiceLoaderNative();
        loaderNative.init();
    }

    public static JuiceLoaderNative getLoaderNative(){
        return loaderNative;
    }
}