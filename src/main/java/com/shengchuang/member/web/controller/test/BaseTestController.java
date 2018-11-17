package com.shengchuang.member.web.controller.test;

import com.shengchuang.common.util.Assert;
import com.shengchuang.base.AbstractController;
import com.shengchuang.member.web.controller.TransferController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BaseTestController extends AbstractController {

    @Value("${profiles}")
    protected String profiles;

    @Autowired
    TransferController transferController;

    @ModelAttribute
    private void modelAttributes(HttpServletRequest request, HttpServletResponse response) {
        Assert.state(profiles != "prd", "不支持测试");
    }

}
