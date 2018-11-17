package com.shengchuang.member.additional.service;

import com.shengchuang.member.additional.domain.MiningMachine;
import com.shengchuang.member.additional.repository.MiningMachineRepository;
import com.shengchuang.common.mvc.pageable.PageRequestMap;
import com.shengchuang.common.mvc.repository.query.Criteria;
import com.shengchuang.common.util.TimeUtil;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.base.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MiningMachineService extends AbstractService<MiningMachine, Integer> {

    @Autowired
    private MiningMachineRepository miningMachineDao;

    /**
     * 分页查询
     *
     * @param user           查询用户,null则查询所有
     * @param pageRequestMap 前段传的查询条件
     * @return
     */
    public Page<MiningMachine> getPage(User user, PageRequestMap pageRequestMap) {
        Criteria<MiningMachine> conditions = toPageConditions(pageRequestMap);
        conditions.addOrderByDesc("createTime");
        Criteria<MiningMachine> criteria = conditions;

        if (user != null) {
            criteria.andEqual("userId", user.getId());
        }
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

    public List<MiningMachine> getMachines(User user) {
        Date startTimeToday = TimeUtil.getStartTimeToday();
        Date date = TimeUtil.addDay(startTimeToday, -1000);
        Criteria<MiningMachine> conditions = createCriteria();
        List<MiningMachine> list = conditions.andEqual("userId", user.getId())
                .andGe("createTime", date).getList();

        return list;
    }

}
