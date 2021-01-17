import java.io.*;
import java.net.*;

public class Consumer {
    public static void main(String[] args) throws IOException{
        InetAddress multiCastAddress = InetAddress.getByName(args[0]); //multicastAddress
        int multicastPort = Integer.parseInt(args[1]);  //multiCastPort

        InetSocketAddress inetSocketAddress = new InetSocketAddress(multiCastAddress, multicastPort);
        MulticastSocket multicastSocket = new MulticastSocket(multicastPort);
        multicastSocket.joinGroup(multiCastAddress);

        while(true){
            DatagramPacket d = new DatagramPacket(new byte[1000], 1000, inetSocketAddress);
            multicastSocket.receive(d);

            String data = new String(d.getData());
            float dataReceived = Float.parseFloat(data);

            System.out.println("Consumer received: " + dataReceived);
        }

    }    
}
