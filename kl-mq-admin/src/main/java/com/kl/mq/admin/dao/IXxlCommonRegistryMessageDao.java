package com.kl.mq.admin.dao;

import com.kl.mq.admin.core.model.KlCommonRegistryMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author xuxueli 2018-11-20
 */
@Mapper
public interface IKlCommonRegistryMessageDao {

    public int add(@Param("KlCommonRegistryMessage") KlCommonRegistryMessage KlCommonRegistryMessage);

    public List<KlCommonRegistryMessage> findMessage(@Param("excludeIds") List<Integer> excludeIds);

    public int cleanMessage(@Param("messageTimeout") int messageTimeout);

}
