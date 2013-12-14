package org.apache.streams.kafka.repository.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class ConsoleRepository implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleRepository.class);

    private BlockingQueue<Object> outqueue;

    private ObjectMapper mapper = new ObjectMapper();

    public ConsoleRepository(BlockingQueue<Object> outqueue) {
        this.outqueue = outqueue;
    }

    public void save(Object entry) {

        try {
            String text = mapper.writeValueAsString(entry);

            System.out.println(text);
        } catch (JsonProcessingException e) {
            LOGGER.warn("save: {}", e);
        }

    }

    @Override
    public void run() {
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
