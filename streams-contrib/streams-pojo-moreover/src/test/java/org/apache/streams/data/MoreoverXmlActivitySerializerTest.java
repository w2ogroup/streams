package org.apache.streams.data;


import org.apache.commons.io.IOUtils;
import org.apache.streams.pojo.Activity;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.List;

import static java.util.regex.Pattern.matches;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class MoreoverXmlActivitySerializerTest {
    ActivitySerializer serializer;
    private String xml;

    @Before
    public void setup() throws IOException {
        serializer = new MoreoverXmlActivitySerializer();
        xml = loadXml();
    }

    @Test
    public void loadData() throws Exception {
        List<Activity> activities = serializer.deserializeAll(xml);
        for (Activity activity : activities) {
            assertThat(activity, is(not(nullValue())));
            assertThat(activity.getActor(), is(not(nullValue())));
            assertThat(activity.getActor().getDisplayName(), is(not(nullValue())));
            assertThat(activity.getObject(), is(not(nullValue())));
            if(activity.getObject().getId() != null) {
                assertThat(matches("id:.*:[a-z]*s:[a-zA-Z0-9]*", activity.getObject().getId()), is(true));
            }
            assertThat(activity.getObject().getObjectType(), is(not(nullValue())));
            assertThat(activity.getContent(), is(not(nullValue())));
            assertThat(matches("id:providers:[a-zA-Z0-9]*", activity.getProvider().getId()), is(true));
            System.out.println(activity.getPublished());
        }
    }

    private String loadXml() throws IOException {
        StringWriter writer = new StringWriter();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("/org/apache/streams/data/moreover.xml");
        IOUtils.copy(resourceAsStream, writer, Charset.forName("UTF-8"));
        return writer.toString();
    }

}
