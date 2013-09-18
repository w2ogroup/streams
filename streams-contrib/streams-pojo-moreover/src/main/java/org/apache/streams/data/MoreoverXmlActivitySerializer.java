package org.apache.streams.data;

import com.moreover.api.Article;
import com.moreover.api.ArticlesResponse;
import org.apache.commons.lang.SerializationException;
import org.apache.streams.data.util.MoreoverUtils;
import org.apache.streams.pojo.Activity;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Deserializes the Moreover Article XML and converts it to an instance of {@link Activity}
 */
public class MoreoverXmlActivitySerializer implements ActivitySerializer {

    //JAXBContext is threadsafe (supposedly)
    private final JAXBContext articleContext;
    private final JAXBContext articlesContext;

    public MoreoverXmlActivitySerializer() {
        articleContext = createContext(Article.class);
        articlesContext = createContext(ArticlesResponse.class);
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
        return MoreoverUtils.convert(article);
    }

    @Override
    public List<Activity> deserializeAll(String serializedList) {
        ArticlesResponse response = deserializeMoreoverResponse(serializedList);
        List<Activity> activities = new LinkedList<Activity>();
        for(Article article : response.getArticles().getArticle()) {
            activities.add(MoreoverUtils.convert(article));
        }
        return activities;
    }

    private Article deserializeMoreover(String serialized){
        try {
            Unmarshaller unmarshaller = articleContext.createUnmarshaller();
            return (Article) unmarshaller.unmarshal(new StringReader(serialized));
        } catch (JAXBException e) {
            throw new SerializationException("Unable to deserialize Moreover data");
        }
    }

    private ArticlesResponse deserializeMoreoverResponse(String serialized){
        try {
            Unmarshaller unmarshaller = articleContext.createUnmarshaller();
            return (ArticlesResponse) unmarshaller.unmarshal(new StringReader(serialized));
        } catch (JAXBException e) {
            throw new SerializationException("Unable to deserialize Moreover data");
        }
    }

    private JAXBContext createContext(Class articleClass) {
        JAXBContext context;
        try {
            context = JAXBContext.newInstance(articleClass);
        } catch (JAXBException e) {
            throw new IllegalStateException("Unable to create JAXB Context for Moreover data", e);
        }
        return context;
    }
}
