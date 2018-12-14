package com.shengchuang;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute;
import java.util.*;

import static javax.persistence.metamodel.Attribute.PersistentAttributeType.*;

public class JpaEntityInfo {

    private final EntityManager em;
    private List<String> attrNames;
    private final Class<?> type;
    private static final Map<Class, JpaEntityInfo> map = new HashMap<>();

    public static List<String> attributeNames(EntityManager em, Class type) {
        return map.computeIfAbsent(type, t -> new JpaEntityInfo(em, t)).attrNames(0);
    }

    private JpaEntityInfo(EntityManager em, Class type) {
        this.em = em;
        this.type = type;
    }

    private List<String> attrNames(int depath) {
        if (attrNames == null) {
            List<String> list = new ArrayList<>();
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<?> query = criteriaBuilder.createQuery();
            Root<?> root = query.from(type);
            //noinspection unchecked
            Set<Attribute<?, ?>> attributes = (Set<Attribute<?, ?>>) root.getModel().getAttributes();
            for (Attribute<?, ?> attribute : attributes) {
                Attribute.PersistentAttributeType attributeType = attribute.getPersistentAttributeType();
                System.out.println(attributeType);
                Class<?> attrJavaType = attribute.getJavaType();
                if ((attrJavaType == type && depath > 1) || depath > 5) continue;
                if ((attributeType == MANY_TO_ONE) || (attributeType == ONE_TO_ONE)) {
                    for (String attrName : new JpaEntityInfo(em, attrJavaType).attrNames(++depath)) {
                        String e = attribute.getName() + "." + attrName;
                        list.add(e);
                    }
                } else if (attributeType == BASIC) {
                    list.add(attribute.getName());
                }
            }
            attrNames = Collections.unmodifiableList(list);
        }
        return attrNames;
    }
}
