package com.kl.mq.admin.dao;

import com.kl.mq.admin.core.model.KlCommonRegistryData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author xuxueli 2018-11-20
 */
@Mapper
public interface IKlCommonRegistryDataDao {


    public int refresh(@Param("KlCommonRegistryData") KlCommonRegistryData KlCommonRegistryData);

    public int add(@Param("KlCommonRegistryData") KlCommonRegistryData KlCommonRegistryData);


    public List<KlCommonRegistryData> findData(@Param("key") String key);

    public int cleanData(@Param("timeout") int timeout);

    public int deleteDataValue(@Param("key") String key,
                               @Param("value") String value);

    public int count();

}
