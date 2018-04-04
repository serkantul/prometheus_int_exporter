package org.onosproject.monitoring.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.socket.DatagramPacket;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.PushGateway;
import org.onosproject.monitoring.packet.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;


//import java.net.DatagramPacket;

public class IntPushGateway {



    static final CollectorRegistry pushRegistry = new CollectorRegistry();
    private static final HashMap<Integer, Gauge> hopLatencyGaugeList = new HashMap<Integer, Gauge>();
    private static final HashMap<Byte, Gauge> queueOccupancyGaugeList = new HashMap<Byte, Gauge>();
    private static final HashMap<Integer, Gauge> portTxGaugeList = new HashMap<Integer, Gauge>();


    // Registration per flow latency
    static final Gauge total_flow_latency_gauge = Gauge.build()
            .name("total_flow_latency_ms")
            .help("Shows total flows latency")
            .labelNames("flowspath")
            .register(pushRegistry);


    /**
     * Triger when server receive a packet
     */
    public boolean receivePacket(DatagramPacket packet) throws DeserializationException {

        Ethernet eth;
        IPv4 ip;
        UDP udp;
        TelemetryReport report;

        ByteBuf buf = packet.copy().content();
        byte[] data = new byte[buf.readableBytes()];

        buf.readBytes(data);

        // TODO: Need to do some check of result
        eth = Ethernet.deserializer().deserialize(data, 0, data.length);
        ip = (IPv4) eth.getPayload();
        udp = (UDP) ip.getPayload();

        if( udp.getPayload() instanceof TelemetryReport) {
            report = (TelemetryReport) udp.getPayload();
        } else {
            return false;
        }


        // Asume the TelemetryReport data is get correctly

        // Create and send report to Push Gateway
        try {
            if( createReport(report) ) {

                PushGateway pg = new PushGateway("127.0.0.1:9091");
                pg.pushAdd(pushRegistry, "intDemo");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;

    }

    private boolean createReport(TelemetryReport report) throws UnknownHostException {

        // Check if has the flow we want to know
        if( !report.hasTrackedFlow() ) { return false; }

        // TODO: Need to do some check of result
        Ethernet eth = (Ethernet) report.getPayload();
        IPv4 ip = (IPv4) eth.getPayload();
        TCP tcp = (TCP) ip.getPayload();
        P4Int int_report = (P4Int) tcp.getPayload();

        // Get the flow
        Flow flow = new Flow(InetAddress.getByAddress(ByteBuffer.allocate(4).putInt(ip.getSourceAddress()).array()).getHostAddress(),
                                 InetAddress.getByAddress(ByteBuffer.allocate(4).putInt(ip.getDestinationAddress()).array()).getHostAddress(),
                                 tcp.getSourcePort(),
                                 tcp.getDestinationPort(),
                                 ip.getProtocol()
        );

        // Get the path information
        FlowPath path = new FlowPath(int_report);

        // Check
        System.out.println(flow);
        System.out.println(path);
        System.out.println(path.switchs);

        // Get per flow latency information
        total_flow_latency_gauge.labels(flow + "" + path).set(path.getAllLatency());

        // Registration of per switch if in instruction bitmap


        // Hop latency
        for( SwitchInfo s : path.switchs ) {
            Gauge hop_latency_gauge = Gauge.build()
                    .name("switch_" + s.switchId + "_hop_latency_ms")
                    .help("Shows hop latency of switch " + s.switchId)
                    .labelNames("flows")
                    .register(pushRegistry);
            hopLatencyGaugeList.put(s.switchId, hop_latency_gauge);

            hop_latency_gauge.labels(flow.toString()).set(s.hopLatency);
        }

        // Queue occupancy
        for( SwitchInfo s : path.switchs ) {
            Gauge queue_occupancy_gauge = Gauge.build()
                    .name("switch_" + s.switchId + "_queue_occupancy")
                    .help("Shows queue occupancy of switch " + s.switchId)
                    .labelNames("queueId")
                    .register(pushRegistry);
            queueOccupancyGaugeList.put(s.queueId, queue_occupancy_gauge);

            queue_occupancy_gauge.labels(String.valueOf(s.queueId)).set(s.queueOccupancy);
        }

        // Port Tx utilization
        for( SwitchInfo s : path.switchs ) {
            Gauge port_tx_gauge = Gauge.build()
                    .name("switch_" + s.switchId + "_port_tx")
                    .help("Shows port tx utilization of switch" + s.switchId)
                    .labelNames("portId")
                    .register(pushRegistry);
            portTxGaugeList.put(s.portId, port_tx_gauge);

            port_tx_gauge.labels(String.valueOf(s.portId)).set(s.portTx);
        }

        return true;
    }

    /**
     * 5-tuple values for flows.
     */
    static class Flow {

        String fromIP;
        String destIP;
        int fromPort;
        int destPort;
        int protocol;

        public Flow(String fromIP, String destIP, int fromPort, int destPort, int protocol) {
            this.fromIP = fromIP;
            this.destIP = destIP;
            this.fromPort = fromPort;
            this.destPort = destPort;
            this.protocol = protocol;
        }

        public String toString() {
            return fromIP + ":" + fromPort + "->" + destIP + ":" + destPort;
        }

    }

    /**
     * Switch list on flow path.
     */
    static class FlowPath {

        ArrayList<SwitchInfo> switchs = new ArrayList<>();

        public FlowPath(P4Int intPacket) {
            int count = intPacket.getTotalHopCount();

            for(int i=0; i<count; i++) {
                switchs.add(new SwitchInfo(intPacket.getMetadata().get(i)));
            }
        }

        public String toString() {
            String str = new String();
            for( SwitchInfo s : this.switchs ) {
                str = str + "_" + s.switchId;
            }

            return str;
        }

        public int getAllLatency() {
            int latency = 0;
            for( SwitchInfo s : this.switchs ) {
                latency = latency + s.hopLatency;
            }

            return latency;
        }

    }

    /**
     * Store the switch information we concern about
     */
    static class SwitchInfo {

        int switchId;
        byte queueId;
        int queueOccupancy;
        int hopLatency;
        int portId;
        int portTx;

        public SwitchInfo(P4IntTransitHop node) {
            this.switchId = node.getSwitchId();
            this.queueId = node.getQueueId();
            this.queueOccupancy = node.getQueueOccupancy();
            this.hopLatency = node.getHopLatency();
            this.portId = node.getEgressPortId();
            this.portTx = node.getEgressPortTxUtil();
        }

    }



}
