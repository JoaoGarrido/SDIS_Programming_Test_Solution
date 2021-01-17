import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(args[0], Integer.parseInt(args[1]));

        while(socket.isClosed());
        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        float sent = 1; 
        out.writeFloat(sent);
        System.out.println("Client sent: " + sent);

        float received = in.readFloat();
        System.out.println("Client received: " + received);

        socket.close();
    }

}