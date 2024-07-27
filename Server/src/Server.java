import RequestHandlers.RequestHandler;
import RequestHandlers.Response;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

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

    public void start() throws IOException {
        while (true) {
            try {
                socket = server.accept();
                System.out.println("CONNECTED " + socket.getInetAddress());
                new ClientListener(socket);
            } catch (IOException e) {
                server.close();
                throw new RuntimeException(e);
            }
        }
    }
}
