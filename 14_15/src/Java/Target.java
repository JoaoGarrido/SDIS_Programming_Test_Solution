import java.io.*;
import java.net.*;

public class Target {
    public static void main(String[] args) throws IOException{
        int port = Integer.parseInt(args[0]);
        ServerSocket serverSocket = new ServerSocket(port);

        Socket socket = serverSocket.accept();

        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            while(socket.isConnected()) {
                int received = in.readInt();
                System.out.println("Target received: " + received);
                int sent = 2; 
                out.writeInt(sent);
                System.out.println("Target sent: " + sent);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
