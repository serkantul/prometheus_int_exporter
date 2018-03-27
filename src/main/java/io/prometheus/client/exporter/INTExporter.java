package io.prometheus.client.exporter;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import org.onosproject.monitoring.packet.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static java.lang.String.valueOf;

public class INTExporter {

    private final CollectorRegistry pushRegistry = new CollectorRegistry();
    private final PushGateway pushGateway;
    private static  final String JOB_NAME = "INTExporter";

    /**
     * Holds Gauge objects for each switch
     * s1_latency_ms = {flow1 -> 80, flow2 -> 90} s2_latency_ms = {flow1 -> 80, flow2 -> 90}
     * s3_latency_ms = {flow1 -> 80, flow2 -> 90} s4_latency_ms = {flow1 -> 80, flow2 -> 90}
     */
    private final HashMap<Integer, Gauge> hopLatencyDurationGaugeList;

    /**
     * Gauge object that observes total latency for each flow and its path
     * e.g. flow_latency_duration = {flow1.s1.s2.s3 -> 80, flow2.s1.s2.s3 -> 90}
     */
    private final Gauge flowLatencyDuration;

    /**
     * For each switch in the topology, queue occupancy metrics are collected. A gauge instance for each switch
     * is instantiated for this purpose. Each queueID is a label that records each queues’ queue occupancy percentage
     * e.g. queue_congestion_status_percentage_{SwitchID} = [{queueID}=5, ….]
     */
    private final HashMap<Integer, Gauge> queueCongestionStatusGaugeList;

    /**
     * For each switch in the topology, egress port tx utilization metrics are collected. A gauge instance for
     * each switch is instantiated for this purpose. Each port id is a label that records egress port tx utilization
     * percentage. E.g. egress_port_tx_utilization_percentage_{SwitchID}= [{PortID}=5, ….]
     */
    private final HashMap<Integer, Gauge> egressPortTxUtilizationGaugeList;

    public INTExporter(String pushGWAddress) {
        pushGateway = new PushGateway(pushGWAddress);
        hopLatencyDurationGaugeList = new HashMap<>();
        flowLatencyDuration = Gauge.build()
                .name("flow_latency_duration")
                .help("Shows total flows latency and its path")
                .labelNames("flowandpath")
                .register(pushRegistry);
        queueCongestionStatusGaugeList = new HashMap<>();
        egressPortTxUtilizationGaugeList = new HashMap<>();
    }


    public void pushMetrics(TelemetryReport report) {
        int srcPort = 0, dstPort = 0;
        IpAddress srcIP, dstIP;
        if (report.hasTrackedFlow()) {
            Ethernet eth = (Ethernet) report.getPayload();
            if (eth.getEtherType() == Ethernet.TYPE_IPV4) {
                IPv4 ipv4 = (IPv4) eth.getPayload();
                srcIP = Ip4Address.valueOf(ipv4.getSourceAddress());
                dstIP = Ip4Address.valueOf(ipv4.getDestinationAddress());
                Object l4Payload = null;
                int ipProto = ipv4.getProtocol();
                if (ipProto == IPv4.PROTOCOL_TCP) {
                    TCP tcp = (TCP) ipv4.getPayload();
                    srcPort = tcp.getSourcePort();
                    dstPort = tcp.getDestinationPort();
                    l4Payload = tcp.getPayload();
                } else if (ipProto == IPv4.PROTOCOL_UDP) {
                    UDP udp = (UDP) ipv4.getPayload();
                    srcPort = udp.getSourcePort();
                    dstPort = udp.getDestinationPort();
                    l4Payload = udp.getPayload();
                } else {
                    System.err.println("Unexpected L4 protocol!");
                }
                if (l4Payload != null && ipv4.getDscp() == TCP.INT_DSCP) {
                    P4Int p4Int = (P4Int) l4Payload;
                    String flowLabel = prepareFlowLabel(srcIP, srcPort, dstIP, dstPort, ipProto);
                    pushMetrics(p4Int.getMetadata(), flowLabel);
                }
            } else {
                System.err.println("Unexpected L3 protocol!");
            }
        }
    }

