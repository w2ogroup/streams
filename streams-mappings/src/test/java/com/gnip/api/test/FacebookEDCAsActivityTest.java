package com.gnip.api.test;

//import org.codehaus.jackson.map.ObjectMapper;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.gnip.api.GnipActivityFixer;
import org.apache.streams.Activity;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

//import com.fasterxml.jackson.xml.XmlMapper;
//import com.gnip.xmlpojo.generated.FacebookEDC;

/**
 * Created with IntelliJ IDEA.
 * User: rebanks
 * Date: 8/21/13
 * Time: 11:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class FacebookEDCAsActivityTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(FacebookEDCAsActivityTest.class);

    private ObjectMapper jsonMapper = new ObjectMapper();
    XmlMapper xmlMapper = new XmlMapper();

    @Test
    public void Tests()   throws Exception
    {
        InputStream is = FacebookEDCAsActivityTest.class.getResourceAsStream("/FacebookEDC.xml");
        if(is == null) System.out.println("null");
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, Boolean.FALSE);
        xmlMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, Boolean.TRUE);
        xmlMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, Boolean.TRUE);
        jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, Boolean.FALSE);
        jsonMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, Boolean.TRUE);
        jsonMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, Boolean.TRUE);

        try {
            while (br.ready()) {
                String line = br.readLine();
                //LOGGER.debug(line);

                Activity activity = xmlMapper.readValue(line, Activity.class);

                Activity activity2 = GnipActivityFixer.fix(activity);

                String des = jsonMapper.writeValueAsString(activity2);

                LOGGER.info(des);
            }
        } catch( Exception e ) {
            LOGGER.error(e.getMessage());
            Assert.fail();
        }
    }
}
