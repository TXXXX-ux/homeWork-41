import server.EchoClient;

public class Main_client {
    public static void main(String[] args) {
        EchoClient.connectTo(8089).run();
    }
}