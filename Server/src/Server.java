import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;
// добавить многопользовательскую возможность
// добавить post запрос
// добавить варианты ответов сервера
// добавить ресурсы и прикрутить json
public class Server {
    ServerSocket server;
    Socket socket;
    private final int PORT = 12138;
    public Server() {
        try {
            server = new ServerSocket(PORT);
            server.setReuseAddress(true);
            System.out.println("started on port " + PORT);
            start();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void start() {
        while (true) {
            try {
                socket = server.accept();
                System.out.println("CONNECTED");
                handle(socket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private void handle(Socket socket) {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             OutputStream output = socket.getOutputStream()) {
            String requestLine;
            StringBuilder finalRequest = new StringBuilder();
            System.out.println("try to read");
            // Чтение строки запроса и заголовков
            while ((requestLine = input.readLine()) != null && !requestLine.isEmpty()) {
                requestLine = requestLine.replace("\\n", "\r\n");
                System.out.println("reading: " + requestLine);
                finalRequest.append(requestLine).append("\n");
            }
            System.out.println("headers read");
            // Чтение тела запроса (если есть)
            if (input.ready()) {
                while (input.ready() && (requestLine = input.readLine()) != null) {
                    finalRequest.append(requestLine).append("\n");
                }
            }
            System.out.println("read: " + finalRequest.toString());
            Response response = RequestHandler.handleRequest(finalRequest.toString());
            output.write(response.gerResponseHeader().getBytes(StandardCharsets.UTF_8));
            if (response.getResponseBody() != null) output.write(response.getResponseBody());
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
