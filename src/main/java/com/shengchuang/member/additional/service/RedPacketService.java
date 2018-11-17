package com.shengchuang.member.additional.service;

import com.shengchuang.member.additional.domain.RedPacket;
import com.shengchuang.common.mvc.domain.Page;
import com.shengchuang.common.mvc.pageable.PageRequestMap;
import com.shengchuang.common.mvc.repository.query.Criteria;
import com.shengchuang.common.util.TimeUtil;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.member.core.service.UserService;
import com.shengchuang.base.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
public class RedPacketService extends AbstractService<RedPacket, Integer> {
    @Autowired
    private UserService userService;

    /**
     * 分页查询
     *
     * @param user           查询用户,null则查询所有
     * @param pageRequestMap 前段传的查询条件
     * @return
     */
    public Page<RedPacket> getPage(User user, PageRequestMap pageRequestMap) {
        Criteria<RedPacket> conditions = toPageConditions(pageRequestMap);
        conditions.addOrderByDesc("createTime", "updateTime");
        Criteria<RedPacket> criteria = conditions;

        userService.addUserFilter(user, pageRequestMap, criteria);

        Date startTime = pageRequestMap.getDateValue("startTime");
        Date endTime = pageRequestMap.getDateValue("endTime");
        if (startTime != null) {
            criteria.andGe("createTime", startTime, Date.class);
        }
        if (endTime != null) {
            endTime = TimeUtil.addDay(endTime, 1);
            criteria.andLt("createTime", endTime, Date.class);
        }

        Date date = pageRequestMap.getDateValue("date");
        if (date != null) {
            criteria.andBetween("createTime", TimeUtil.getStartTimeOfDate(date), TimeUtil.getOverTimeOfDate(date));
        }

        String in = pageRequestMap.get("in");
        String out = pageRequestMap.get("out");
        if (in != null) {
            criteria.andGt("amount", 0);
        }
        if (out != null) {
            criteria.andLt("amount", 0);
        }
        return getPage(conditions);
    }

    /**
     * status 是否领取
     * 获取个人当天红包
     */
    public List<RedPacket> redPacketListByDay(User user, boolean status) {
        Criteria<RedPacket> conditions = createCriteria();
        Criteria<RedPacket> criteria = conditions;
        criteria.andEqual("userId", user.getId())
                .andBetween("createTime", TimeUtil.getStartTimeToday(), TimeUtil.getOverTimeToday());
        if (status) { //领取
            criteria.andEqual("status", RedPacket.STATUS_YES);
        } else {
            criteria.andEqual("status", RedPacket.STATUS_NO);
        }
        List<RedPacket> redPackets = conditions.getList();
        return redPackets;
    }


}
