package com.semantria.trident.controllers;

import com.semantria.trident.trident.state.ElasticSearch.ElasticSearchController;
import org.elasticsearch.client.transport.TransportClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.tuple.TridentTuple;

import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: mdelaet
 * Date: 8/14/13
 * Time: 3:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class SentimentESController implements ElasticSearchController {

    private Logger logger = LoggerFactory.getLogger(SentimentESController.class);
    private String clientName;

    public SentimentESController(String clientName) {
        this.clientName = clientName;
    }


    @Override
    public void bulkIndex(List<TridentTuple> tridentTuples, TransportClient transportClient) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void bulkUpdate(List<TridentTuple> tridentTuples, TransportClient transportClient) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getIndex(TridentTuple tuple) throws Exception {
        String id = tuple.getStringByField("id");
        return this.clientName+id.split(":")[0];
    }

    @Override
    public String getType(TridentTuple tuple) throws Exception {
        return "semantria";  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getSource(TridentTuple tuple) throws Exception {
        return createSourceJSON(tuple.getStringByField("flattend_json")).toString();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getId(TridentTuple tuple) throws Exception {
        return tuple.getStringByField("id");  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getUpdateScript(String index, String type) {
        return "ctx._source.w2o = tags";  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map<String, Object> getScriptParams(String index, String type, List<String> matches) {
        Map<String, List<String>> tags = new HashMap<String, List<String>>();
        for(String tag : matches) {
            String[] split = tag.split("_"); //Should it still be an _?
            if(tags.get(split[0]) == null) {
                tags.put(split[0], new ArrayList<String>());
            }
            tags.get(split[0]).add(split[1]);
        }
        Map<String, Object> params = new HashMap<String, Object>();
        Map<String,Object> outer = new HashMap<String,Object>();
        outer.put("tags", tags);
        params.put("tags", outer);

        return params;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map<String, Object> getScriptParams(String index, String type, Float sentiment) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getPercolateIndex(TridentTuple tuple) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public String getPercolateSource(TridentTuple tuple) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    //TODO! Make this method NOT sysomos specific
    public JSONObject createSourceJSON(String sementriaJson) throws Exception{
        JSONObject json = new JSONObject(sementriaJson);
        JSONObject w2o = new JSONObject();
        try {
            JSONArray tagArray = new JSONArray();
            JSONArray semantriaTags = json.getJSONObject("sementria").optJSONArray("non_system_tags");
            Set<String> combined = new HashSet<String>();
            if(semantriaTags != null) {
                for(int i=0; i < semantriaTags.length(); ++i) {
                    combined.add(semantriaTags.getString(i));
                }
            }
            if(tagArray.length() > 0) {
                for(int i=0; i < tagArray.length(); ++i) {
                    combined.add(tagArray.getString(i));
                }
                w2o.put("tags", tagArray);
                w2o.put("combined_tags", new JSONArray(combined));
            }
            else if(combined.size() > 0) {
                w2o.put("combined_tags", new JSONArray(combined));
            }
            if(w2o.length() > 0) {
                json.put("w2o", w2o);
            }

        } catch (Exception e) {
            logger.error("Exception while trying to add GeoJson data : ", e);
        }
        return json;
    }
}

