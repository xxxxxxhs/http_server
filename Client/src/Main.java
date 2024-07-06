import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.Buffer;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Console console = new Console();
        try {
            Socket socket = new Socket(InetAddress.getLocalHost(), 12138);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            String msg;

            while (!(msg = console.readLineWN()).equals("exit")){
                writer.write(msg + "\n");
                System.out.println("sending " + msg);
                writer.flush();

                StringBuilder response = new StringBuilder();
                String line;

                // Чтение заголовков
                while ((line = reader.readLine()) != null && !line.isEmpty()) {
                    response.append(line).append("\n");
                }
                response.append("\n");  // Добавляем разделитель между заголовками и телом

                // Чтение тела
                while ((line = reader.readLine()) != null) {
                    response.append(line).append("\n");
                }
                System.out.println(response.toString());
            }
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}