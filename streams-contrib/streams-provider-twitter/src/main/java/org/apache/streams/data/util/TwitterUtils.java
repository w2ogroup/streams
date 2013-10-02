package org.apache.streams.data.util;


import com.twitter.Users;
import org.apache.streams.pojo.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import static org.apache.streams.data.util.ActivityUtil.ensureExtensions;

import com.twitter.Tweet;

/**
* Created with IntelliJ IDEA.
* User: mdelaet
* Date: 9/30/13
* Time: 9:16 AM
* To change this template use File | Settings | File Templates.
*/
public class TwitterUtils {
    private TwitterUtils() {
    }

//    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String DATE_FORMAT = "EEE MMM dd HH:mm:ss Z yyyy";

    public static Activity convert(Tweet tweet) {
        Activity activity = new Activity();
        activity.setId(formatId(tweet.getId_str()));
        activity.setActor(buildActor(tweet));
        activity.setVerb("posted");
        activity.setObject(buildActivityObject(tweet));
        activity.setTarget(buildTarget(tweet));
        activity.setPublished(parse(tweet.getCreated_at()));
        activity.setGenerator(buildGenerator(tweet));
        activity.setIcon(getIcon(tweet));
        activity.setProvider(buildProvider(tweet));
        activity.setTitle("");
        activity.setContent(tweet.getText());
        activity.setUrl(getUrls(tweet));
        activity.setLinks(getLinks(tweet));
        addTwitterExtension(activity, tweet);
        addLocationExtension(activity, tweet);
        return activity;
    }

    public static String formatId(String id_str) {
        return "id:twitter:tweet:".concat(id_str);
    }

    public static Actor buildActor(Tweet tweet) {
        Actor actor = new Actor();
        LinkedHashMap<Object,Object> user = (LinkedHashMap<Object,Object>) tweet.getUser();
        actor.setId(formatId(tweet.getId_str()));
        actor.setDisplayName(user.get("screen_name").toString());
        actor.setId(user.get("id_str").toString());
        if (user.get("url")!=null){
            actor.setUrl(user.get("url").toString());
        }
        return actor;
    }

    public static ActivityObject buildActivityObject(Tweet tweet) {
        ActivityObject actObj = new ActivityObject();
        actObj.setId(formatId(tweet.getId_str()));
        actObj.setObjectType("tweet");
        return actObj;
    }

    public static ActivityObject buildTarget(Tweet tweet) {
        return null;
    }

    public static Date parse(String str) {
        Date date;
        String dstr;
        DateFormat fmt = new SimpleDateFormat(DATE_FORMAT);
        DateFormat out = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try {
            date = fmt.parse(str);
            dstr = out.format(date);
            return out.parse(dstr);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format", e);
        }
    }

    public static Generator buildGenerator(Tweet tweet) {
        return null;
    }

    public static Icon getIcon(Tweet tweet) {
        return null;
    }

    public static Provider buildProvider(Tweet tweet) {
        Provider provider = new Provider();
        provider.setId("id:providers:twitter");
        return provider;
    }

    public static java.util.List<Object> getLinks(Tweet tweet) {
        return null;
    }

    public static String getUrls(Tweet tweet) {
        return null;
    }

    public static void addTwitterExtension(Activity activity, Tweet tweet) {
        Map<String, Object> extenstions = ensureExtensions(activity);
        Map<String, Object> twitter = new HashMap<String, Object>();
        twitter.put("id", formatId(tweet.getId_str()));
        twitter.put("source", tweet.getSource());
        twitter.put("in_reply_to_screen_name", tweet.getIn_reply_to_screen_name());
        twitter.put("retweeted", tweet.getRetweeted());
        twitter.put("contributors", tweet.getContributors());
        twitter.put("possibly_sensitive", tweet.getPossibly_sensitive());
        twitter.put("truncated", tweet.getTruncated());
        twitter.put("lang", tweet.getLang());
        twitter.put("entities", tweet.getEntities());
        twitter.put("in_reply_to_user_id_str", tweet.getIn_reply_to_user_id_str());
        twitter.put("id", tweet.getId());
        twitter.put("in_reply_to_status_id_str", tweet.getIn_reply_to_status_id_str());
        twitter.put("favorited", tweet.getFavorited());
        twitter.put("in_reply_to_status_id", tweet.getIn_reply_to_status_id());
        twitter.put("in_reply_to_user_id", tweet.getIn_reply_to_user_id_str());
        twitter.put("retweeted_count", tweet.getRetweet_count());
        twitter.put("__id", tweet.get__id());
        twitter.put("users", tweet.getUser());
        extenstions.put("twitter", twitter);
    }

    public static void addLocationExtension(Activity activity, Tweet tweet) {
        Map<String, Object> extentions = ensureExtensions(activity);
        Map<String, Object> location = new HashMap<String, Object>();
        location.put("id", formatId(tweet.getId_str()));
        location.put("geo", tweet.getGeo());
        location.put("place", tweet.getPlace());
        location.put("coordinates", tweet.getCoordinates());
        extentions.put("location", location);
    }
}
