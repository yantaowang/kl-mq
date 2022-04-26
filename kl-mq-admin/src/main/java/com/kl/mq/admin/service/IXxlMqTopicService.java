package com.kl.mq.admin.service;

import com.Kl.mq.admin.core.model.KlMqTopic;
import com.Kl.mq.admin.core.result.ReturnT;

import java.util.Map;

/**
 * @author xuxueli 2016-5-28 15:30:33
 */
public interface IKlMqTopicService {

    public Map<String, Object> pageList(int start, int length, int bizId, String topic);

    public KlMqTopic load(String topic);

    public ReturnT<String> add(KlMqTopic KlMqTopic);

    public ReturnT<String> update(KlMqTopic KlMqTopic);

    public ReturnT<String> delete(String topic);


}
