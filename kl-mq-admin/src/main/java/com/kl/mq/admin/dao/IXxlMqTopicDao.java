package com.kl.mq.admin.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author xuxueli 2018-11-20
 */
@Mapper
public interface IKlMqTopicDao {

    public List<KlMqTopic> pageList(@Param("offset") int offset,
                                       @Param("pagesize") int pagesize,
                                       @Param("bizId") int bizId,
                                       @Param("topic") String topic);
    public int pageListCount(@Param("offset") int offset,
                             @Param("pagesize") int pagesize,
                             @Param("bizId") int bizId,
                             @Param("topic") String topic);

    public KlMqTopic load(@Param("topic") String topic);

    public int add(@Param("KlMqTopic") KlMqTopic KlMqTopic);

    public int update(@Param("KlMqTopic") KlMqTopic KlMqTopic);

    public int delete(@Param("topic") String topic);

    public List<KlMqTopic> findAlarmByTopic(@Param("topics") List<String> topics);

}
