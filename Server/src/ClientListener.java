import RequestHandlers.RequestHandler;
import RequestHandlers.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class ClientListener extends Thread {

    private Socket socket;
    private BufferedReader input;
    private OutputStream output;
    public ClientListener(Socket socket) throws IOException {
        this.socket = socket;
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.output = socket.getOutputStream();
        this.socket.setSoTimeout(15000);
        start();
    }

    @Override
    public void run() {
        try {
            while (true) { // Бесконечный цикл для обработки нескольких запросов
                String requestLine;
                StringBuilder finalRequest = new StringBuilder();
                // Чтение строки запроса и заголовков
                try {
                    while ((requestLine = input.readLine()) != null && !requestLine.isEmpty()) {
                        finalRequest.append(requestLine).append("\r\n");
                    }
                } catch (SocketTimeoutException e) {
                    System.out.println("Socket timeout: " + e.getMessage());
                    break;
                }
                // Если запрос пустой, значит соединение закрыто клиентом
                if (finalRequest.length() == 0) {
                    break;
                }
                // Чтение тела запроса (если есть)
                int contentLength = 0;
                String[] headers = finalRequest.toString().split("\r\n");
                for (String header : headers) {
                    if (header.toLowerCase().startsWith("content-length:")) {
                        contentLength = Integer.parseInt(header.split(":")[1].trim());
                    }
                }

                finalRequest.append("\n");

                char[] body = new char[contentLength];
                if (contentLength > 0) {
                    int bytesRead = input.read(body, 0, contentLength);
                    finalRequest.append(body);
                }
                Response response = RequestHandler.handleRequest(finalRequest.toString());
                System.out.println(response.toString());
                output.write(response.getResponseHeader().getBytes(StandardCharsets.UTF_8));
                if (response.getResponseBody() != null) {
                    output.write(response.getResponseBody());
                }
                output.flush();
            }
        } catch (IOException e) {
            System.err.println("IOException in ClientListener: " + e.getMessage());
        } finally {
            try {
                input.close();
                output.close();
                socket.close();
                System.out.println("DISCONNECTED " + socket.getInetAddress());
            } catch (IOException e) {
                System.err.println("Failed to close resources: " + e.getMessage());
            }
        }
    }
}
