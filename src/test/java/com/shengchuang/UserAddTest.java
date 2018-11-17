package com.shengchuang;

import com.shengchuang.member.additional.service.UserLevelService;
import com.shengchuang.common.mvc.repository.CommonRepository;
import com.shengchuang.member.core.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableJpaRepositories(repositoryFactoryBeanClass = CommonRepository.class)
@ActiveProfiles("dev")
public class UserAddTest {

    @Autowired
    UserService userService;
    @Autowired
    TestService testService;
    @Autowired
    UserLevelService userLevelService;

    @Test
    public void addUser() {
        boolean id = userService.createCriteria().exists();
        System.out.println(id);
    }


}
