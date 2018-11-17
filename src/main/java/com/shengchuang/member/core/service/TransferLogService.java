package com.shengchuang.member.core.service;

import com.shengchuang.common.mvc.pageable.PageRequestMap;
import com.shengchuang.common.mvc.repository.query.Criteria;
import com.shengchuang.common.util.TimeUtil;
import com.shengchuang.member.core.domain.TransferLog;
import com.shengchuang.member.core.domain.TransferSetting;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.base.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TransferLogService extends AbstractService<TransferLog, Integer> {

    @Autowired
    private UserService userService;

    /**
     * 分页查询
     *
     * @param user           查询用户,null则查询所有
     * @param pageRequestMap 前段传的查询条件
     * @return
     */
    public Page<TransferLog> getPage(User user, PageRequestMap pageRequestMap) {
        Criteria<TransferLog> conditions = toPageConditions(pageRequestMap);
        Criteria<TransferLog> criteria = conditions;
        if (user != null) {
            criteria.andEqual("to.userId", user.getId());
        } else {
            String username = pageRequestMap.get("username");
            if (isNotBlank(username)) {
                User find = userService.findByUsername(username);
                if (find != null) {
                    criteria.andEqual("to.userId", find.getId());
                } else {
                    conditions.setPageResultEmpty();
                }
            }
        }

        Date date = pageRequestMap.getDateValue("date");
        if (date != null) {
            criteria.andBetween("to.time", TimeUtil.getStartTimeOfDate(date), TimeUtil.getOverTimeOfDate(date));
        }
        return getPage(conditions);
    }

    public double getSumTodey(int userId, int typeOut, int typeIn, int transferType) {
        Object sum = createCriteria().andEqual("from.type", typeOut)
                .andEqual("to.type", typeIn)
                .andEqual("transferType", transferType)
                .andEqual("from.userId", userId)
                .andBetween("from.time", TimeUtil.getStartTimeToday(), TimeUtil.getOverTimeToday())
                .addSelectSum("amount")
                .getOneObject();
        return sum == null ? 0 : (double) sum;
    }

    public double getSumTodey(int userId, TransferSetting setting) {
        return getSumTodey(userId, setting.getTypeOut(), setting.getTypeOut(), setting.getTransferType());
    }

}
