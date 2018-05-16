package com.eagle.jobcrawler.crawler;

import com.eagle.jobcrawler.HttpHeaderValueConstants;
import org.apache.http.HttpHeaders;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ZhipinDetailCrawler implements IDetailCrawler {
    private static final Logger logger = LoggerFactory.getLogger(ZhipinDetailCrawler.class);
    public String query(String subPath) {
        logger.debug("爬取详情:" + subPath);
        Document doc = null;
        try {
            doc = Jsoup.connect("https://www.zhipin.com" + subPath)
                    .header(HttpHeaders.USER_AGENT, HttpHeaderValueConstants.UserAgent)
                    .header(HttpHeaders.ACCEPT, HttpHeaderValueConstants.Accept)
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        Elements elements = doc.select("#main > div.job-box > div > div.job-detail > div.detail-content > div.job-sec");
        String detail = null;
        for (Element ele : elements) {
            if (ele.select("h3").first().html().contentEquals("职位描述")) {
                detail = ele.select(".text").first().html();
                break;
            }
        }
        if (detail != null) {
            logger.debug("爬取详情:" + subPath + "成功");
        } else {
            logger.debug("爬取详情:" + subPath + "失败");
        }
        return detail;
    }
}
