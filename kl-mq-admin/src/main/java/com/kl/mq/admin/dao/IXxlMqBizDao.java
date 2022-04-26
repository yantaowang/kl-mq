package com.kl.mq.admin.dao;

import com.kl.mq.admin.core.model.KlMqBiz;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author xuxueli 2018-11-20
 */
@Mapper
public interface IKlMqBizDao {

    public List<KlMqBiz> findAll();

    public KlMqBiz load(@Param("id") int id);

    public int add(KlMqBiz KlMqBiz);

    public int update(@Param("KlMqBiz") KlMqBiz KlMqBiz);

    public int delete(@Param("id") int id);

}
