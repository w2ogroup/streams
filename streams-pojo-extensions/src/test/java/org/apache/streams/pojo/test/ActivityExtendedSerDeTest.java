package org.apache.streams.pojo.test;

import com.google.common.base.Joiner;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BoundedInputStream;
import org.apache.streams.pojo.ActivityExtended;
import org.apache.streams.pojo.Activity;
import org.apache.streams.pojo.Extensions;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: sblackmon
 * Date: 8/20/13
 * Time: 5:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class ActivityExtendedSerDeTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(ActivityExtendedSerDeTest.class);

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void TestActivity()
    {
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, Boolean.FALSE);
        mapper.configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, Boolean.TRUE);
        mapper.configure(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, Boolean.TRUE);

        InputStream is = ActivityExtendedSerDeTest.class.getResourceAsStream("/gnip_twitter_extended.json");
        Joiner joiner = Joiner.on(" ").skipNulls();
        is = new BoundedInputStream(is, 10000);
        String json;
        try {
            json = joiner.join(IOUtils.readLines(is));
            LOGGER.debug(json);

            Activity ser = mapper.readValue(json, Activity.class);

            String des = mapper.writeValueAsString(ser);
            LOGGER.debug(des);

        } catch( Exception e ) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Ignore
    @Test
    public void TestActivityExtended()
    {
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, Boolean.FALSE);
        mapper.configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, Boolean.TRUE);
        mapper.configure(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, Boolean.TRUE);

        InputStream is = ActivityExtendedSerDeTest.class.getResourceAsStream("/gnip_twitter_extended.json");
        Joiner joiner = Joiner.on(" ").skipNulls();
        is = new BoundedInputStream(is, 10000);
        String json;
        try {
            json = joiner.join(IOUtils.readLines(is));
            LOGGER.debug(json);

            ActivityExtended ser = mapper.readValue(json, ActivityExtended.class);

            Extensions extensions = ser.getExtensions();

            String des = mapper.writeValueAsString(extensions);

            Assert.assertTrue(extensions.getAdditionalProperties().size() > 0);
            LOGGER.debug(des);

        } catch( Exception e ) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
