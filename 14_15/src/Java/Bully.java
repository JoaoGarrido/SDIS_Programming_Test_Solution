import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class Bully {
    public enum BullyStatus{
        Sleep("Sleep"),
        Election("Election"),
        Leader("Leader"),
        Finished("Finished");

        BullyStatus(String representation){
            this.representation = representation;
        }

        String representation;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        InetAddress address = InetAddress.getByName(args[0]);
        int port = Integer.parseInt(args[1]);
        int id = Integer.parseInt(args[2]);
        int T = Integer.parseInt(args[3]);
        int delay = Integer.parseInt(args[4]);

        InetSocketAddress socketAddress = new InetSocketAddress(address, port);
        MulticastSocket multicastSocket = new MulticastSocket(port);
        multicastSocket.joinGroup(address);

        int initialDelay = new Random().nextInt(id * delay);

        DatagramPacket received = new DatagramPacket(new byte[1000], 1000, socketAddress);

        int leaderID = -1;

        BullyStatus status = BullyStatus.Sleep;
        while(status != BullyStatus.Finished){
            if(status == BullyStatus.Sleep){
                try {
                    multicastSocket.setSoTimeout(initialDelay);
                    multicastSocket.receive(received);
                    multicastSocket.setSoTimeout(0); //got the packet
                    String receivedStr = new String(received.getData(), StandardCharsets.UTF_8);
                    System.out.println("" + id + ", in state " + status.representation + ", received: "+ receivedStr);
                    if(receivedStr.charAt(0) == 'E'){ //election message
                        String sub = receivedStr.substring(1, 2);
                        int electionID = Integer.parseInt(sub);
                        //is it of lower id?
                        if (id < electionID){
                            status = BullyStatus.Leader;
                            leaderID = id;

                            String electionMessage = ("E" + id);
                            byte[] electionMessageBytes = electionMessage.getBytes(StandardCharsets.UTF_8);
                            System.out.println("" + id + ", in state " + status.representation + ", sent: " + electionMessage);
                            multicastSocket.send(new DatagramPacket(electionMessageBytes, electionMessageBytes.length, socketAddress));
                        }
                        else{
                            status = BullyStatus.Election;
                            leaderID = electionID;
                        }
                    }

                }catch (java.net.SocketTimeoutException e){//packet reception timeout
                    status = BullyStatus.Leader; //elects itself
                    leaderID = id;

                    String electionMessage = ("E" + id);
                    byte[] electionMessageBytes = electionMessage.getBytes(StandardCharsets.UTF_8);
                    System.out.println("" + id + ", in state " + status.representation + ", sent: " + electionMessage);
                    multicastSocket.send(new DatagramPacket(electionMessageBytes, electionMessageBytes.length, socketAddress));
                }
            }
            else if (status == BullyStatus.Election){
                do {
                    multicastSocket.receive(received);
                    String receivedStr = new String(received.getData(), StandardCharsets.UTF_8);
                    System.out.println("" + id + ", in state " + status.representation + ", received: "+ receivedStr);
                    if(receivedStr.charAt(0) == 'E'){
                        int electionID = Integer.parseInt(receivedStr.substring(1, 2));
                        if(electionID < leaderID){
                            leaderID = electionID;
                        }
                    }
                    if(receivedStr.charAt(0) == 'L'){//if received an leader announcement message
                        int leaderAnnouncementID = Integer.parseInt(receivedStr.substring(1, 2));
                        if(leaderAnnouncementID != leaderID){
                            System.out.println("" + leaderAnnouncementID + " wasn't "+ id + "'s leader! (" + leaderID + ")");
                        }
                        status = BullyStatus.Finished;
                        break;
                    }
                }while(true);
            }
            else if (status == BullyStatus.Leader){
                try {
                    multicastSocket.setSoTimeout(2 * T);
                    multicastSocket.receive(received);
                    multicastSocket.setSoTimeout(0);
                    String receivedStr = new String(received.getData(), StandardCharsets.UTF_8);
                    System.out.println("" + id + ", in state " + status.representation + ", received: "+ receivedStr);
                    if(receivedStr.charAt(0) == 'E'){//if received an election message
                        int electionID = Integer.parseInt(receivedStr.substring(1, 2));
                        if(electionID < id){ //if id is bigger wait for stronger leader
                            status = BullyStatus.Election;
                            leaderID = electionID;
                        }
                    }
                }catch (SocketTimeoutException e){
                    String electionMessage = ("L" + id);
                    byte[] electionMessageBytes = electionMessage.getBytes(StandardCharsets.UTF_8);
                    multicastSocket.send(new DatagramPacket(electionMessageBytes, electionMessageBytes.length, socketAddress));
                    System.out.println("" + id + ", in state " + status.representation + ", sent: " + electionMessage);
                    status = BullyStatus.Finished;
                }
            }
        }

        System.out.println("" + id + " finished with leader: " + leaderID);
    }
}
