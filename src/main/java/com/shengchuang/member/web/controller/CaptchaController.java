package com.shengchuang.member.web.controller;

import com.shengchuang.common.mvc.view.JsonMap;
import com.shengchuang.base.AbstractController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

/**
 * 验证码
 *
 * @author HuangChengwei
 */
@Controller
public class CaptchaController extends AbstractController {

    public static final String LOGIN_CAPTCHA = "login.captcha";
    public static final String VERIFICATION_CODE = "VERIFICATION_CODE";

    @RequestMapping("/captcha.jpg")
    public void captcha(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        //设置页面不缓存
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");

        HttpSession session = request.getSession(true);

        int width = 73;
        int height = 35;
        BufferedImage image = new BufferedImage(width, height, 1);

        Graphics g = image.getGraphics();

        Random random = new Random();

        g.setColor(getRandColor(100, 200));
        g.fillRect(0, 0, width, height);

        g.setFont(new Font("Arial", 0, 24));

        g.setColor(getRandColor(200, 250));
        for (int i = 0; i < 188; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            g.drawLine(x, y, x + xl, y + yl);
        }
        StringBuilder sb = new StringBuilder(4);
        for (int i = 0; i < 4; i++) {
            String num = String.valueOf(random.nextInt(10));
            g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110),
                    20 + random.nextInt(110)));
            g.drawString(num, 13 * i + 6, 24);
            sb.append(num);
        }
        session.setAttribute(VERIFICATION_CODE, sb.toString());
        g.dispose();
        ServletOutputStream responseOutputStream = response.getOutputStream();

        ImageIO.write(image, "JPEG", responseOutputStream);

        responseOutputStream.flush();
        responseOutputStream.close();
    }

    //给定范围获得随机颜色
    private Color getRandColor(int fc, int bc) {
        Random random = new Random();
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    @PostMapping("/check/captcha")
    public JsonMap checkCaptcha(String code) {
        JsonMap jsonMap = new JsonMap();
        String sessionCode = (String) getSession().getAttribute(VERIFICATION_CODE);
        if (code != null && !code.equalsIgnoreCase(sessionCode)) {
            return jsonMap.failedMsg("验证码错误");
        }
        getSession().removeAttribute(VERIFICATION_CODE);
        getSession().setAttribute(LOGIN_CAPTCHA, true);
        return jsonMap;
    }

}
