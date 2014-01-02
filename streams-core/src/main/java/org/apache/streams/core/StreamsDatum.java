package org.apache.streams.core;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Map;

/**
 * Created by sblackmon on 1/2/14.
 */
public abstract class StreamsDatum {

    public DateTime timestamp;

    public BigInteger sequenceid;

    public Map<String, Object> metadata;

    public Serializable document;

    public DateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(DateTime timestamp) {
        this.timestamp = timestamp;
    }

    public BigInteger getSequenceid() {
        return sequenceid;
    }

    public void setSequenceid(BigInteger sequenceid) {
        this.sequenceid = sequenceid;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public Serializable getDocument() {
        return document;
    }

    public void setDocument(Serializable document) {
        this.document = document;
    }
}
