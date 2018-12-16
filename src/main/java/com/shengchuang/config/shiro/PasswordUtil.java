package com.shengchuang.config.shiro;

import com.shengchuang.common.util.StringUtil;
import org.apache.shiro.crypto.hash.SimpleHash;

public interface PasswordUtil {

    /**
     * 加密
     *
     * @param source 源字符串
     * @param salt   盐值(username)
     * @return
     */
    static String encodeStringPassword(String source, Object salt) {
        // Assert.state(source != null && source.length() >= 6, "密码长度至少6位数");
        SimpleHash password = new SimpleHash(UserRealm.ALGORITHM_NAME, source, salt, UserRealm.HASH_ITERATIONS);
        return password.toString();
    }

    /**
     * @param source   输入
     * @param salt     盐值
     * @param password 验证
     * @return
     */
    static boolean checkPassword(Object source, Object salt, String password) {
        if (source == null || salt == null || password == null)
            return false;
        SimpleHash tmp = new SimpleHash(UserRealm.ALGORITHM_NAME, source, salt, UserRealm.HASH_ITERATIONS);
        return password.equals(tmp.toString());
    }

    static void main(String[] args) {
        System.out.println(StringUtil.randomPwd(16));
    }

    static String test() {
        try {
            return test("a");
        } finally {
            return test("b");
        }
    }

    static String test(String x) {
        System.out.println(x);
        return x;
    }

}