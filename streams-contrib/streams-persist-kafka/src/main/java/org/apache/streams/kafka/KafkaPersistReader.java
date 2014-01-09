package org.apache.streams.kafka;

import com.typesafe.config.Config;
import kafka.consumer.ConsumerConfig;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;
import org.apache.streams.config.StreamsConfigurator;
import org.apache.streams.core.StreamsDatum;
import org.apache.streams.core.StreamsPersistReader;
import org.apache.streams.core.StreamsPersistWriter;
import org.apache.streams.core.StreamsResultSet;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by sblackmon on 1/8/14.
 */
public class KafkaPersistReader implements StreamsPersistReader, Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamsPersistReader.class);

    private KafkaConfiguration config;

    protected volatile Queue<StreamsDatum> persistQueue;

    private ConsumerConnector consumer;

    public KafkaPersistReader() {
        Config config = StreamsConfigurator.config.getConfig("kafka");
        this.config = KafkaConfigurator.detectConfiguration(config);
        this.persistQueue  = new ConcurrentLinkedQueue<StreamsDatum>();
    }

    public KafkaPersistReader(Queue<StreamsDatum> persistQueue) {
        Config config = StreamsConfigurator.config.getConfig("kafka");
        this.config = KafkaConfigurator.detectConfiguration(config);
        this.persistQueue = persistQueue;
    }

    public KafkaPersistReader(KafkaConfiguration config) {
        this.config = config;
        this.persistQueue = new ConcurrentLinkedQueue<StreamsDatum>();
    }

    public KafkaPersistReader(KafkaConfiguration config, Queue<StreamsDatum> persistQueue) {
        this.config = config;
        this.persistQueue = persistQueue;
    }
    @Override
    public void start() {

        Properties props = new Properties();

        props.put("zookeeper.connect", this.config.getZkconnect());
        props.put("group.id", this.config.getTopic());

        consumer = kafka.consumer.Consumer.createJavaConsumerConnector(new ConsumerConfig(props));

    }

    public String getZkConnectString(String[] hosts, int port) {
        StringBuilder connect = new StringBuilder();

        for (String host : hosts) {
            connect.append(host);
            connect.append(":");
            connect.append(port);
            connect.append(",");
        }

        connect.replace(connect.length() - 1, connect.length(), "");

        return connect.toString();
    }

    @Override
    public void stop() {

    }

    @Override
    public StreamsResultSet readAll() {
        return null;
    }

    @Override
    public StreamsResultSet readNew(BigInteger sequence) {
        return null;
    }

    @Override
    public StreamsResultSet readRange(DateTime start, DateTime end) {
        return null;
    }
}
