package org.apache.streams.kafka;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import org.apache.streams.core.StreamsDatum;
import org.apache.streams.pojo.json.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class KafkaPersistReaderTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaPersistReaderTask.class);

    private KafkaPersistReader reader;

    private KafkaStream kafkaStream;

    public KafkaPersistReaderTask(KafkaPersistReader reader, KafkaStream kafkaStream) {
        this.reader = reader;
        this.kafkaStream = kafkaStream;
    }

    @Override
    public void run() {

        while(true) {
            try {
                ConsumerIterator<byte[], byte[]> it = kafkaStream.iterator();
                while (it.hasNext()) {
                    StreamsDatum entry = new StreamsDatum(reader.mapper.readValue(it.next().message(), Activity.class));
                    reader.getPersistQueue().offer(entry);
                }
                Thread.sleep(new Random().nextInt(100));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
