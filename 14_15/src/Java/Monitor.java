import java.io.*;
import java.net.*;

public class Monitor {
    public static void main(String[] args) throws IOException{
        int port = Integer.parseInt(args[1]);
        int period = Integer.parseInt(args[2]);
        int timeout = Integer.parseInt(args[3]);

        Socket socket = new Socket(args[0], port);

        while(socket.isClosed());
        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        socket.setSoTimeout(timeout);

        try {
            while(socket.isConnected()){
                int sent = 1;
                out.writeInt(sent);
                System.out.println("Monitor sent: " + sent);
                int received = in.readInt();
                System.out.println("Monitor received: " + received);
                Thread.sleep(period);
            }
        }
        catch (SocketException eTimeout) {
            System.out.println("Socket timeout!");
        }
        catch (InterruptedException ignored){

        }

        socket.close();
    }
}
