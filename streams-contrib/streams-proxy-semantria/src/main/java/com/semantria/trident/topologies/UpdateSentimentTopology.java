package com.semantria.trident.topologies;

import backtype.storm.Config;
import backtype.storm.tuple.Fields;
import com.w2olabs.dashboardlite.controllers.GnipActivityESController;
import com.semantria.trident.trident.function.data.activitystreams.RawDataToActivityStream;
import com.semantria.trident.trident.spout.gnip.GnipDataExtractor;
import com.semantria.trident.trident.state.ElasticSearch.ElasticSearchBulkUpdateSentiment;
import com.semantria.trident.trident.state.ElasticSearch.ElasticSearchState;
import com.semantria.trident.state.Semantria.SemantriaGetInfo;
import com.semantria.trident.state.Semantria.SemantriaState;
import com.semantria.trident.trident.state.kafka.KafkaConfiguration;
import com.w2olabs.stormutil.utils.TopologyConfiguration;
import storm.kafka.KafkaConfig;
import storm.kafka.trident.OpaqueTridentKafkaSpout;
import storm.kafka.trident.TridentKafkaConfig;
import storm.trident.Stream;
import storm.trident.TridentTopology;

/**
 * Created with IntelliJ IDEA.
 * User: mdelaet
 * Date: 9/18/13
 * Time: 8:44 AM
 * To change this template use File | Settings | File Templates.
 */
public class UpdateSentimentTopology {
    public static void main(String[] args) throws Exception{
        TopologyConfiguration w2oConfig = new TopologyConfiguration(args[0]);
        TridentTopology sentimenttopology = new TridentTopology();
        Config stormConfig = new Config();
        KafkaConfiguration w2oKafka = w2oConfig.getKafkaConfiguration();


        SemantriaConfiguration sConfig = w2oConfig.getSemantriaConfiguration();

        String[] kafkaTopics = getKafkaTopics(w2oConfig);
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
        Stream merged = sentimenttopology.merge(new Fields("gnip_json", "provider"), allStreams);

        SemantriaState.Factory sFactory = new SemantriaState.Factory(sConfig);
        ElasticSearchState.Factory esFactory = new ElasticSearchState.Factory(w2oConfig.getElasticSearchConfiguration(), new GnipActivityESController(), w2oKafka);

        merged.partitionPersist(sFactory, new Fields("id", "gnip_json"), new SemantriaGetInfo()).newValuesStream()
                .partitionPersist(esFactory, new Fields("id", "sentiemnt", "gnip_json"), new ElasticSearchBulkUpdateSentiment());
    }


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
}
