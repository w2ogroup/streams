package org.apache.streams.kafka.repository.impl;

import org.apache.streams.pojo.json.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class KafkaActivityStreamsRepository {

    private static final Logger log = LoggerFactory.getLogger(KafkaActivityStreamsRepository.class);

    public KafkaActivityStreamsRepository() {

    }

    public void save(Activity entry) {

        // put
    }

    public List<Activity> getActivities(Date lastUpdated) {
        List<Activity> results = new ArrayList<Activity>();

        // get

        return results;
    }

}
