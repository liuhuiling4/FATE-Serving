package com.webank.ai.fate.serving.federatedml.model;

import com.webank.ai.fate.core.bean.ReturnResult;
import com.webank.ai.fate.serving.core.bean.Context;
import com.webank.ai.fate.serving.core.bean.Dict;
import com.webank.ai.fate.serving.core.bean.FederatedParams;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.exp;

public class HeteroLRGuest extends HeteroLR {
    private static final Logger LOGGER = LogManager.getLogger();

    private double sigmod(double x) {
        return 1. / (1. + exp(-x));
    }

    @Override
    public Map<String, Object> handlePredict(Context context , List<Map<String, Object>> inputData, FederatedParams predictParams) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Double> forwardRet = forward(inputData);
        double score = forwardRet.get(Dict.SCORE);

        LOGGER.info("guest score:{}", score);

        try {
            ReturnResult hostPredictResponse = this.getFederatedPredict(context,predictParams,Dict.FEDERATED_INFERENCE,true);
            //predictParams.put("federatedResult", hostPredictResponse);
            //context.setFederatedResult(hostPredictResponse);
            LOGGER.info("host response is {}", hostPredictResponse.getData());
            double hostScore = (double) hostPredictResponse.getData().get(Dict.SCORE);
            LOGGER.info("host score:{}", hostScore);
            score += hostScore;
        } catch (Exception ex) {
            LOGGER.error("get host predict failed:", ex);
        }

        double prob = sigmod(score);
        result.put(Dict.PROB, prob);
        result.put(Dict.GUEST_MODEL_WEIGHT_HIT_RATE + ":{}", forwardRet.get(Dict.MODEL_WRIGHT_HIT_RATE));
        result.put(Dict.GUEST_INPUT_DATA_HIT_RATE + ":{}", forwardRet.get(Dict.INPUT_DATA_HIT_RATE));

        return result;
    }
}
