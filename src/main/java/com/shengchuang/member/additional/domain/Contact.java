package com.shengchuang.member.additional.domain;

import com.shengchuang.member.core.domain.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * 联系人
 */
@Data
@NoArgsConstructor
@Entity(name = "contact")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键自增
    private Integer id; //主键

    /**
     * 用户ID
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 联系人ID
     */
    @Column(name = "contact_id")
    private Integer contactId;

    @OneToOne
    @JoinColumn(name = "contact_id", insertable = false, updatable = false)
    private User contact;

    /**
     * 状态
     */
    private Integer status;

    ///** 关系 */
    //private Integer relation;

    @Transient
    private User user;

    /**
     * @param userId
     * @param contactId
     */
    public Contact(Integer userId, Integer contactId) {
        super();
        this.userId = userId;
        this.contactId = contactId;
    }

}
