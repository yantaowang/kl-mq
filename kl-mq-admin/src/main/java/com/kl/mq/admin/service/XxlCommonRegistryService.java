package com.kl.mq.admin.service;

import com.Kl.mq.admin.core.model.KlCommonRegistryData;
import com.Kl.mq.admin.core.result.ReturnT;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;
import java.util.Map;

/**
 * common regsitry, borrowed from "Kl-registry"
 *
 * @author xuxueli 2018-11-26
 */
public interface KlCommonRegistryService {

    /**
     * refresh registry-value, check update and broacase
     */
    ReturnT<String> registry(String accessToken, List<KlCommonRegistryData> KlCommonRegistryDataList);

    /**
     * remove registry-value, check update and broacase
     */
    ReturnT<String> remove(String accessToken, List<KlCommonRegistryData> KlCommonRegistryDataList);

    /**
     * discovery registry-data, read file
     */
    ReturnT<Map<String, List<String>>> discovery(String accessToken, List<String> keys);

    /**
     * monitor update
     */
    DeferredResult<ReturnT<String>> monitor(String accessToken, List<String> keys);

}
