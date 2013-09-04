package org.apache.streams.cassandra.repository.impl;

import com.datastax.driver.core.*;

import com.datastax.driver.core.exceptions.AlreadyExistsException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.rave.model.ActivityStreamsEntry;
import org.apache.rave.model.ActivityStreamsObject;
import org.apache.rave.portal.model.impl.ActivityStreamsEntryImpl;
import org.apache.rave.portal.model.impl.ActivityStreamsObjectImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class CassandraActivityStreamsRepository {

    private final String KEYSPACE_NAME = "keytest";
    private final String TABLE_NAME = "coltest";

    private static final Log LOG = LogFactory.getLog(CassandraActivityStreamsRepository.class);

    private Cluster cluster;
    private Session session;

    public CassandraActivityStreamsRepository() {
        cluster = Cluster.builder().addContactPoint("127.0.0.1").build();
        session = cluster.connect();

        //TODO: cassandra 2 will have support for CREATE KEYSPACE IF NOT EXISTS
        try {
            session.execute("CREATE KEYSPACE " + KEYSPACE_NAME + " WITH replication = { 'class': 'SimpleStrategy','replication_factor' : 1 };");
        } catch (AlreadyExistsException ignored) {
        }
        //connect to the keyspace
        session = cluster.connect(KEYSPACE_NAME);
        try {
            session.execute("CREATE TABLE " + TABLE_NAME + " (" +
                    "id text, " +
                    "published timestamp, " +
                    "verb text, " +
                    "actor_displayname text, " +
                    "actor_id text, " +
                    "actor_url text, " +
                    "target_displayname text, " +
                    "target_id text, " +
                    "target_url text, " +
                    "object_displayname text, " +
                    "object_id text, " +
                    "PRIMARY KEY (id, target_displayname, published));");
        } catch (AlreadyExistsException ignored) {
        }
    }

    public void save(ActivityStreamsEntry entry) {
        String sql = "INSERT INTO " + TABLE_NAME + " (id, published, verb, actor_displayname, actor_id, actor_url, target_displayname, target_id, target_url, object_displayname, object_id) VALUES ('" +
                entry.getId() + "','" +
                entry.getPublished().getTime() + "','" +
                entry.getVerb() + "','" +
                entry.getActor().getDisplayName() + "','" +
                entry.getActor().getId() + "','" +
                entry.getActor().getUrl() + "','" +
                entry.getTarget().getDisplayName() + "','" +
                entry.getTarget().getId() + "','" +
                entry.getTarget().getUrl() + "','" +
                entry.getObject().getDisplayName() + "','" +
                entry.getObject().getId() + "')";
        session.execute(sql);
    }

    public List<ActivityStreamsEntry> getActivitiesForFilters(List<String> filters, Date lastUpdated) {
        String cql = "SELECT * FROM " + TABLE_NAME + " WHERE ";
        if(!filters.isEmpty()){
            cql = cql + " target_displayname IN ('"+ StringUtils.join(filters, "','")+"') AND ";
        }
        cql = cql + "published > " + lastUpdated.getTime() + "LIMIT 10 ALLOW FILTERING";

        //execute the cql query and store the results
        ResultSet set = session.execute(cql);

        //iterate through the results and create a new ActivityStreamsEntry for every result returned
        List<ActivityStreamsEntry> results = new ArrayList<ActivityStreamsEntry>();
        for (Row row : set) {
            ActivityStreamsEntry entry = new ActivityStreamsEntryImpl();
            ActivityStreamsObject actor = new ActivityStreamsObjectImpl();
            ActivityStreamsObject target = new ActivityStreamsObjectImpl();
            ActivityStreamsObject object = new ActivityStreamsObjectImpl();

            actor.setDisplayName(row.getString("actor_displayname"));
            actor.setId(row.getString("actor_id"));
            actor.setUrl(row.getString("actor_url"));

            target.setDisplayName(row.getString("target_displayname"));
            target.setId(row.getString("target_id"));
            target.setUrl(row.getString("target_url"));

            object.setDisplayName(row.getString("object_displayname"));
            object.setUrl(row.getString("object_id"));

            entry.setPublished(row.getDate("published"));
            entry.setVerb(row.getString("verb"));
            entry.setId(row.getString("id"));
            entry.setActor(actor);
            entry.setTarget(target);
            entry.setObject(object);

            results.add(entry);
        }

        return results;
    }

    public void dropTable(String table){
        String cql = "DROP TABLE " + table;
        session.execute(cql);
    }


    @Override
    protected void finalize() throws Throwable {
        try {
            cluster.shutdown();
        } finally {
            super.finalize();
        }
    }

}
