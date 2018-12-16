package com.shengchuang.shop.domain;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
@Getter
@Setter
@NoArgsConstructor

@Cacheable
@Entity(name = "user")
public class User implements Serializable {

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

}
