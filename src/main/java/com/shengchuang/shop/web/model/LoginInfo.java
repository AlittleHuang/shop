package com.shengchuang.shop.web.model;

import com.shengchuang.common.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LoginInfo implements JsonUtil.AbleToJsonString {

    private String username;
    private String password;
    private Long expired;

}
