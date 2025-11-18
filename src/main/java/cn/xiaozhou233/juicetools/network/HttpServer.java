package cn.xiaozhou233.juicetools.network;

import cn.xiaozhou233.juiceloader.JuiceLoader;
import cn.xiaozhou233.juicetools.Test;
import cn.xiaozhou233.juicetools.tools.Dump;
import cn.xiaozhou233.juicetools.tools.Reflect;
import com.google.gson.Gson;
import fi.iki.elonen.NanoHTTPD;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HttpServer extends NanoHTTPD {
    private static final Gson gson = new Gson();

    public HttpServer(int port) throws IOException {
        super(port);
        start(SOCKET_READ_TIMEOUT, false);
        System.out.println("JuiceTools Http Server started on port " + port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        String path = session.getUri();
        HashMap<String, Object> jsonMap = new HashMap<>();

        try {
            switch (path) {
                case "/":
                    jsonMap.put("code", 0);
                    jsonMap.put("msg", "JuiceTools Http Server");
                    return jsonResponse(jsonMap);

                case "/dump": {
                    String className = session.getParms().get("class");
                    if (className == null || className.isEmpty()) {
                        jsonMap.put("code", -1);
                        jsonMap.put("msg", "Missing 'class' parameter");
                        return jsonResponse(jsonMap);
                    }

                    byte[] classBytes = Dump.dumpClass(className);
                    if (classBytes == null) {
                        jsonMap.put("code", -2);
                        jsonMap.put("msg", "Class not found: " + className);
                        return jsonResponse(jsonMap);
                    }

                    Response res = newFixedLengthResponse(Response.Status.OK, "application/octet-stream",
                            new java.io.ByteArrayInputStream(classBytes), classBytes.length);

                    res.addHeader("Content-Disposition", "attachment; filename=\"" + className + ".class\"");
                    return res;
                }
                case "/getvalue": {
                    String className = session.getParms().get("class");
                    String fieldName = session.getParms().get("field");
                    if (className == null || className.isEmpty()) {
                        jsonMap.put("code", -1);
                        jsonMap.put("msg", "Missing 'class' parameter");
                        return jsonResponse(jsonMap);
                    }
                    if (fieldName == null || fieldName.isEmpty()) {
                        jsonMap.put("code", -1);
                        jsonMap.put("msg", "Missing 'field' parameter");
                        return jsonResponse(jsonMap);
                    }
                    jsonMap.put("code", 0);
                    jsonMap.put("field", fieldName);
                    jsonMap.put("value", Reflect.getValue(className, fieldName));
                    return jsonResponse(jsonMap);
                }
                case "/redefine": {
                    if (!session.getMethod().equals(Method.POST)) {
                        String html = "<html><body>" +
                                "<form method='POST' enctype='multipart/form-data'>" +
                                "JSON: <input type='text' name='json'><br>" +
                                "Select file: <input type='file' name='file'><br>" +
                                "<input type='submit' value='Upload'>" +
                                "</form></body></html>";
                        return newFixedLengthResponse(html);
                    }
                    Map<String, String> files = new java.util.HashMap<>();
                    session.parseBody(files);
                    String jsonData = session.getParms().get("json");
                    System.out.println("Received JSON: " + jsonData);
                    byte[] classBytes = Files.readAllBytes(new File(files.get("file")).toPath());

                    HashMap info = gson.fromJson(jsonData, HashMap.class);
                    String className = ((String) info.get("class")).replace(".", "/");
                    int length = classBytes.length;
                    if (className == null || className.isEmpty())
                        throw new Exception("Missing 'class' parameter in JSON");
                    if (length == 0)
                        throw new Exception("Missing 'length' parameter in JSON");
                    if (classBytes.length != length)
                        throw new Exception("Class length mismatch: " + classBytes.length + " != " + length);
                    try {
                        System.out.printf("Redefining class %s with %d bytes\n", className, length);
                        boolean success = JuiceLoader.redefineClassByName(className, classBytes, length);
                        if (success) {
                            jsonMap.put("code", 0);
                            jsonMap.put("msg", "Class redefined successfully");
                        } else {
                            jsonMap.put("code", -1);
                            jsonMap.put("msg", "Class redefinition failed");
                        }
                        Test.print();
                        return jsonResponse(jsonMap);
                    } catch (Throwable e) {
                        e.printStackTrace();
                        jsonMap.put("code", -500);
                        jsonMap.put("msg", "Internal error: " + e.toString());
                        return jsonResponse(jsonMap);
                    }
                }
                case "/getclasses": {
                    // package cn.xiaozhou233.juiceloader;
                    // public native Class<?>[] getLoadedClasses();
                    try {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (Class<?> loadedClass : JuiceLoader.getLoadedClasses()) {
                            if (loadedClass == null) continue;
                            stringBuilder.append(loadedClass.getName()).append("\n");
                        }
                        return newFixedLengthResponse(stringBuilder.toString());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                default:
                    jsonMap.put("code", -1);
                    jsonMap.put("msg", "Unknown path: " + path);
                    return jsonResponse(jsonMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonMap.put("code", -500);
            jsonMap.put("msg", "Internal server error: " + e.getClass().getName() + ": " + e.getMessage());
            return jsonResponse(jsonMap);
        }
    }

    private Response jsonResponse(HashMap<String, Object> map) {
        String json = gson.toJson(map);
        Response res = newFixedLengthResponse(Response.Status.OK, "application/json", json);
        res.addHeader("Content-Type", "application/json; charset=utf-8");
        return res;
    }

}
