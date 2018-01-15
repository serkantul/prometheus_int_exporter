package org.onosproject.monitoring.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Client {

    public static void main(String[] args) throws Exception {
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
    }
}
