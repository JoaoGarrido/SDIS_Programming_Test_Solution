import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.charset.*;

public class Producer {
    public static void main(String[] args) throws IOException, InterruptedException {
            InetAddress mcastaddr = InetAddress.getByName(args[0]);
            int port = Integer.parseInt(args[1]);
    
            InetSocketAddress group = new InetSocketAddress(mcastaddr, port);
            MulticastSocket multicastSocket = new MulticastSocket(port);
            multicastSocket.joinGroup(mcastaddr);
    
            for (String s : Arrays.copyOfRange(args, 2, args.length)){
                Thread.sleep(1000);
                byte[] msgBytes = s.getBytes(StandardCharsets.UTF_8);
                DatagramPacket datagramPacket = new DatagramPacket(msgBytes, msgBytes.length, group);
                multicastSocket.send(datagramPacket);
                System.out.println("Producer sent: " + s);
            }
        }
    }
    