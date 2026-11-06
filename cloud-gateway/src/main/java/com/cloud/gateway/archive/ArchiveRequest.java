package com.cloud.gateway.archive;

import java.io.Serializable;

/**
 * 档案创建/更新请求体。
 */
public class ArchiveRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;
    private String category;
    private String description;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
