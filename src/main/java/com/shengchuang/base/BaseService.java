package com.shengchuang.base;

import com.shengchuang.common.mvc.repository.CommonDao;
import com.shengchuang.common.mvc.repository.EntityInfo;
import com.shengchuang.common.mvc.repository.query.Criteria;
import com.shengchuang.common.util.ReflectUtil;
import lombok.experimental.Delegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class BaseService<T, ID> {
    protected Class<T> entityType;

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
        dao = commonDao.getEntityDao(entityType);
    }

    List<T> findByIds(List<ID> ids) {
        String idFieldName = EntityInfo.get(entityType).getIdFieldName();
        return criteria().andIn(idFieldName, ids).getList();
    }

    List<T> findByIds(ID... ids) {
        return findByIds(Arrays.asList(ids));
    }


    public Criteria<T> criteria() {
        return dao.criteria();
    }

    public T getOne(ID id) {
        return dao.getOne(id);
    }

    public T findOneByAttrs(String attrName, Object... value) {
        return dao.findOneByAttrs(attrName, value);
    }

    public List<T> findByAttr(Class<T> type, String attrName, Object... value) {
        return dao.findByAttr(type, attrName, value);
    }

    @Transactional
    public T save(T entity) {
        return dao.save(entity);
    }

    @Transactional
    public T saveSelective(T entity) {
        return dao.saveSelective(entity);
    }

    @Transactional
    public List<T> saveAll(Collection<T> entities) {
        return dao.saveAll(entities);
    }

    @Transactional
    public void delete(T entity) {
        dao.delete(entity);
    }
}
