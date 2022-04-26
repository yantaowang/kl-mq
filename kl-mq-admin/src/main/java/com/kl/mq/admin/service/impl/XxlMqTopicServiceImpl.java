package com.kl.mq.admin.service.impl;

import com.Kl.mq.admin.core.model.KlMqTopic;
import com.Kl.mq.admin.core.result.ReturnT;
import com.Kl.mq.admin.dao.IKlMqMessageDao;
import com.Kl.mq.admin.dao.IKlMqTopicDao;
import com.Kl.mq.admin.service.IKlMqTopicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xuxueli 2016-5-28 15:30:33
 */
@Service
public class KlMqTopicServiceImpl implements IKlMqTopicService {
    private static Logger logger = LoggerFactory.getLogger(KlMqTopicServiceImpl.class);


    @Resource
    private IKlMqTopicDao KlMqTopicDao;
    @Resource
    private IKlMqMessageDao KlMqMessageDao;


    @Override
    public Map<String, Object> pageList(int start, int length, int bizId, String topic) {
        // page list
        List<KlMqTopic> list = KlMqTopicDao.pageList(start, length, bizId, topic);
        int list_count = KlMqTopicDao.pageListCount(start, length, bizId, topic);

        // package result
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", list_count);		// 总记录数
        maps.put("recordsFiltered", list_count);	// 过滤后的总记录数
        maps.put("data", list);  					// 分页列表
        return maps;
    }

    @Override
    public KlMqTopic load(String topic) {
        return KlMqTopicDao.load(topic);
    }

    @Override
    public ReturnT<String> add(KlMqTopic KlMqTopic) {

        // valid
        if (KlMqTopic.getTopic()==null || KlMqTopic.getTopic().trim().length()==0) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "消息主题不可为空");
        }
        if (!(KlMqTopic.getTopic().length()>=4 && KlMqTopic.getTopic().length()<=255)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "消息主题长度非法[4~255]");
        }

        // exist
        KlMqTopic exist = KlMqTopicDao.load(KlMqTopic.getTopic());
        if (exist != null) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "消息主题不可重复");
        }


        int ret = KlMqTopicDao.add(KlMqTopic);
        return ret>0?ReturnT.SUCCESS:ReturnT.FAIL;
    }


    @Override
    public ReturnT<String> update(KlMqTopic KlMqTopic) {

        // valid
        if (KlMqTopic.getTopic()==null || KlMqTopic.getTopic().trim().length()==0) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "消息主题不可为空");
        }
        if (!(KlMqTopic.getTopic().length()>=4 && KlMqTopic.getTopic().length()<=255)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "消息主题长度非法[4~255]");
        }

        int ret = KlMqTopicDao.update(KlMqTopic);
        return ret>0?ReturnT.SUCCESS:ReturnT.FAIL;
    }

    @Override
    public ReturnT<String> delete(String topic) {

        // valid, limit use
        int count = KlMqMessageDao.pageListCount(0, 1, topic, null, null, null);
        if (count > 0) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "禁止删除，该Topic下存在消息");
        }


        int ret = KlMqTopicDao.delete(topic);
        return ret>0?ReturnT.SUCCESS:ReturnT.FAIL;
    }

}
