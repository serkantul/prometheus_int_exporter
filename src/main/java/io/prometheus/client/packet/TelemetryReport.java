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

package io.prometheus.client.packet;

import java.nio.ByteBuffer;

import static io.prometheus.client.packet.PacketUtils.checkHeaderLength;
import static io.prometheus.client.packet.PacketUtils.checkInput;

/**
 * Representation of Telemetry Report Packet.
 *
 */
public class TelemetryReport extends BasePacket {

    private static final short TELEMETRY_REPORT_FIXED_HEADER_LENGTH = 12;
    private static final short TELEMETRY_DROP_HEADER_LENGTH = 12;
    private static final short TELEMETRY_SWITCH_LOCAL_HEADER_LENGTH = 16;

    private static final short TYPE_TELEMETRY_DROP = 1;
    private static final short TYPE_TELEMETRY_SWITCH_LOCAL = 2;

    protected byte ver;
    protected byte nextProto;
    protected byte d;
    protected byte q;
    protected byte f;
    protected short reserved;
    protected byte hwId;
    protected int sequence;
    protected int ingressTstamp;
    protected int egressTstamp;
    protected int switchId;
    protected short ingressPortId;
    protected short egressPortId;
    protected byte queueId;
    protected int queueOccupancy;
    protected byte dropReason;
    protected short pad;

    /**
     * Gets telemetry report version.
     *
     * @return telemetry report version
     */
    public byte getVersion() {
        return this.ver;
    }

    /**
     * Gets next protocal.
     *
     * @return next protocal
     *
     * 0 - Ethernet
     * 1 - Telemetry Drop header, followed by Ethernet
     * 2 - Telemetry Switch Local header, followed by Ethernet
     */
    public byte getNextProto() {
        return this.nextProto;
    }

    /**
     * Indicates that at least one packet matching a drop watchlist was dropped.
     *
     * @return true if D bit is set
     */
    public boolean hasDroppedPacket() {
        return this.d == 1;
    }

    /**
     * Indicates the presence of congestion on a monitored queue.
     *
     * @return true if Q bit is set
     */
    public boolean hasCongestion() {
        return this.q == 1;
    }

    /**
     * Indicates that this telemetry report is for a tracked flow.
     *
     * @return true if F bit is set
     */
    public boolean hasTrackedFlow() {
        return this.f == 1;
    }

    /**
     * Gets the hardware subsystem within the source that generated this report.
     *
     * @return hardware ID
     */
    public byte getHwId() {
        return this.hwId;
    }

    /**
     * Gets the sequence number.
     *
     * @return sequence number
     */
    public int getSequence() {
        return this.sequence;
    }

    /**
     * Gets egress time stamp.
     *
     * @return egress time stamp
     */
    public int getEgressTstamp() {
        return this.egressTstamp;
    }

    /**
     * Gets ingress time stamp.
     *
     * @return ingress time stamp
     */
    public int getIngressTimeStamp() {
        return this.ingressTstamp;
    }

    /**
     * Gets switch id.
     *
     * @return switch id
     */
    public int getSwitchId() {
        return this.switchId;
    }

    /**
     * Gets ingress port ID.
     *
     * @return ingress port ID
     */
    public short getIngressPortId() {
        return this.ingressPortId;
    }

    /**
     * Gets egress port ID.
     *
     * @return egress port ID
     */
    public short getEgressPortId() {
        return this.egressPortId;
    }

    /**
     * Gets queue ID.
     *
     * @return queue ID
     */
    public byte getQueueId() {
        return this.queueId;
    }

    /**
     * Gets queue occupancy.
     *
     * @return queue occupancy
     */
    public int getQueueOccupancy() {
        return this.queueOccupancy;
    }

    /**
     * Gets the reason why a packet was dropped.
     *
     * @return drop reason
     */
    public byte getDropReason() {
        return this.dropReason;
    }

    /**
     * Deserializer function for Telemetry Report packets.
     *
     * @return deserializer function
     */
    public static Deserializer<TelemetryReport> deserializer() {
        return (data, offset, length) -> {
            checkInput(data, offset, length, TELEMETRY_REPORT_FIXED_HEADER_LENGTH);

            final ByteBuffer bb = ByteBuffer.wrap(data, offset, length);
            TelemetryReport reportPacket = new TelemetryReport();

            int firstWord = bb.getInt();
            reportPacket.ver = (byte) (firstWord >> 28 & 0xf);
            reportPacket.nextProto = (byte) (firstWord >> 24 & 0xf);
            reportPacket.d = (byte) (firstWord >> 23 & 0x1);
            reportPacket.q = (byte) (firstWord >> 22 & 0x1);
            reportPacket.f = (byte) (firstWord >> 21 & 0x1);
            reportPacket.reserved = (short) (firstWord >> 6 & 0x7fff);
            reportPacket.hwId = (byte) (firstWord & 0x3f);

            reportPacket.sequence = bb.getInt();
            reportPacket.ingressTstamp = bb.getInt();

            if (reportPacket.nextProto == TYPE_TELEMETRY_DROP) {
                checkHeaderLength(length, TELEMETRY_REPORT_FIXED_HEADER_LENGTH +
                        TELEMETRY_DROP_HEADER_LENGTH);
                reportPacket.switchId = bb.getInt();
                reportPacket.ingressPortId = bb.getShort();
                reportPacket.egressPortId = bb.getShort();
                reportPacket.queueId = bb.get();
                reportPacket.dropReason = bb.get();
                reportPacket.pad = bb.getShort();
            }
            else if (reportPacket.nextProto == TYPE_TELEMETRY_SWITCH_LOCAL) {
                checkHeaderLength(length, TELEMETRY_REPORT_FIXED_HEADER_LENGTH +
                        TELEMETRY_SWITCH_LOCAL_HEADER_LENGTH);
                reportPacket.switchId = bb.getInt();
                reportPacket.ingressPortId = bb.getShort();
                reportPacket.egressPortId = bb.getShort();
                int queueStatus = bb.getInt();
                reportPacket.queueId = ((byte) ((queueStatus & 0xFF000000) >> 24));
                reportPacket.queueOccupancy = (queueStatus & 0x00FFFFFF);
                reportPacket.egressTstamp = bb.getInt();
            }

            reportPacket.payload = Data.deserializer()
                    .deserialize(data, bb.position(), bb.limit() - bb.position());
            reportPacket.payload.setParent(reportPacket);

            return reportPacket;
        };
    }

    @Override
    public byte[] serialize() {
        return new byte[0];
    }
}
