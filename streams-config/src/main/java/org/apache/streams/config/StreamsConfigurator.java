package org.apache.streams.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Created with IntelliJ IDEA.
 * User: sblackmon
 * Date: 9/23/13
 * Time: 10:44 AM
 * To change this template use File | Settings | File Templates.
 */
public class StreamsConfigurator {

    /*
        Pull all configuration files from the classpath, system properties, and environment variables
     */
    public static Config config = ConfigFactory.load();

}
