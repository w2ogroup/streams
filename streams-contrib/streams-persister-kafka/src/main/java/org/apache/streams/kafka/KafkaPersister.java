package org.apache.streams.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.apache.streams.StreamsPersister;
import org.apache.streams.config.StreamsConfigurator;
import org.apache.streams.pojo.json.Activity;
import org.apache.streams.util.GuidUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class KafkaPersister implements StreamsPersister, Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaPersister.class);

    private BlockingQueue<Object> outqueue;

    private ObjectMapper mapper = new ObjectMapper();

    private KafkaConfiguration config;

    private Producer<String, String> producer;

    public KafkaPersister(BlockingQueue<Object> outqueue) {
        Config config = StreamsConfigurator.config.getConfig("kafka");
        this.config = KafkaConfigurator.detectConfiguration(config);
        this.outqueue = outqueue;
    }

    private void setup() {
        Properties props = new Properties();

        props.put("metadata.broker.list", config.getBrokerlist());
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        props.put("partitioner.class", "example.producer.SimplePartitioner");
        props.put("request.required.acks", "1");

        ProducerConfig config = new ProducerConfig(props);

        producer = new Producer<String, String>(config);
    }

    public void save(Object entry) {

        try {
            String text = mapper.writeValueAsString(entry);

            String hash = GuidUtils.generateGuid(text);

            KeyedMessage<String, String> data = new KeyedMessage<String, String>(config.getTopic(), hash, text);

            producer.send(data);

        } catch (JsonProcessingException e) {
            LOGGER.warn("save: {}", e);
        }// put
    }

    public void save(Activity entry) {

        try {
            String text = mapper.writeValueAsString(entry);

            String hash = GuidUtils.generateGuid(entry.getId());

            KeyedMessage<String, String> data = new KeyedMessage<String, String>(config.getTopic(), hash, text);

            producer.send(data);

        } catch (JsonProcessingException e) {
            LOGGER.warn("save: {}", e);
        }// put

    }

    @Override
    public void run() {

        setup();

        while(true) {
            try {
                Object entry = outqueue.take();
                save(entry);
                Thread.sleep(new Random().nextInt(100));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
