package org.apache.streams.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.typesafe.config.Config;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.consumer.TopicFilter;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;
import kafka.serializer.StringDecoder;
import kafka.utils.VerifiableProperties;
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
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by sblackmon on 1/8/14.
 */
public class KafkaPersistReader implements StreamsPersistReader, Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamsPersistReader.class);

    private KafkaConfiguration config;

    protected volatile Queue<StreamsDatum> persistQueue;

    protected ObjectMapper mapper = new ObjectMapper();

    protected ConsumerConnector consumerConnector;
    protected Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
    protected Map<String, List<KafkaStream<String, String>>> consumerMap;
    protected List<KafkaStream<String, String>> consumerStreams;

    protected ExecutorService executor;

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
        VerifiableProperties vprops = new VerifiableProperties(props);

        props.put("zookeeper.connect", this.config.getZkconnect());
        props.put("group.id", this.config.getTopic());

        consumerConnector = kafka.consumer.Consumer.createJavaConsumerConnector(new ConsumerConfig(props));

        topicCountMap.put(this.config.getTopic(), new Integer(10));

        consumerMap = consumerConnector.createMessageStreams(topicCountMap, new StringDecoder(vprops), new StringDecoder(vprops));

        executor = Executors.newFixedThreadPool(10);

        for (final KafkaStream stream : consumerStreams) {
            executor.submit(new KafkaPersistReaderTask(this, stream));
        }

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
    public void setPersistQueue(Queue<StreamsDatum> persistQueue) {
        this.persistQueue = persistQueue;
    }

    @Override
    public Queue<StreamsDatum> getPersistQueue() {
        return this.persistQueue;
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
