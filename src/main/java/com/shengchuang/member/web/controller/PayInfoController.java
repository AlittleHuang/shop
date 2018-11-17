package com.shengchuang.member.web.controller;

import com.shengchuang.common.mvc.domain.Page;
import com.shengchuang.common.mvc.view.JsonMap;
import com.shengchuang.common.mvc.view.JsonVO;
import com.shengchuang.common.util.Assert;
import com.shengchuang.member.core.domain.PayInfo;
import com.shengchuang.member.core.service.PayInfoService;
import com.shengchuang.base.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.View;

import java.util.List;


@RestController
public class PayInfoController extends AbstractController {

    @Autowired
    private PayInfoService payInfoService;

    @PostMapping("/front/pay/info/add")
    public View add(PayInfo payInfo) {
        Integer userId = getSessionUser().getId();
        payInfo.setUserId(userId);
        payInfoService.update(payInfo);
        return new JsonMap("添加成功");
    }


    @PostMapping("/front/pay/info/addOrUpdate")
    public View addOrUpdate(PayInfo payInfo) {
        checkSecondPwd();
        Integer userId = getSessionUser().getId();
        payInfo.setUserId(userId);
        payInfoService.update(payInfo);
        return new JsonMap("保存成功");
    }

    @PostMapping("/front/pay/info/update")
    public View update(PayInfo payinfo) {
        Assert.notNull(payinfo.getId(), "id错误");
        PayInfo one = payInfoService.getOne(payinfo.getId());
        Assert.notNull(one, "id错误");
        Assert.state(one.getUserId().equals(getSessionUser().getId()), "id错误");
        payinfo.setStatus(one.getStatus());
        payinfo.setUserId(one.getUserId());
        payInfoService.update(payinfo);
        return new JsonMap("保存成功");
    }

    /**
     * 根据用户id获取支付方式
     *
     * @return
     */
    @RequestMapping("/front/getPat")
    public View getPayByUserId() {
        Integer userId = getSessionUser().getId();
        List<PayInfo> payList = payInfoService.createCriteria()
                .andEqual("userId", userId).andEqual("status", 0).getList();

    	/*Set<Integer> set=new HashSet<>();
    	set.add(userId);
        List<PayInfo> payList=payInfoService.findByUserInIn(set);*/
        return new JsonMap().add("content", payList);
    }

    @RequestMapping("/front/pay/info")
    public View getOne(Integer id) {
        JsonMap fiel = new JsonMap().success(false);
        if (id == null) return fiel;
        PayInfo one = payInfoService.getOne(id);
        if (one == null) return fiel;
        if (one.getUserId().equals(getSessionUser().getId()))
            return new JsonVO(one);
        return fiel;
    }


    @RequestMapping("/front/pay/info/alipay")
    public View alypay() {
        return getBytype("ALIPAY");
    }

    @GetMapping("/front/pay/info/weixinpay")
    public View weixinpay() {
        return getBytype("WEIXINPAY");
    }


    private View getBytype(String type) {
        JsonMap fiel = new JsonMap().success(false);
        //  List<PayInfo> one = payInfoService.findByUserIdAndType(getSessionUser().getId(), type);
        List<PayInfo> one =
                payInfoService.findByUserIdAndTypeAndStatusNot(getSessionUser().getId(), type, PayInfo.DELETE);
        if (one.isEmpty()) return fiel;
        if (one.get(0).getUserId().equals(getSessionUser().getId()))
            return new JsonMap().add("content", one);
        return fiel;
    }

    @RequestMapping("/front/pay/info/list")
    public View list(PayInfo contact) {
        List<PayInfo> list = payInfoService.createCriteria()
                .andEqual("userId", getSessionUser().getId())
                .andNotIn("type", PayInfoService.NOT_BANK_TYPE)
                .andNotEqual("status", PayInfo.DELETE)
                .addOrderByDesc("order")
                .addOrderByAsc("id")
                .getList();
        return new JsonMap().add("content", list);
    }

    @RequestMapping("/pc/pay/info/list")
    public View listPc() {
        Page<PayInfo> page = payInfoService.getPage(getSessionUser(), getPageRequestMap());
        return new JsonVO(page);
    }

    @RequestMapping("/front/pay/info/setdefault")
    public View setDefault(Integer id) {
        Assert.notNull(id, "id错误");
        PayInfo payInfo = payInfoService.getOne(id);
        Assert.notNull(payInfo, "id不存在");
        Assert.notNull(payInfo.getUserId().equals(getSessionUser().getId()), "id错误");
        payInfo.setOrder(System.currentTimeMillis());
        payInfoService.save(payInfo);
        return new JsonMap();
    }

    @PostMapping("/font/pay/info/alipay/update")
    public View updateAlipay(PayInfo payInfo) {
        payInfo.setUserId(getSessionUser().getId());
        payInfo.setType(PayInfoService.NOT_BANK_TYPE.get(1));
        payInfoService.update(payInfo);
        return new JsonMap("保存成功");
    }

    @PostMapping("/font/pay/info/weixinpay/update")
    public View updateWeixinpay(PayInfo payInfo) {
        payInfo.setUserId(getSessionUser().getId());
        payInfo.setType(PayInfoService.NOT_BANK_TYPE.get(0));
        payInfoService.update(payInfo);
        return new JsonMap("保存成功");
    }

    /**
     * 刪除支付信息
     *
     * @param id
     * @return
     */
    @PostMapping("/front/pay/delete")
    public View deletePay(Integer id) {
        Assert.notNull(id, "id错误");
        PayInfo payInfo = payInfoService.getOne(id);
        Assert.notNull(payInfo, "id不存在");
        Assert.notNull(payInfo.getUserId().equals(getSessionUser().getId()), "id错误");
        payInfo.setStatus(PayInfo.DELETE);
        payInfoService.save(payInfo);
        //  payInfoService.delete(payInfo);
        return new JsonMap("刪除成功");
    }

}
