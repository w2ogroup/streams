package com.sysomos.api.test;

import com.sysomos.api.Sysomos;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
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
public class SysomosSerDeTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(SysomosSerDeTest.class);

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void Tests()
    {
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, Boolean.FALSE);
        mapper.configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, Boolean.TRUE);
        mapper.configure(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, Boolean.TRUE);

        InputStream is = SysomosSerDeTest.class.getResourceAsStream("/sysomos_jsons.txt");
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        try {
            while (br.ready()) {
                String line = br.readLine();
                LOGGER.debug(line);

                Sysomos ser = mapper.readValue(line, Sysomos.class);

                String des = mapper.writeValueAsString(ser);
                LOGGER.debug(des);
            }
        } catch( Exception e ) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
