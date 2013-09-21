package com.semantria.trident.util;

import com.semantria.mapping.Document;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mdelaet
 * Date: 8/16/13
 * Time: 2:19 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ISentiment {

    public String html2text(String hmtlString);
    public HashMap<String, Object> get_all_info(Object doc) throws Exception;
    public JSONObject make_sentiment_json(String ckey, String csecert, String appName, String id, String doc, String config_id);
//    public List<JSONObject> batch_make_sentiment_json(String ckey, String csecert, String appName, List<Document> docs, String config_name);
}
