package com.shengchuang.base;

import com.alibaba.fastjson.JSONObject;
import com.shengchuang.common.mvc.repository.CommonDao;
import com.shengchuang.common.mvc.repository.EntityInfo;
import com.shengchuang.common.mvc.repository.query.Criteria;
import com.shengchuang.common.mvc.repository.query.JpaEntityInfo;
import com.shengchuang.common.util.ReflectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class BaseService<T, ID> {
    protected Class<T> entityType;

    private CommonDao.EntityDao<T, ID> dao;
    protected CommonDao commonDao;
    @Autowired
    protected EntityManager em;

    public BaseService(Class<T> entityType) {
        this.entityType = entityType;
    }

    public BaseService(){
        //noinspection unchecked
        entityType = (Class<T>) ReflectUtil.getSuperClassGenricType(getClass());
    }

    @Autowired
    void setCommonDao(CommonDao commonDao) {
        this.commonDao = commonDao;
        dao = commonDao.getEntityDao(entityType);
    }

    public List<T> findByIds(List<ID> ids) {
        String idFieldName = EntityInfo.get(entityType).getIdFieldName();
        return criteria().andIn(idFieldName, ids).getList();
    }

    public List<T> findByIds(ID... ids) {
        return findByIds(Arrays.asList(ids));
    }


    public Criteria<T> criteria() {
        return dao.criteria();
    }

    @SuppressWarnings("unchecked")
    public Criteria<T> criteria(JSONObject queryMap) {
        Criteria<T> criteria = criteria();
        JpaEntityInfo info = JpaEntityInfo.get(em, entityType);
        for (JpaEntityInfo.AttrInfo attrInfo : info.getAttrInfos()) {

            String key;
            Object value;

            key = attrInfo.name;
            value = queryMap.getObject(key, attrInfo.type);
            if (value != null) {
                criteria.andEqual(attrInfo.name, value);
            }

            key = attrInfo.name + "[gt]";
            value = queryMap.getObject(key, attrInfo.type);
            if (value instanceof Comparable) {
                criteria.andGt(key, (Comparable) value);
            }

            key = attrInfo.name + "[ge]";
            value = queryMap.getObject(key, attrInfo.type);
            if (value instanceof Comparable) {
                criteria.andGe(key, (Comparable) value);
            }

            key = attrInfo.name + "[lt]";
            value = queryMap.getObject(key, attrInfo.type);
            if (value instanceof Comparable) {
                criteria.andLt(key, (Comparable) value);
            }

            key = attrInfo.name + "[le]";
            value = queryMap.getObject(key, attrInfo.type);
            if (value instanceof Comparable) {
                criteria.andLe(key, (Comparable) value);
            }

            key = "[or]" + attrInfo.name + "[gt]";
            value = queryMap.getObject(key, attrInfo.type);
            if (value instanceof Comparable) {
                criteria.orGt(key, (Comparable) value);
            }

            key = "[or]" + attrInfo.name + "[ge]";
            value = queryMap.getObject(key, attrInfo.type);
            if (value instanceof Comparable) {
                criteria.orGe(key, (Comparable) value);
            }

            key = "[or]" + attrInfo.name + "[lt]";
            value = queryMap.getObject(key, attrInfo.type);
            if (value instanceof Comparable) {
                criteria.orLt(key, (Comparable) value);
            }

            key = "[or]" + attrInfo.name + "[le]";
            value = queryMap.getObject(key, attrInfo.type);
            if (value instanceof Comparable) {
                criteria.orLe(key, (Comparable) value);
            }

            key = attrInfo.name + "[isNull]";
            value = queryMap.getBoolean(key);
            if (value != null) {
                criteria.andIsNull(key, (boolean) value);
            }

            key = "[or]" + attrInfo.name + "[isNull]";
            value = queryMap.getBoolean(key);
            if (value != null) {
                criteria.orIsNull(key, (boolean) value);
            }

        }

        return criteria;
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

    @Transactional
    public void deleteAll(Iterable<T> entities) {
        for (T entity : entities) {
            delete(entity);
        }
    }

}
