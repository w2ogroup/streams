package org.apache.streams.twitter.provider;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesSampleEndpoint;
import com.twitter.hbc.core.endpoint.StreamingEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.BasicClient;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.apache.streams.twitter.TwitterStreamConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;

/**
 * Created by sblackmon on 12/10/13.
 */
public class TwitterStreamProvider implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(TwitterStreamProvider.class);

    private TwitterStreamConfiguration config;

    BlockingQueue<String> queue = new LinkedBlockingQueue<String>(10000);

    private ListeningExecutorService executor = MoreExecutors.listeningDecorator(newFixedThreadPoolWithQueueSize(5, 20));

    private static ExecutorService newFixedThreadPoolWithQueueSize(int nThreads, int queueSize) {
        return new ThreadPoolExecutor(nThreads, nThreads,
                5000L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(queueSize, true), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public TwitterStreamProvider() {
        this.config = new TwitterStreamConfiguration();
    }

    public TwitterStreamProvider(TwitterStreamConfiguration config) {
        this.config = config;
    }

    @Override
    public void run() {
        StreamingEndpoint endpoint = new StatusesSampleEndpoint();

        Optional<List<String>> track = Optional.fromNullable(config.getTrack());
        Optional<List<Integer>> follow = Optional.fromNullable(config.getFollow());

        if( track.isPresent() ) endpoint.addPostParameter("track", Joiner.on(",").join(track.get()));
        if( follow.isPresent() ) endpoint.addPostParameter("follow", Joiner.on(",").join(follow.get()));

        Authentication auth = new OAuth1(config.getOauth().getConsumerKey(),
                config.getOauth().getConsumerSecret(),
                config.getOauth().getAccessToken(),
                config.getOauth().getAccessTokenSecret());

        BasicClient client = new ClientBuilder()
                .name("apache/streams/streams-contrib/streams-provider-twitter")
                .hosts(Constants.STREAM_HOST)
                .endpoint(endpoint)
                .authentication(auth)
                .processor(new StringDelimitedProcessor(queue))
                .build();

        for (int i = 0; i < 10; i++) {
            executor.submit(new TwitterEventProcessor(queue));
        }

        client.connect();

    }

    public class TwitterStreamCloser implements Runnable {

        BlockingQueue<String> queue;

        public TwitterStreamCloser(BlockingQueue<String> queue) {
            this.queue = queue;
        }

        public void run() {
            for (int i = 0; i < 10; i++) {
                queue.add(TwitterEventProcessor.TERMINATE);
            }
        }

    }


}
