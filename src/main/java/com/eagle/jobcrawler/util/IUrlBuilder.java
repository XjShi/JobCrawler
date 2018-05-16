package com.eagle.jobcrawler.util;

public interface IUrlBuilder {
    String buildPageUrl(String cityId, String jobId, int pageIndex);
}
