package org.apache.streams.twitter.provider;

import com.google.common.collect.Lists;
import org.apache.streams.twitter.TwitterOAuthConfiguration;
import org.apache.streams.twitter.TwitterStreamConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by sblackmon on 12/10/13.
 */
public class TwitterStreamProviderExample {

    private final static Logger LOGGER = LoggerFactory.getLogger(TwitterStreamProviderExample.class);

    public static void main(String[] args)
    {
        TwitterStreamConfiguration twitterStreamConfiguration = new TwitterStreamConfiguration();
        TwitterOAuthConfiguration twitterOAuthConfiguration = new TwitterOAuthConfiguration();
        twitterOAuthConfiguration.setConsumerKey("PKF2225CQ8I1eaC0cP8oBg");
        twitterOAuthConfiguration.setConsumerSecret("gM0fhysKrYxZU9V7KSX3RuLVXcCPHw5oP32H0SA");
        twitterOAuthConfiguration.setAccessToken("281592383-DMabF7UmiZqDAyzHwNPe09iruBSplrt9nHdavZP4");
        twitterOAuthConfiguration.setAccessTokenSecret("uA1oJcSEkWB9gAchE3J1FsCZlagxgunVRmfXx62OZU");
        twitterStreamConfiguration.setOauth(twitterOAuthConfiguration);
        twitterStreamConfiguration.setTrack(Lists.newArrayList("hey"));

        TwitterStreamProvider stream = new TwitterStreamProvider(twitterStreamConfiguration);

        try {
            Thread provider = (new Thread(stream));
            provider.start();
        } catch( Exception x ) {
            LOGGER.info(x.getMessage());
        }

        // run until user exits
    }
}
