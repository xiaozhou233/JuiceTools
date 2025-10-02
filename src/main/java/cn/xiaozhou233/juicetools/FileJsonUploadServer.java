package cn.xiaozhou233.juicetools;

import fi.iki.elonen.NanoHTTPD;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

public class FileJsonUploadServer extends NanoHTTPD {

    private final String uploadDir = "uploads";

    public FileJsonUploadServer(int port) throws IOException {
        super(port);
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        start(SOCKET_READ_TIMEOUT, false);
        System.out.println("Server started on port " + port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        if (session.getMethod() == Method.POST) {
            try {
                Map<String, String> files = new java.util.HashMap<>();
                session.parseBody(files); // 解析 body，包含文件和字段

                // 获取 JSON 数据
                String jsonData = session.getParms().get("json");
                System.out.println("Received JSON: " + jsonData);

                // 获取文件
                String tmpFilePath = files.get("file"); // HTML input name="file"
                if (tmpFilePath != null) {
                    File tmpFile = new File(tmpFilePath);
                    File destFile = new File(uploadDir, session.getParms().get("file"));
                    try (FileOutputStream fos = new FileOutputStream(destFile)) {
                        fos.write(java.nio.file.Files.readAllBytes(tmpFile.toPath()));
                    }
                    return newFixedLengthResponse("File and JSON received successfully");
                } else {
                    return newFixedLengthResponse("No file uploaded, but JSON received");
                }
            } catch (Exception e) {
                e.printStackTrace();
                return newFixedLengthResponse("Error: " + e.getMessage());
            }
        } else {
            // 返回上传表单
            String html = "<html><body>" +
                    "<form method='POST' enctype='multipart/form-data'>" +
                    "JSON: <input type='text' name='json'><br>" +
                    "Select file: <input type='file' name='file'><br>" +
                    "<input type='submit' value='Upload'>" +
                    "</form></body></html>";
            return newFixedLengthResponse(html);
        }
    }

    public static void main(String[] args) throws IOException {
        new FileJsonUploadServer(8080);
    }
}
