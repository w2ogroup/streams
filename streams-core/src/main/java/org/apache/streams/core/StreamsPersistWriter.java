package org.apache.streams.core;

/**
 * Created by sblackmon on 12/13/13.
 */
public interface StreamsPersistWriter {

    void start();
    void stop();

    public void write( StreamsDatum entry );

}
