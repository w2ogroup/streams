package com.w2olabs.stormutil.trident.state.Semantria;

import com.semantria.mapping.output.DocAnalyticData;
import org.apache.commons.codec.digest.DigestUtils;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkResponse;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.operation.TridentCollector;
import storm.trident.state.State;
import storm.trident.tuple.TridentTuple;
import com.semantria.mapping.Document;
import com.semantria.Session;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import storm.trident.state.StateFactory;
import java.util.*;

import com.semantria.mapping.configuration.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: mdelaet
 * Date: 9/13/13
 * Time: 2:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class SemantriaState  implements State {

    private static Logger log = LoggerFactory.getLogger(SemantriaState.class);

    public String doc_field;
    public String ckey;
    public String csecert;
    public String appName;
    public String id_field;
    public String config_name;
    public List<JSONObject> all_info;
    public List<Float> full_doc_sentiment_score;
    public CallbackHandler callbackHandler;
    public String config_id;

    private Session session;

    public SemantriaState(SemantriaConfiguration config){
        doc_field = config.getSemantriaDocField();
        id_field = config.getSemantriaIdField();
        config_name = config.getSemantriaConfigurationID();
        all_info = new ArrayList<JSONObject>();
        ckey = config.getSemantriaCkey();
        csecert = config.getSemantriaCsecert();
        appName = config.getSemantriaAppname();
        full_doc_sentiment_score = new ArrayList<Float>();
        callbackHandler = new CallbackHandler();
        config_id = null;
        session = Session.createSession(ckey, csecert, appName);
        session.setCallbackHandler(callbackHandler);
    }

    @Override
    public void beginCommit(Long txid) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void commit(Long txid) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<Document> batchDocs(List<TridentTuple> tuples){
        log.info("tuple list size in batchDocs {}", tuples.size());
        List<Document> docs = new ArrayList<Document>();
        for (TridentTuple tuple : tuples){
            String doc_string = tuple.getStringByField(doc_field);
            String id = tuple.getStringByField(id_field);
            Document doc = new Document(id, doc_string);
            docs.add(doc);
        }
        log.info("docs size at the end of batchDocs: {}", docs.size());
        return docs;
    }

    public void serverConfiguration(String config_name){
        config_id = null;
        List<Configuration> avialable_configs = session.getConfigurations();
        log.info("Avialable configs: {}", avialable_configs);
        for (Configuration con : avialable_configs){
            if (con.getName().equals(config_name)){
                config_id = con.getId();
            }
        }
        log.info("Config id: {}", config_name);
        if (config_id == null){
            Configuration config = new Configuration();
            config.setName(config_name);
            List<Configuration> configs = new ArrayList<Configuration>();
            configs.add(config);
            session.addConfigurations(configs);

            List<Configuration> aConfigs = session.getConfigurations();
            for (Configuration conf : aConfigs){
                if (conf.getName().equals(config_name)){
                    config_id = conf.getId();
                }
            }
        }

        log.info("Configuration id is: " + config_id);
    }

    public String getPreprocessesContent(String contentField, JSONObject json_doc){
        String output = new String();
        String preprossedContent = new String();
        try{
            preprossedContent = json_doc.getString(contentField);
        } catch (Exception e){
            log.error("can't process {} into a JSONObject", json_doc);
        }
        if (preprossedContent.length() > 8000){
            output = preprossedContent.substring(0,8000);
        } else {
            output = preprossedContent;
        }
        return output;
    }

    public void batchAnalyze(List<TridentTuple> tuples) throws Exception{
        log.info("Size of tuple list is: {}", tuples.size());
        List<Document> docs = new ArrayList<Document>();
        for (TridentTuple tuple : tuples){
            String preprocess_content = new String();
            String provider_id = new String();

            JSONObject json_obj_doc = new JSONObject();
            try {
                json_obj_doc = new JSONObject(tuple.getStringByField("gnip_json"));
            } catch (Exception e){
                log.info("Caught Exception processing json, {}", e);
            }

            try {
                if (json_obj_doc.has("provider")){
                    provider_id = json_obj_doc.getJSONObject("provider").getString("id");
                } else if (json_obj_doc.has("target")) {
                    provider_id = json_obj_doc.getJSONObject("target").getString("id");
                } else {
                    log.info("Unable to determine provider, {}", json_obj_doc);
                }
            } catch (Exception e) {
                log.info("Caught exception getting provider id from {}, exception: {}", json_obj_doc.toString(), e);
            }

            log.info("Provider id is : {}", provider_id);
            try{
                if (provider_id.equals("{link}")){
                    if (json_obj_doc.has("provider")){
                        preprocess_content = getPreprocessesContent("body", json_obj_doc);
                    } else if (json_obj_doc.has("target")) {
                        preprocess_content = getPreprocessesContent("content", json_obj_doc);
                    }
                } else if (provider_id.equals("id:providers:facebook")){
                    preprocess_content = getPreprocessesContent("content", json_obj_doc);
                } else {
                    log.info("Unable to determine content, {}", json_obj_doc);
                }
            } catch (Exception e) {
                log.info("Could not process content out of tuple because of unknown id: {}, and caught exception: {}", provider_id, e);
                log.info("json with unknown provider id: {}", json_obj_doc.toString());
            }

            String doc_id = new String();
            try {
                doc_id = json_obj_doc.getString("id");
            } catch(Exception e) {
                log.info("no id field in the document, exception: {}", e);
            }

            try {
                doc_id = DigestUtils.md5Hex(doc_id.toString());
            } catch(Exception e) {
                log.info("Failed to hash doc_id from tuple, {}, exception: {}", doc_id, e);
            }

            Document sin_doc = new Document(doc_id, preprocess_content);
            docs.add(sin_doc);
        }

        log.info("Size docs list is: {}", docs.size());
        if (docs.size() >0){
            try {
                serverConfiguration(config_name);
            } catch(Exception e) {
                log.info("Failed to create semantria configuration, {}, with exception {}", config_name, e);
            }

            Integer response = session.queueBatch(docs, config_id);
            if (response!=202){
                throw new Exception("Response from Semantria was not 202 but " + response);
            }
        } else {
            log.info("No tuples survived to upload into semantria.");
        }

    }

    public void sleepMessages(Integer len){
        for (int i =0; i < len; i++){
            try{
                Thread.sleep(4000);
            } catch (Exception e) {
                log.error("Caught execption while trying sleep, " +e);
            }
            log.debug("Semantria isn't real time.");
        }
    }

    public void batchGetFullSentimentScore(List<TridentTuple> tuples){
        sleepMessages(1);

        List<DocAnalyticData> processedDocs  = new ArrayList<DocAnalyticData>();
        for (TridentTuple tuple : tuples){
            String id = tuple.getStringByField(id_field);
            processedDocs.add(session.getDocument(id, config_id));
        }

        if (processedDocs.size() == tuples.size()){
            for (TridentTuple tuple : tuples){
                for (DocAnalyticData pd : processedDocs){
                    if (tuple.getStringByField(id_field).equals(pd.getId())){
                        full_doc_sentiment_score.add(pd.getSentimentScore());
                    }
                }
            }
        }
    }

    public void batchSemantriaProcess(List<TridentTuple> tuples){
        try{
            batchAnalyze(tuples);
        } catch(Exception e) {
            log.error("was not able to add files to semantria, caught exception: " + e);
        }
        batchGetFullSentimentScore(tuples);
    }

    public void bulkProcess(TridentCollector collector, List<TridentTuple> tuples) throws Exception {
        this.batchSemantriaProcess(tuples);

        Iterator<TridentTuple> itTuple = tuples.iterator();
        Iterator<Float> itSentiment = this.full_doc_sentiment_score.iterator();

        if (tuples.size()==this.full_doc_sentiment_score.size()){
            while (itTuple.hasNext() && itSentiment.hasNext()){
                List<Object> values = itTuple.next().getValues();
                values.add(itSentiment.next());
                collector.emit(values);
            }
        } else {
            log.error("Not all the tuples have corresponding responses from Semantria");
            throw new Exception();
        }
    }

    public static class Factory implements StateFactory {
        public SemantriaConfiguration config = new SemantriaConfiguration();

        @Deprecated
        public Factory() {

        }

        public Factory(SemantriaConfiguration config) {
            this.config = config;
        }

        public Factory(String doc_field_name, String key, String secert, String app,
                       String id_field_name, String configuration_id){
            this.config.setCKey(key);
            this.config.setIdField(id_field_name);
            this.config.setDocField(doc_field_name);
            this.config.setCSecert(secert);
            this.config.setAppName(app);
            this.config.setConfigurationName(configuration_id);
        }

        public State makeState(Map conf, int partitionIndex, int numPartitions){
            log.info("Creating SematriaState with ckey : {}", this.config.get(SemantriaConfiguration.SEMANTRIA_CKEY));
            log.info("Creating SemantriaState with csecert : {}", this.config.get(SemantriaConfiguration.SEMANTRIA_CSECERT));
            log.info("Creating SemantriaState with appName : {}", this.config.get(SemantriaConfiguration.SEMANTRIA_APPNAME));
            log.info("Creating SemantriaState with configuration name : {}", this.config.get(SemantriaConfiguration.SEMANTRIA_CONFIGURATION_NAME));
            log.info("Creating SemantriaState with doc_field : {}", this.config.get(SemantriaConfiguration.SEMANTRIA_DOC_FIELD));
            log.info("Creating SemantriaState with id_field : {}", this.config.get(SemantriaConfiguration.SEMANTRIA_ID_FIELD));
            return new SemantriaState(this.config);
        }

        public SemantriaConfiguration getConfig(){
            return config;
        }

    }
}
