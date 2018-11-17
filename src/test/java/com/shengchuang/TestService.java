package com.shengchuang;

import com.shengchuang.member.additional.repository.StatisticalReportRepository;
import com.shengchuang.member.additional.service.ContactService;
import com.shengchuang.member.additional.service.SettlementService;
import com.shengchuang.member.additional.service.UserLevelService;
import com.shengchuang.common.util.NumberUtil;
import com.shengchuang.common.util.StringUtil;
import com.shengchuang.member.core.domain.Balance;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.member.core.domain.enmus.BalanceType;
import com.shengchuang.member.core.service.BalanceLogService;
import com.shengchuang.member.core.service.BalanceService;
import com.shengchuang.member.core.service.UserService;
import com.shengchuang.member.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class TestService {

    @Autowired
    Task task;

    @Autowired
    UserService userService;

    @Autowired
    StatisticalReportRepository repository;

    @Autowired
    BalanceService balanceService;

    @Autowired
    BalanceLogService balanceLogService;

    @Autowired
    SettlementService settlementService;

    @Autowired
    ContactService contactService;
    @Autowired
    UserLevelService userLevelService;

    @Transactional
    public void balanceFix() {

    }

    @Transactional
    public void registerTest() {
        User root = userService.getOne(User.ROOT_USER_ID);
        ArrayList<User> all = new ArrayList<>();
        ArrayList<User> pUser = new ArrayList<>();
        pUser.add(root);
        initBalance(root);
        Random random = NumberUtil.getRandom();

        while (true) {
            ArrayList<User> tmp = new ArrayList<>();
            for (User user : pUser) {
                int length = random.nextInt(3) + 3;
                for (int i = 0; i < length; i++) {
                    User add = createUser(user);
                    add = settlementService.register(add);
                    initBalance(add);
                    all.add(add);
                    tmp.add(add);
                }
            }
            if (all.size() > 1000) {
                break;
            }
            pUser = tmp;
        }

//        userService.saveAll(all);
//        System.out.println("done");
    }

    public void initBalance() {
        List<User> all = userService.findAll();
        for (User user : all) {
            initBalance(user);
        }
    }

    private void initBalance(User user) {
        Random random = NumberUtil.getRandom();
        for (BalanceType type : BalanceType.values()) {
            Balance balance = balanceService.findOrCreateByUserIdAndType(user.getId(), type);
            balance.setAmount((double) random.nextInt(10000) + 5000);
            balanceService.save(balance);
        }
    }

    /**
     * 创建User
     *
     * @param referrer 推荐人
     * @return User
     */
    private User createUser(User referrer) {
        User user = new User();

        user.setUsername(StringUtil.randomString(16));
        user.setPassword("123123");
        user.setSecondpwd("123123");
        user.setPhone("181" + NumberUtil.randomInt(8));
        Date now = new Date();
        user.setRegistTime(now);
        user.setActiveTime(now);
        user.setLevel(1);
        user.setRole(0);//管理员
        user.setStatus(1);
        user.setReferrerId(referrer.getId());
        user.setReferrer(referrer);
        user.setMiningType(1);
        userService.encodePassword(user);
        return user;
    }

}
