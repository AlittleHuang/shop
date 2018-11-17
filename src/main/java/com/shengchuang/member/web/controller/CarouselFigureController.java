package com.shengchuang.member.web.controller;

import com.shengchuang.member.additional.domain.CarouselFigure;
import com.shengchuang.member.additional.service.CarouselFigureService;
import com.shengchuang.common.mvc.domain.Page;
import com.shengchuang.common.mvc.view.JsonMap;
import com.shengchuang.common.mvc.view.JsonVO;
import com.shengchuang.base.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;

public class CarouselFigureController extends AbstractController {
    @Autowired
    CarouselFigureService carouselFigureService;

    @RequestMapping("/admin/banner/list")
    public View bannerListPage() {
        Page<CarouselFigure> page = carouselFigureService.getPage(getPageRequestMap());
        return new JsonVO(page);
    }

    @RequestMapping("/admin/addCarouselFigure")
    public View addCarouselFigure(CarouselFigure carouselFigure) {
        carouselFigureService.insertSelective(carouselFigure);
        return new JsonMap("操作成功！");
    }

    @RequestMapping("/admin/banner/delete")
    public View deleteBannerById(int id) {
        carouselFigureService.delete(id);
        return new JsonMap("操作成功！");
    }


    @RequestMapping("/admin/banner/edit")
    public View getBannerDetails(int id) {
        CarouselFigure carouselFigure = carouselFigureService.selectOne(id);
        return new JsonVO(carouselFigure);
    }

    @RequestMapping("/admin/banner/update")
    public View update(CarouselFigure carouselFigure) {
        carouselFigureService.updataSelective(carouselFigure);
        return new JsonMap("操作成功!");
    }

    @RequestMapping("/admin/banner/editOper")
    public View changShow(int id) {
        carouselFigureService.changDisplay(id);
        return new JsonMap("操作成功!");
    }
}
