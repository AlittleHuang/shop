package com.shengchuang.member.additional.service;

import com.shengchuang.member.additional.domain.CarouselFigure;
import com.shengchuang.common.mvc.domain.Page;
import com.shengchuang.common.mvc.pageable.PageRequestMap;
import com.shengchuang.common.mvc.repository.query.Criteria;
import com.shengchuang.common.util.TimeUtil;
import com.shengchuang.base.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class CarouselFigureService extends AbstractService<CarouselFigure, Integer> {

    /**
     * @param pageRequestMap
     * @return
     * @Update fw
     */
    public Page<CarouselFigure> getPage(PageRequestMap pageRequestMap) {
        Criteria<CarouselFigure> conditions = toPageConditions(pageRequestMap).addOrderByAsc("orderNumber");

        //开始时间查询条件
        Date startTime = pageRequestMap.getDateValue("startTime");
        //结束时间查询条件
        Date endTime = pageRequestMap.getDateValue("endTime");
        if (startTime != null && !"".equals(startTime)) {
            conditions.andGe("createTime", startTime, Date.class);
        }
        if (endTime != null && !"".equals(endTime)) {
            endTime = TimeUtil.addDay(endTime, 1);
            conditions.andLt("createTime", endTime, Date.class);
        }
        //时间查询条件
        Date date = pageRequestMap.getDateValue("date");
        if (date != null) {
            conditions
                    .andBetween("createTime", TimeUtil.getStartTimeOfDate(date), TimeUtil.getOverTimeOfDate(date));
        }
        Page<CarouselFigure> page = getPage(conditions, pageRequestMap);
        return page;
    }


    @Transactional
    public void delete(int id) {
        delete(id);
    }


    public void changDisplay(int id) {
        CarouselFigure carouselFigure = getOne(id);
        Integer display = carouselFigure.getDisplay();
        display = display == null ? 0 : display;
        carouselFigure.setDisplay((display + 1) % 2);
        saveSelective(carouselFigure);
    }


    public void insertSelective(CarouselFigure carouselFigure) {
        carouselFigure.setDisplay(1);
        carouselFigure.setCreateTime(new Date());
        insertSelective(carouselFigure);

    }


    public CarouselFigure selectOne(int id) {
        CarouselFigure carouselFigure = getOne(id);
        return carouselFigure;
    }


    public void updataSelective(CarouselFigure carouselFigure) {
        carouselFigure.setUpdateTime(new Date());
        saveSelective(carouselFigure);
    }


    public Page<CarouselFigure> getShow() {
        Criteria<CarouselFigure> criteria = createCriteria();
        criteria.andEqual("display", 1);
        criteria.addOrderByAsc("orderNumber");
        Page<CarouselFigure> page = criteria.getFixPage();
        return page;
    }

}
