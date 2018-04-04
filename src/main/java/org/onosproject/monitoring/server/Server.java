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
        int port = 27072;
        new Server(port).run();
    }

    private final int port;
    public Server(int port){
        this.port = port;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new EpollEventLoopGroup();
        //EventLoopGroup workerGroup = new EpollEventLoopGroup();

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
            //workerGroup.shutdownGracefully();
        }

    }


/*
    public void run() throws Exception{
        final ServerHandler serverHandler = new ServerHandler();
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(serverHandler);
                        }
                    });

            ChannelFuture f = bootstrap.bind().sync();
            f.channel().closeFuture().sync();

        } finally {
            group.shutdownGracefully();
        }
    }
*/

    /*
    private int port;

    public Server(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        // Multithreaded event loops that handles I/O operation
        // boss - accepts the connection
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // worker - handles the traffic of the accepted connection
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // Helper class that sets up a server
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                // Instantiate a new Channel to accept incoming connections
                .channel(NioServerSocketChannel.class)

                // The ChannelInitializer is a special handler that is
                // purposed to help a user to configure it's own Channel.
                // It is most likely that you want to configure the
                // ChannelPipeline of the new Channel by example adding some
                // handlers such as ServerHandler
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ServerHandler());
                    }
                })

                // The max size of the queue is 128
                .option(ChannelOption.SO_BACKLOG, 128);
                // Needed for TCP .. will be removed
                //.childOption(ChannelOption.SO_KEEPALIVE, true);

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(port).sync();

            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();

        } finally {
            // Release all allocated resources
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 27072;
        new Server(port).run();
    }
    */
}

