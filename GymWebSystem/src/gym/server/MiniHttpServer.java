package gym.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MiniHttpServer {

    private final int port;
    private final Path webRoot;
    private final ApiHandler apiHandler;
    private volatile boolean running = true;
    private final ExecutorService pool;

    public MiniHttpServer(int port, Path webRoot) {
        this.port = port;
        this.webRoot = webRoot;
        this.apiHandler = new ApiHandler();
        this.pool = Executors.newFixedThreadPool(10);
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("==============================================");
            System.out.println("  Gym Membership Web System is running!");
            System.out.println("  Open your browser: http://localhost:" + port);
            System.out.println("  Press Ctrl+C to stop");
            System.out.println("==============================================");

            while (running) {
                Socket socket = serverSocket.accept();
                pool.submit(() -> handleClient(socket));
            }
        }
    }

    public void stop() {
        running = false;
        pool.shutdown();
    }

    private void handleClient(Socket socket) {
        try (Socket client = socket;
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             OutputStream out = socket.getOutputStream()) {

            String requestLine = reader.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                return;
            }
            String[] parts = requestLine.split(" ");
            if (parts.length < 2) {
                return;
            }
            String method = parts[0];
            String fullPath = parts[1];

            String path = fullPath;
            Map<String, String> queryParams = new HashMap<>();
            int qIndex = fullPath.indexOf('?');
            if (qIndex >= 0) {
                path = fullPath.substring(0, qIndex);
                parseQueryParams(fullPath.substring(qIndex + 1), queryParams);
            }
            path = URLDecoder.decode(path, "UTF-8");

            int contentLength = 0;
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                int colon = line.indexOf(':');
                if (colon > 0) {
                    String headerName = line.substring(0, colon).trim().toLowerCase();
                    String headerValue = line.substring(colon + 1).trim();
                    if (headerName.equals("content-length")) {
                        try {
                            contentLength = Integer.parseInt(headerValue);
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            }

            String body = "";
            if (contentLength > 0) {
                char[] bodyChars = new char[contentLength];
                int read = reader.read(bodyChars, 0, contentLength);
                if (read > 0) {
                    body = new String(bodyChars, 0, read);
                }
            }

            byte[] responseBytes;
            String contentType;

            if (path.startsWith("/api/")) {
                String json = apiHandler.handle(method, path, queryParams, body);
                responseBytes = json.getBytes(StandardCharsets.UTF_8);
                contentType = "application/json; charset=utf-8";
            } else {
                StaticFileResult file = serveStaticFile(path);
                if (file == null) {
                    responseBytes = "<h1>404 Not Found</h1>".getBytes(StandardCharsets.UTF_8);
                    contentType = "text/html; charset=utf-8";
                    sendResponse(out, 404, contentType, responseBytes);
                    return;
                }
                responseBytes = file.bytes;
                contentType = file.contentType;
            }

            sendResponse(out, 200, contentType, responseBytes);

        } catch (Exception e) {
            System.err.println("Error handling request: " + e.getMessage());
        }
    }

    private void sendResponse(OutputStream out, int statusCode, String contentType,
                              byte[] body) throws IOException {
        String statusText = statusCode == 200 ? "OK" : "Not Found";
        String headers = "HTTP/1.1 " + statusCode + " " + statusText + "\r\n"
                + "Content-Type: " + contentType + "\r\n"
                + "Content-Length: " + body.length + "\r\n"
                + "Access-Control-Allow-Origin: *\r\n"
                + "Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS\r\n"
                + "Access-Control-Allow-Headers: Content-Type\r\n"
                + "Cache-Control: no-cache\r\n"
                + "Connection: close\r\n"
                + "\r\n";
        out.write(headers.getBytes(StandardCharsets.UTF_8));
        out.write(body);
        out.flush();
    }

    private void parseQueryParams(String query, Map<String, String> params)
            throws UnsupportedEncodingException {
        for (String pair : query.split("&")) {
            int eq = pair.indexOf('=');
            if (eq > 0) {
                String key = URLDecoder.decode(pair.substring(0, eq), "UTF-8");
                String value = URLDecoder.decode(pair.substring(eq + 1), "UTF-8");
                params.put(key, value);
            }
        }
    }

    private StaticFileResult serveStaticFile(String path) throws IOException {
        if (path.equals("/") || path.isEmpty()) {
            path = "/index.html";
        }
        String normalized = path.replace("\\", "/");
        if (normalized.contains("..")) {
            return null;
        }
        Path filePath = webRoot.resolve(normalized.substring(1)).normalize();
        if (!filePath.startsWith(webRoot) || !Files.exists(filePath) || Files.isDirectory(filePath)) {
            return null;
        }

        byte[] bytes = Files.readAllBytes(filePath);
        String fileName = filePath.getFileName().toString().toLowerCase();
        String contentType;
        if (fileName.endsWith(".html")) {
            contentType = "text/html; charset=utf-8";
        } else if (fileName.endsWith(".css")) {
            contentType = "text/css; charset=utf-8";
        } else if (fileName.endsWith(".js")) {
            contentType = "application/javascript; charset=utf-8";
        } else if (fileName.endsWith(".png")) {
            contentType = "image/png";
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            contentType = "image/jpeg";
        } else if (fileName.endsWith(".svg")) {
            contentType = "image/svg+xml";
        } else if (fileName.endsWith(".ico")) {
            contentType = "image/x-icon";
        } else {
            contentType = "application/octet-stream";
        }
        return new StaticFileResult(bytes, contentType);
    }

    private static class StaticFileResult {
        final byte[] bytes;
        final String contentType;

        StaticFileResult(byte[] bytes, String contentType) {
            this.bytes = bytes;
            this.contentType = contentType;
        }
    }
}
