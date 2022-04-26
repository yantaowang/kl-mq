package com.kl.mq.admin.service.impl;

import com.Kl.mq.admin.core.model.KlMqBiz;
import com.Kl.mq.admin.core.result.ReturnT;
import com.Kl.mq.admin.dao.IKlMqBizDao;
import com.Kl.mq.admin.dao.IKlMqTopicDao;
import com.Kl.mq.admin.service.IKlMqBizService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xuxueli 2018-11-20
 */
@Service
public class KlMqBizServiceImpl implements IKlMqBizService {

    @Resource
    private IKlMqBizDao KlMqBizDao;
    @Resource
    private IKlMqTopicDao KlMqTopicDao;


    @Override
    public List<KlMqBiz> findAll() {
        return KlMqBizDao.findAll();
    }

    @Override
    public KlMqBiz load(int id) {
        return KlMqBizDao.load(id);
    }

    @Override
    public ReturnT<String> add(KlMqBiz KlMqBiz) {

        // valid
        if (KlMqBiz.getBizName()==null || KlMqBiz.getBizName().trim().length()==0) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "业务线名称不可为空");
        }
        if (!(KlMqBiz.getBizName().trim().length()>=4 && KlMqBiz.getBizName().trim().length()<=64)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "业务线名称长度非法[2-64]");
        }

        // exist
        List<KlMqBiz> list = findAll();
        if (list != null) {
            for (KlMqBiz item: list) {
                if (item.getBizName().equals(KlMqBiz.getBizName())) {
                    return new ReturnT<>(ReturnT.FAIL_CODE, "业务线名称不可重复");
                }
            }
        }

        int ret = KlMqBizDao.add(KlMqBiz);
        return ret>0?ReturnT.SUCCESS:ReturnT.FAIL;
    }

    @Override
    public ReturnT<String> update(KlMqBiz KlMqBiz) {

        // valid
        if (KlMqBiz.getBizName()==null || KlMqBiz.getBizName().trim().length()==0) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "业务线名称不可为空");
        }
        if (!(KlMqBiz.getBizName().trim().length()>=4 && KlMqBiz.getBizName().trim().length()<=64)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "业务线名称长度非法[2-64]");
        }

        // exist
        List<KlMqBiz> list = findAll();
        if (list != null) {
            for (KlMqBiz item: list) {
                if (item.getId()!=KlMqBiz.getId() && item.getBizName().equals(KlMqBiz.getBizName())) {
                    return new ReturnT<>(ReturnT.FAIL_CODE, "业务线名称不可重复");
                }
            }
        }

        int ret = KlMqBizDao.update(KlMqBiz);
        return ret>0?ReturnT.SUCCESS:ReturnT.FAIL;
    }

    @Override
    public ReturnT<String> delete(int id) {

        // valid limit not use
        int count = KlMqTopicDao.pageListCount(0, 1, id, null);
        if (count > 0) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "禁止删除，该业务线下存在Topic");
        }

        int ret = KlMqBizDao.delete(id);
        return ret>0?ReturnT.SUCCESS:ReturnT.FAIL;
    }

}
