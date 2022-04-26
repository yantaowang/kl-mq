package com.kl.mq.admin.service;

import com.Kl.mq.admin.core.result.ReturnT;
import com.Kl.mq.client.message.KlMqMessage;

import java.util.Date;
import java.util.Map;

/**
 * Created by xuxueli on 16/8/28.
 */
public interface IKlMqMessageService {

    public Map<String,Object> pageList(int offset, int pagesize, String topic, String status, Date addTimeStart, Date addTimeEnd);

    public ReturnT<String> delete(int id);

    public ReturnT<String> update(KlMqMessage message);

    public ReturnT<String> add(KlMqMessage message);

    public Map<String,Object> dashboardInfo();

    public ReturnT<Map<String,Object>> chartInfo(Date startDate, Date endDate);

    public ReturnT<String> clearMessage(String topic, String status, int type);

}