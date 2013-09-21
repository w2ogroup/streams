package com.w2olabs.stormutil.trident.state.Semantria;

import com.w2olabs.stormutil.utils.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: mdelaet
 * Date: 9/13/13
 * Time: 4:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class SemantriaConfiguration extends Configuration {
    public static final String SEMANTRIA_CKEY = "semantria.ckey";
    public static final String SEMANTRIA_CSECERT = "semantria.csecert";
    public static final String SEMANTRIA_APPNAME = "semantria.appName";
    public static final String SEMANTRIA_CONFIGURATION_NAME = "semantria.configuration.id";
    public static final String SEMANTRIA_DOC_FIELD = "semantria.doc.field";
    public static final String SEMANTRIA_ID_FIELD = "semantria.id.field";

    public SemantriaConfiguration() {
        this(null);
    }

    public SemantriaConfiguration(String propertiesFileName) {
        super(propertiesFileName);
    }

    public void setCKey(String ckey){
        super.set(SEMANTRIA_CKEY, ckey);
    }

    public void setCSecert(String csecert){
        super.set(SEMANTRIA_CSECERT, csecert);
    }

    public void setAppName(String appName){
        super.set(SEMANTRIA_APPNAME, appName);
    }

    public void setConfigurationName(String config_id){
        super.set(SEMANTRIA_CONFIGURATION_NAME, config_id);
    }
    public void setDocField(String doc_field){
        super.set(SEMANTRIA_DOC_FIELD, doc_field);
    }
    public void setIdField(String id_field){
        super.set(SEMANTRIA_ID_FIELD, id_field);
    }

    public String getSemantriaCkey(){
        return super.get(SEMANTRIA_CKEY);
    }

    public String getSemantriaCsecert(){
        return super.get(SEMANTRIA_CSECERT);
    }

    public String getSemantriaAppname(){
        return super.get(SEMANTRIA_APPNAME);
    }

    public String getSemantriaConfigurationID(){
        return super.get(SEMANTRIA_CONFIGURATION_NAME);
    }

    public String getSemantriaDocField(){
        return super.get(SEMANTRIA_DOC_FIELD);
    }

    public String getSemantriaIdField(){
        return super.get(SEMANTRIA_ID_FIELD);
    }
}
