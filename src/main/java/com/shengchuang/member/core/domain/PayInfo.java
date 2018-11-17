package com.shengchuang.member.core.domain;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 收付款信息
 */
@Entity(name = "pay_info")
public class PayInfo {

    public static final int DELETE = -1;
    public static final int NORMAL = 0;
    private static final Map<String, String> TYPE_MAP = new HashMap<>();

    static {
        TYPE_MAP.put("WEIXINPAY", "微信");
        TYPE_MAP.put("ALIPAY", "支付宝");
        TYPE_MAP.put("PBOCCNBJ", "中国人民银行");
        TYPE_MAP.put("ICBKCNBJ", "中国工商银行");
        TYPE_MAP.put("ABOCCNBJ", "中国农业银行");
        TYPE_MAP.put("BKCHCNBJ", "中国银行");
        TYPE_MAP.put("PCBCCNBJ", "中国建设银行");
        TYPE_MAP.put("COMMCNSH", "交通银行");
        TYPE_MAP.put("PSBCCNBJ", "中国邮政储蓄银行");
        TYPE_MAP.put("CMBCCNBS", "招商银行");
        TYPE_MAP.put("CIBKCNBJ", "中信银行");
        TYPE_MAP.put("HXBKCNBJ", "华夏银行");
        TYPE_MAP.put("SPDBCNSH", "浦发银行");
        TYPE_MAP.put("SZDBCNBS", "平安银行");
        TYPE_MAP.put("GDBKCN22", "广发银行");
        TYPE_MAP.put("FJIBCNBA", "兴业银行");
        TYPE_MAP.put("ZJCBCN2N", "浙商银行");
        TYPE_MAP.put("CHBHCNBT", "渤海银行");
        TYPE_MAP.put("HFBACNSD", "恒丰银行");
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键自增
    private Integer id;
    @Column(name = "user_id")
    private Integer userId;
    /**
     * WEIXINPAY 微信 <br>
     * ALIPAY 支付宝 <br>
     * PBOCCNBJ 中国人民银行 <br>
     * ICBKCNBJ 中国工商银行 <br>
     * ABOCCNBJ 中国农业银行 <br>
     * BKCHCNBJ 中国银行 <br>
     * PCBCCNBJ 中国建设银行 <br>
     * COMMCNSH 交通银行 <br>
     * PSBCCNBJ 中国邮政储蓄银行 <br>
     * CMBCCNBS 招商银行 <br>
     * CIBKCNBJ 中信银行 <br>
     * HXBKCNBJ 华夏银行 <br>
     * SPDBCNSH 浦发银行 <br>
     * SZDBCNBS 平安银行 <br>
     * GDBKCN22 广发银行 <br>
     * FJIBCNBA 兴业银行 <br>
     * ZJCBCN2N 浙商银行 <br>
     * CHBHCNBT 渤海银行 <br>
     * HFBACNSD 恒丰银行 <br>
     *
     * @see PayInfo#TYPE_MAP
     */
    private String type;
    /**
     * 账号
     */
    private String account;
    /**
     * 联系电话
     */
    private String phone;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 开户名(或昵称)
     */
    private String name;
    /**
     * 排序
     */
    @Column(name = "`order`")
    private Long order;

    /**
     * 图片
     */
    @Column(name = "code_url")
    private String codeUrl;

    @Transient
    private String displayType;

    public PayInfo() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDisplayType() {
        displayType = TYPE_MAP.get(type);
        return displayType;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOrder() {
        return order;
    }

    public void setOrder(Long order) {
        this.order = order;
    }

    public String getCodeUrl() {
        return codeUrl;
    }

    public void setCodeUrl(String codeUrl) {
        this.codeUrl = codeUrl;
    }

    public boolean checkType() {
        return TYPE_MAP.containsKey(type);
    }
}
