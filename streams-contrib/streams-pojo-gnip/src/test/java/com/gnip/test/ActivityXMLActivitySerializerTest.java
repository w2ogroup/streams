package com.gnip.test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.streams.data.ActivityXMLActivitySerializer;
import org.junit.Test;

import java.io.File;
import java.util.Scanner;

import static junit.framework.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: rebanks
 * Date: 9/6/13
 * Time: 11:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class ActivityXMLActivitySerializerTest {

    private static final String XML_DATA = "<entry xmlns:gnip=\"http://www.gnip.com/schemas/2010\" xmlns=\"http://www.w3.org/2005/Atom\">  <id>http://gdata.youtube.com/feeds/api/videos/gF3kVQ5_lCw</id>  <published>2013-09-06T12:31:02.000Z</published>  <updated>2013-09-06T12:31:02.000Z</updated>  <title>PrudentialCarolinas1 posted a video to YouTube</title>  <category term=\"VideoPosted\" label=\"Video Posted\"/>  <link rel=\"alternate\" type=\"text/html\" href=\"http://www.youtube.com/watch?v=gF3kVQ5_lCw&amp;feature=youtube_gdata\"/>  <source>    <link rel=\"self\" type=\"application/atom+xml\" href=\"http://gdata.youtube.com/feeds/api/videos?q=crest%20-%22pacific%20crest%22&amp;orderby=published\"/>    <title>YouTube - Keyword - Uploads - crest -\"pacific crest\"</title>    <updated>2013-09-06T09-26-04Z</updated>    <gnip:rule xmlns:gnip=\"http://www.gnip.com/schemas/2010\">crest -\"pacific crest\"</gnip:rule>  </source>  <service:provider xmlns:service=\"http://activitystrea.ms/service-provider\">    <name>YouTube</name>    <uri>http://www.youtube.com/</uri>    <icon/>  </service:provider>  <activity:verb xmlns:activity=\"http://activitystrea.ms/spec/1.0/\">http://activitystrea.ms/schema/1.0/post</activity:verb>  <activity:object xmlns:activity=\"http://activitystrea.ms/spec/1.0/\">    <activity:object-type>http://activitystrea.ms/schema/1.0/video</activity:object-type>    <id>object:http://gdata.youtube.com/feeds/api/videos/gF3kVQ5_lCw</id>    <title type=\"text\">Homes for Sale - 818 Forest Crest Dr Greensboro NC 27406 - Customer Care</title>    <content type=\"text\">4 beds 2.1 baths Customer Care PRUDENTIAL CAROLINAS REALTY-IW http://www.prucarolinas.com/property/818-Forest-Crest-Drive_349335.</content>    <link rel=\"alternate\" type=\"text/html\" href=\"http://www.youtube.com/watch?v=gF3kVQ5_lCw&amp;feature=youtube_gdata\"/>    <link xmlns:atommedia1=\"http://purl.org/syndication/atommedia\" rel=\"enclosure\" type=\"application/x-shockwave-flash\" href=\"http://www.youtube.com/v/gF3kVQ5_lCw?version=3&amp;f=videos&amp;app=youtube_gdata\" atommedia1:duration=\"34\"/>    <link xmlns:atommedia1=\"http://purl.org/syndication/atommedia\" rel=\"preview\" href=\"http://i.ytimg.com/vi/gF3kVQ5_lCw/0.jpg\" atommedia1:height=\"360\" atommedia1:width=\"480\" atommedia1:duration=\"00:00:17\"/>    <category scheme=\"http://gdata.youtube.com/schemas/2007/categories.cat\" term=\"Travel\"/>  </activity:object>  <author>    <name>PrudentialCarolinas1</name>    <uri>http://www.youtube.com/user/PrudentialCarolinas1</uri>  </author>  <activity:author xmlns:activity=\"http://activitystrea.ms/spec/1.0/\">    <activity:object-type>http://activitystrea.ms/schema/1.0/person</activity:object-type>    <link rel=\"alternate\" type=\"text/html\" length=\"0\" href=\"http://www.youtube.com/user/PrudentialCarolinas1\"/>    <id>http://gdata.youtube.com/feeds/api/users/PrudentialCarolinas1</id>  </activity:author>  <activity:actor xmlns:activity=\"http://activitystrea.ms/spec/1.0/\">    <activity:object-type>http://activitystrea.ms/schema/1.0/person</activity:object-type>    <link rel=\"alternate\" type=\"text/html\" length=\"0\" href=\"http://www.youtube.com/user/PrudentialCarolinas1\"/>    <id>http://gdata.youtube.com/feeds/api/users/PrudentialCarolinas1</id>  </activity:actor>  <gnip:matching_rules>    <gnip:matching_rule rel=\"source\" tag=\"Crest_MX\">crest -\"pacific crest\"</gnip:matching_rule>  </gnip:matching_rules></entry>";
//    private static final String XML_DATA2 =
//    "<entry xmlns:gnip="http://www.gnip.com/schemas/2010" xmlns="http://www.w3.org/2005/Atom">  <id>http://gdata.youtube.com/feeds/api/videos/s6Xsa8B4vL4</id>  <published>2013-09-06T16:01:11.000Z</published>  <updated>2013-09-06T16:01:11.000Z</updated>  <title>YDS Video Ders Say&#x131;sal posted a video to YouTube</title>  <category term="VideoPosted" label="Video Posted"/>  <link rel="alternate" type="text/html" href="http://www.youtube.com/watch?v=s6Xsa8B4vL4&amp;feature=youtube_gdata"/>  <source>    <link rel="self" type="application/atom+xml" href="http://gdata.youtube.com/feeds/api/videos?q=%28Olay%20%7C%20Olays%20%7C%20%22Olay%27s%22%20%7C%20%23Olay%20%7C%20%23Olays%20%7C%20%22Professional%20Pro-X%22%20%7C%20%22Smooth%20Finish%20Facial%20Hair%20Removal%20Duo%22%20%7C%20%22Professional%20ProX%22%20%7C%20%22Smooth%20Finish%20Facial%20Hair%20Removal%20Duos%22%20%7C%20%22fresh%20effects%22%20%7C%20fresheffects%20%7C%20%22fresh%20effect%22%20%7C%20fresheffect%20%7C%20%22total%20effects%22%20%7C&amp;orderby=published"/>    <title>YouTube - Keyword - Uploads - (Olay | Olays | "Olay's" | #Olay | #Olays | "Professional Pro-X" | "Smooth Finish Facial Hair Removal Duo" | "Professional ProX" | "Smooth Finish Facial Hair Removal Duos" | "fresh effects" | fresheffects | "fresh effect" | fresheffect | "total effects" |</title>    <updated>2013-09-06T12-23-07Z</updated>    <gnip:rule xmlns:gnip="http://www.gnip.com/schemas/2010">(Olay | Olays | "Olay's" | #Olay | #Olays | "Professional Pro-X" | "Smooth Finish Facial Hair Removal Duo" | "Professional ProX" | "Smooth Finish Facial Hair Removal Duos" | "fresh effects" | fresheffects | "fresh effect" | fresheffect | "total effects" |</gnip:rule>  </source>  <service:provider xmlns:service="http://activitystrea.ms/service-provider">    <name>YouTube</name>    <uri>http://www.youtube.com/</uri>    <icon/>  </service:provider>  <activity:verb xmlns:activity="http://activitystrea.ms/spec/1.0/">http://activitystrea.ms/schema/1.0/post</activity:verb>  <activity:object xmlns:activity="http://activitystrea.ms/spec/1.0/">    <activity:object-type>http://activitystrea.ms/schema/1.0/video</activity:object-type>    <id>object:http://gdata.youtube.com/feeds/api/videos/s6Xsa8B4vL4</id>    <title type="text">Fotoelektrik Olay   2</title>    <content type="text"></content>    <link rel="alternate" type="text/html" href="http://www.youtube.com/watch?v=s6Xsa8B4vL4&amp;feature=youtube_gdata"/>    <link xmlns:atommedia1="http://purl.org/syndication/atommedia" rel="enclosure" type="application/x-shockwave-flash" href="http://www.youtube.com/v/s6Xsa8B4vL4?version=3&amp;f=videos&amp;app=youtube_gdata" atommedia1:duration="857"/>    <link xmlns:atommedia1="http://purl.org/syndication/atommedia" rel="preview" href="http://i.ytimg.com/vi/s6Xsa8B4vL4/0.jpg" atommedia1:height="360" atommedia1:width="480" atommedia1:duration="00:07:08.500"/>    <category scheme="http://gdata.youtube.com/schemas/2007/categories.cat" term="People"/>  </activity:object>  <author>    <name>YDS Video Ders Say&#x131;sal</name>    <uri>http://www.youtube.com/user/YDS Video Ders Say&#x131;sal</uri>  </author>  <activity:author xmlns:activity="http://activitystrea.ms/spec/1.0/">    <activity:object-type>http://activitystrea.ms/schema/1.0/person</activity:object-type>    <link rel="alternate" type="text/html" length="0" href="http://www.youtube.com/user/YDS Video Ders Say&#x131;sal"/>    <id>http://gdata.youtube.com/feeds/api/users/7jgS9i6n8p5Qs6vUwPLuEg</id>  </activity:author>  <activity:actor xmlns:activity="http://activitystrea.ms/spec/1.0/">    <activity:object-type>http://activitystrea.ms/schema/1.0/person</activity:object-type>    <link rel="alternate" type="text/html" length="0" href="http://www.youtube.com/user/YDS Video Ders Say&#x131;sal"/>    <id>http://gdata.youtube.com/feeds/api/users/7jgS9i6n8p5Qs6vUwPLuEg</id>  </activity:actor>  <gnip:matching_rules>    <gnip:matching_rule rel="source" tag="Olay_CA">(Olay | Olays | "Olay's" | #Olay | #Olays | "Professional Pro-X" | "Smooth Finish Facial Hair Removal Duo" | "Professional ProX" | "Smooth Finish Facial Hair Removal Duos" | "fresh effects" | fresheffects | "fresh effect" | fresheffect | "total effects" |</gnip:matching_rule>  </gnip:matching_rules></entry>";

    @Test
    public void deserializationTest() throws Exception {
        ActivityXMLActivitySerializer serializer = new ActivityXMLActivitySerializer();
        Object obj = serializer.deserialize(XML_DATA);
        ObjectMapper mapper;
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, Boolean.FALSE);
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, Boolean.TRUE);
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, Boolean.TRUE);

//        System.out.println(mapper.writeValueAsString(obj));
    }

    @Test
    public void deserializationTest2() throws Exception{
        System.out.println(System.getProperty("user.dir"));
//        Scanner scanner = new Scanner(new File("streams-contrib/streams-pojo-gnip/src/test/resources/YoutubeEDC.xml"));
        Scanner scanner = new Scanner(YouTubeEDCSerDeTest.class.getResourceAsStream("/YoutubeEDC.xml"));
        ActivityXMLActivitySerializer serializer = new ActivityXMLActivitySerializer();
        while(scanner.hasNextLine()) {
            String xml = scanner.nextLine();
            try {
                 serializer.deserialize(xml);
            } catch (Exception e) {
                fail("Exception while deserializing : "+xml );
            }
        }
    }


}
