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

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for class {@link P4Int}.
 */
public class P4IntTest {

    private static byte[] bytePacketINT = {
            // shim header
            (byte) 0x01, (byte) 0x00, (byte) 0x08, (byte) 0x00, // type, reserved, length, reserved

            // metadata header
            (byte) 0x00, (byte) 0x02, (byte) 0x10, (byte) 0x01, // control, max_hop_cnt, total_hop_cnt
            (byte) 0x90, (byte) 0x00, (byte) 0x00, (byte) 0x00, // instruction_bitmap, reserved

            // metadata
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, // switch id
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, // queue ID, queue occupancy

            // tail header
            (byte) 0x06, (byte) 0x00, (byte) 0x16, (byte) 0x00 // next_proto, destination port, dscp
    };

    private static Deserializer<P4Int> deserializer = P4Int.deserializer();

    /**
     * Tests deserialize.
     */
    @Test
    public void testDeserialize() throws Exception {
        P4Int intPacket = deserializer.deserialize(bytePacketINT, 0, bytePacketINT.length);

        assertThat(intPacket.getHeaderType(), is((byte) 0x01));
        assertThat(intPacket.getHeaderLength(), is((byte) 0x08));
        assertThat(intPacket.getVersion(), is((byte) 0x00));
        assertThat(intPacket.getReplicationType(), is((byte) 0x00));
        assertThat(intPacket.getInstructionCount(), is((byte) 0x02));
        assertThat(intPacket.getMaxHopCount(), is((byte) 0x10));
        assertThat(intPacket.getTotalHopCount(), is((byte) 0x01));
        assertThat(intPacket.getMetadata().get(0).getSwitchId(), is(0x0001));
        assertThat(intPacket.getMetadata().get(0).getQueueId(), is((byte) 0x00));
        assertThat(intPacket.getMetadata().get(0).getQueueOccupancy(), is(0x000001));
        assertThat(intPacket.getNextProtocol(), is((byte) 0x06));
        assertThat(intPacket.getDestinationPort(), is((short) 0x0016));
        assertThat(intPacket.getDscp(), is((byte) 0x00));
    }

    @Test
    public void testDeserializeBadInput() throws Exception {
        PacketTestUtils.testDeserializeBadInput(deserializer);
    }

    @Test
    public void testDeserializeTruncated() throws Exception {
        PacketTestUtils.testDeserializeTruncated(deserializer, bytePacketINT);
    }
}
