package com.shengchuang;

import com.shengchuang.common.mvc.repository.CommonRepository;
import com.shengchuang.common.mvc.repository.query.Criteria;
import com.shengchuang.member.core.domain.Balance;
import com.shengchuang.member.core.service.BalanceService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableJpaRepositories(repositoryFactoryBeanClass = CommonRepository.class)
@ActiveProfiles("dev")
public class BalanceTest {

    @Autowired
    BalanceService balanceService;

    @Test
    public void test(){
        List<Balance> amount = balanceService.createCriteria().andGe("amount", 1).getList();

    }

}
