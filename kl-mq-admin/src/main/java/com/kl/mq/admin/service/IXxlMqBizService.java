package com.kl.mq.admin.service;

import com.Kl.mq.admin.core.model.KlMqBiz;
import com.Kl.mq.admin.core.result.ReturnT;

import java.util.List;

/**
 * @author xuxueli 2018-11-20
 */
public interface IKlMqBizService {

    public List<KlMqBiz> findAll();

    public KlMqBiz load(int id);

    public ReturnT<String> add(KlMqBiz KlMqBiz);

    public ReturnT<String> update(KlMqBiz KlMqBiz);

    public ReturnT<String> delete(int id);

}
