package com.shengchuang.member.core.service;

import com.shengchuang.common.mvc.domain.Page;
import com.shengchuang.common.mvc.pageable.PageRequestMap;
import com.shengchuang.common.mvc.repository.query.Criteria;
import com.shengchuang.common.util.Assert;
import com.shengchuang.member.core.domain.PayInfo;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.member.core.repository.PayInfoRepository;
import com.shengchuang.base.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
public class PayInfoService extends AbstractService<PayInfo, Integer> {

    public static final List<String> NOT_BANK_TYPE = Arrays.asList("WEIXINPAY", "ALIPAY");
    @Autowired
    PayInfoRepository payInfoRepository;

    public Integer findIdByUserIdAndType(int userId, String Type) {
        return (Integer) createCriteria().andEqual("userId", userId).andEqual("type", Type).addOrderByDesc("id")
                .addSelect("id").limit(1).getOneObject();
    }

    public PayInfo findDefault(Integer userId) {
        return createCriteria().andEqual("userId", userId).andNotIn("type", NOT_BANK_TYPE).limit(1)
                .addOrderByDesc("status").getOne();
    }

    /**
     * 银行卡数量
     *
     * @param userId
     * @return
     */
    public int countBankCardByUserId(int userId) {
        return (int) createCriteria().andEqual("userId", userId).andNotIn("type", NOT_BANK_TYPE).count();
    }

    /**
     * 支付方式数量
     *
     * @param userId
     * @return
     */
    public int countByUserId(int userId) {
        return (int) createCriteria().andEqual("userId", userId).count();
    }

    public void update(PayInfo payInfo) {
        String type = payInfo.getType();
        Assert.state(payInfo.checkType(), "类型错误");
        if ("WEIXINPAY".equals(type)) {
            Assert.notEmpty(payInfo.getName(), "请输入微信用户名");
        } else if ("ALIPAY".equals(type)) {
            Assert.notEmpty(payInfo.getName(), "请输入支付宝用户名");
        } else {
            Assert.notEmpty(payInfo.getName(), "请输入开户人姓名");
        }
        Assert.notEmpty(payInfo.getAccount(), "请输入账号");
        if ("WEIXINPAY".equals(type) || "ALIPAY".equals(type)) {
            synchronized ((type + payInfo.getUserId().toString()).intern()) {
                List<PayInfo> old = findByUserIdAndType(payInfo.getUserId(), type);
                if (!old.isEmpty())
                    payInfo.setId(old.get(0).getId());
                if (payInfo.getStatus() == null)
                    payInfo.setStatus(PayInfo.NORMAL);
                saveSelective(payInfo);
                return;
            }
        }

        Assert.state(countBankCardByUserId(payInfo.getUserId()) < 10, "最多添加10张银行卡");
        payInfo.setStatus(PayInfo.NORMAL);
        payInfo.setOrder(0L);
        saveSelective(payInfo);
    }


    public List<PayInfo> findByUserIdAndType(int userId, String type) {
        return payInfoRepository.findByUserIdAndType(userId, type);
    }

    public List<PayInfo> findByType(String type) {
        return payInfoRepository.findByType(type);
    }

    public List<PayInfo> findByUserInIn(Set<Integer> userIds) {
        return payInfoRepository.findByUserIdIn(userIds);
    }

    public List<PayInfo> findByUserInInAndTypeIn(Set<Integer> userIds, List<String> typs) {
        return payInfoRepository.findByUserIdInAndTypeIn(userIds, typs);
    }

    public long findUndeleteByUserId(Integer userId) {
        return createCriteria().andEqual("userId", userId).andNotEqual("status", PayInfo.DELETE).count();
    }

    public long findUndeleteByUserIdAndType(Integer userId, Integer type) {
        if (type == 0) {
            return createCriteria().andEqual("userId", userId).andEqual("type", "ALIPAY")
                    .andNotEqual("status", PayInfo.DELETE).count();
        } else if (type == 1) {
            return createCriteria().andEqual("userId", userId).andEqual("type", "WEIXINPAY")
                    .andNotEqual("status", PayInfo.DELETE).count();
        } else {
            return createCriteria().andEqual("userId", userId).andNotEqual("type", "ALIPAY")
                    .andNotEqual("type", "WEIXINPAY").andNotEqual("status", PayInfo.DELETE).count();
        }
    }

    public Page<PayInfo> getPage(User user, PageRequestMap pageRequestMap) {
        Criteria<PayInfo> conditions = toPageConditions(pageRequestMap);
        Criteria<PayInfo> criteria = conditions;
        criteria.andEqual("userId", user.getId());
        criteria.andNotIn("type", NOT_BANK_TYPE);
        criteria.andNotEqual("status", PayInfo.DELETE);
        criteria.addOrderByDesc("order");
        criteria.addOrderByAsc("id");
        return getPage(criteria);
    }

    public List<PayInfo> findByUserIdAndTypeAndStatusNot(Integer userId, String type, Integer delete) {
        return payInfoRepository.findByUserIdAndTypeAndStatusNot(userId, type, delete);
    }
}
