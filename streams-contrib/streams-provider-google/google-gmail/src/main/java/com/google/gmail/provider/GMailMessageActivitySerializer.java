package com.google.gmail.provider;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.googlecode.gmail4j.GmailMessage;
import org.apache.commons.lang.NotImplementedException;
import org.apache.streams.data.ActivitySerializer;
import org.apache.streams.pojo.json.*;
import org.joda.time.DateTime;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.apache.streams.data.util.ActivityUtil.ensureExtensions;

/**
* Created with IntelliJ IDEA.
* User: mdelaet
* Date: 9/30/13
* Time: 9:24 AM
* To change this template use File | Settings | File Templates.
*/
public class GMailMessageActivitySerializer implements ActivitySerializer<GmailMessage> {

    @Override
    public String serializationFormat() {
        return "gmail.v1";
    }

    @Override
    public GmailMessage serialize(Activity activity) {
        return null;
    }

    @Override
    public Activity deserialize(GmailMessage gmailMessage) {

        Activity activity = new Activity();
        activity.setId(formatId("gmail", String.valueOf(gmailMessage.getMessageNumber())));
        activity.setPublished(gmailMessage.getSendDate());
        Provider provider = new Provider();
        provider.setId("http://gmail.com");
        provider.setDisplayName("GMail");
        activity.setProvider(provider);
        Actor actor = new Actor();
        actor.setId(gmailMessage.getFrom().getEmail());
        actor.setDisplayName(gmailMessage.getFrom().getName());
        activity.setActor(actor);
        activity.setVerb("email");
        ActivityObject object = new ActivityObject();
        object.setId(gmailMessage.getTo().get(0).getEmail());
        object.setDisplayName(gmailMessage.getTo().get(0).getName());
        activity.setObject(object);
        activity.setContent(gmailMessage.getContentText());
        return activity;
    }

    @Override
    public List<Activity> deserializeAll(String serializedList) {
        throw new NotImplementedException("Not currently implemented");
    }

    public Activity convert(ObjectNode event) {
        return null;
    }

    public static Generator buildGenerator(ObjectNode event) {
        return null;
    }

    public static Icon getIcon(ObjectNode event) {
        return null;
    }

    public static Provider buildProvider(ObjectNode event) {
        Provider provider = new Provider();
        provider.setId("id:providers:twitter");
        return provider;
    }

    public static List<Object> getLinks(ObjectNode event) {
        return null;
    }

    public static String getUrls(ObjectNode event) {
        return null;
    }

    public static void addGMailExtension(Activity activity, GmailMessage gmailMessage) {
        Map<String, Object> extensions = ensureExtensions(activity);
        extensions.put("gmail", gmailMessage);
    }

    public static String formatId(String... idparts) {
        return Joiner.on(":").join(Lists.asList("id:gmail", idparts));
    }

}
