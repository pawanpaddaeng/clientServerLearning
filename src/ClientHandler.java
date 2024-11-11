import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    //arraylist to keep track of which clients to send the messages to
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket; //socket passed from the server class
    private BufferedReader bufferedReader;//take message
    private BufferedWriter bufferWriter;//send messages
    private String clientUsername;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            //this is a character stream to send
            //message, there is also a byteStream
            this.bufferWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            //to recieve message
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine();//type username press enter, thus username is first line
            clientHandlers.add(this);
            broadcastMessage("SERVER: " + clientUsername+" has entered" );

        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferWriter);
        }
    }

    @Override
    public void run() {
        String messageFromClient;

        while (socket.isConnected()) {
            try{
                //blocking operation
                messageFromClient = bufferedReader.readLine();
                broadcastMessage(messageFromClient);
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferWriter);
                break;
            }
        }
    }

    public void broadcastMessage(String messageToClient) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.clientUsername.equals(clientUsername)) {
                    clientHandler.bufferWriter.write(messageToClient);
                    //the previous one doesnt send a new line
                    clientHandler.bufferWriter.newLine();
                    clientHandler.bufferWriter.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferWriter);
            }
        }
    }

    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessage("SERVER: " + clientUsername+" has left" );
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferWriter) {

        removeClientHandler();
        //with streams you only need to close the outer wrapper
        try {
            if(bufferedReader != null) {
                bufferedReader.close();
            }
            if(bufferWriter != null) {
                bufferWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
