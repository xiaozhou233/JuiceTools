package cn.xiaozhou233.juicetools;

import cn.xiaozhou233.juiceloader.JuiceLoaderNative;
import cn.xiaozhou233.juicetools.gui.StartGui;
import cn.xiaozhou233.juicetools.network.HttpServer;

import java.io.IOException;

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

        System.out.println("Loading Library...");
        StartGui.showInfo("Loading Library...");
        try {
            System.load(ClassLoader.getSystemResource("lib/libjuiceloader.dll").getPath());
        } catch (Exception e) {
            StartGui.showInfo("Error loading library: " + e.getMessage());
            throw new RuntimeException(e);
        }

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
        loaderNative.init();
        StartGui.showInfo("Library init success!");
        StartGui.showInfo("Loaded Classes: " + loaderNative.getLoadedClasses().length);

        StartGui.showInfo("JuiceTools is ready!");

        StartGui.showControlPanel();
    }
}
