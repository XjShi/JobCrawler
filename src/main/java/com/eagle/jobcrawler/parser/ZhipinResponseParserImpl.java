package com.eagle.jobcrawler.parser;

import com.eagle.jobcrawler.model.Job;
import com.eagle.jobcrawler.SourceType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class ZhipinResponseParserImpl implements IResponseParser {
    public List<Job> parse(String htmlText) {
        Document document = Jsoup.parse(htmlText);
        Elements jobElements = document.select("#main > div > div.job-list > ul > li > div.job-primary");
        List<Job> jobs = new ArrayList<Job>();
        for (Element jobElement : jobElements) {
            // parse primary info
            Elements primaryInfo = jobElement.select(".info-primary");
            Elements topInfo = primaryInfo.select(".name > a"); // Java开发工程师 10k-20k
            String jobId = topInfo.attr("data-jid");
            String detailSubPath = topInfo.first().attr("href");
            String jobName = topInfo.select(".job-title").first().html();
            String treatment = topInfo.select(".red").first().html();
            Elements bottomInfo = primaryInfo.select("p");
            List<String> list = parseBottomInfo(bottomInfo.first());

            // parse company info
            Elements companyInfo = jobElement.select(".info-company > .company-text");
            topInfo = companyInfo.select(".name > a"); // 搜狐畅游

            Job job = new Job();
            job.setJobId(jobId);
            job.setJobName(jobName);
            job.setTreatment(treatment);
            job.setCity(list.get(0));
            job.setWorkExperience(list.get(1));
            job.setDegree(list.get(2));
            job.setCompany(topInfo.first().html());
            job.setSource(SourceType.BossZhipin.name());
            job.setDetailUrlPath(detailSubPath);

            jobs.add(job);
        }
        return jobs;
    }

    /**
     * 解析 "北京 | 3-5年 | 不限"、"计算机软件 | D轮 | 1000人以上"
     * @param element
     * @return (地址、年限、学历)、（行业、公司规模、员工人数)
     */
    private List<String> parseBottomInfo(Element element) {
        Elements vlines = element.select(".vline");
        String text = element.html();
        String[] split = text.split(vlines.first().outerHtml());
        List<String> list = new ArrayList<String>(split.length);
        for (String s : split) {
            s = s.trim();
            list.add(s);
        }
        return list;
    }
}
