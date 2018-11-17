package com.shengchuang.imageservice.controller.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.shengchuang.imageservice.utils.ImageUploadUtil;
import lombok.Getter;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;


public class Uploader {
    // 输出文件地址
    private String url = "";
    // 上传文件名
    private String name = "";
    // 状态
    private String state = "";
    // 文件类型
    private String type = "";

    private String originalName = "";

    private String title = "";

    @JSONField(serialize = false)
    private HttpServletRequest request;

    public Uploader(HttpServletRequest request) {
        this.request = request;
    }

    public void upload() {
        boolean isMultipart = ServletFileUpload.isMultipartContent(this.request);
        if (!isMultipart) {
            this.state = "未上传文件";
            return;
        }

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile multipartFile = multipartRequest.getFile("upfile");

        this.name = ImageUploadUtil.uploadImage(multipartFile);
        this.url = ImageUploadUtil.getResourceUri(this.name);
        this.originalName = multipartFile.getOriginalFilename();
        this.type = originalName.substring(originalName.lastIndexOf("."));
        this.state = "SUCCESS";

    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public String getState() {
        return state;
    }

    public String getType() {
        return type;
    }

    public String getOriginalName() {
        return originalName;
    }

    public String getTitle() {
        return title;
    }
}
