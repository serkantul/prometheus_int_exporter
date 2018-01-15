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

/**
 * Representation of an INT Transit Hop.
 */
public class P4IntTransitHop {

    protected int switchId;
    protected short ingressPortId;
    protected short egressPortId;
    protected int hopLatency;
    protected int ingressTstamp;
    protected int egressTstamp;
    protected byte queueId;
    protected int queueCongestion;
    protected int queueOccupancy;
    protected int egressPortTxUtil;
    protected int reserved;

    /**
     * Gets transit hop switch id.
     *
     * @return transit hop switch id
     */
    public int getSwitchId() {
        return this.switchId;
    }

    /**
     * Sets transit hop switch id.
     *
     * @param switchId the switch id to set
     * @return this
     */
    public P4IntTransitHop setSwitchId(final int switchId) {
        this.switchId = switchId;
        return this;
    }

    /**
     * Gets transit hop ingress port ID.
     *
     * @return transit hop ingress port ID
     */
    public short getIngressPortId() {
        return this.ingressPortId;
    }

    /**
     * Sets transit hop ingress port ID.
     *
     * @param ingressPortId the ingress port ID to set
     * @return this
     */
    public P4IntTransitHop setIngressPortId(final short ingressPortId) {
        this.ingressPortId = ingressPortId;
        return this;
    }

    /**
     * Gets transit hop egress port ID.
     *
     * @return transit hop egress port ID
     */
    public short getEgressPortId() {
        return this.egressPortId;
    }

    /**
     * Sets transit hop egress port ID.
     *
     * @param egressPortId the egress port ID to set
     * @return this
     */
    public P4IntTransitHop setEgressPortId(final short egressPortId) {
        this.egressPortId = egressPortId;
        return this;
    }

    /**
     * Gets transit hop hop-latency.
     *
     * @return transit hop hop-latency
     */
    public int getHopLatency() {
        return this.hopLatency;
    }

    /**
     * Sets transit hop hop-latency.
     *
     * @param hopLatency the hop latency to set
     * @return this
     */
    public P4IntTransitHop setHopLatency(final int hopLatency) {
        this.hopLatency = hopLatency;
        return this;
    }

    /**
     * Gets transit hop ingress time stamp.
     *
     * @return transit hop ingress time stamp
     */
    public int getIngressTimeStamp() {
        return this.ingressTstamp;
    }

    /**
     * Sets transit hop ingress time stamp.
     *
     * @param ingressTstamp the ingress time stamp to set
     * @return this
     */
    public P4IntTransitHop setIngressTimeStamp(final int ingressTstamp) {
        this.ingressTstamp = ingressTstamp;
        return this;
    }

    /**
     * Gets transit hop egress time stamp.
     *
     * @return transit hop egress time stamp
     */
    public int getEgressTimeStamp() {
        return this.egressTstamp;
    }

    /**
     * Sets transit hop egress time stamp.
     *
     * @param egressTstamp the egress time stamp to set
     * @return this
     */
    public P4IntTransitHop setEgressTimeStamp(final int egressTstamp) {
        this.egressTstamp = egressTstamp;
        return this;
    }

    /**
     * Gets transit hop queue ID.
     *
     * @return transit hop queue ID
     */
    public byte getQueueId() {
        return this.queueId;
    }

    /**
     * Sets transit hop queue ID.
     *
     * @param queueId the queue ID to set
     * @return this
     */
    public P4IntTransitHop setQueueId(final byte queueId) {
        this.queueId = queueId;
        return this;
    }

    /**
     * Gets transit hop queue congestion.
     *
     * @return transit hop queue congestion
     */
    public int getQueueCongestion() {
        return this.queueCongestion;
    }

    /**
     * Sets transit hop queue congestion.
     *
     * @param queueCongestion the queue congestion to set
     * @return this
     */
    public P4IntTransitHop setQueueCongestion(final int queueCongestion) {
        this.queueCongestion = queueCongestion;
        return this;
    }

    /**
     * Gets transit hop queue occupancy.
     *
     * @return transit hop queue occupancy
     */
    public int getQueueOccupancy() {
        return this.queueOccupancy;
    }

    /**
     * Sets transit hop queue occupancy.
     *
     * @param queueOccupancy the queue occupancy to set
     * @return this
     */
    public P4IntTransitHop setQueueOccupancy(final int queueOccupancy) {
        this.queueOccupancy = queueOccupancy;
        return this;
    }

    /**
     * Gets transit hop egress port TX utilization.
     *
     * @return transit hop egress port TX utilization
     */
    public int getEgressPortTxUtil() {
        return this.egressPortTxUtil;
    }

    /**
     * Sets transit hop egress port TX utilization.
     *
     * @param egressPortTxUtil the egress port TX utilization to set
     * @return this
     */
    public P4IntTransitHop setEgressPortTxUtil(final int egressPortTxUtil) {
        this.egressPortTxUtil = egressPortTxUtil;
        return this;
    }
}
