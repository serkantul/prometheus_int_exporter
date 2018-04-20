package org.onosproject.monitoring.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.prometheus.client.exporter.INTExporter;
import org.onosproject.monitoring.packet.TelemetryReport;

public class ServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private INTExporter intExporter;

    public ServerHandler() {
        intExporter = new INTExporter("localhost:9091");
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket datagramPacket) throws Exception {
        // receive INT report
        byte[] content = new byte[datagramPacket.content().readableBytes()];
        datagramPacket.content().readBytes(content);
        TelemetryReport telemetryReport = TelemetryReport.deserializer().deserialize(content, 0, content.length);
        intExporter.pushMetrics(telemetryReport);
    }
}
