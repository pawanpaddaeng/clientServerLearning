import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    //server socket object for the server
    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {

        try{

            while(!serverSocket.isClosed()) {
                //blocking method will wait here
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");

                //class implementing runnable
                ClientHandler clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {

        }
    }

    public void closeServerSocker() {
        try {
            if(serverSocket != null) {
                serverSocket.close();
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234, 50, InetAddress.getByName("0.0.0.0"));
        Server server = new Server(serverSocket);
        server.startServer();
    }
}
