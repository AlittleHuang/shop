package com.shengchuang.config.system;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FilePath {

    @Value("${system.image.upload.path}")
    private String imageUploadPath;

    @Value("${system.image.resours.uri}")
    private String imageResoursUri;

    public String getImageResoursUri() {
        return imageResoursUri;
    }

    public String getImageUploadPath() {
        return imageUploadPath;
    }
}
