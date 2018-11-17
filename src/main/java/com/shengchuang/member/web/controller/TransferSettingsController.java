package com.shengchuang.member.web.controller;

import com.shengchuang.common.mvc.view.JsonMap;
import com.shengchuang.common.mvc.view.JsonVO;
import com.shengchuang.common.util.Assert;
import com.shengchuang.member.core.domain.TransferSetting;
import com.shengchuang.member.core.service.TransferSettingsService;
import com.shengchuang.base.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.View;

@Controller
public class TransferSettingsController extends AbstractController {

    @Autowired
    private TransferSettingsService transferSettingsService;

    @GetMapping({"/admin/transfer/settings", "/front/transfer/settings"})
    public View list(TransferSetting transferSetting) {
        return new JsonVO(transferSettingsService.createCriteria().andEqual(transferSetting).getList());
    }

    @PostMapping("/admin/transfer/setting/save")
    public View save(TransferSetting transferSetting) {
        if (transferSetting.getId() == null)
            transferSetting = transferSettingsService.addTransferSetting(transferSetting);
        else {
            TransferSetting one = transferSettingsService.getOne(transferSetting.getId());
            Assert.notNull(one, "参数错误");
            transferSetting = transferSettingsService.saveSelective(transferSetting);
        }
        return new JsonMap("保存成功").add("content", transferSetting);
    }

    @PostMapping("/admin/transfer/setting/delete")
    public View delete(Integer id) {
        transferSettingsService.deleteById(id);
        return new JsonMap("删除成功");
    }

}
