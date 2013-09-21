package com.semantria.trident.function;

import backtype.storm.tuple.Values;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;

import com.semantria.trident.util.SemantriaToJSON;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mdelaet
 * Date: 9/13/13
 * Time: 11:09 AM
 * To change this template use File | Settings | File Templates.
 */
public class LexalyticsSenimantAnalyzerSingleDocFunction extends BaseFunction {

    private static final Logger LOGGER = LoggerFactory.getLogger(LexalyticsSenimantAnalyzerSingleDocFunction.class);

    public String doc_field;
    public String ckey;
    public String csecert;
    public String appName;
    public String id_field;
    public String config_id;

    public LexalyticsSenimantAnalyzerSingleDocFunction(String doc_field_name, String key, String secert, String app,
                                                       String id_field_name, String configuration_id){
        doc_field = doc_field_name;
        ckey = key;
        csecert = secert;
        appName = app;
        id_field = id_field_name;
        config_id = configuration_id;
    }

    @Override
    public void prepare(Map conf, TridentOperationContext context) {
        super.prepare(conf, context);
    }

    public void execute(TridentTuple tuple, TridentCollector collector) {

        String doc = tuple.getStringByField(doc_field);
        String id = tuple.getStringByField(id_field);

        SemantriaToJSON parser = new SemantriaToJSON();
        JSONObject sentimentJSON = parser.make_sentiment_json(ckey, csecert, appName, id, doc, config_id);

        collector.emit(new Values(sentimentJSON));
    }

}
