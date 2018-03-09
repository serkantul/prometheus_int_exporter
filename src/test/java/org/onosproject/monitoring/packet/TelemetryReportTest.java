/*
 * Copyright 2017-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onosproject.monitoring.packet;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Tests for class {@link TelemetryReport}.
 */
public class TelemetryReportTest {

    private static byte[] bytePacketReport = {
            // Telemetry Report Fixed Header
            (byte) 0x10, (byte) 0xe0, (byte) 0x00, (byte) 0x01, // version, next protocol, D, Q, F, reserved, hw_id
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xab, // sequence
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xcd, // ingress timestamp
    };

    private static byte[] bytePacketDropReport = {
            // Telemetry Report Fixed Header
            (byte) 0x11, (byte) 0xe0, (byte) 0x00, (byte) 0x01, // version, next protocol, D, Q, F, reserved, hw_id
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xab, // sequence
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xcd, // ingress timestamp

            // Telemetry Drop Report Header
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, // switch id
            (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01, // ingress port id, egress port id
            (byte) 0x01, (byte) 0x01, (byte) 0x00, (byte) 0x00, // queue ID, drop reason, pad
    };

    private static byte[] bytePacketSwitchLocalReport = {
            // Telemetry Report Fixed Header
            (byte) 0x12, (byte) 0xe0, (byte) 0x00, (byte) 0x01, // version, next protocol, D, Q, F, reserved, hw_id
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xab, // sequence
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xcd, // ingress timestamp

            // Telemetry Switch Local Report Header
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, // switch id
            (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01, // ingress port id, egress port id
            (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0xab, // queue ID, queue occupancy
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xcd, // egress timestamp
    };

    private static Deserializer<TelemetryReport> deserializer = TelemetryReport.deserializer();

    /**
     * Tests deserialize.
     */
    @Test
    public void testDeserializeReport() throws Exception {
        TelemetryReport reportPacket = deserializer.deserialize(bytePacketReport, 0, bytePacketReport.length);

        assertThat(reportPacket.getVersion(), is((byte) 0x01));
        assertThat(reportPacket.getNextProto(), is((byte) 0x00));
        assertThat(reportPacket.hasDroppedPacket(), is((boolean) true));
        assertThat(reportPacket.hasCongestion(), is((boolean) true));
        assertThat(reportPacket.hasTrackedFlow(), is((boolean) true));
        assertThat(reportPacket.getHwId(), is((byte) 0x01));
        assertThat(reportPacket.getSequence(), is((int) 0xab));
        assertThat(reportPacket.getIngressTimeStamp(), is((int) 0xcd));
    }

    @Test
    public void testDeserializeDropReport() throws Exception {
        TelemetryReport reportPacket = deserializer.deserialize(bytePacketDropReport, 0,
                                                                bytePacketDropReport.length);

        assertThat(reportPacket.getVersion(), is((byte) 0x01));
        assertThat(reportPacket.getNextProto(), is((byte) 0x01));
        assertThat(reportPacket.hasDroppedPacket(), is((boolean) true));
        assertThat(reportPacket.hasCongestion(), is((boolean) true));
        assertThat(reportPacket.hasTrackedFlow(), is((boolean) true));
        assertThat(reportPacket.getHwId(), is((byte) 0x01));
        assertThat(reportPacket.getSequence(), is((int) 0xab));
        assertThat(reportPacket.getIngressTimeStamp(), is((int) 0xcd));

        assertThat(reportPacket.getSwitchId(), is((int) 0x01));
        assertThat(reportPacket.getIngressPortId(), is((short) 0x01));
        assertThat(reportPacket.getEgressPortId(), is((short) 0x01));
        assertThat(reportPacket.getQueueId(), is((byte) 0x01));
        assertThat(reportPacket.getDropReason(), is((byte) 0x01));
    }

    @Test
    public void testDeserializeSwitchLocalReport() throws Exception {
        TelemetryReport reportPacket = deserializer.deserialize(bytePacketSwitchLocalReport, 0,
                                                                bytePacketSwitchLocalReport.length);

        assertThat(reportPacket.getVersion(), is((byte) 0x01));
        assertThat(reportPacket.getNextProto(), is((byte) 0x02));
        assertThat(reportPacket.hasDroppedPacket(), is((boolean) true));
        assertThat(reportPacket.hasCongestion(), is((boolean) true));
        assertThat(reportPacket.hasTrackedFlow(), is((boolean) true));
        assertThat(reportPacket.getHwId(), is((byte) 0x01));
        assertThat(reportPacket.getSequence(), is((int) 0xab));
        assertThat(reportPacket.getIngressTimeStamp(), is((int) 0xcd));

        assertThat(reportPacket.getSwitchId(), is((int) 0x01));
        assertThat(reportPacket.getIngressPortId(), is((short) 0x01));
        assertThat(reportPacket.getEgressPortId(), is((short) 0x01));
        assertThat(reportPacket.getQueueId(), is((byte) 0x01));
        assertThat(reportPacket.getQueueOccupancy(), is((int) 0xab));
        assertThat(reportPacket.getEgressTstamp(), is((int) 0xcd));
    }

    @Test
    public void testDeserializeBadInput() throws Exception {
        PacketTestUtils.testDeserializeBadInput(deserializer);
    }

    @Test
    public void testDeserializeTruncated() throws Exception {
        PacketTestUtils.testDeserializeTruncated(deserializer, bytePacketReport);
        PacketTestUtils.testDeserializeTruncated(deserializer, bytePacketDropReport);
        PacketTestUtils.testDeserializeTruncated(deserializer, bytePacketSwitchLocalReport);
    }

    @Test
    public void testDeserializeFromPcap() throws Exception {
        final String fname = "src/test/resources/int_report.pcap";
        byte[] byteFromPcap = PacketTestUtils.readFromPcapFile(fname);
        assertNotNull(byteFromPcap);

        Ethernet eth = Ethernet.deserializer().deserialize(byteFromPcap, 0, byteFromPcap.length);
        IPv4 ip = (IPv4) eth.getPayload();
        UDP udp = (UDP) ip.getPayload();
        byte[] bytePacketUdp = udp.serialize();

        TelemetryReport reportPacket = deserializer.deserialize(bytePacketUdp, 0, bytePacketUdp.length);

        assertThat(reportPacket.getVersion(), is((byte) 0x00));
        assertThat(reportPacket.getNextProto(), is((byte) 0x00));
        assertThat(reportPacket.hasDroppedPacket(), is((boolean) false));
        assertThat(reportPacket.hasCongestion(), is((boolean) false));
        assertThat(reportPacket.hasTrackedFlow(), is((boolean) true));
        assertThat(reportPacket.getHwId(), is((byte) 0x00));
        assertThat(reportPacket.getSequence(), is((int) 0x00));
        assertThat(reportPacket.getIngressTimeStamp(), is((int) 0x00));

    }

}
