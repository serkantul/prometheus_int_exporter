package org.onosproject.monitoring.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println(msg);
    }

    /*
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        ByteBuf readedMessage = (ByteBuf) msg;

        try {
            for(int i=0; i<10; ++i){
                System.out.println(readedMessage.getInt(i));
            }

        } finally {
            readedMessage.release();
        }

    }
    */

    /*
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf m = (ByteBuf) msg;

        try {
            long currentTimeMillis = (m.readUnsignedInt() - 2208988800L) * 1000L;
            System.out.println(new Date(currentTimeMillis));
            ctx.close();

        } finally {
            m.release();
        }
    }
    */

    /*
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
    */
}
