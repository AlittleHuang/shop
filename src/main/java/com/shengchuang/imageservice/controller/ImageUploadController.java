package com.shengchuang.imageservice.controller;

import com.shengchuang.common.mvc.view.JsonMap;
import com.shengchuang.common.util.JsonUtil;
import com.shengchuang.imageservice.controller.domain.Uploader;
import com.shengchuang.imageservice.utils.ImageUploadUtil;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping({"/image-service", ""})
public class ImageUploadController {

    @ResponseBody
    @RequestMapping({"/image/upload"})
    public View imageUpload(HttpServletRequest request, MultipartFile multipartFile) {
        String fileName = ImageUploadUtil.uploadImage(multipartFile);
        String preFix = request.getContextPath();
        preFix = preFix.equals("") ? "" : "/" + preFix;
        return new JsonMap().add("url", preFix + ImageUploadUtil.getResourceUri(fileName));
    }

    @ResponseBody
    @RequestMapping("/umediter/image/upload")
    public String imageUpload(HttpServletRequest request) {

        Uploader up = new Uploader(request);
        up.upload();

        String result = JsonUtil.encode(up);

        String callback = request.getParameter("callback");
        if (callback == null) {
            return result;
        } else {
            return ("<script>" + callback + "(" + result + ")</script>");
        }
    }

    @ResponseBody
    @RequestMapping(value = "/{fileName}", produces = "image/jpeg")
    public Resource getImageResource(@PathVariable String fileName) {
        return ImageUploadUtil.getImageResource(fileName);
    }

}
