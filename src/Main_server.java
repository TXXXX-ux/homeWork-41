import server.EchoServer;

public class Main_server {
    public static void main(String[] args) {
        EchoServer.bindToPort(8089).run();
    }
}