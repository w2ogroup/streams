package org.apache.streams.core;

import java.math.BigInteger;

public interface StreamsResultSet extends Iterable<StreamsDatum> {
    /**
     * Get the time that the result set started collecting
     * @return long representing time since epoch
     */
    long getStartTime();

    /**
     * Get the time that the result set stopped collecting
     * @return long representing time since epoch
     */
    long getEndTime();

    /**
     * Get the source Unique identifier
     * @return String id
     */
    String getSourceId();

    /**
     * Get the maximum id of the items in the result set
     * @return the max sequence ID
     */
    BigInteger getMaxSequence();
}
