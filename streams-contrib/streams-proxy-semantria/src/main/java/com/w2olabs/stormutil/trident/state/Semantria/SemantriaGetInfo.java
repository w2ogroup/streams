package com.w2olabs.stormutil.trident.state.Semantria;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseStateUpdater;
import storm.trident.tuple.TridentTuple;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mdelaet
 * Date: 9/13/13
 * Time: 3:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class SemantriaGetInfo  extends BaseStateUpdater<SemantriaState> {

    private Logger logger = LoggerFactory.getLogger(SemantriaGetInfo.class);

    @Override
    public void updateState(SemantriaState state, List<TridentTuple> tuples, TridentCollector collector){
        try {
            state.bulkProcess(collector, tuples);
        }catch(Exception e){
            logger.debug("Exception caught: " + e);
        }

    }
}
