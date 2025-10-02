package cn.xiaozhou233.juicetools;

import cn.xiaozhou233.juicetools.network.HttpServer;

import java.io.IOException;

public class JuiceTools {
    public static void main(String[] args) {
        try {
            new HttpServer(8080);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
