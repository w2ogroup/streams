package org.apache.streams.storm;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.streams.StreamsConfiguration;
import org.apache.streams.config.ConfigurationFactory;
import org.apache.streams.util.RegexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Validation;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: sblackmon
 * Date: 9/20/13
 * Time: 11:17 AM
 * To change this template use File | Settings | File Templates.
 */
public class TopologyReflectionLauncher {

    private static final Logger log = LoggerFactory.getLogger(TopologyReflectionLauncher.class);

    private static StreamsConfiguration streamsConfiguration;

    private static List<Pair<String,Class>> topologies;

    private static List<Pair<String,Class>> resolveClasses(List<Pair<String,String>> topologyPairs) throws IOException, ClassNotFoundException {

        List<Pair<String,Class>> topologies = new ArrayList<Pair<String,Class>>();

        for( Pair<String,String> pair : topologyPairs ) {
            String topologyId = pair.getLeft();
            Class topologyClass = Class.forName(pair.getRight());
            topologies.add(new ImmutablePair(topologyId, topologyClass));
        }

        return topologies;
    }

    private static List<Pair<String,Class>> loadTopologiesFromPipelineTopologyListFile(File file) throws IOException, ClassNotFoundException {

        List<String> lines = IOUtils.readLines(FileUtils.openInputStream(file));
        String pattern = "^([\\w-]*)[\\s]*?([\\w.]*)$";

        List<Pair<String,String>> topologyPairs = RegexUtils.getTwoMatchedGroupsList(lines, pattern);

        topologies = resolveClasses(topologyPairs);

        for( Pair<String,String> pair : topologyPairs ) {
            String topologyId = pair.getLeft();
            Class topologyClass = Class.forName(pair.getRight());
            topologies.add(new ImmutablePair(topologyId, topologyClass));
        }

        return topologies;
    }

    private static List<Pair<String,Class>> loadTopologiesFromPipelineGraphFile(File file) throws IOException, ClassNotFoundException {

        List<String> lines = IOUtils.readLines(FileUtils.openInputStream(file));
        String pattern = "$([\\w-]*)\\s([\\w.)";

        List<Pair<String,String>> topologyPairs = RegexUtils.getTwoMatchedGroupsList(lines, pattern);

        topologies = resolveClasses(topologyPairs);

        return topologies;
    }

    public static boolean isLocal(String[] args) {
        if(args.length >= 1 && args[1].equals("deploy"))
            return false;
        else return true;
    }

    public static void main(String[] args) throws Exception {

        if(args.length < 3) {
            log.error("Not enough arguments");
            log.error("  storm deploy <configfile> <pipelinefile>");
            return;
        }
        if(!args[1].equals("deploy")) {
            log.error("Not a deploy");
            log.error("  storm deploy <configfile> <pipelinefile>");
            return;
        }

        ObjectMapper mapper = new ObjectMapper();

        URL configFileUrl = TopologyReflectionLauncher.class.getResource(args[0]);

        File configFile;
        try {
            configFile = new File(configFileUrl.toURI());
        } catch(URISyntaxException e) {
            configFile = new File(configFileUrl.getPath());
        }

        try {
            streamsConfiguration = new ConfigurationFactory<StreamsConfiguration>(StreamsConfiguration.class,
                    Validation.buildDefaultValidatorFactory().getValidator(),
                    mapper,
                    "streams").build(configFile);
        } catch(IOException e) {
            log.error("No config file", e.getMessage());
            e.printStackTrace();
            return;
        }

        String pipelineIdentifier = streamsConfiguration.getPipeline();

        URL pipelineFileUrl = TopologyReflectionLauncher.class.getResource(args[1]);

        File pipelineFile;
        try {
            pipelineFile = new File(pipelineFileUrl.toURI());
        } catch(URISyntaxException e) {
            pipelineFile = new File(pipelineFileUrl.getPath());
        }

        topologies = loadTopologiesFromPipelineTopologyListFile(pipelineFile);

        for( Pair<String,Class> topology : topologies ) {
            Class topologyClass = topology.getRight();

            try {
                Constructor ctor = topologyClass.getDeclaredConstructor(
                    String.class,
                    StreamsConfiguration.class);
                ctor.setAccessible(true);
                Object topologyObject = ctor.newInstance(pipelineIdentifier, streamsConfiguration);
                Runnable runnable = (Runnable) topologyObject;
                runnable.run();
            } catch (InstantiationException x) {
                log.warn(x.getMessage());
                x.printStackTrace();
            } catch (IllegalAccessException x) {
                log.warn(x.getMessage());
                x.printStackTrace();
            } catch (InvocationTargetException x) {
                log.warn(x.getMessage());
                x.printStackTrace();
            } catch (NoSuchMethodException x) {
                log.warn(x.getMessage());
                x.printStackTrace();

                try {
                    Constructor ctor = topologyClass.getDeclaredConstructor(
                            String[].class);
                    ctor.setAccessible(true);
                    Object topologyObject = ctor.newInstance(args);
                    Method main = topologyClass.getMethod("main", String[].class);
                    main.invoke(topologyObject, args);
                } catch (InstantiationException x2) {
                    log.warn(x2.getMessage());
                    x.printStackTrace();
                } catch (IllegalAccessException x2) {
                    log.warn(x2.getMessage());
                    x.printStackTrace();
                } catch (InvocationTargetException x2) {
                    log.warn(x2.getMessage());
                    x.printStackTrace();
                } catch (NoSuchMethodException x2) {
                    log.error(x2.getMessage());
                    x.printStackTrace();
                }
            }
        }
    }
}
