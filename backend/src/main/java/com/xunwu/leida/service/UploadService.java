package com.xunwu.leida.service;

import com.xunwu.leida.common.BizException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * 图片上传:只收 image/* 且扩展名在白名单内,大小 ≤2MB,UUID 命名存本地
 */
@Service
public class UploadService {

    private static final long MAX_SIZE = 2L * 1024 * 1024;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");

    private final Path uploadDir;
    private final String publicBaseUrl;

    public UploadService(@Value("${app.upload.dir:uploads}") String dir,
                         @Value("${app.upload.public-base-url:}") String publicBaseUrl) {
        this.uploadDir = Paths.get(dir).toAbsolutePath().normalize();
        // 去掉末尾斜杠,避免拼出双斜杠
        this.publicBaseUrl = publicBaseUrl == null ? ""
                : publicBaseUrl.replaceAll("/+$", "");
    }

    /** 返回可直接提交的图片 url */
    public String saveImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BizException(400, "请选择要上传的图片");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new BizException(400, "图片大小不能超过 2MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new BizException(400, "只允许上传图片文件");
        }
        String extension = extensionOf(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BizException(400, "只支持 jpg/jpeg/png/gif/webp 格式");
        }
        // 文件名用 UUID,不使用原始文件名,避免路径穿越
        String filename = UUID.randomUUID() + "." + extension;
        try {
            Files.createDirectories(uploadDir);
            file.transferTo(uploadDir.resolve(filename));
        } catch (IOException e) {
            throw new BizException(500, "图片保存失败");
        }
        return publicBaseUrl + "/uploads/" + filename;
    }

    private String extensionOf(String originalFilename) {
        if (originalFilename == null) {
            return "";
        }
        int dot = originalFilename.lastIndexOf('.');
        if (dot < 0 || dot == originalFilename.length() - 1) {
            return "";
        }
        return originalFilename.substring(dot + 1).toLowerCase(Locale.ROOT);
    }
}
