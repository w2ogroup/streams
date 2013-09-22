package com.semantria.trident.topologies;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.tuple.Fields;
import backtype.storm.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.semantria.SemantriaConfiguration;
import com.semantria.trident.function.data.activitystreams.RawDataToActivityStream;
import com.semantria.trident.spout.gnip.GnipDataExtractor;
import com.semantria.trident.state.ElasticSearch.ElasticSearchBulkUpdateSentiment;
import com.semantria.trident.state.ElasticSearch.ElasticSearchIndex;
import com.semantria.trident.state.ElasticSearch.ElasticSearchState;
import com.semantria.trident.state.Semantria.SemantriaBatchAnalyze;
import com.semantria.trident.state.Semantria.SemantriaState;
import com.semantria.trident.state.kafka.KafkaConfiguration;
import org.apache.streams.StreamsConfiguration;
import org.apache.streams.storm.StreamsTopology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.kafka.KafkaConfig;
import storm.kafka.trident.OpaqueTridentKafkaSpout;
import storm.kafka.tridentKafkaConfig;
import storm.trident.Stream;
import storm.trident.TridentTopology;
import storm.tridentTopology;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: mdelaet
 * Date: 9/17/13
 * Time: 3:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class ActivitySemantriaRequestTopology extends StreamsConfiguration implements Runnable {

    private Logger log = LoggerFactory.getLogger(ActivitySemantriaRequestTopology.class);

    private SemantriaConfiguration semantriaConfiguration;

//    public ActivitySemantriaRequestTopology(String pipelineIdentifier, StreamsConfiguration configuration) {
//        super(pipelineIdentifier,configuration);
//        this.pipelineIdentifier = pipelineIdentifier;
//        this.configuration = configuration;
//    }

    public static SemantriaConfiguration provideConfiguration(StreamsConfiguration streamsConfiguration) {

        ObjectMapper mapper = new ObjectMapper();

        SemantriaConfiguration semantriaConfiguration;

        JsonNode config;
        try {
            config = mapper.readValue(mapper.writeValueAsString(streamsConfiguration), JsonNode.class);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return null;
        }

        try {
            semantriaConfiguration = mapper.readValue(mapper.writeValueAsString(config.get("semantria")), SemantriaConfiguration.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return null;
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return null;
        }

        return semantriaConfiguration;

    }

//    public static void main(String[] args) throws Exception{
//
//        ActivitySemantriaRequestTopology topology
//
//    }

    private static String[] getKafkaTopics(TopologyConfiguration conf) {
        String[] ptStreams = conf.getStringArray("gnip.pt.streams");
        String[] edcStreams = conf.getStringArray("gnip.edc.streams");
        String[] result = new String[ptStreams.length+edcStreams.length];
        int index = 0;
        for(String s : ptStreams) {
            String[] split = s.split(":");
            result[index++] = conf.get("kafka.gnip.topic")+"_"+split[1].substring(0, split[1].indexOf('}'))+":"+split[1].substring(0, split[1].indexOf('}'));
            System.out.println(result[index - 1]);
        }
        for(String s : edcStreams) {
            String[] split = s.split(":");
            result[index++] = conf.get("kafka.gnip.topic")+"_"+split[1].substring(0, split[1].indexOf('}'))+":"+split[1].substring(0, split[1].indexOf('}'));
            System.out.println(result[index - 1]);
        }
        return result;
    }

    @Override
    public void run() {

        TridentTopology topology = new TridentTopology();
        Config stormConfig = new Config();

        semantriaConfiguration = provideConfiguration((StreamsConfiguration) this.configuration);

        semantriaConfiguration.setDocField("gnip_json");
        semantriaConfiguration.setIdField("id");

        String[] kafkaTopics = getKafkaTopics(w2oConfig);
        log.debug("The kafka Topics are: " + kafkaTopics);
        Stream[] streams = new Stream[kafkaTopics.length];
        for(int i=0; i < kafkaTopics.length; ++i) {
            String[] split = kafkaTopics[i].split(":");
            String source = split[1];
            if(source.equals("facebook") || source.equals("twitter") || source.equals("tumblr") || source.equals("foursquare") || source.equals("youtube") || source.equals("googleplus") || source.equals("reddit")) {
//            if(source.equals("youtube")) {
                TridentKafkaConfig kafkaConfig = new TridentKafkaConfig(new KafkaConfig.StaticHosts(w2oKafka.getHostPortsList(), w2oKafka.getPartitionsPerTopic()), split[0]);
                kafkaConfig.forceStartOffsetTime(-1);
                streams[i] = sentimenttopology.newStream(w2oConfig.getClientName() + "KafkaSpoutSentiment" + split[1], new OpaqueTridentKafkaSpout(kafkaConfig))
                        .each(kafkaConfig.scheme.getOutputFields(), new GnipDataExtractor(split[1]), new Fields("gnip_data", "provider")).project(new Fields("gnip_data", "provider"));
                if(split[1].equals("facebook")) {
                    streams[i] = streams[i].each(new Fields("gnip_data"), new RawDataToActivityStream("gnip_data", "org.apache.streams.data.FacebookPostActivitySerializer"), new Fields("gnip_json")).project(new Fields("gnip_json", "provider"));
                }
                else if(split[1].equals("twitter") || split[1].equals("tumblr") || split[1].equals("foursquare")) {  //power track sources
                    streams[i] = streams[i].each(new Fields("gnip_data"), new RawDataToActivityStream("gnip_data", "org.apache.streams.data.PowerTrackActivitySerializer"), new Fields("gnip_json")).project(new Fields("gnip_json", "provider"));
                }
                else if(split[1].equals("youtube")) {
                    streams[i] = streams[i].each(new Fields("gnip_data"), new RawDataToActivityStream("gnip_data", "org.apache.streams.data.ActivityXMLActivitySerializer"), new Fields("gnip_json")).project(new Fields("gnip_json", "provider"));
                }
                else if(split[1].equals("googleplus")) {
                    streams[i] = streams[i].each(new Fields("gnip_data"), new RawDataToActivityStream("gnip_data", "com.gplus.api.GPlusActivitySerializer"), new Fields("gnip_json")).project(new Fields("gnip_json", "provider"));
                }
                else if(split[1].equals("reddit")) {
                    streams[i] = streams[i].each(new Fields("gnip_data"), new RawDataToActivityStream("gnip_data", "com.reddit.api.RedditActivitySerializer"), new Fields("gnip_json")).project(new Fields("gnip_json", "provider"));
                }
            }
        }

        int notNull = 0;
        for(int i=0; i < streams.length; ++i) {
            if(streams[i] != null)
                ++notNull;
        }
        Stream[] allStreams = new Stream[notNull];
        int index = 0;
        for(int i=0; i < streams.length; ++i) {
            if(streams[i] != null) {
                allStreams[index++] = streams[i];
            }
        }




        log.debug("Kafka host : " + w2oKafka.getBrokerList());

        Stream merged = sentimenttopology.merge(new Fields("gnip_json", "provider"), allStreams);

        SemantriaState.Factory sFactory = new SemantriaState.Factory(sConfig);

        merged.partitionPersist(sFactory, new Fields("id", "gnip_json"), new SemantriaBatchAnalyze());

        if (args.length >= 1 && args[1].equals("deploy")) {
            stormConfig.setNumWorkers(2);
            stormConfig.setMaxSpoutPending(20);
//            stormConfig.setNumAckers(2);
            stormConfig.setMessageTimeoutSecs(30);
            StormSubmitter.submitTopology(w2oConfig.getClientName() + "KafkaGnipToSemantria", stormConfig, sentimenttopology.build());
        } else {
            stormConfig.setMessageTimeoutSecs(120);
            stormConfig.setMaxSpoutPending(50);
            stormConfig.setMaxSpoutPending(50);
            //stormConfig.setDebug(true);
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("KafkaGnipToSemantria", stormConfig, sentimenttopology.build());
            Utils.sleep(20 * 60 * 60 * 1000L);
            cluster.killTopology("KafkaGnipToSemantria");
            cluster.shutdown();
        }
    }
}