    /**
     * Collects the metric values from the P4IntTransitHop and push the metrics
     * to the Prometheus Push GW.
     * @param metadata
     * @param flowStr
     */
    private void pushMetrics(List<P4IntTransitHop> metadata, String flowStr) {
        int totalFlowLatency = 0;
        StringBuffer flowPath = new StringBuffer();
        for (P4IntTransitHop transitHop : metadata) {
            int switchId = transitHop.getSwitchId();
            int hopLatency = transitHop.getHopLatency();
            int queueId = transitHop.getQueueId();
            int queueCongestion = transitHop.getQueueCongestion();
            short egressPortId = transitHop.getEgressPortId();
            int portTxUtil = transitHop.getEgressPortTxUtil();
            totalFlowLatency += hopLatency;
            flowPath.append("s").append(switchId).append(".");
            setHopLatencyMetric(switchId, flowStr, hopLatency);
            setQueueCongestionMetric(switchId, queueId, queueCongestion);
            setEgressPortUtilizationMetric(switchId, egressPortId, portTxUtil);
        }
        setFlowLatencyMetric(flowPath, flowStr, totalFlowLatency);
        try {
            pushGateway.pushAdd(pushRegistry, JOB_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param flowPath
     * @param flowStr
     * @param totalFlowLatency
     */
    private void setFlowLatencyMetric(StringBuffer flowPath, String flowStr, int totalFlowLatency) {
        String label = new StringBuffer().append(flowPath).append("-").append(flowStr).toString();
        flowLatencyDuration.labels(label).set(totalFlowLatency);
    }

    /**
     * @param switchId
     * @param egressPortId
     * @param portTxUtil
     */
    private void setEgressPortUtilizationMetric(int switchId, short egressPortId, int portTxUtil) {
        Gauge gauge = egressPortTxUtilizationGaugeList.get(switchId);
        if (gauge == null) {
            gauge = Gauge.build()
                    .name("egress_port_tx_utilization_percentage_s" + switchId)
                    .help("Shows egress port TX utilization for each switch port")
                    .labelNames("port_id")
                    .register(pushRegistry);
            egressPortTxUtilizationGaugeList.put(switchId, gauge);
        }
        gauge.labels(valueOf(egressPortId)).set(portTxUtil);
    }

    /**
     * @param switchId
     * @param queueId
     * @param queueCongestion
     */
    private void setQueueCongestionMetric(int switchId, int queueId, int queueCongestion) {
        Gauge gauge = queueCongestionStatusGaugeList.get(switchId);
        if (gauge == null) {
            gauge = Gauge.build()
                    .name("queue_congestion_status_percentage_s" + switchId)
                    .help("Shows queue congestion status of each queue for the switch")
                    .labelNames("queueId")
                    .register(pushRegistry);
            queueCongestionStatusGaugeList.put(switchId, gauge);
        }
        gauge.labels(valueOf(queueId)).set(queueCongestion);
    }

    /**
     * @param switchId
     * @param flowStr
     * @param hopLatency
     */
    private void setHopLatencyMetric(int switchId, String flowStr, int hopLatency) {
        Gauge gauge = hopLatencyDurationGaugeList.get(switchId);
        if (gauge == null) {
            gauge = Gauge.build()
                    .name("hop_latency_duration_s" + switchId)
                    .help("Shows hop latency for each flow")
                    .labelNames("flow")
                    .register(pushRegistry);
            hopLatencyDurationGaugeList.put(switchId, gauge);
        }
        gauge.labels(flowStr).set(hopLatency);
    }

    /**
     * Prepares a String object that identifies a 5-tuple-flow.
     *
     * @param srcIP
     * @param srcPort
     * @param dstIP
     * @param dstPort
     * @param ipProto
     * @return 5-tuple-flow as string
     */
    private String prepareFlowLabel(IpAddress srcIP, int srcPort, IpAddress dstIP, int dstPort, int ipProto) {
        StringBuffer sb = new StringBuffer();
        sb.append(srcIP.toString()).append(":").append(srcPort).append(":");
        sb.append(dstIP.toString()).append(":").append(dstPort).append(":");
        sb.append(ipProto);
        return sb.toString();
    }


}
