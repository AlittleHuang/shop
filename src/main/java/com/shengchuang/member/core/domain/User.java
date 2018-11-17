package com.shengchuang.member.core.domain;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 用户
 *
 * @author HuangChengwei
 */
@Data
@NoArgsConstructor

@Cacheable
@Entity(name = "user")
public class User implements Serializable {

	
    /**
     * 手动
     */
    public static final int AGENT_SD = 0;

    /**
     * 自动
     */
    public static final int AGENT_ZD = 1;
    /**
     * 冻结状态
     */
    public static final Integer FREEZE_DJ = 1;
    /**
     * 解冻状态
     */
    public static final Integer FREEZE_JD = 0;

    public static final String TOKEN = "sdf21fsaf5421sdfas";
    
    public static final Integer IDSTATUS0 = 0;//未提交
    public static final Integer IDSTATUS1 = 1;//审核中
    public static final Integer IDSTATUS2 = 2;//已通过
    public static final Integer IDSTATUS3 = 3;//未通过

    public static final int ROOT_USER_ID = 1;
    public static final int SYSTEM_ID = 10000001;
    public static final int LEVLE_0 = 0;
    public static final int LEVLE_1 = 1;
    public static final List<Consumer<User>> insertCallbackList = new ArrayList<>();
    public static final List<Consumer<User>> removeCallbackList = new ArrayList<>();
    private static final String[] levelName = {"V0", "V1","V2","V3","V4","V5","V6","V7","V8","V9"};// TODO levelName
    private static final String[] idStatusName = {"未上传", "未审核", "已审核", "未通过"};// TODO levelName
    public static final int MAX_LEVEL = levelName.length - 1;
    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 用户名
     */
    @Column(unique = true)
    private String username;
    /**
     * 密码
     */
    @JSONField(serialize = false)
    private String password;
    /**
     * 二级密码
     */
    @JSONField(serialize = false)
    private String secondpwd;
    /**
     * 用户图像地址
     */
    private String imgsrc;
    /**
     * 用户身份证号码
     */
   // @JSONField(serialize = false)
    @Column(name = "id_card")
    private String idCard;
    
    /**
     * 用户身份证正面
     */
    @Column(name = "zm_url")
    private String zmUrl;
    
    /**
     * 用户身份证手持
     */
    @Column(name = "sc_url")
    private String scUrl;
    
    /**
     * 用户身份证反面
     */
   // @JSONField(serialize = false)
    @Column(name = "fm_url")
    private String fmUrl;
    
    /**
     * 身份证受理状态
     * 0未提交
     * 1审核中
     * 2已通过
     * 3未通过
     */
    @Column(name = "id_status")
    private Integer idStatus;
    
    /**
     * 用户邮箱
     */
    @Column(name = "email")
    private String email;
    /**
     * 用户真实姓名
     */
    @Column(name = "actual_name")
    private String actualName;
    /**
     * 注册时间
     */
    @Column(name = "regist_time")
    private Date registTime;
    /**
     * 注册时间
     */
    @Column(name = "active_time")
    private Date activeTime;
    /**
     * 关联推荐人id
     */
    @Column(name = "referrer_id")
    private Integer referrerId;

    /**
     * 矿机类型
     */
    @Column(nullable = false)
    private Integer miningType;

    /**
     * 推荐人
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referrer_id", insertable = false, updatable = false)
    private User referrer;

    /**
     * 会员等级
     */
    private Integer level;
    
    /**
     * 会员手动等级
     */
    private Integer adminLevel;

    /**
     * 角色(0用户,1管理员)
     */
    private Integer role;
    @Column(/*unique = false*/)
    private String phone;

    /**
     * 解冻(0解冻，1冻结)
     */
    private Integer freeze;
    
    /**
     * 是否手动修改等级
     */
    private Integer agent;

    /**
     * 状态 0->未激活, 1->激活
     */
    @Column(nullable = false)
    private Integer status;

    @Transient
    public Object data;

    public User(String username) {
        this.username = username;
    }

    
    public String getIdStatusName() {
        if(idStatus == null) return null;
        return getIdStatusName(idStatus);
    }

	public static String getIdStatusName(int idStatus) {
		if (idStatus < 0 || idStatus >= idStatusName.length)
			return "状态错误";
		return idStatusName[idStatus];
	}

    public String getUserLevelName() {
        return  getLevelName();
    }

    public String getLevelName() {
        if (level == null) return null;
        return getLevelName(level);
    }

    public static String getLevelName(int level) {
        if (level < 0 || level >= levelName.length)
            return "等级错误";
        return levelName[level];
    }

    public static Map<Integer, User> mapById(Collection<User> users) {
        return users.stream().collect(Collectors.toMap(User::getId, user -> user));
    }

    public static List<Integer> idList(Collection<User> users){
        return users.stream().map(User::getId).collect(Collectors.toList());
    }

    @PostPersist
    public void afterInsert() {
        for (Consumer<User> callback : insertCallbackList) {
            callback.accept(this);
        }
    }

    @PostRemove
    public void afterRemove() {
        for (Consumer<User> callback : removeCallbackList) {
            callback.accept(this);
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", secondpwd='" + secondpwd + '\'' +
                ", imgsrc='" + imgsrc + '\'' +
                ", idCard='" + idCard + '\'' +
                ", zmUrl='" + zmUrl + '\'' +
                ", scUrl='" + scUrl + '\'' +
                ", fmUrl='" + fmUrl + '\'' +
                ", idStatus=" + idStatus +
                ", email='" + email + '\'' +
                ", actualName='" + actualName + '\'' +
                ", registTime=" + registTime +
                ", activeTime=" + activeTime +
                ", referrerId=" + referrerId +
                ", miningType=" + miningType +
                //", referrer=" + referrer +
                ", level=" + level +
                ", adminLevel=" + adminLevel +
                ", role=" + role +
                ", phone='" + phone + '\'' +
                ", freeze=" + freeze +
                ", agent=" + agent +
                ", status=" + status +
                ", data=" + data +
                '}';
    }
}
