package com.shengchuang.member.core.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.shengchuang.member.core.domain.enmus.BalanceType;
import com.shengchuang.member.core.domain.util.InfoUtils;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;

/**
 * 充值订单
 *
 * @author HuangChengwei
 */
@Data
@NoArgsConstructor
@Entity(name = "recharge")
public class Recharge {

    /**
     * 待审核
     */
    public static final int STATUS_0 = 0;

    /**
     * 审核通过
     */
    public static final int STATUS_1 = 1;

    /**
     * 审核不通过
     */
    public static final int STATUS_2 = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键自增
    private Integer id;
    /**
     * 关联用户id
     */
    @Column(name = "user_id")
    private Integer userId;
    /**
     * 币种类型
     */
    @Column(name = "balance_type")
    private Integer balanceType;
    /**
     * 充值金额
     */
    private Double amount;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "uodate_time")
    private Date updateTime;

    /**
     * 其他信息
     */
    @Lob
    @JSONField(serialize = false)
    private String info;
    /**
     * 审核信息
     */
    @Column(name = "re_info")
    private String reInfo;
    /**
     * 联系方式
     */
    private String phone;

    @Transient
    private String username;

    /**
     * 转账凭证图片
     */
    @Column(name = "pay_proof")
    private String payProof;

    @Transient
    private String typeName;
    @Transient
    private Map<String, Object> infoMap;

    public String getTypeName() {
        if (typeName == null) {
            BalanceType type = BalanceType.of(balanceType);
            typeName = type == null ? "其他" : type.name;
        }
        return typeName;
    }

    @PostLoad
    public void postLoad() {
        infoMap = InfoUtils.toInfoMap(info);
    }

}
