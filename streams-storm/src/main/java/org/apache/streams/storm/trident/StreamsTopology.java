package org.apache.streams.storm.trident;

import backtype.storm.Config;
import org.apache.streams.StreamsConfiguration;
import org.apache.streams.config.StreamsConfigurator;
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
    Config stormConfig;
    String runmode;

    protected StreamsTopology() {

        runmode = StreamsConfigurator.config.getConfig("storm").getString("runmode");
        stormConfig = new Config();

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

    public String getRunmode() {
        return runmode;
    }

    public void setRunmode(String runmode) {
        this.runmode = runmode;
    }

    public Config getStormConfig() {
        return stormConfig;
    }

    public void setStormConfig(Config stormConfig) {
        this.stormConfig = stormConfig;
    }
}
