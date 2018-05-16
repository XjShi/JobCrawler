package com.eagle.jobcrawler;

public enum SourceType {
    Invalid("Invlid", null),
    BossZhipin("BossZhipin", "https://www.zhipin.com"),
    Lagou("Lagou", "https://www.lagou.com"),
    _51job("51job", "https://www.51job.com"),
    Zhilian("Zhilian", "https://www.zhaopin.com");

    private String name;
    private String baseUrl;

    SourceType(String name, String baseUrl) {
        this.name = name;
        this.baseUrl = baseUrl;
    }

    public String getName() {
        return name;
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
