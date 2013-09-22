package org.apache.streams.storm.trident;

import org.apache.streams.StreamsConfiguration;
import storm.trident.TridentTopology;

/**
 * Created with IntelliJ IDEA.
 * User: sblackmon
 * Date: 9/20/13
 * Time: 5:48 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class StreamsTopology extends TridentTopology {

    StreamsConfiguration configuration;

    protected StreamsTopology() {
    }

    protected StreamsTopology(StreamsConfiguration configuration) {
        this.configuration = configuration;
    }

    public StreamsConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(StreamsConfiguration configuration) {
        this.configuration = configuration;
    }

}
