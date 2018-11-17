package com.shengchuang.member.web.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.shengchuang.common.mvc.view.JsonMap;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.base.AbstractController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Controller
public class ShareController extends AbstractController {

    /**
     * 推广链接二维码
     */
    @RequestMapping("/front/qrcode.png")
    public void myQRCode(HttpServletRequest request, HttpServletResponse resp) throws IOException, WriterException {

        String promotionUrl = getRegisterUrl();// 推广链接
        if (StringUtils.isBlank(promotionUrl)) {
            return;
        }
        writeQrCode2Response(resp, promotionUrl);
    }

    /**
     * 转账二维码
     */
    @RequestMapping("/front/transferqrcode.png")
    public void transferQRCode(HttpServletRequest request, HttpServletResponse resp)
            throws IOException, WriterException {

        String promotionUrl = getTransferUrl();// 转账链接
        if (StringUtils.isBlank(promotionUrl)) {
            return;
        }
        writeQrCode2Response(resp, promotionUrl);
    }

    @RequestMapping("/front/receipt/qrcode.png")
    public void receiptQrcode() {

    }

    private void writeQrCode2Response(HttpServletResponse resp, String content)
            throws IOException, WriterException {
        try (ServletOutputStream stream = resp.getOutputStream()) {
            int width = 300;// 图片的宽度
            int height = 300;// 高度
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix m = writer.encode(content, BarcodeFormat.QR_CODE, width, height);
            //1.1去白边
            int[] rec = m.getEnclosingRectangle();

            int resWidth = rec[2] + 1;

            int resHeight = rec[3] + 1;

            BitMatrix resMatrix = new BitMatrix(resWidth, resHeight);

            resMatrix.clear();

            for (int i = 0; i < resWidth; i++) {

                for (int j = 0; j < resHeight; j++) {

                    if (m.get(i + rec[0], j + rec[1])) {
                        resMatrix.set(i, j);
                    }
                }

            }
            //2
            width = resMatrix.getWidth();
            height = resMatrix.getHeight();
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            for (int x = 0; x < width; x++) {

                for (int y = 0; y < height; y++) {

                    image.setRGB(x, y, resMatrix.get(x, y) == true ?

                            Color.BLACK.getRGB() : Color.WHITE.getRGB());
                }

            }
            MatrixToImageWriter.writeToStream(resMatrix, "png", stream);
            stream.flush();
        }

    }


    /**
     * 推广链接二维码
     */
    @RequestMapping("/front/share/link")
    public JsonMap registerUrl() {
        return new JsonMap().add("url", getRegisterUrl());
    }


    private String getRegisterUrl() {
        HttpServletRequest request = request();
        User member = getSessionUser();
        String path = request.getContextPath();
        String tglink = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String basepath = tglink + path + "/";
        String suffix = "fronts/register.html?u=" + member.getUsername();
        return basepath + suffix;
    }

    private String getTransferUrl() {
        HttpServletRequest request = request();
        User member = getSessionUser();
        String username = member.getUsername();
        String path = request.getContextPath();
        //String tglink = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        //String basepath = tglink + path + "/";
        //String suffix = "zhuanliancelog.html?test=" + username;
        return username;
    }

}
