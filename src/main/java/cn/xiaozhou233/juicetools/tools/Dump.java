package cn.xiaozhou233.juicetools.tools;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Dump {
    public static byte[] dumpClass(String className) {
        try {
            return readStream(
                    ClassLoader.getSystemResourceAsStream(className.replace('.', '/') + ".class")
            );
        } catch (Exception e) {
            return ("ERROR: " + Arrays.toString(e.getStackTrace())).getBytes(StandardCharsets.UTF_8);
        }
    }

    public static byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inStream.read(buffer)) != -1)
            outStream.write(buffer, 0, len);
        outStream.close();
        return outStream.toByteArray();
    }
}
