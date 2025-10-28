package cn.xiaozhou233.juicetools;

import cn.xiaozhou233.juiceloader.JuiceLoaderNative;
import cn.xiaozhou233.juicetools.gui.StartGui;
import cn.xiaozhou233.juicetools.network.HttpServer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class JuiceTools {
    private static JuiceLoaderNative loaderNative;
    public static void main(String[] args) {
        System.out.println("JuiceTools is starting...");
        init();
    }

    public static void init() {
        System.out.println("Starting GUI...");
        StartGui.start();
        StartGui.showInfo("Init!");
        // JuiceLoader has been loaded library
//        System.out.println("Loading Library...");
//        StartGui.showInfo("Loading Library...");
//        loadLibrary();

        System.out.println("Starting HTTP Server...");
        StartGui.showInfo("Starting HTTP Server...");
        try {
            int port = 8080;
            new HttpServer(port);
            StartGui.showInfo(String.format("HTTP Server Started! [localhost:%s]", port));
        } catch (IOException e) {
            StartGui.showInfo("Error starting HTTP Server: " + e.getMessage());
            throw new RuntimeException(e);
        }

        StartGui.showInfo("Invoke library init...");
        loaderNative = new JuiceLoaderNative();
        System.out.println("Passed loaderNative.init(); Because JuiceLoader has been init it.");
        //loaderNative.init();
        StartGui.showInfo("Library init success!");
        StartGui.showInfo("Loaded Classes: " + loaderNative.getLoadedClasses().length);

        StartGui.showInfo("JuiceTools is ready!");

        StartGui.showControlPanel();
    }

    public static JuiceLoaderNative getLoaderNative() {
        return loaderNative;
    }

    private static void loadLibrary() {
        try (InputStream in = JuiceTools.class.getResourceAsStream("/lib/libjuiceloader.dll")) {
            if (in == null) throw new RuntimeException("DLL not found in jar");

            File tempDll = File.createTempFile("libjuiceloader", ".dll");
            System.out.println("[Debug][JuiceTools] Loading native from: " + tempDll.getAbsolutePath());
            tempDll.deleteOnExit();

            try (FileOutputStream out = new FileOutputStream(tempDll)) {
                byte[] buffer = new byte[4096];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
            }

            System.load(tempDll.getAbsolutePath());
        } catch (IOException e) {
            StartGui.showInfo("Error loading library: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
