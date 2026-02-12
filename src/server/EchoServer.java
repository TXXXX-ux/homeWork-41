package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.function.Function;

public class EchoServer {
    private final int port;
    private final Map<String, Function<String, String>> commands = new HashMap<>();

    private EchoServer(int port) {
        this.port = port;
        initCommands();
    }

    private void initCommands() {
        commands.put("date", msg -> LocalDateTime.now().toString());
        commands.put("time", msg -> LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        commands.put("reverse", msg -> new StringBuilder(msg.replaceFirst("reverse ", "")).reverse().toString());
        commands.put("upper", msg -> msg.replaceFirst("upper ", "").toUpperCase());
    }

    public static EchoServer bindToPort(int port) {
        return new EchoServer(port);
    }

    public void run() {
        try (ServerSocket server = new ServerSocket(port)) {

            while (true) {
                try (Socket clientSocket = server.accept()) {
                    handle(clientSocket);
                }
            }
        } catch (IOException e) {
            System.out.printf("Вероятнее всего порт %s занят. %n", port);
            e.printStackTrace();
        }
    }

    private void handle(Socket socket) throws IOException {
        InputStream is = socket.getInputStream();
        OutputStream os = socket.getOutputStream();

        try (Scanner sc = new Scanner(new InputStreamReader(is, "UTF-8"));
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, "UTF-8"), true)) {

            while (sc.hasNextLine()) {
                String message = sc.nextLine().strip();
                System.out.printf("Got: %s%n", message);

                String response;
                if (message.equalsIgnoreCase("bye")) {
                    writer.println("Bye bye!");
                    return;
                } else if (message.equalsIgnoreCase("date")) {
                    response = LocalDateTime.now().toString();
                } else if (message.equalsIgnoreCase("time")) {
                    response = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                } else if (message.toLowerCase().startsWith("reverse ")) {
                    String body = message.substring(8);
                    response = new StringBuilder(body).reverse().toString();
                } else if (message.toLowerCase().startsWith("upper ")) {
                    String body = message.substring(6);
                    response = body.toUpperCase();
                } else {
                    response = new StringBuilder(message).reverse().toString();
                }
                writer.println(response);
            }
        } catch (NoSuchElementException e) {
            System.out.println("Client dropped connection!");
        }
    }
}
