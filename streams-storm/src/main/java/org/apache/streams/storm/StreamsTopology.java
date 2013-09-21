package org.apache.streams.storm;

import org.apache.streams.StreamsConfiguration;

/**
 * Created with IntelliJ IDEA.
 * User: sblackmon
 * Date: 9/20/13
 * Time: 5:48 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class StreamsTopology<StreamsConfiguration> {

    protected String pipelineIdentifier;

    protected StreamsConfiguration configuration;

    public StreamsTopology(String pipelineIdentifier, StreamsConfiguration configuration) {
        this.pipelineIdentifier = pipelineIdentifier;
        this.configuration = configuration;
    }
}
