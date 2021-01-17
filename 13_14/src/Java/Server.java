import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) throws IOException{
        ServerSocket socket = new ServerSocket(Integer.parseInt(args[0]));
        float avg = Float.parseFloat(args[1]);

        while(true){
            Socket a = socket.accept();
            
            Thread t = new Thread() {
                public void run() {
                    DataInputStream in = null;
                    DataOutputStream out = null;
                    try {
                        in = new DataInputStream(a.getInputStream());
                        out = new DataOutputStream(a.getOutputStream());

                        float received = in.readFloat();
                        System.out.println("Server received: " + received);
                        out.writeFloat(avg);
                        System.out.println("Server sent: " + avg);
                    } catch (IOException ignored) {
                    }
                }
            };
            t.start();
        }

    }
}