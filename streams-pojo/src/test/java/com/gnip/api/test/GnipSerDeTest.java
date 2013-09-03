package com.gnip.api.test;

import org.codehaus.jackson.map.DeserializationConfig;
//import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
//import com.fasterxml.jackson.xml.XmlMapper;
//import com.gnip.xmlpojo.generated.FacebookEDC;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import com.ms.Activity;

/**
 * Created with IntelliJ IDEA.
 * User: rebanks
 * Date: 8/21/13
 * Time: 11:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class GnipSerDeTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(GnipSerDeTest.class);

    private ObjectMapper mapper = new ObjectMapper();
//    XmlMapper mapper = new XmlMapper();

    @Test
    public void Tests()   throws Exception
    {
//        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, Boolean.FALSE);
//        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, Boolean.TRUE);
//        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, Boolean.TRUE);

        InputStream is = GnipSerDeTest.class.getResourceAsStream("/FacebookEDC.xml");
        if(is == null) System.out.println("null");
//        InputStreamReader isr = new InputStreamReader(is);
//        BufferedReader br = new BufferedReader(isr);
        XmlMapper xmlMapper = new XmlMapper();
//        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, Boolean.FALSE);
//        xmlMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, Boolean.TRUE);
//        xmlMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, Boolean.TRUE);
        Activity entries = xmlMapper.readValue(new File("/Users/rebanks/git/streams/streams-pojo/src/test/resources/FacebookEDC.xml"), Activity.class);

        ObjectMapper jsonMapper = new ObjectMapper();
        String json = jsonMapper.writeValueAsString(entries);
        System.out.println(json);

//        try {
//            while (br.ready()) {
//                String line = br.readLine();
//                LOGGER.debug(line);
//
//                Sysomos ser = mapper.readValue(line, Sysomos.class);
//
//                String des = mapper.writeValueAsString(ser);
//                LOGGER.debug(des);
//            }
//        } catch( Exception e ) {
//            e.printStackTrace();
//            Assert.fail();
//        }
    }
}
