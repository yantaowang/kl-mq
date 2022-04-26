package com.kl.mq.admin.controller;

import com.Kl.mq.admin.core.result.ReturnT;
import com.Kl.mq.admin.service.IKlMqMessageService;
import com.Kl.mq.client.message.KlMqMessage;
import com.Kl.mq.client.message.KlMqMessageStatus;
import com.Kl.mq.client.util.DateUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

/**
 * Base 
 * @author xuxueli 2016-3-19 13:56:28
 */
@Controller
@RequestMapping("/message")
public class MessageController {

	@Resource
	private IKlMqMessageService KlMqMessageService;

	@RequestMapping("")
	public String index(Model model, String topic){

		model.addAttribute("status", KlMqMessageStatus.values());
		model.addAttribute("topic", topic);

		return "message/message.index";
	}
	
	@RequestMapping("/pageList")
	@ResponseBody
	public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,
										@RequestParam(required = false, defaultValue = "10") int length,
										String topic,
										String status,
										String filterTime){

		// parse param
		Date addTimeStart = null;
		Date addTimeEnd = null;
		if (filterTime!=null && filterTime.trim().length()>0) {
			String[] temp = filterTime.split(" - ");
			if (temp!=null && temp.length == 2) {
				addTimeStart = DateUtil.parseDateTime(temp[0]);
				addTimeEnd = DateUtil.parseDateTime(temp[1]);
			}
		}

		return KlMqMessageService.pageList(start, length, topic, status, addTimeStart, addTimeEnd);
	}
	
	@RequestMapping("/delete")
	@ResponseBody
	public ReturnT<String> delete(int id){
		return KlMqMessageService.delete(id);
	}

	@RequestMapping("/update")
	@ResponseBody
	public ReturnT<String> update(long id,
                                  String topic,
                                  String group,
                                  String data,
                                  String status,
                                  @RequestParam(required = false, defaultValue = "0") int retryCount,
                                  @RequestParam(required = false, defaultValue = "0") long shardingId,
                                  @RequestParam(required = false, defaultValue = "0") int timeout,
                                  String effectTime){

		// effectTime
		Date effectTimeObj = null;
		if (effectTime!=null && effectTime.trim().length()>0) {
			effectTimeObj = DateUtil.parseDateTime(effectTime);
			if (effectTimeObj == null) {
				return new ReturnT<String>(ReturnT.FAIL_CODE, "生效时间格式非法");
			}
		}

        // message
        KlMqMessage message = new KlMqMessage();
        message.setId(id);
        message.setTopic(topic);
        message.setGroup(group);
        message.setData(data);
        message.setStatus(status);
        message.setRetryCount(retryCount);
        message.setShardingId(shardingId);
        message.setTimeout(timeout);
        message.setEffectTime(effectTimeObj);

		return KlMqMessageService.update(message);
	}

	@RequestMapping("/add")
	@ResponseBody
	public ReturnT<String> add(String topic,
                               String group,
                               String data,
                               String status,
                               @RequestParam(required = false, defaultValue = "0") int retryCount,
                               @RequestParam(required = false, defaultValue = "0") long shardingId,
                               @RequestParam(required = false, defaultValue = "0") int timeout,
                               String effectTime){

        // effectTime
        Date effectTimeObj = null;
		if (effectTime!=null && effectTime.trim().length()>0) {
			effectTimeObj = DateUtil.parseDateTime(effectTime);
			if (effectTimeObj == null) {
				return new ReturnT<String>(ReturnT.FAIL_CODE, "生效时间格式非法");
			}
		}

        // message
        KlMqMessage message = new KlMqMessage();
        message.setTopic(topic);
        message.setGroup(group);
        message.setData(data);
        message.setStatus(status);
        message.setRetryCount(retryCount);
        message.setShardingId(shardingId);
        message.setTimeout(timeout);
        message.setEffectTime(effectTimeObj);

		return KlMqMessageService.add(message);
	}

    @RequestMapping("/clearMessage")
    @ResponseBody
    public ReturnT<String> clearMessage(String topic, String status, int type){
	    return KlMqMessageService.clearMessage(topic, status, type);
    }

}
