package com.kl.mq.admin.controller;

import com.Kl.mq.admin.core.model.KlMqBiz;
import com.Kl.mq.admin.core.result.ReturnT;
import com.Kl.mq.admin.service.IKlMqBizService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xuxueli 2018-11-20
 */
@Controller
@RequestMapping("/biz")
public class BizController {

    @Resource
    private IKlMqBizService KlMqBizService;

    @RequestMapping("")
    public String index(Model model){

        List<KlMqBiz> bizList = KlMqBizService.findAll();
        model.addAttribute("bizList", bizList);

        return "biz/biz.index";
    }

    @RequestMapping("/save")
    @ResponseBody
    public ReturnT<String> save(KlMqBiz KlMqBiz){
        return KlMqBizService.add(KlMqBiz);
    }

    @RequestMapping("/update")
    @ResponseBody
    public ReturnT<String> update(KlMqBiz KlMqBiz){
        return KlMqBizService.update(KlMqBiz);
    }


    @RequestMapping("/remove")
    @ResponseBody
    public ReturnT<String> remove(int id){
        return KlMqBizService.delete(id);
    }

}
