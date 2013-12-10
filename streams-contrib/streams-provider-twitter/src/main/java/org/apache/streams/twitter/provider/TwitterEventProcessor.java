package org.apache.streams.twitter.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.BlockingQueue;

/**
 * Created by sblackmon on 12/10/13.
 */
public class TwitterEventProcessor implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(TwitterEventProcessor.class);

    private ObjectMapper mapper = new ObjectMapper();

    private BlockingQueue<String> queue;

    public final static String TERMINATE = new String("TERMINATE");

    public TwitterEventProcessor(BlockingQueue<String> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {

        while(true) {
            try {
                String queueElement = queue.take();
                Thread.sleep(new Random().nextInt(100));
                if(queueElement==TERMINATE) {
                    LOGGER.info("Terminating!");
                    break;
                }
                process(queueElement);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void process(String item) {

        // check what outputs are bound, write to each
        LOGGER.debug(item);
    }
};
