package com.shengchuang;

import com.shengchuang.common.mvc.repository.CommonRepository;
import com.shengchuang.member.core.service.UserService;
import com.shengchuang.member.core.service.UserTreeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableJpaRepositories(repositoryFactoryBeanClass = CommonRepository.class)
@ActiveProfiles("dev")
public class UserTreeTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserTreeService userTreeService;

    @Test
    public void test() {
        Collection<Integer> ids = userTreeService.getAllUserIds();
        for (Integer id : ids) {
            List<Integer> x = userTreeService.getTeamIds(id, false);
            List<Integer> y = userTreeService.getTeamIds(id, false);
            for (Integer integer : x) {
                boolean contains = y.contains(integer);
                if (!contains)
                    System.out.println("!y.contains(integer)" + integer);
            }
            for (Integer integer : y) {
                boolean contains = x.contains(integer);
                if (!contains) {
                    System.out.println("!x.contains(integer):" + integer);
                }
            }
        }
        System.out.println("done");
    }

}
