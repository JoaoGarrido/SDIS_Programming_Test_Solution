# Intro
SDIS FEUP MIEEC programming test solutions. 
Necessary tools for developing a UDP multicast and TCP client-server in Java.

A huge thanks to [ZeD4805](https://github.com/ZeD4805). All the Java source code was (almost) a direct copy of his solutions.

# Java

## UDP

For UDP the local network ip should be 230.0.0.0 and ports over 5000.

### Multicast join

```Java
InetAddress multiCast_address = InetAddress.getByName(String ip);
InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress multiCast_address, int multicast_port);
MulticastSocket multicast_socket = new MulticastSocket(int multicast_port);
multicast_socket.joinGroup(multiCastAddress);
```

### Receive
```Java
DatagramPacket datagramPacket = new DatagramPacket(new byte[1000], 1000, inetSocketAddress); //Create packet to store data
multicastSocket.receive(DatagramPacket datagramPacket); //Wait for packet
String data = new String(datagramPacket.getData()); //Create string from byte array packet
```

### Send
To convert from string to bytes a charset needs to be chosen. Include import java.nio.charset.*; and use StandardCharsets.UTF_8 .
```Java
byte[] msgBytes = s.getBytes(StandardCharsets.UTF_8); //Create byte array from string
DatagramPacket datagramPacket = new DatagramPacket(byte[] msgBytes, int msgBytes.length,InetSocketAddress group); //Create packet
multicastSocket.send(DatagramPacket datagramPacket); //Send packet
```

## TCP

For TCP the local ip should be 127.0.0.1 and ports over 5000.

### Socket creation
Normal socket:
```Java
Socket socket = new Socket(String server_ip, int port);
```

Server socket:
```Java
ServerSocket server_socket = new ServerSocket(int port);
```
A server socket waits until a sockets tries to connect to it:
```Java
Socket socket = server_socket.accept();
```

### Datastream
DataStream are the streams used for the read and write operations 
```Java
DataInputStream in = new DataInputStream(socket.getInputStream());
```

```Java
DataOutputStream out = new DataOutputStream(socket.getOutputStream());
```

### Read/Write
Read and write functions have several versions for different variable types.

Read: 
```Java
int received = in.readInt();
```

Write:
```Java
float avg = 10.15;
out.writeFloat(avg);
```

### Timeout
Throws an exception after timeout ms without response :
```Java
socket.setSoTimeout(int timeout);

try{
    ...
}
catch (SocketException eTimeout) {
    ...
}
```

## Closing socket
```Java
socket.close();
```
        
### Check socket status
Check if socket is already available:
```Java
while(socket.isClosed());
```

Work while the socket is connected:
```Java
while(socket.isConnected()){
    ...
}
```

## Utilities

### Create Thread
```Java
Thread t = new Thread() {
    public void run() {
        ...
    }
}
t.start();
```

### Delay
```Java
Thread.sleep(int period);
```

### Parse
Integer:
```Java
int number = Integer.parseInt(String str);
```

Float:
```Java
float number = Float.parseFloat(String str);
```

### Print
```Java
System.out.println("Something: " + data);
```

### Loop through array of args
```Java
for (String s : Arrays.copyOfRange(String[] args, int starting_element , int args.length)){
    ...
}