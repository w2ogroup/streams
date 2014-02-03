package com.google.gmail.provider;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gmail.GMailConfiguration;
import com.googlecode.gmail4j.GmailClient;
import com.googlecode.gmail4j.GmailConnection;
import com.googlecode.gmail4j.http.HttpGmailConnection;
import com.googlecode.gmail4j.javamail.ImapGmailClient;
import com.googlecode.gmail4j.javamail.ImapGmailConnection;
import com.googlecode.gmail4j.rss.RssGmailClient;
import com.typesafe.config.Config;
import org.apache.streams.config.StreamsConfigurator;
import org.apache.streams.core.StreamsDatum;
import org.apache.streams.core.StreamsProvider;
import org.apache.streams.core.StreamsResultSet;
import com.google.gmail.GMailConfigurator;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * Created by sblackmon on 12/10/13.
 */
public class GMailProvider /*extends BaseRichSpout*/ implements StreamsProvider, Serializable {

    private final static Logger LOGGER = LoggerFactory.getLogger(GMailProvider.class);

    private GMailConfiguration config;

    private Class klass;

    public GMailConfiguration getConfig() {
        return config;
    }

    public void setConfig(GMailConfiguration config) {
        this.config = config;
    }

    protected BlockingQueue inQueue = new LinkedBlockingQueue<String>(10000);

    protected volatile Queue<StreamsDatum> providerQueue = new ConcurrentLinkedQueue<StreamsDatum>();

    public BlockingQueue<Object> getInQueue() {
        return inQueue;
    }

    protected GmailClient rssClient;
    protected ImapGmailClient imapClient;

    protected ListeningExecutorService executor = MoreExecutors.listeningDecorator(newFixedThreadPoolWithQueueSize(5, 20));

    private static ExecutorService newFixedThreadPoolWithQueueSize(int nThreads, int queueSize) {
        return new ThreadPoolExecutor(nThreads, nThreads,
                5000L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(queueSize, true), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public GMailProvider() {
        Config config = StreamsConfigurator.config.getConfig("gmail");
        this.config = GMailConfigurator.detectConfiguration(config);
    }

    public GMailProvider(GMailConfiguration config) {
        this.config = config;
    }

    public GMailProvider(Class klass) {
        Config config = StreamsConfigurator.config.getConfig("gmail");
        this.config = GMailConfigurator.detectConfiguration(config);
        this.klass = klass;
    }

    public GMailProvider(GMailConfiguration config, Class klass) {
        this.config = config;
        this.klass = klass;
    }

    @Override
    public void start() {

        Preconditions.checkNotNull(this.klass);

        Preconditions.checkNotNull(config.getUserName());
        Preconditions.checkNotNull(config.getPassword());

        rssClient = new RssGmailClient();
        GmailConnection rssConnection = new HttpGmailConnection(config.getUserName(), config.getPassword().toCharArray());
        rssClient.setConnection(rssConnection);

        imapClient = new ImapGmailClient();
        GmailConnection imapConnection = new ImapGmailConnection();
        imapConnection.setLoginCredentials(config.getUserName(), config.getPassword().toCharArray());
        imapClient.setConnection(imapConnection);

        new Thread(new GMailImapProviderTask(this)).start();
    }

    @Override
    public void stop() {
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Queue<StreamsDatum> getProviderQueue() {
        return this.providerQueue;
    }

    @Override
    public StreamsResultSet readCurrent() {
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
