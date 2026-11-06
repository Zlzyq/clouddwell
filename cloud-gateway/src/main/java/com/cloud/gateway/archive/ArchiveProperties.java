package com.cloud.gateway.archive;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 档案文件存储目录（相对网关进程工作目录，默认 ./data/archives）。
 */
@ConfigurationProperties(prefix = "cloud.gateway.archive")
public class ArchiveProperties {

    /** 档案根目录：元数据 meta.json + files/{id}/ */
    private String uploadDir = "./data/archives";

    /** 单文件最大字节，默认 10MB */
    private long maxFileSize = 10 * 1024 * 1024L;

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }
}
