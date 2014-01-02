package org.apache.streams.console;

import org.apache.streams.core.StreamsDatum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class ConsolePersistWriterTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsolePersistWriterTask.class);

    private ConsolePersistWriter writer;

    public ConsolePersistWriterTask(ConsolePersistWriter writer,
                                    BlockingQueue<Object> outqueue) {
        this.writer = writer;
    }

    @Override
    public void run() {
        while(true) {
            try {
                StreamsDatum entry = writer.persistQueue.remove();
                writer.write(entry);
                Thread.sleep(new Random().nextInt(100));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
