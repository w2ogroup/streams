package org.apache.streams.data;

import com.moreover.api.Article;
import org.apache.commons.lang.SerializationException;
import org.apache.streams.pojo.Activity;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.List;

/**
 * Deserializes the Moreover Article XML and converts it to an instance of {@link Activity}
 */
public class MoreoverArticleActivitySerializer implements ActivitySerializer {

    //JAXBContext is threadsafe (supposedly)
    private final JAXBContext context;

    public MoreoverArticleActivitySerializer() {
        context = createContext();
    }

    @Override
    public String serializationFormat() {
        return "application/xml+vnd.moreover.com.v1";
    }

    @Override
    public String serialize(Activity deserialized) {
        throw new UnsupportedOperationException("Cannot currently serialize to Moreover");
    }

    @Override
    public Activity deserialize(String serialized) {
        Article article = deserializeMoreover(serialized);
        article.getId();
        return null;
    }

    @Override
    public List<Activity> deserializeAll(String serializedList) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private Article deserializeMoreover(String serialized){
        try {
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (Article) unmarshaller.unmarshal(new StringReader(serialized));
        } catch (JAXBException e) {
            throw new SerializationException("Unable to deserialize Moreover data");
        }
    }

    private JAXBContext createContext() {
        JAXBContext context;
        try {
            context = JAXBContext.newInstance(Article.class);
        } catch (JAXBException e) {
            throw new IllegalStateException("Unable to create JAXB Context for Moreover data", e);
        }
        return context;
    }
}
