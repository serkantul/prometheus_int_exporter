package io.prometheus.client.exporter;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import io.prometheus.client.Summary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class ExampleExporter {

    private static final String switchIds[] = new String[]{"s1", "s2", "s3", "s4"};
    private static final ArrayList<Report> reportTable = new ArrayList<Report>();

    //Holds Gauge objects for each switch
    private static final HashMap<String, Gauge> hopLatencyGaugeList = new HashMap<String, Gauge>();
    static {
        // s1_latency_ms = {flow1 -> 80, flow2 -> 90} s2_latency_ms = {flow1 -> 80, flow2 -> 90}
        // s3_latency_ms = {flow1 -> 80, flow2 -> 90} s4_latency_ms = {flow1 -> 80, flow2 -> 90}
        for (String swId : switchIds) {
            Gauge hop_latency_ms = Gauge.build().name(swId + "_hop_latency_ms").
                    help("Shows hop latency of " + swId).labelNames("flows").register();
            hopLatencyGaugeList.put(swId, hop_latency_ms);
        }
    }

    //private static final HashMap<String, Histogram> hopLatencyHistogramList = new HashMap<String, Histogram>();

    //Gauge object that observes total latency for each flow and its path
    // e.g. total_flow_latency_ms = {flow1.s1.s2.s3 -> 80, flow2.s1.s2.s3 -> 90}
    static final Gauge total_flow_latency_gauge = Gauge.build().name("total_flow_latency_ms").help("Shows total flows latency").labelNames("flowspath").register();

    /**
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        new HTTPServer(1234);
        System.out.println("HttpServer is active");
        //createSwitchLatencyHistograms();
        createReports();

        while (true) {
            for (Report report: reportTable) {
                setRandomFlowLatencies(report, 10, 90);

            }
            Thread.sleep(1000);
        }
    }

    /**
     * Creates Gauge objects for all switches. For each switch, a gauge is created and latencies
     * for each flow is saved.
     */
    public static void createSwitchLatencyGauges() {
        for (String swId:switchIds) {
            Gauge hop_latency_ms = Gauge.build().name(swId + "_hop_latency_ms").
                    help("Shows hop latency of " + swId).labelNames("flows").register();
            hopLatencyGaugeList.put(swId, hop_latency_ms);
        }
    }

    /**
     * Creates Histogram objects for all switches. For each switch, a histogram is created and latencies
     * are observed.
     */
    /*public static void createSwitchLatencyHistograms() {
        double[] buckets = new double[]{10, 20, 30, 40, 50, 60, 70, 80, 90};
        for (String swId:switchIds) {
            Histogram hop_latency_histogram = Histogram.build().name(swId + "_hop_latency_histogram_ms").
                    help("Shows hop latency of " + swId).buckets(buckets).register();
            hopLatencyHistogramList.put(swId, hop_latency_histogram);
        }
    }
*/
    /**
     * Creates mock Report objects.
     */
    private static void createReports() {
        //s1 - s3 - s2
        Flow flow1 = new Flow("10.0.10.1", "10.0.20.1", 1234, 8080, 6);
        Flow flow2 = new Flow("10.0.10.2", "10.0.20.1", 1234, 8080, 6);
        //s1 - s4 -s2
        Flow flow3 = new Flow("10.0.10.1", "10.0.20.2", 1234, 8080, 6);
        //s2 - s3 - s1
        Flow flow4 = new Flow("10.0.20.1", "10.0.10.1", 8080, 1234, 6);
        Flow flow5 = new Flow("10.0.20.2", "10.0.10.1", 8080, 1234, 6);
        //s2 - s4 - s1
        Flow flow6 = new Flow("10.0.20.1", "10.0.10.2", 8080, 1234, 6);

        Report report1 = new Report(flow1);
        report1.addINTData(new INTData("s1"));
        report1.addINTData(new INTData("s3"));
        report1.addINTData(new INTData("s2"));
        reportTable.add(report1);

        Report report2 = new Report(flow2);
        report2.addINTData(new INTData("s1"));
        report2.addINTData(new INTData("s3"));
        report2.addINTData(new INTData("s2"));
        reportTable.add(report2);


        Report report3 = new Report(flow3);
        report3.addINTData(new INTData("s1"));
        report3.addINTData(new INTData("s4"));
        report3.addINTData(new INTData("s2"));
        reportTable.add(report3);

        Report report4 = new Report(flow4);
        report4.addINTData(new INTData("s2"));
        report4.addINTData(new INTData("s3"));
        report4.addINTData(new INTData("s1"));
        reportTable.add(report4);

        Report report5 = new Report(flow5);
        report5.addINTData(new INTData("s2"));
        report5.addINTData(new INTData("s3"));
        report5.addINTData(new INTData("s1"));
        reportTable.add(report5);


        Report report6 = new Report(flow6);
        report6.addINTData(new INTData("s2"));
        report6.addINTData(new INTData("s4"));
        report6.addINTData(new INTData("s1"));
        reportTable.add(report6);
    }

    /**
     * Randomly sets flow latency value in INTData
     * @param report Report objects that holds <code>INTData</code>
     * @param min min value selected as random for flow latency
     * @param max max value selected as random for flow latency
     */
    private static void setRandomFlowLatencies(Report report, int min, int max) {
        String flow = report.getFlowAsString();
        int latency = ThreadLocalRandom.current().nextInt(min, max + 1);
        total_flow_latency_gauge.labels(flow + report.getPathAsString()).set(latency);
        int hopLatency = latency;
        for (INTData intData:report.path) {
            Gauge hopLatencyGauge = hopLatencyGaugeList.get(intData.switchId);
            //Histogram hopLatencyHistogram = hopLatencyHistogramList.get(intData.switchId);
            hopLatency = ThreadLocalRandom.current().nextInt(min, hopLatency + 1);
            hopLatencyGauge.labels(flow).set(hopLatency);
            //hopLatencyHistogram.observe(hopLatency);
            intData.hopLatency = hopLatency;
            System.out.println("Hop latency for flow " + flow + " " + intData.switchId + " " + hopLatency);
        }
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

        public void setFromIP(String fromIP) {
            this.fromIP = fromIP;
        }

        public void setDestIP(String destIP) {
            this.destIP = destIP;
        }

        public void setFromPort(int fromPort) {
            this.fromPort = fromPort;
        }

        public void setDestPort(int destPort) {
            this.destPort = destPort;
        }

        public void setProtocol(int protocol) {
            this.protocol = protocol;
        }

        public String toString() {
            return fromIP + ":" + fromPort + "->" + destIP + ":" + destPort;
        }


    }

    /**
     * Report object holds flow and associated INTData generated by each switch on the path
     */
    static class Report {
        Flow flow;
        ArrayList<INTData> path = new ArrayList();

        public Report(Flow flow) {
            this.flow = flow;
        }

        public String getFlowAsString() {
            return flow.toString();
        }

        public void addINTData(INTData data) {
            path.add(data);
        }

        /**
         * Returns path as String like _s1_s2_s3
         * @return
         */
        public String getPathAsString() {
            String str = "";
            for (INTData data:path) {
                str += "_" + data.switchId;
            }
            return str;
        }
    }

    /**
     * Holds the switch ID and latency value.
     */
    static class INTData {
        private String switchId;
        private double hopLatency;

        public  INTData(String switchId) {
            this.switchId = switchId;
        }

        public String getSwitchId() {
            return switchId;
        }

        public void setHopLatency(double hopLatency) {
            this.hopLatency = hopLatency;
        }
    }
}

