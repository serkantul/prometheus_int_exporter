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

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.onosproject.monitoring.packet.PacketUtils.checkInput;

/**
 * Representation of an INT Packet.
 *
 * This implementation is based on INT over TCP/UDP
 */
public class P4Int extends BasePacket {

    private static final short INT_METADATA_HEADER_LENGTH = 8;  // bytes
    private static final short INT_SHIM_HEADER_LENGTH = 4;  // bytes
    private static final short INT_TAIL_HEADER_LENGTH = 4;  // bytes

    // INT shim header
    protected byte type;    // 8
    protected byte shimRsvd1;  // 8
    protected byte length; // 8
    protected byte shimRsvd2;  // 8

    // INT tail header
    protected byte nextProto;  // 8
    protected short destPort;  // 16
    protected byte dscp;    // 8

    // INT metadata header
    protected byte ver; // 2
    protected byte rep; // 2
    protected byte c;   // 1
    protected byte e;   // 1
    protected short rsvd1;  // 5
    protected byte insCnt; // 5
    protected byte maxHopCnt; // 8
    protected byte totalHopCnt;   // 8
    protected short instructionBitmap; // 16
    protected short rsvd2;  // 16

    protected short controlField;

    protected List<P4IntTransitHop> transitHops = new ArrayList<>();

    /**
     * Gets INT header type.
     *
     * @return INT header type
     */
    public byte getHeaderType() {
        return this.type;
    }

    /**
     * Sets INT header type.
     *
     * @param type the type to set
     * @return this
     */
    public P4Int setHeaderType(final byte type) {
        this.type = type;
        return this;
    }

    /**
     * Gets total length of INT header.
     *
     * @return total length of INT header
     */
    public byte getHeaderLength() {
        return this.length;
    }

    /**
     * Sets INT header length.
     *
     * @param length the length to set
     * @return this
     */
    public P4Int setHeaderLength(final byte length) {
        this.length = length;
        return this;
    }

    /**
     * Gets next protocol.
     *
     * @return next protocol
     */
    public byte getNextProtocol() {
        return this.nextProto;
    }

    /**
     * Sets next protocol.
     *
     * @param nextProto the next protocol to set
     * @return this
     */
    public P4Int setNextProtocol(final byte nextProto) {
        this.nextProto = nextProto;
        return this;
    }

    /**
     * Gets original destination port for TCP/UDP protocol.
     *
     * @return original destination port for TCP/UDP protocol
     */
    public short getDestinationPort() {
        return this.destPort;
    }

    /**
     * Sets original destination port for TCP/UDP protocol.
     *
     * @param destPort the destination port to set
     * @return this
     */
    public P4Int setDestinationPort(final short destPort) {
        this.destPort = destPort;
        return this;
    }

    /**
     * Gets IPv4 DSCP.
     *
     * @return IPv4 DSCP
     */
    public byte getDscp() {
        return this.dscp;
    }

    /**
     * Sets IPv4 DSCP.
     *
     * @param dscp the DSCP to set
     * @return this
     */
    public P4Int setDscp(final byte dscp) {
        this.dscp = dscp;
        return this;
    }

    /**
     * Gets INT metadata header version.
     *
     * @return INT metadata header version
     */
    public byte getVersion() {
        return this.ver;
    }

    /**
     * Sets INT metadata header version.
     *
     * @param ver the version to set
     * @return this
     */
    public P4Int setVersion(final byte ver) {
        this.ver = ver;
        return this;
    }

    /**
     * Gets replication request type.
     *
     * @return replication request type
     */
    public byte getReplicationType() {
        return this.rep;
    }

    /**
     * Sets replication request type.
     *
     * @param rep the replication request type to set
     * @return this
     */
    public P4Int setReplicationType(final byte rep) {
        this.rep = rep;
        return this;
    }

    /**
     * Check if this packet is replicas.
     *
     * @return true if C bit is set
     */
    public boolean isCopy() {
        return this.c == 1;
    }

    /**
     * Sets copy flag.
     *
     * @return this
     */
    public P4Int setCbit() {
        this.c = 1;
        return this;
    }

    /**
     * Check if max hop count is exceeded.
     *
     * @return true if E bit is set
     */
    public boolean isExceedMaxHopCount() {
        return this.e == 1;
    }

    /**
     * Sets exceed max hop count flag.
     *
     * @return this
     */
    public P4Int setEbit() {
        this.e = 1;
        return this;
    }

    /**
     * Gets instruction count.
     *
     * @return instruction count
     */
    public byte getInstructionCount() {
        return this.insCnt;
    }

    /**
     * Sets instruction count.
     *
     * @param insCnt the instruction count to set
     * @return this
     */
    public P4Int setInstructionCount(final byte insCnt) {
        this.insCnt = insCnt;
        return this;
    }

