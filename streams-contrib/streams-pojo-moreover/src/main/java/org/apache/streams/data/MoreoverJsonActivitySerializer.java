package org.apache.streams.data;

import org.apache.streams.pojo.Activity;

import java.util.List;

/**
 * Deserializes Moreover JSON format into Activities
 */
public class MoreoverJsonActivitySerializer implements ActivitySerializer {
    @Override
    public String serializationFormat() {
        return "application/json+vnd.moreover.com.v1";
    }

    @Override
    public String serialize(Activity deserialized) {
        throw new UnsupportedOperationException("Cannot currently serialize to Moreover JSON");
    }

    @Override
    public Activity deserialize(String serialized) {
        return null;
    }

    @Override
    public List<Activity> deserializeAll(String serializedList) {
        return null;
    }
}
