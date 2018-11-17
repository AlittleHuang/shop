package com.shengchuang.base;

import com.shengchuang.common.mvc.domain.PageFix;
import com.shengchuang.common.mvc.pageable.PageRequestMap;
import com.shengchuang.common.mvc.repository.CommonRepository;
import com.shengchuang.common.mvc.repository.query.Criteria;
import com.shengchuang.common.util.StreamUtil;
import com.shengchuang.common.util.SystemVariables;
import com.shengchuang.common.util.TimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

//import com.shengchuang.common.mvc.repository.query.Criteria;
//import com.shengchuang.common.mvc.repository.query.Criteria;

public abstract class AbstractService<T, ID> implements StreamUtil {

    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired(required = false)
    protected JpaSpecificationExecutor<T> jpaSpecificationExecutor;
    @Autowired
    EntityManager entityManager;
    @Autowired(required = false)
    CommonRepository<T, ID> commonRepository;

    public <T> List<T> find(Criteria<T> conditions) {
        return conditions.getList();
    }


    public <T> T findOne(Criteria<T> conditions) {
        return conditions.getOne();
    }

    public <T> com.shengchuang.common.mvc.domain.Page<T> emptyPage() {
        return (com.shengchuang.common.mvc.domain.Page<T>) PageFix.empty();
    }

    public <T> boolean exists(Criteria<T> conditions) {
        return conditions.exists();
    }


    public <T> com.shengchuang.common.mvc.domain.Page<T> getPage(Criteria<T> conditions, Pageable pageable) {
        return conditions.setPageable(pageable).getFixPage();
    }

    public <T> com.shengchuang.common.mvc.domain.Page<T> getPage(Criteria<T> conditions) {
        return conditions.getFixPage();
    }

    public com.shengchuang.common.mvc.domain.Page<T> getPage(PageRequestMap pageRequestMap, Consumer<Criteria<T>> criteriaConsumer) {
        Criteria<T> criteria = toPageCriteria(pageRequestMap);
        Criteria<T> c = criteria;
        criteriaConsumer.accept(c);
        criteria.addOrderByDesc("time");
        return criteria.getFixPage();
    }

    public <T> List<?> findObjList(Criteria<T> conditions) {
        return conditions.getObjList();
    }

    public <T> Object findOneObj(Criteria<T> conditions) {
        return conditions.getOneObject();
    }

    protected void redLog(Object o) {
        if (SystemVariables.IS_WINDOWS_SYSTEM) {
            System.err.println(o);
        }
    }

    public void lockTable(Object entity, LockModeType lockMode) {
        entityManager.lock(entity, lockMode);
    }

    public void lockTable(Object entity, LockModeType lockMode,
                          Map<String, Object> properties) {
        entityManager.lock(entity, lockMode, properties);
    }

    public Criteria<T> createCriteria() {
        return commonRepository.criteria();
    }

    public Criteria<T> createCriteria(Pageable pageable) {
        Criteria<T> conditions = createCriteria();
        conditions.setPageable(pageable);
        return conditions.and();
    }

    public <S extends T> S save(S entity) {
        return commonRepository.save(entity);
    }

    @SuppressWarnings("unchecked")
    public <S extends T> void saveAll(S... entity) {
        saveAll(Arrays.asList(entity));
    }

//    public <S extends T> Optional<S> findOne(Example<S> example) {
//        return commonDao.findOne(example);
//    }

    public Page<T> findAll(Pageable pageable) {
        return new PageFix<>(commonRepository.findAll(pageable));
    }

    public List<T> findAll() {
        return commonRepository.findAll();
    }

    public List<T> findAll(Sort sort) {
        return commonRepository.findAll(sort);
    }

    public Optional<T> findById(ID id) {
        return commonRepository.findById(id);
    }

    public List<T> findAllById(Iterable<ID> ids) {
        return commonRepository.findAllById(ids);
    }

    public boolean lockTable() {
        T one = createCriteria().limit(1).getOne();
        if (one != null) {
            lockTable(one, LockModeType.PESSIMISTIC_WRITE);
            return true;
        }
        return false;
    }

    @Transactional
    public <S extends T> List<S> saveAll(Iterable<S> entities) {
        return commonRepository.saveAll(entities);
    }