    /**
     * Gets max hop count.
     *
     * @return max hop count
     */
    public byte getMaxHopCount() {
        return this.maxHopCnt;
    }

    /**
     * Sets max hop count.
     *
     * @param maxHopCnt the instruction count to set
     * @return this
     */
    public P4Int setMaxHopCount(final byte maxHopCnt) {
        this.maxHopCnt = maxHopCnt;
        return this;
    }

    /**
     * Gets total hop count.
     *
     * @return total hop count
     */
    public byte getTotalHopCount() {
        return this.totalHopCnt;
    }

    /**
     * Sets total hop count.
     *
     * @param totalHopCnt the total hop count to set
     * @return this
     */
    public P4Int setTotalHopCount(final byte totalHopCnt) {
        this.totalHopCnt = totalHopCnt;
        return this;
    }

    /**
     * Gets INT metadata stack.
     *
     * @return list of INT transit hop that store the INT metadata
     */
    public List<P4IntTransitHop> getMetadata() {
        return this.transitHops;
    }

    /**
     * Deserializer function for INT packets.
     *
     * @return deserializer function
     */
    public static Deserializer<P4Int> deserializer() {
        return (data, offset, length) -> {
            checkInput(data, offset, length, INT_SHIM_HEADER_LENGTH +
                    INT_METADATA_HEADER_LENGTH + INT_TAIL_HEADER_LENGTH);

            final ByteBuffer bb = ByteBuffer.wrap(data, offset, length);
            P4Int intPacket = new P4Int();

            // INT shim header
            intPacket.type = bb.get();
            intPacket.shimRsvd1 = bb.get();
            intPacket.length = bb.get();
            intPacket.shimRsvd2 = bb.get();

            // INT metadata header
            intPacket.controlField = bb.getShort();
            intPacket.maxHopCnt = bb.get();
            intPacket.totalHopCnt = bb.get();
            intPacket.instructionBitmap = bb.getShort();
            intPacket.rsvd2 = bb.getShort();

            // INT control field
            intPacket.ver = (byte) ((intPacket.controlField & 0xC000) >> 14);
            intPacket.rep = (byte) ((intPacket.controlField & 0x3000) >> 12);
            intPacket.c = (byte) ((intPacket.controlField & 0x0800) >> 11);
            intPacket.e = (byte) ((intPacket.controlField & 0x0400) >> 10);
            intPacket.rsvd1 = (byte) ((intPacket.controlField & 0x03e0) >> 5);
            intPacket.insCnt = (byte) (intPacket.controlField & 0x001f);

            if (bb.remaining() - INT_TAIL_HEADER_LENGTH < (intPacket.totalHopCnt * intPacket.insCnt) * 4) {
                throw new DeserializationException("INT metadata length < total hop count * instruction count");
            }

            // INT metadata
            for (int i = 0; i < intPacket.totalHopCnt; i++) {
                P4IntTransitHop hop = new P4IntTransitHop();
                if ((intPacket.instructionBitmap & 0x8000) != 0) {
                    hop.setSwitchId(bb.getInt());
                }
                if ((intPacket.instructionBitmap & 0x4000) != 0) {
                    hop.setIngressPortId(bb.getShort());
                    hop.setEgressPortId(bb.getShort());
                }
                if ((intPacket.instructionBitmap & 0x2000) != 0) {
                    hop.setHopLatency(bb.getInt());
                }
                if ((intPacket.instructionBitmap & 0x1000) != 0) {
                    int queueStatus = bb.getInt();
                    hop.setQueueId((byte) ((queueStatus & 0xFF000000) >> 24));
                    hop.setQueueOccupancy(queueStatus & 0x00FFFFFF);
                }
                if ((intPacket.instructionBitmap & 0x0800) != 0) {
                    hop.setIngressTimeStamp(bb.getInt());
                }
                if ((intPacket.instructionBitmap & 0x0400) != 0) {
                    hop.setEgressTimeStamp(bb.getInt());
                }
                if ((intPacket.instructionBitmap & 0x0200) != 0) {
                    int queueStatus = bb.getInt();
                    hop.setQueueId((byte) ((queueStatus & 0xFF000000) >> 24));
                    hop.setQueueCongestion(queueStatus & 0x00FFFFFF);
                }
                if ((intPacket.instructionBitmap & 0x0100) != 0) {
                    hop.setEgressPortTxUtil(bb.getInt());
                }

                intPacket.transitHops.add(hop);
            }

            // INT tail header
            intPacket.nextProto = bb.get();
            intPacket.destPort = bb.getShort();
            intPacket.dscp = bb.get();

            intPacket.payload = Data.deserializer()
                    .deserialize(data, bb.position(), bb.limit() - bb.position());
            intPacket.payload.setParent(intPacket);

            return intPacket;
        };
    }

    @Override
    public byte[] serialize() {
        // TODO: implement serialize function
        return new byte[0];
    }
}
