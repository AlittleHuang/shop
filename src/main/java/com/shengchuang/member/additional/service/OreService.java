package com.shengchuang.member.additional.service;

import com.shengchuang.member.additional.domain.Ore;
import com.shengchuang.member.additional.repository.OreRepository;
import com.shengchuang.common.mvc.repository.query.Criteria;
import com.shengchuang.base.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class OreService extends AbstractService<Ore, Integer> {
    @Autowired
    private OreRepository oreRepository;

    /**
     * 查询个人当天算力
     *
     * @param userId
     * @return
     */
    public Ore getByCurrDate(Integer userId) {
        Criteria<Ore> conditions = createCriteria();
        LocalDate date = LocalDate.now();
        List<Ore> list = conditions.andEqual("userId", userId)
                .andEqual("date", date).limit(1).addOrderByDesc("id").getList();
        Ore ore;
        if (list.size() == 0) {
            ore = new Ore(0.0, userId);
        } else {
            ore = list.get(0);
        }
        return ore;
    }
}
