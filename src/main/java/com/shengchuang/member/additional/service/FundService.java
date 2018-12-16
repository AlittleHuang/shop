package com.shengchuang.member.additional.service;

import com.shengchuang.member.additional.domain.Fund;
import com.shengchuang.common.mvc.pageable.PageRequestMap;
import com.shengchuang.common.mvc.repository.query.Criteria;
import com.shengchuang.common.util.Assert;
import com.shengchuang.common.util.TimeUtil;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.member.core.service.UserService;
import com.shengchuang.base.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FundService extends AbstractService<Fund, Integer> {

    @Autowired
    private UserService userService;

    public Page<Fund> getPage(int userId, PageRequestMap pageRequestMap) {
        Criteria<Fund> conditions = toPageConditions(pageRequestMap)
                .addOrderByDesc("createTime").and().andEqual("userId", userId);
        Page<Fund> page = getPage(conditions, pageRequestMap);
        return page;
    }

    /**
     * @param pageRequestMap
     * @return
     * @Update fw
     */
    public Page<Fund> getPage(PageRequestMap pageRequestMap) {
        Criteria<Fund> conditions = toPageConditions(pageRequestMap).addOrderByDesc("createTime");
        //用户名查询条件
        String username = pageRequestMap.get("username");
        if (isNotBlank(username)) {
            Integer userId = userService.findUserIdByUsername(username);
            if (userId != null) {
                conditions.andEqual("userId", userId);
            } else {
                conditions.setPageResultEmpty();
            }
        }
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
        //币种条件类型
        if (pageRequestMap.getInteger("type") != null && !"".equals(pageRequestMap.getIntValue("type"))) {
            int type = pageRequestMap.getIntValue("type");
            conditions.andEqual("type", type);
        }
        //时间查询条件
        Date date = pageRequestMap.getDateValue("date");
        if (date != null) {
            conditions
                    .andBetween("createTime", TimeUtil.getStartTimeOfDate(date), TimeUtil.getOverTimeOfDate(date));
        }
        Page<Fund> page = getPage(conditions, pageRequestMap);
        return page;
    }

    public void loadUsername(List<Fund> content) {
        if (content == null)
            return;
        for (Fund fund : content) {
            User user = userService.getOne(fund.getUserId());
        }
    }

    public void agree(Integer id) {
        audit(id, true);
    }

    public void disagree(Integer id) {
        audit(id, false);
    }

    /**
     * 审核
     *
     * @param id
     * @param status true通过,false 不通过
     */
    public void audit(Integer id, boolean status) {
        Optional<Fund> optional = findById(id);
        Assert.state(optional.isPresent(), "记录不存在");
        Fund fund = optional.get();
        Assert.state(Fund.STATUS_0 == fund.getStatus(), "已审核过");
        fund.setStatus(status ? Fund.STATUS_1 : Fund.STATUS_2);
        if (status) {
            fund.setUpdateTime(new Date());
        }
        save(fund);
    }

    /**
     * 添加资产记录
     *
     * @param fund
     */
    public void add(Fund fund) {
        fund.setStatus(Fund.STATUS_0);
        fund.setCreateTime(new Date());
        save(fund);
    }

}
