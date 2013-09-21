package com.semantria.trident.state.Semantria;

import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseStateUpdater;
import storm.trident.tuple.TridentTuple;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: mdelaet
 * Date: 9/18/13
 * Time: 9:52 AM
 * To change this template use File | Settings | File Templates.
 */
public class SemantriaBatchAnalyze extends BaseStateUpdater<SemantriaState> {
    private Logger logger = LoggerFactory.getLogger(SemantriaBatchAnalyze.class);
    @Override
    public void updateState(SemantriaState state, List<TridentTuple> tuples, TridentCollector collector) {
        try {
            logger.debug("calling updateState on " + state + " about to call batchAnalyze on the tuples" );
            state.batchAnalyze(tuples);
        } catch (Exception e) {
            logger.info("Exception caught in SemantriaBatchAnalyze.", e);
        }
    }
}
