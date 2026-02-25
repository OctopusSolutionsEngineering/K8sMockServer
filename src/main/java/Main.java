import java.net.UnknownHostException;

public class Main {
    public static void main(final String[] args) throws UnknownHostException, InterruptedException {
       final Server server =  new Server();

       // Restart the server every 5 minutes
       while (true) {
           System.out.println("Starting server...");
           server.start();

           System.out.println("Server started. Will restart in 5 minutes...");

           // Wait for 5 minutes (300,000 milliseconds)
           Thread.sleep(5 * 60 * 1000);

           System.out.println("Stopping server for restart...");
           server.stop();
       }
    }
}
