/*
 * Copyright 2015-present Open Networking Foundation
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

import org.jnetpcap.Pcap;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.onosproject.monitoring.packet.DeserializationException;
import org.onosproject.monitoring.packet.Deserializer;

import java.nio.ByteBuffer;
import java.util.Date;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertTrue;

/**
 * Utilities for testing packet methods.
 */
public final class PacketTestUtils {

    private PacketTestUtils() {
    }

    /**
     * Tests that the Deserializer function is resilient to bad input parameters
     * such as null input, negative offset and length, etc.
     *
     * @param deserializer deserializer function to test
     */
    public static void testDeserializeBadInput(Deserializer deserializer) {
        byte[] bytes = ByteBuffer.allocate(4).array();

        try {
            deserializer.deserialize(null, 0, 4);
            fail("NullPointerException was not thrown");
        } catch (NullPointerException e) {
            assertTrue(true);
        } catch (DeserializationException e) {
            fail("NullPointerException was not thrown");
        }

        // input byte array length, offset and length don't make sense
        expectDeserializationException(deserializer, bytes, -1, 0);
        expectDeserializationException(deserializer, bytes, 0, -1);
        expectDeserializationException(deserializer, bytes, 0, 5);
        expectDeserializationException(deserializer, bytes, 2, 3);
        expectDeserializationException(deserializer, bytes, 5, 0);
    }

    /**
     * Tests that the Deserializer function is resilient to truncated input, or
     * cases where the input byte array does not contain enough bytes to
     * deserialize the packet.
     *
     * @param deserializer deserializer function to test
     * @param header       byte array of a full-size packet
     */
    public static void testDeserializeTruncated(Deserializer deserializer,
                                                byte[] header) {
        byte[] truncated;

        for (int i = 0; i < header.length; i++) {
            truncated = new byte[i];

            ByteBuffer.wrap(header).get(truncated);

            expectDeserializationException(deserializer, truncated, 0, truncated.length);
        }
    }

    /**
     * Run the given deserializer function against the given inputs and verify
     * that a DeserializationException is thrown. The the test will fail if a
     * DeserializationException is not thrown by the deserializer function.
     *
     * @param deserializer deserializer function to test
     * @param bytes        input byte array
     * @param offset       input offset
     * @param length       input length
     */
    public static void expectDeserializationException(Deserializer deserializer,
                                                      byte[] bytes, int offset, int length) {
        try {
            deserializer.deserialize(bytes, offset, length);
            fail("DeserializationException was not thrown");
        } catch (DeserializationException e) {
            assertTrue(true);
        }
    }

    public static byte[] readFromPcapFile(String fname) {
        final StringBuilder errbuf = new StringBuilder(); //For any error msgs
        final ByteBuffer bb = ByteBuffer.allocate(1000);
        System.out.printf("Opening file for reading: %s%n", fname);
        Pcap pcap = Pcap.openOffline(fname, errbuf);
        if (pcap == null) {
            System.err.printf("Error while opening pcap file for reading: "
                    + errbuf.toString());
            return null;
        }


        /***************************************************************************
         * Create a packet handler which will receive packets from the
         * libpcap loop.
         **************************************************************************/
        PcapPacketHandler<String> jpacketHandler = new PcapPacketHandler<String>() {

            public void nextPacket(PcapPacket packet, String user) {
                bb.put(packet.getByteArray(0, packet.size()));
                System.out.printf("Received at %s caplen=%-4d len=%-4d %s\n",
                        new Date(packet.getCaptureHeader().timestampInMillis()),
                        packet.getCaptureHeader().caplen(), // Length actually captured
                        packet.getCaptureHeader().wirelen(), // Original length
                        user // User supplied object
                );
            }
        };
        try {
            //Read one packet
            pcap.loop(1, jpacketHandler, "jNetPcap rocks!");
        } finally {
            pcap.close();

        }
        byte[] rb = new byte[bb.position()];
        bb.rewind();
        bb.get(rb);
        return rb;
    }
}
