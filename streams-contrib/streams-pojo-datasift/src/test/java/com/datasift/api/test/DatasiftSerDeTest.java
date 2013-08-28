package com.datasift.api.test;

import com.datasift.api.Datasift;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created with IntelliJ IDEA.
 * User: sblackmon
 * Date: 8/20/13
 * Time: 5:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class DatasiftSerDeTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(DatasiftSerDeTest.class);

    private ObjectMapper mapper = new ObjectMapper();

    @Ignore
    @Test
    public void Tests()
    {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, Boolean.FALSE);
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, Boolean.TRUE);
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, Boolean.TRUE);

        InputStream is = DatasiftSerDeTest.class.getResourceAsStream("/datasift_jsons.txt");
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        try {
            while (br.ready()) {
                String line = br.readLine();
                LOGGER.debug(line);

                Datasift ser = mapper.readValue(line, Datasift.class);

                String des = mapper.writeValueAsString(ser);
                LOGGER.debug(des);
            }
        } catch( Exception e ) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
