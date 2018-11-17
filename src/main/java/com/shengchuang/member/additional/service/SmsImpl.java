package com.shengchuang.member.additional.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.springframework.util.Assert;

import com.shengchuang.common.exception.BusinessException;
import com.shengchuang.common.sms.AbstractSms;
import com.shengchuang.common.util.TimeUtil;

public class SmsImpl extends AbstractSms {

    private static final String encode = "UTF-8";

    private static final String username = "peng";  //用户名

    private static final String password_md5 = "1adbb3178591fd5bb0c248518f39bf6d";  //密码

    private static final String apikey = "88be67ab5258cd612f4e9e8ac30ce8a5";
    //apikey秘钥（请登录 http://m.5c.com.cn 短信平台-->账号管理-->我的信息 中复制apikey）


    private final String key;
    private final SmsType type;
    private final String phoneNumber;
    private String name;
    private String customize;
    private String userName;

    /**
     * @param phoneNumber 电话号码
     * @param type        业务类型
     */
    public SmsImpl(String phoneNumber, SmsType type) {
        Assert.state(type != SmsType.CUSTOMIZE,
                "自定义内容使用构造函数SmsImpl(String phoneNumber, String customize)");
        this.phoneNumber = phoneNumber;
        key = phoneNumber + "-" + type;
        this.type = type;
    }
    
    public SmsImpl(String phoneNumber, SmsType type,String userName) {
    	Assert.state(type != SmsType.CUSTOMIZE,
    			"自定义内容使用构造函数SmsImpl(String phoneNumber, String customize)");
    	this.phoneNumber = phoneNumber;
    	key = phoneNumber + "-" + type;
    	this.type = type;
    	this.userName = userName;
    }

    
    /**
     * 发送自定义内容短信
     *
     * @param phoneNumber
     * @param customize
     */
    public SmsImpl(String phoneNumber, String customize) {
        this.phoneNumber = phoneNumber;
        this.type = SmsType.CUSTOMIZE;
        key = phoneNumber + "-" + type;
        setKeepTime(0);
        this.customize = customize;
    }

    /**
     * @param code 验证码, keepTime<=0 时 code==null
     */
    @Override
    protected void send(String code) {
        logger.info("验证码:" + code);
        String content = getTemplate(code);
        logger.debug(content);
/*        if (!StringUtil.isPhoneNumber(phoneNumber)) {
            logger.warn("手机号码错误");
            return;
        }*/
        String phoneNumber = this.phoneNumber.startsWith("+") ? this.phoneNumber : ("+(86)" + this.phoneNumber);
        StringBuffer buffer = new StringBuffer();
        try {
            content = URLEncoder.encode(content, encode);
//            content = URLEncoder.encode(content, encode);
            //对短信内容做Urlencode编码操作。注意：如 把发送链接存入buffer中，如连接超时，可能是您服务器不支持域名解析，
            // 请将下面连接中的：【m.5c.com.cn】修改为IP：【115.28.23.78】 hk.5c.com.cn    58.96.181.93
//            String host = "http://www.test.com";
            String host = "http://m.5c.com.cn";
            String param = "?username=" +
                    username + "&password_md5=" + password_md5 + "&mobile=" +
                    URLEncoder.encode(phoneNumber, encode) + "&apikey=" + apikey +
                    "&content=" + content + "&encode=" + encode;
//            param = URLEncoder.encode(param, encode);
            buffer.append(host + "/api/send/index.php" + param);
            // logger.info(buffer);
            URL url = new URL(buffer.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String result = reader.readLine();
            logger.info(result);
        } catch (Exception e) {
            logger.error("发送短信异常", e);
        }
    }

    /**
     * 这是模板
     */
    private String getTemplate(String code) {
    	
        long minutes = keepTime / TimeUtil.MILLIS_PER_MINUTE;
        if (type == SmsType.RESET_PASSWORD || type == SmsType.RESET_SECENDPWD)
            return "【MMD】尊敬的用户，您正在找回密码，验证码是：" + code + "，请不要把验证码泄露给其他人。"
                    + minutes + "分钟内有效。";
        else if (type == SmsType.REGISTER)
        	return"【MMD]】Dear user "+userName+", your new mine machine's CAPTCA is:" + code + ". please don't leak it to anyone else. Valid for"+minutes+" minutes."; 
//            return "【MMD】尊敬的用户，您新增矿机的验证码是:" + code + "，请不要把验证码泄露给其他人。"
//                    + minutes + "分钟内有效。";
        else if (type == SmsType.NOTIFICATION)
            return "【MMD】您有新的交易订单生成，请登录USC查看详情。祝您生活愉快";
        else if (type == SmsType.WITHDRAW)
            return "【MMD】您在申请提现，您的验证码是:" + code + "，若非您本人操作，请及时修改密码。";
        else if (type == SmsType.BUYSERORDER) {
            return "【MMD交易】您的节点编号：" + name + ",C2C交易购买订单已生成，请及时查看卖家信息并及时转账，并在平台确认已打款，3小时内未打款将影响节点信誉及不退还诚信金。";
        } else if (type == SmsType.SELLERORDER) {
            return "【MMD交易】您的节点编号：" + name + ",C2C交易挂卖已售出，请及时登录查看详情，并查询是否已收到款，收款后请及时为买家确认，3小时内未确认将影响节点信誉及不退还诚信金。";
        } else if (type == SmsType.SELLERCOMPLETE) {
            return "【MMD交易】您的节点编号：" + name + ",C2C购买订单已完成，卖家已确认收款，请及时登录查看相应资产。";
        } else if (type == SmsType.BUYERCOMPLETE) {
            return "【MMD交易】您的节点编号：" + name + ",C2C交易，买家已确认付款，请及时登录查看订单并确认收款，3小时内未确认将影响节点信誉及不退还诚信金。";
        } else if (type == SmsType.TRANSFER) {
        	//return "[MMD] Dear user "+userName+", you are transferring money, the verification code is: "+code+", please do not disclose the verification code to others. Valid for "+minutes+" minutes.";
        	return "【MMD】尊敬的"+userName+"用户，您正在转账，验证码是:" + code + "，请不要把验证码泄露给其他人。"
                    + minutes + "分钟内有效。";
        }else if (type == SmsType.CUSTOMIZE) {
            return customize;
        }

        throw new BusinessException("不支持的类型");
    }

    @Override
    protected String getKey() {
        return key;
    }

    public enum SmsType {
        /**
         * 注册
         */
        REGISTER,
        /**
         * 重置密码
         */
        RESET_PASSWORD,
        /**
         * 重置二级密码
         */
        RESET_SECENDPWD,
        /**
         * 交易通知短信
         */
        NOTIFICATION,
        /**
         * 提现
         */
        WITHDRAW,
        /**
         * 转账
         */
        TRANSFER,
        /**
         * 自定义
         */
        CUSTOMIZE,
        /**
         * 买家订单
         */
        BUYSERORDER,
        /**
         * 卖家订单
         */
        SELLERORDER,
        /**
         * 确认打款
         */
        BUYERCOMPLETE,
        /**
         * 确认收款
         */
        SELLERCOMPLETE,
        /**
         * 其他
         */
        OTHER
    }
}
