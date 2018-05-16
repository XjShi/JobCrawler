package com.eagle.jobcrawler.util;

import com.eagle.jobcrawler.SourceType;

public class UrlBuilderFactory {
    public static IUrlBuilder newUrlBuild(SourceType sourceType) {
        switch (sourceType) {
            case BossZhipin:
                return new BossZhipinUrlBuilder();
            default:
                return null;
        }
    }
}
