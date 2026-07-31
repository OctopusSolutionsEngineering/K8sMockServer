import java.net.UnknownHostException;

public class Main {
    public static void main(final String[] args) throws UnknownHostException, InterruptedException {
       final Server server =  new Server();

       // Restart the server every 15 minutes
       while (true) {
           System.out.println("Starting server...");
           server.start();

           System.out.println("Server started. Will restart in 15 minutes...");

           // Wait for 15 minutes (900,000 milliseconds)
           Thread.sleep(15 * 60 * 1000);

           System.out.println("Stopping server for restart...");
           server.stop();
       }
    }
}
