package org.onosproject.monitoring.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ServerHandler extends SimpleChannelInboundHandler<String> {

    private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();

        channels.add(ctx.channel());
        for(Channel channel : channels){

            if(channel == incoming){
                channel.write("You have been connected to the server!\n");
            }
            else{
                channel.write("SERVER: " + incoming.remoteAddress() + " has been connected!\n");
            }
            channel.flush();
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();

        for(Channel channel : channels){
            channel.write("SERVER: " + incoming.remoteAddress() + " has been disconnected!\n");
            channel.flush();
        }
        channels.remove(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Channel incoming = ctx.channel();

        System.out.println("[" + incoming.remoteAddress() + "]: " + msg);
        ReferenceCountUtil.release(msg);
    }

    /*
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Channel incoming = ctx.channel();
        for(Channel channel : channels){
            if(channel != incoming){
                channel.write("[" + incoming.remoteAddress() + "]" + msg + "\n");
                channel.flush();
            }
        }

    }
    */

    /*
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;

        try {
            while (in.isReadable()) {
                System.out.print((char) in.readByte());
                System.out.flush();
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }
    */

    /*
    @Override
    public void channelActive(ChannelHandlerContext ctx){
        ByteBuf messageBuffer = ctx.alloc().buffer(40);

        for(int i=0; i<10; ++i){
            messageBuffer.writeInt(i);
            System.out.println("Writen Data: " + i);
        }

        ctx.writeAndFlush(messageBuffer);
        ctx.close();
    }
    */

    /*
    Echo Server Handler

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ctx.writeAndFlush(msg);
    }
    */

    /* Time Server Handler

       the channelActive() method will be invoked when a connection
       is established and ready to generate traffic.

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        // Allocate new message buffer which has at least 4 Bytes
        final ByteBuf time = ctx.alloc().buffer(4);
        time.writeInt((int) (System.currentTimeMillis() / 1000L + 2208988800L));

        // We don't have ByteBuffer.flip() because we don't need it as in NIO buffer
        // Instead ByteBuffer uses two pointers - for read and for write

        // A ChannelFuture represents an I/O operation which has not yet occurred.
        // It means, any requested operation might not have been performed yet because
        // all operations are asynchronous in Netty.
        final ChannelFuture f = ctx.writeAndFlush(time);

        // Please note that, close() also might not close the connection immediately,
        // and it returns a ChannelFuture.
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                assert f == future;
                ctx.close();
            }
        });

        // This is also valid and simplifies the code
        // f.addListener(ChannelFutureListener.CLOSE);
    }
    */

    /*
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
    */
}
