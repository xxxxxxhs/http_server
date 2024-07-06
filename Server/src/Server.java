import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
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
                 BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
                String requestLine;
                while ((requestLine = input.readLine()) != null && !requestLine.isEmpty()) {
                    System.out.println("Request: " + requestLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}
