package org.apache.streams.core;

/**
 * Created by sblackmon on 12/13/13.
 */
public interface StreamsProcessor {

    void start();
    void stop();

    public StreamsDatum process( StreamsDatum entry );

}
