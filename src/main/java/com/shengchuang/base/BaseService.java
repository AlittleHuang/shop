package com.shengchuang.base;

import com.alibaba.fastjson.util.TypeUtils;
import com.shengchuang.common.mvc.pageable.PageRequestMap;
import com.shengchuang.common.mvc.repository.CommonDao;
import com.shengchuang.common.mvc.repository.EntityInfo;
import com.shengchuang.common.mvc.repository.query.Criteria;
import com.shengchuang.common.mvc.repository.query.JpaEntityInfo;
import com.shengchuang.common.util.ReflectUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.*;

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

    public Criteria<T> criteria(PageRequestMap queryMap) {
        return query(criteria(), queryMap);
    }

    public Criteria<T> query(Criteria<T> criteria, PageRequestMap queryMap) {
        JpaEntityInfo info = JpaEntityInfo.get(em, entityType);
        Set<JpaEntityInfo.AttrInfo> attrSet = info.getAttrInfos();
        for (JpaEntityInfo.AttrInfo attrInfo : attrSet) {

            String[] array;
            String value;

            String name = attrInfo.name;
            array = queryMap.getArray(name);
            Set<?> set = cast(array, attrInfo.type);
            if (!set.isEmpty()) {
                criteria.andIn(name, set);
            }

            array = queryMap.getArray("[or]" + name);
            set = cast(array, attrInfo.type);
            if (!set.isEmpty()) {
                criteria.orIn(name, set);
            }

            array = queryMap.getArray(name + "[not]");
            set = cast(array, attrInfo.type);
            if (!set.isEmpty()) {
                criteria.andNotIn(name, set);
            }

            array = queryMap.getArray("[or]" + name + "[not]");
            set = cast(array, attrInfo.type);
            if (!set.isEmpty()) {
                criteria.orNotIn(name, set);
            }

            criteria.andGt(name, queryMap.get(name + "[gt]"));
            criteria.andGe(name, queryMap.get(name + "[ge]"));
            criteria.andLt(name, queryMap.get(name + "[lt]"));
            criteria.andLe(name, queryMap.get(name + "[le]"));

            criteria.orGt(name, queryMap.get("[or]" + name + "[gt]"));
            criteria.orGe(name, queryMap.get("[or]" + name + "[ge]"));
            criteria.orLt(name, queryMap.get("[or]" + name + "[lt]"));
            criteria.orLe(name, queryMap.get("[or]" + name + "[le]"));

            value = queryMap.get(name + "[isNull]");
            boolean isNull = "true".equalsIgnoreCase(value);
            if (isNull || "false".equalsIgnoreCase(value)) {
                criteria.andIsNull(name, isNull);
            }

            value = queryMap.get("[or]" + name + "[isNull]");
            isNull = "true".equalsIgnoreCase(value);
            if (isNull || "false".equalsIgnoreCase(value)) {
                criteria.orIsNull(name, queryMap.getBoolean("[or]" + name + "[isNull]"));
            }
        }

        String[] array = queryMap.getArray("[order]");
        for (String s : array) {
            if (attrSet.contains(s)) {
                criteria.addOrderByAsc(s);
            }
        }

        return criteria.setPageable(queryMap);
    }

    @NotNull
    private <T> Set<T> cast(String[] array, Class<T> type) {
        if (array == null || array.length == 0) {
            //noinspection unchecked
            return Collections.EMPTY_SET;
        }
        HashSet<T> objects = new HashSet<>();
        for (String s : array) {
            if (s == null || s.length() == 0) continue;
            T cast = TypeUtils.cast(s, type, null);
            if (cast != null) {
                objects.add(cast);
            }
        }
        return objects;
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
