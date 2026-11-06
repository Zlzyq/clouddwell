package com.cloud.auth.demo;

/**
 * 用户档案（内存数据）。
 */
public final class DemoUserProfile {

    private final Long userId;
    private final String userName;
    private final String role;
    private final String avatarUrl;

    public DemoUserProfile(Long userId, String userName, String role, String avatarUrl) {
        this.userId = userId;
        this.userName = userName;
        this.role = role;
        this.avatarUrl = avatarUrl;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getRole() {
        return role;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public static DemoUserProfile of(Long userId) {
        return switch (userId.intValue()) {
            case 1 -> new DemoUserProfile(1L, "管理员", "超级管理员", "/img/avatar-1.png");
            case 2 -> new DemoUserProfile(2L, "运营专员", "运营专员", "/img/avatar-2.svg");
            case 3 -> new DemoUserProfile(3L, "测试用户A", "普通用户", "/img/avatar-3.svg");
            case 4 -> new DemoUserProfile(4L, "测试用户B", "普通用户", "/img/avatar-3.svg");
            case 5 -> new DemoUserProfile(5L, "测试用户C", "普通用户", "/img/avatar-3.svg");
            case 6 -> new DemoUserProfile(6L, "测试用户D", "普通用户", "/img/avatar-3.svg");
            case 7 -> new DemoUserProfile(7L, "测试用户E", "普通用户", "/img/avatar-3.svg");
            case 8 -> new DemoUserProfile(8L, "测试用户F", "普通用户", "/img/avatar-3.svg");
            case 9 -> new DemoUserProfile(9L, "测试用户G", "普通用户", "/img/avatar-3.svg");
            case 10 -> new DemoUserProfile(10L, "测试用户H", "普通用户", "/img/avatar-3.svg");
            case 11 -> new DemoUserProfile(11L, "测试用户I", "普通用户", "/img/avatar-3.svg");
            case 12 -> new DemoUserProfile(12L, "测试用户J", "普通用户", "/img/avatar-3.svg");
            case 13 -> new DemoUserProfile(13L, "测试用户K", "普通用户", "/img/avatar-3.svg");
            case 14 -> new DemoUserProfile(14L, "测试用户L", "普通用户", "/img/avatar-3.svg");
            case 15 -> new DemoUserProfile(15L, "测试用户M", "普通用户", "/img/avatar-3.svg");
            case 16 -> new DemoUserProfile(16L, "测试用户N", "普通用户", "/img/avatar-3.svg");
            case 17 -> new DemoUserProfile(17L, "测试用户O", "普通用户", "/img/avatar-3.svg");
            case 18 -> new DemoUserProfile(18L, "测试用户P", "普通用户", "/img/avatar-3.svg");
            case 19 -> new DemoUserProfile(19L, "测试用户Q", "普通用户", "/img/avatar-3.svg");
            case 20 -> new DemoUserProfile(20L, "测试用户R", "普通用户", "/img/avatar-3.svg");
            case 10086 -> new DemoUserProfile(10086L, "普通用户", "普通用户", "/img/avatar-3.svg");
            case 10010 -> new DemoUserProfile(10010L, "小熊专属账号", "超级管理员", "/img/avatar-1.png");
            default -> null;
        };
    }
}
