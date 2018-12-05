package com.shengchuang.shop.service;

import com.shengchuang.common.mvc.repository.CommonDao;
import com.shengchuang.shop.domain.Regions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RegionsService {

    private Map<Integer, Regions> regionsMap;
    private CommonDao commonDao;

    @Autowired
    private void initRegionsMap(CommonDao commonDao) {
        this.commonDao = commonDao;
        reload();
    }

    public void reload() {
        List<Regions> type = commonDao.createCriteria(Regions.class).getList();
        regionsMap = type.stream().collect(Collectors.toMap(Regions::getId, Function.identity()));
    }

    public String areaToString(Integer areaId) {
        Assert.state(isAreaId(areaId), "参数错误");
        Regions regions = regionsMap.get(areaId);
        StringBuilder name = new StringBuilder(regions.getName());
        Integer pId = regions.getPid();
        while (pId != null) {
            Regions r = regionsMap.get(pId);
            name.insert(0, r.getName() + "，");
            pId = r.getPid();
        }
        return name.toString();
    }

    public boolean isAreaId(Integer areaId) {
        Regions regions = regionsMap.get(areaId);
        return regions != null && regions.getType() == 2;
    }
}
