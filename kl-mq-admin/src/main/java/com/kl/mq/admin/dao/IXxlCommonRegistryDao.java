package com.kl.mq.admin.dao;

import com.kl.mq.admin.core.model.KlCommonRegistry;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author xuxueli 2018-11-20
 */
@Mapper
public interface IKlCommonRegistryDao {

    public List<KlCommonRegistry> pageList(@Param("offset") int offset, @Param("pagesize") int pagesize);

    public KlCommonRegistry load(@Param("key") String key);

    public int add(@Param("KlCommonRegistry") KlCommonRegistry KlCommonRegistry);

    public int update(@Param("KlCommonRegistry") KlCommonRegistry KlCommonRegistry);

    public int cleanDead();

}
