package com.google.gmail.provider;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.googlecode.gmail4j.GmailClient;
import com.googlecode.gmail4j.GmailMessage;
import com.googlecode.gmail4j.javamail.JavaMailGmailMessage;
import com.sun.mail.imap.DefaultFolder;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;
import com.sun.mail.imap.IMAPSSLStore;
import org.apache.streams.core.StreamsDatum;
import org.apache.streams.pojo.json.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.internet.MimeMultipart;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by sblackmon on 12/10/13.
 */
public class GMailImapProviderTask implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(GMailImapProviderTask.class);

    private GMailProvider provider;

    public GMailImapProviderTask(GMailProvider provider) {
        this.provider = provider;
    }

    ObjectMapper mapper = new ObjectMapper();

    @Override
    public void run() {

        Calendar calendar = new GregorianCalendar();

        calendar.set(Calendar.YEAR, 2000);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, Boolean.FALSE);

        mapper.addMixInAnnotations(IMAPSSLStore.class, MessageMixIn.class);
        mapper.addMixInAnnotations(IMAPFolder.class, MessageMixIn.class);
        mapper.addMixInAnnotations(IMAPMessage.class, MessageMixIn.class);
        mapper.addMixInAnnotations(MimeMultipart.class, MessageMixIn.class);
        mapper.addMixInAnnotations(JavaMailGmailMessage.class, MessageMixIn.class);

        final List<GmailMessage> messages = this.provider.imapClient.getMessagesBy(
                GmailClient.EmailSearchStrategy.DATE_GT,
                calendar.getTime().toString()
        );

        for (GmailMessage message : messages) {

            Activity activity;
            GMailMessageActivitySerializer serializer = new GMailMessageActivitySerializer();
            activity = serializer.deserialize(message);
            //messageAsText = mapper.writeValueAsString(message);
            StreamsDatum entry = new StreamsDatum(activity);
            this.provider.getProviderQueue().offer(entry);

        }

    }

    interface MessageMixIn {
        @JsonManagedReference
        @JsonIgnore

        IMAPSSLStore getDefaultFolder(); // we don't need it!
        @JsonManagedReference
        @JsonIgnore
        IMAPSSLStore getPersonalNamespaces(); // we don't need it!
        @JsonManagedReference
        @JsonIgnore
        IMAPFolder getStore(); // we don't need it!
//        @JsonManagedReference
//        @JsonIgnore
//        @JsonBackReference
        //IMAPFolder getParent(); // we don't need it!
        @JsonManagedReference
        @JsonIgnore
        @JsonBackReference
        IMAPMessage getFolder(); // we don't need it!
        @JsonManagedReference
        @JsonIgnore
        @JsonProperty("parent")
        @JsonBackReference
        MimeMultipart getParent();
    }

}
