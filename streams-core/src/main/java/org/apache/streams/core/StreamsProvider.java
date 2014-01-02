package org.apache.streams.core;

import org.joda.time.DateTime;

import java.math.BigInteger;

/**
 * Created by sblackmon on 12/13/13.
 */
public interface StreamsProvider {

    void start();
    void stop();

    public StreamsResultSet readCurrent();
    public StreamsResultSet readNew(BigInteger sequence);
    public StreamsResultSet readRange(DateTime start, DateTime end);

}
