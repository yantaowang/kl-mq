package com.kl.mq.admin.controller;

import com.Kl.mq.admin.core.model.KlMqBiz;
import com.Kl.mq.admin.core.model.KlMqTopic;
import com.Kl.mq.admin.core.result.ReturnT;
import com.Kl.mq.admin.service.IKlMqBizService;
import com.Kl.mq.admin.service.IKlMqTopicService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author xuxueli 2018-11-21
 */
@Controller
@RequestMapping("/topic")
public class TopicController {

    @Resource
    private IKlMqTopicService KlMqTopicService;
    @Resource
    private IKlMqBizService KlMqBizService;


    @RequestMapping("")
    public String index(Model model){

        List<KlMqBiz> bizList = KlMqBizService.findAll();
        model.addAttribute("bizList", bizList);

        return "topic/topic.index";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,
                                        @RequestParam(required = false, defaultValue = "10") int length,
                                        int bizId,
                                        String topic){
        return KlMqTopicService.pageList(start, length, bizId, topic);
    }

    @RequestMapping("/delete")
    @ResponseBody
    public ReturnT<String> delete(String topic){
        return KlMqTopicService.delete(topic);
    }

    @RequestMapping("/update")
    @ResponseBody
    public ReturnT<String> update(KlMqTopic KlMqTopic){
        return KlMqTopicService.update(KlMqTopic);
    }

    @RequestMapping("/add")
    @ResponseBody
    public ReturnT<String> add(KlMqTopic KlMqTopic){
        return KlMqTopicService.add(KlMqTopic);
    }

}
