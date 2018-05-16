package com.eagle.jobcrawler.crawler;

import com.eagle.jobcrawler.SourceType;

public class DetailCrawlerFactory {
    public static IDetailCrawler detailCrawler(SourceType sourceType) {
        switch (sourceType) {
            case BossZhipin:
                return new ZhipinDetailCrawler();
            default:
                return null;
        }
    }
}
