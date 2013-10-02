package com.twitter.test;

import com.twitter.Tweet;
import org.apache.streams.data.ActivitySerializer;
import org.apache.streams.data.TwitterJsonActivitySerializer;
import org.apache.streams.pojo.Activity;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import static java.util.regex.Pattern.matches;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
* Created with IntelliJ IDEA.
* User: sblackmon
* Date: 8/20/13
* Time: 5:57 PM
* To change this template use File | Settings | File Templates.
*/
public class TweetSerDeTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(TweetSerDeTest.class);
    private ActivitySerializer serializer = new TwitterJsonActivitySerializer();
    private ObjectMapper mapper = new ObjectMapper();

//    @Ignore
    @Test
    public void Tests()
    {
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, Boolean.FALSE);
        mapper.configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, Boolean.TRUE);
        mapper.configure(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, Boolean.TRUE);

        InputStream is = TweetSerDeTest.class.getResourceAsStream("/twitter_dr_jsons.txt");
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        try {
            while (br.ready()) {
                String line = br.readLine();

                LOGGER.info(line);

                Tweet ser = mapper.readValue(line, Tweet.class);

                String des = mapper.writeValueAsString(ser);
                LOGGER.debug(des);

                Activity activity = serializer.deserialize(des);

                assertThat(activity, is(not(nullValue())));
                assertThat(activity.getActor(), is(not(nullValue())));
                assertThat(activity.getObject(), is(not(nullValue())));
                assertThat(activity.getAdditionalProperties().get("extensions"),is(not(nullValue())));
                if(activity.getObject().getId() != null) {
                    assertThat(matches("id:.*:[a-z]*:[a-zA-Z0-9]*", activity.getObject().getId()), is(true));
                }
                assertThat(activity.getObject().getObjectType(), is(not(nullValue())));
            }
        } catch( Exception e ) {
            System.out.println(e);
            e.printStackTrace();
            Assert.fail();
        }
    }
}
