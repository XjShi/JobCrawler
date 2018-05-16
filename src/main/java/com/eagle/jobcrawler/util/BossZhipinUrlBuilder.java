package com.eagle.jobcrawler.util;

import com.eagle.jobcrawler.SourceType;

public class BossZhipinUrlBuilder implements IUrlBuilder {
    @Override
    public String buildPageUrl(String cityId, String jobId, int pageIndex) {
        return SourceType.BossZhipin.getBaseUrl() + "/" + cityId + "-" + jobId + "/?page=" + pageIndex;
    }
}
