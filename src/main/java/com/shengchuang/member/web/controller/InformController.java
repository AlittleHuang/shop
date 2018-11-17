package com.shengchuang.member.web.controller;

import com.shengchuang.member.additional.domain.Inform;
import com.shengchuang.member.additional.service.InformService;
import com.shengchuang.common.mvc.pageable.PageRequestMap;
import com.shengchuang.common.mvc.view.JsonMap;
import com.shengchuang.common.mvc.view.JsonVO;
import com.shengchuang.common.util.Assert;
import com.shengchuang.base.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.View;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class InformController extends AbstractController {

    @Autowired
    private InformService informService;

    @RequestMapping("/admin/inform/save")
    public View save(Inform inform) {
       // String reg = "(?i)<img[^>]*>";
       // Pattern p = Pattern.compile(reg);
      //  Matcher m = p.matcher(inform.getHtml());
     //   Assert.state(m.find(), "添加失败，请添加公告内容图片");
        inform.setTime(dateTimeParameter("time"));
        informService.update(inform);
        return new JsonMap("保存成功");
    }

    @RequestMapping("/admin/inform/add")
    public View add(Inform inform) {
        inform.setTime(dateTimeParameter("time"));
        informService.update(inform);
        return new JsonMap("添加成功");
    }

    @RequestMapping("/admin/inform/delete")
    public View delete(Integer id) {
        informService.deleteById(id);
        return new JsonMap("删除成功");
    }

    @RequestMapping({"/admin/inform/list", "/front/inform/list"})
    public View list() {
        PageRequestMap pageRequestMap = getPageRequestMap();
        Page<Inform> page = informService.getPage(pageRequestMap);
        List<Inform> content = page.getContent();
        String reg = "(?i)<img[^>]*>";
        Pattern p = Pattern.compile(reg);
        for (int i = 0; i < content.size(); i++) {
            Inform inform = content.get(i);
            Matcher m = p.matcher(inform.getHtml());
            if (m.find()) {
                inform.setUrl(m.group());
            }
        }
        return new JsonVO(page);
    }

    @RequestMapping({"/admin/inform", "/front/inform"})
    public View getOne() {
        Integer id = intParameter("id");
        Assert.notNull(id, "缺少参数id");
        Inform inform = informService.getOne(id);
        Assert.notNull(inform, "id错误");
        Inform upInform = informService.createCriteria().andGt("id", id).addOrderByAsc("id").limit(1).getOne();
        Inform downInform = informService.createCriteria().andLt("id", id).addOrderByDesc("id").limit(1).getOne();
        Integer upId = 0;
        Integer downId = 0;
        String upTitle = "";
        String downTitle = "";
        if (upInform != null) {
            upId = upInform.getId();
            upTitle = upInform.getTitle();
        }
        if (downInform != null) {
            downId = downInform.getId();
            downTitle = downInform.getTitle();
        }
        String reg = "(?i)<img[^>]*>";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(inform.getHtml());
        if (m.find()) {
            inform.setUrl(m.group());
        }
        inform.setUpId(upId);
        inform.setUpTitle(upTitle);
        inform.setDownId(downId);
        inform.setDownTitle(downTitle);
        return new JsonVO(inform);
    }

    @RequestMapping("/front/inform/pre")
    public View pre(Integer id) {
        Inform pre = informService.getPre(id);
        JsonMap jsonMap = new JsonMap();
        if (pre != null) {
            return jsonMap.add("id", pre.getId());
        }
        return jsonMap.success(false);
    }


    @RequestMapping("/front/inform/next")
    public View next(Integer id) {
        Inform next = informService.getNext(id);
        JsonMap jsonMap = new JsonMap();
        if (next != null) {
            return jsonMap.add("id", next.getId());
        }
        return jsonMap.success(false);
    }

    @RequestMapping("/front/inform/near")
    public View near(Integer id) {
        Inform next = informService.getNext(id);
        Inform pre = informService.getPre(id);
        JsonMap jsonMap = new JsonMap();
        if (next != null) {
            jsonMap.add("next", next.getId());
        }
        if (pre != null) {
            jsonMap.add("pre", pre.getId());
        }
        return jsonMap;
    }

    @RequestMapping("/front/inform/latest")
    public View latest() {
        JsonMap jsonMap = new JsonMap();
        Inform inform = informService.createCriteria().addOrderByDesc("time").limit(1).getOne();
        if (inform != null) jsonMap.add("content", inform);
        return jsonMap;
    }

}
