package com.shengchuang.base;

import com.shengchuang.common.mvc.repository.CommonDao;
import com.shengchuang.common.mvc.repository.EntityInfo;
import com.shengchuang.common.util.ReflectUtil;
import lombok.experimental.Delegate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

public class BaseService<T, ID> {
    protected Class<T> entityType;
    @Delegate
    private CommonDao.EntityDao<T, ID> dao;

    public BaseService(Class<T> entityType) {
        this.entityType = entityType;
    }

    public BaseService(){
        //noinspection unchecked
        entityType = (Class<T>) ReflectUtil.getSuperClassGenricType(getClass());
    }

    @Autowired
    void setCommonDao(CommonDao commonDao) {
        commonDao.getEntityDao(entityType);
    }

    List<T> findByIds(List<ID> ids) {
        String idFieldName = EntityInfo.get(entityType).getIdFieldName();
        return criteria().andIn(idFieldName, ids).getList();
    }

    List<T> findByIds(ID... ids) {
        return findByIds(Arrays.asList(ids));
    }

}
