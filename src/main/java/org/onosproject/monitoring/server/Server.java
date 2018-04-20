package org.onosproject.monitoring.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

// A simple chat-fashion server model which accepts connections
// and print the client messages on the server
public class Server {

    public static void main(String[] args) throws Exception {
        int port = 1234;
        new Server(port).run();
    }

    private final int port;
    public Server(int port){
        this.port = port;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new EpollEventLoopGroup();

        try {

            Bootstrap b = new Bootstrap();
            b.group(bossGroup)
                    .channel(EpollDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .option(EpollChannelOption.SO_REUSEPORT, true)
                    .handler(new ServerHandler());


            b.bind(port).sync().channel().closeFuture().await();

        } finally {
            bossGroup.shutdownGracefully();
        }

    }
}

