package com.eagle.jobcrawler;

import com.eagle.jobcrawler.model.Info;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Start {
    public static void main(String[] args) throws IOException {

        // 1.选择爬取网站
        SourceType sourceType = promptSelectSource();
        if (sourceType == SourceType.Invalid) {
            System.out.println("选择无效");
            return;
        }
        Crawler crawler = new Crawler();
        crawler.setSourceType(sourceType);

        // 2.选择爬取职位
        List<Info.Data> dataList = ConfigReader.readJobInfo(sourceType);
        List<java.lang.String> items = dataList.stream().map(Info.Data::getKey).collect(Collectors.toList());
        int select = 0;
        int baseIndex = 1;
        try {
            select = promptSelect("请选择要爬取的职位：", baseIndex, items);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        }
        crawler.setJobId(dataList.get(select - baseIndex).getValue());

        // 3.选择爬取的城市
        dataList = ConfigReader.readCityInfo(sourceType);
        items = dataList.stream().map(Info.Data::getKey).collect(Collectors.toList());
        try {
            select = promptSelect("请选择要爬取的城市：", baseIndex, items);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        }
        crawler.setCityId(dataList.get(select - baseIndex).getValue());
        // 4.爬取
        crawler.start();
    }

    private static SourceType promptSelectSource() throws IOException {
        System.out.println(
                "选择要爬取的网站:\n" +
                        "1. Boss直聘\n" +
                        "2. 拉勾网\n" +
                        "3. 51job\n" +
                        "4. 智联招聘\n");
        int read = System.in.read();
        switch (read) {
            case '1':
                return SourceType.BossZhipin;
            case '2':
                return SourceType.Lagou;
            case '3':
                return SourceType._51job;
            case '4':
                return SourceType.Zhilian;
        }
        return SourceType.Invalid;
    }

    private static int promptSelect(java.lang.String prompt, int baseIndex, List<java.lang.String> itemList) throws IllegalArgumentException {
        StringBuilder sb = new StringBuilder();
        sb.append(prompt).append("\n");
        for (int index = baseIndex; index < itemList.size(); index++) {
            sb.append(index).append(". ").append(itemList.get(index - baseIndex)).append("\n");
        }
        System.out.println(sb);
        Scanner sc = new Scanner(System.in);
        int value = sc.nextInt();
        if (value < baseIndex || value >= baseIndex + itemList.size()) {
            throw new IllegalArgumentException("您输入的序号无效：" + value);
        }
        return value;
    }
}