    public boolean existsById(ID id) {
        return commonRepository.existsById(id);
    }

    @Transactional
    public void flush() {
        commonRepository.flush();
    }

    @Transactional
    public <S extends T> S saveAndFlush(S entity) {
        return commonRepository.saveAndFlush(entity);
    }

    @Transactional
    public void deleteInBatch(Iterable<T> entities) {
        commonRepository.deleteInBatch(entities);
    }

//    public <S extends T> Page<S> findAll(Example<S> example, Pageable pageable) {
//        return commonDao.findAll(example, pageable);
//    }

    public long count() {
        return commonRepository.count();
    }

    //    public void deleteAllInBatch() {
//        commonDao.deleteAllInBatch();
//    }
    @Transactional
    public void deleteById(ID id) {
        commonRepository.deleteById(id);
    }

    public T getOne(ID id) {
        return findById(id).orElse(null);
    }

    @Transactional
    public void delete(T entity) {
        commonRepository.delete(entity);
    }

    //    public <S extends T> long count(Example<S> example) {
//        return commonDao.count(example);
//    }
    @Transactional
    public void deleteAll(Iterable<? extends T> entities) {
        commonRepository.deleteAll(entities);
    }

    //    public <S extends T> List<S> findAll(Example<S> example) {
//        return commonDao.findAll(example);
//    }
//
//    public <S extends T> boolean exists(Example<S> example) {
//        return commonDao.exists(example);
//    }
    @Transactional
    public void deleteAll() {
        commonRepository.deleteAll();
    }
//
//    public <S extends T> List<S> findAll(Example<S> example, Sort sort) {
//        return commonDao.findAll(example, sort);
//    }

    public Optional<T> findOne(Specification<T> spec) {
        return jpaSpecificationExecutor.findOne(spec);
    }

    public List<T> findAll(Specification<T> spec) {
        return jpaSpecificationExecutor.findAll(spec);
    }


    public Page<T> findAll(Specification<T> spec, Pageable pageable) {
        return new PageFix<>(jpaSpecificationExecutor.findAll(spec, pageable));
    }

    public List<T> findAll(Specification<T> spec, Sort sort) {
        return jpaSpecificationExecutor.findAll(spec, sort);
    }

    public long count(Specification<T> spec) {
        return jpaSpecificationExecutor.count(spec);
    }

    public T merge(T entity) {
        return entityManager.merge(entity);
    }

    @Transactional
    public T saveSelective(T entity) {
        return commonRepository.saveSelective(entity);
    }

    protected boolean isBlank(String str) {
        return StringUtils.isBlank(str);
    }

    protected boolean isNotBlank(String str) {
        return StringUtils.isNotBlank(str);
    }

    protected Criteria<T> toPageConditions(PageRequestMap pageRequestMap) {
        Criteria<T> conditions = createCriteria(pageRequestMap);
        try {
            conditions.andEqual(pageRequestMap.asMap());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conditions;
    }


    protected Criteria<T> toPageCriteria(PageRequestMap pageRequestMap) {
        Criteria<T> criteria = createCriteria(pageRequestMap);
        try {
            criteria.and().andEqual(pageRequestMap.asMap());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return criteria;
    }


    /**
     * 通过ID随机生成器生成未使用的ID
     *
     * @param randomId ID 生成器
     * @return ID
     */
    public ID randomId(Supplier<ID> randomId) {
        ID id;
        int count = 0;
        do {
            id = randomId.get();
            if (count++ >= 20)
                throw new IllegalStateException("ID随机生成器错误");
        } while (existsById(id));
        return id;
    }


    /**
     * 添加筛选日期范围条件,前段传入:startTime开始日期,endTime结束日期
     */
    protected <T> Criteria<T> addTimeFilter(Criteria<T> criteria, PageRequestMap pageRequestMap, String fieldName) {
        Date startTime = pageRequestMap.getDateValue("startTime");
        criteria.andGe(fieldName, startTime, Date.class);
        Date endTime = pageRequestMap.getDateValue("endTime");
        if (endTime != null) {
            criteria.andLt(fieldName, TimeUtil.addDay(endTime, 1), Date.class);
        }
        return criteria;
    }


}