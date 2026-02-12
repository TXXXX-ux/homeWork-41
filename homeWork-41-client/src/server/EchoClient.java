package server;

import java.io.*;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class EchoClient {
    private final int port;
    private final String host;

    private EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static EchoClient connectTo(int port) {
        return new EchoClient("127.0.0.1", port);
    }

    public void run() {
        System.out.println("Чтобы выйти напишите 'Bye'\n\n");

        try (Socket socket = new Socket(host, port);
             Scanner networkReader = new Scanner(new InputStreamReader(socket.getInputStream(), "UTF-8"));
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
             Scanner consoleScanner = new Scanner(System.in)) {

            while (true) {
                if (consoleScanner.hasNextLine()) {
                    String message = consoleScanner.nextLine();
                    writer.println(message);

                    if (networkReader.hasNextLine()) {
                        String response = networkReader.nextLine();
                        System.out.println(response);
                    }

                    if ("bye".equalsIgnoreCase(message)) {
                        break;
                    }
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println("Connection dropped!");
        } catch (IOException e) {
            System.out.printf("Can't connect to %s:%s!%n", host, port);
            e.printStackTrace();
        }
    }
}