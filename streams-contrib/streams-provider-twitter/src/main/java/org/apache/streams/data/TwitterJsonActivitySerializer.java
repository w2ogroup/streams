package org.apache.streams.data;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.twitter.Tweet;
import org.apache.streams.data.util.*;

import org.apache.commons.lang.NotImplementedException;
import org.apache.streams.data.ActivitySerializer;
import org.apache.streams.pojo.json.Activity;
import org.apache.streams.pojo.json.Article;

import java.io.IOException;
import java.util.List;

/**
* Created with IntelliJ IDEA.
* User: mdelaet
* Date: 9/30/13
* Time: 9:24 AM
* To change this template use File | Settings | File Templates.
*/
public class TwitterJsonActivitySerializer implements ActivitySerializer {
    @Override
    public String serializationFormat() {
        return "application/json+vnd.twitter.com.v1";
    }

    @Override
    public String serialize(Activity deserialized) {
        throw new UnsupportedOperationException("Cannot currently serialize to Twitter JSON");
    }

    @Override
    public Activity deserialize(String serialized) {
        serialized = serialized.replaceAll("\\[[ ]*\\]", "null");

//        System.out.println(serialized);

        ObjectMapper mapper = new ObjectMapper();
        AnnotationIntrospector introspector = new JaxbAnnotationIntrospector(mapper.getTypeFactory());
        mapper.setAnnotationIntrospector(introspector);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, Boolean.FALSE);
        mapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, Boolean.FALSE);
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, Boolean.TRUE);
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, Boolean.TRUE);
        mapper.configure(DeserializationFeature.WRAP_EXCEPTIONS, Boolean.TRUE);

        Tweet tweet = new Tweet();
        try {
            ObjectNode node = (ObjectNode)mapper.readTree(serialized);
            tweet = mapper.convertValue(node, Tweet.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to deserialize", e);
        }
        return TwitterUtils.convert(tweet);
//        return new Activity();
    }

    @Override
    public List<Activity> deserializeAll(String serializedList) {
        throw new NotImplementedException("Not currently implemented");
    }
}
