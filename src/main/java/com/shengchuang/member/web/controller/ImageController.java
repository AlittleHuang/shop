package com.shengchuang.member.web.controller;

import com.shengchuang.common.mvc.view.JsonMap;
import com.shengchuang.common.util.Assert;
import com.shengchuang.common.util.StringUtil;
import com.shengchuang.common.util.SystemVariables;
import com.shengchuang.config.system.FilePath;
import com.shengchuang.base.AbstractController;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.View;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ImageController extends AbstractController {

    @Autowired
    public ImageController(FilePath filePath) {
        Util.imageUploadPath = filePath.getImageUploadPath();
        Util.imageResoursUri = filePath.getImageResoursUri();
    }

    @ResponseBody
    @RequestMapping({"/admin/upload", "/front/upload"})
    public View imageUpload(HttpServletRequest request,
                            @RequestParam("imageFile") MultipartFile multipartFile) {
        String fileName = Util.uploadImage(multipartFile);
        String preFix = request.getContextPath();
        preFix = preFix.equals("") ? "" : "/" + preFix;
        return new JsonMap().add("data", preFix + "/" + Util.getResourceUri(fileName));
    }

    @ResponseBody
    @RequestMapping({"/admin/upload/images", "/front/upload/images", ""})
    public View imagesUpload(HttpServletRequest request,
                             @RequestParam("images") MultipartFile[] images) {
        List<String> paths = new ArrayList<>();
        String preFix = request.getContextPath();
        preFix = preFix.equals("") ? "" : "/" + preFix;
        for (MultipartFile image : images) {
            paths.add(preFix + "/" + "anon/image/" + Util.uploadImage(image));
        }
        return new JsonMap().add("errno", 0).add("data", paths);
    }

    @ResponseBody
    @RequestMapping(value = {"/upload/image/{fileName}", "/anon/image/{fileName}"}, produces = "image/jpeg")
    public Resource getImageResource(@PathVariable String fileName) {
        return Util.getImageResource(fileName);
    }

    public static class Util {

        private static final Log logger = LogFactory.getLog(Util.class);

        /**
         * 文件保存路径
         */
        //@Value("${upload_path}") // 从config.properties读取
        private static String imageUploadPath;

        /**
         * 访问路径
         */
        private static String imageResoursUri;

        static {
            if (SystemVariables.IS_WINDOWS_SYSTEM) {
                imageUploadPath = "C:" + imageUploadPath;
            } else if (SystemVariables.IS_MAC_SYSTEM) {
                imageUploadPath = "/Volumes/COMMON" + imageUploadPath;
            }
        }

        /**
         * 通过读取文件并获取其width及height的方式，来判断判断当前文件是否图片
         *
         * @param imageFile
         * @return
         */
        public static boolean isImage(File imageFile) {
            if (!imageFile.exists()) {
                return false;
            }
            Image img;
            try {
                img = ImageIO.read(imageFile);
                return isImage(img);
            } catch (Exception e) {
                return false;
            }
        }

        public static boolean isNotImage(File imageFile) {
            return !isImage(imageFile);
        }

        public static boolean isNotImage(String pathname) {
            return !isImage(new File(pathname));
        }

        public static boolean isImage(InputStream imageFile) {
            Image img;
            try {
                img = ImageIO.read(imageFile);
                return isImage(img);
            } catch (Exception e) {
                return false;
            }
        }

        public static boolean isImage(Image img) {
            try {
                return (img != null) && (img.getWidth(null) > 0) && (img.getHeight(null) > 0);
            } catch (Exception e) {
                return false;
            }
        }

        public static boolean isImage(MultipartFile file) {
            if (file == null || !file.getContentType().startsWith("image"))
                return false;
            try {
                return isImage(file.getInputStream());
            } catch (IOException e) {
                return false;
            }
        }

        public static boolean isNotImage(MultipartFile file) {
            return !isImage(file);
        }

        public static String getFileMD5(MultipartFile file) {
            if (file == null) {
                return null;
            }
            try {
                return getMD5(file.getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public static String getFileMD5(File file) {
            if (file == null) {
                return null;
            }
            try {
                return getMD5(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static String getMD5(InputStream in) {
            if (in == null) {
                return null;
            }
            MessageDigest digest;
            byte buffer[] = new byte[1024];
            int len;
            try {
                digest = MessageDigest.getInstance("MD5");
                while ((len = in.read(buffer, 0, 1024)) != -1) {
                    digest.update(buffer, 0, len);
                }
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            BigInteger bigInt = new BigInteger(1, digest.digest());
            return bigInt.toString(16);
        }

        /**
         * 根据文件名获取资源
         *
         * @param fileName
         * @return
         */
        public static Resource getImageResource(String fileName) {
            String imagePath = getpathFix(fileName);
            logger.debug(imagePath);
            File file = new File(imagePath);
            if (isNotImage(file)) {
                return null;
            }
            return new FileSystemResource(file);
        }

        public static String getpathFix(String fileName) {
            String folderFix = "";
            int hashCode = fileName.hashCode();
            int x = Math.abs(hashCode % 100);
            if (x < 10)
                folderFix = "0";
            String folder = folderFix + x + "/";
            return imageUploadPath + folder + fileName;
        }

        public static File getImageFile(String hmPath) {
            if (StringUtil.isEmpty(hmPath)) return null;
            String[] tmp = hmPath.split("/");
            hmPath = tmp[tmp.length - 1];
            try {
                return getImageResource(hmPath).getFile();
            } catch (IOException e) {
                return null;
            }
        }

        /**
         * 根据文件名获取访问路径
         *
         * @param fileName
         * @return
         */
        public static String getResourceUri(String fileName) {
            return imageResoursUri + fileName;
        }

        /**
         * 保存图片到本地
         *
         * @param file SpringMvc上传的图片
         * @return 文件名
         */
        public static String uploadImage(MultipartFile file) {
            if (file == null)
                return null;
            if (isNotImage(file)) {
                return null;
            }
            Assert.state(isImage(file), "文件格式错误");
            String md5 = getFileMD5(file);
            String fileName = md5;
            String pathFix = getpathFix(fileName);
            File out = new File(pathFix);
            File folder = out.getParentFile();
            if (!folder.exists()) {
                folder.mkdirs();
            }
            if (!out.exists()) {
                try {
                    FileCopyUtils.copy(file.getBytes(), out);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            return fileName;
        }
    }

}
