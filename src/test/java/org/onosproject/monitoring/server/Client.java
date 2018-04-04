package org.onosproject.monitoring.server;


import org.onosproject.monitoring.packet.PacketTestUtils;

import java.io.IOException;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Client {

    public static void main(String args[]){


        byte [] buf = null;
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
            System.out.println(address);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }



        buf = PacketTestUtils.readFromPcapFile("/home/iver/prometheus_int_exporter/src/test/resources/int_report.pcap");


        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 27072);
        try {
            socket.send(packet);
            System.out.println(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



/*    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int port = 27072;

        new Client(host, port).run();
        // Write a message to the console after starting the client to
        // make everyone connected to the server see your messages
    }

    private final String host;
    private final int port;

    Client(String host, int port){
        this.host = host;
        this.port = port;
    }

    public void run() throws Exception {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientInitializer());

            Channel channel = bootstrap.connect(host, port).sync().channel();

            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

            while(true){
                channel.write(in.readLine() + "\r\n");
                channel.flush();
            }

        } finally {
            workerGroup.shutdownGracefully();
        }
    }*/
}
