package com.instagram.test;

import com.instagram.pojo.Instagram;
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

public class InstagramSerDeTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(InstagramSerDeTest.class);

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void Tests()
    {
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, Boolean.TRUE);
        mapper.configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, Boolean.TRUE);
        mapper.configure(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, Boolean.TRUE);

        InputStream is = InstagramSerDeTest.class.getResourceAsStream("/instagram_jsons.txt");
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        try {
            while (br.ready()) {
                String line = br.readLine();
                LOGGER.debug(line);

                Instagram ser = mapper.readValue(line, Instagram.class);

                String des = mapper.writeValueAsString(ser);
                LOGGER.debug(des);
            }
        } catch( Exception e ) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
