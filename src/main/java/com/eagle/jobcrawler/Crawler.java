package com.eagle.jobcrawler;

import com.eagle.jobcrawler.crawler.DetailCrawlerFactory;
import com.eagle.jobcrawler.crawler.IDetailCrawler;
import com.eagle.jobcrawler.database.DBUtils;
import com.eagle.jobcrawler.model.Job;
import com.eagle.jobcrawler.parser.IResponseParser;
import com.eagle.jobcrawler.parser.ZhipinResponseParserImpl;
import com.eagle.jobcrawler.util.IUrlBuilder;
import com.eagle.jobcrawler.util.UrlBuilderFactory;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.Source;
import java.io.IOException;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class Crawler {
    private static final Logger logger = LoggerFactory.getLogger(Crawler.class);

    private ScheduledExecutorService pageCrawleScheduledService = Executors.newScheduledThreadPool(5);
    private ScheduledExecutorService detailCrawleScheduleServie = Executors.newScheduledThreadPool(5);

    private SourceType sourceType;
    private String jobId;
    private String cityId;

    private Queue<Job> jobQueue = new ConcurrentLinkedDeque<>();
    private List<Job> jobList = new CopyOnWriteArrayList<>();   // 等待插入数据库的job

    public void setSourceType(SourceType sourceType) {
        this.sourceType = sourceType;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    @Override
    public String toString() {
        return "Crawler{" +
                "sourceType=" + sourceType +
                ", jobId='" + jobId + '\'' +
                ", cityId='" + cityId + '\'' +
                '}';
    }

    public void start() {
        pageCrawleScheduledService.scheduleAtFixedRate(buildTask(), 0, 5, TimeUnit.SECONDS);
        detailCrawleScheduleServie.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Job job = jobQueue.poll();
                if (job == null) {
                    return;
                }
                IDetailCrawler detailCrawler = DetailCrawlerFactory.detailCrawler(SourceType.BossZhipin);
                String detail = detailCrawler.query(job.getDetailUrlPath());
                if (detail != null) {
                    job.setDetail(detail);
                }
                jobList.add(job);
                insertToDatabase();
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    private CrawleTask buildTask() {
        CrawleTask task = new CrawleTask(SourceType.BossZhipin, cityId, jobId);
        task.setCallback(new PageCrawleTaskCallback() {
            @Override
            public void callback(List<Job> jobList, boolean stop) {
                if (stop) {
                    pageCrawleScheduledService.shutdown();
                }
                if (jobList != null) {
                    Crawler.this.jobQueue.addAll(jobList);
                }
            }
        });
        return task;
    }

    /**
     * 把查询好的数据插入数据库
     */
    private void insertToDatabase() {
        if (jobList.size() >= 100) {
            List<String> jobIds = jobList.stream().map(job -> job.getJobId()).collect(Collectors.toList());
            List<String> existsJobIds = DBUtils.getInstance().selectJobIdList(jobIds);
            logger.info("有" + jobList.size() + "条数据准备插入数据库，" +
                    "其中有" + existsJobIds.size() + "条在数据库中已经存在");
            if (existsJobIds != null && existsJobIds.size() > 0) {
                jobList = jobList.stream().filter(job -> !existsJobIds.contains(job.getJobId())).collect(Collectors.toList());
            }
            // 插入数据库
            logger.debug("向db增加" + jobList.size() + "条数据");
            DBUtils.getInstance().insertBatch(jobList);
        }
    }

//    public void start() throws IOException {
//        CloseableHttpClient httpClient = HttpClients.createDefault();
//        try {
//            String url = "https://" + sourceType.getBaseUrl() + "/" + cityId + "-" + jobId;
//            HttpGet httpGet = new HttpGet(url);
//            httpGet.setHeader(HttpHeaders.USER_AGENT, HttpHeaderValueConstants.UserAgent);
//            httpGet.setHeader(HttpHeaders.ACCEPT, HttpHeaderValueConstants.Accept);
//
//            final ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
//                public String handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {
//                    int status = httpResponse.getStatusLine().getStatusCode();
//                    if (status >= HttpStatus.SC_OK && status < HttpStatus.SC_MULTIPLE_CHOICES) {
//                        HttpEntity entity = httpResponse.getEntity();
//                        return entity != null ? EntityUtils.toString(entity) : null;
//                    } else {
//                        throw new ClientProtocolException("Unexpected response status: n" + status);
//                    }
//                }
//            };
//            logger.info("开始爬取:" + url);
//            String responseBody = httpClient.execute(httpGet, responseHandler);
//            IResponseParser responseParser = new ZhipinResponseParserImpl();
//            List<Job> jobs = responseParser.parse(responseBody);
//            IDetailCrawler detailCrawler = DetailCrawlerFactory.detailCrawler(SourceType.BossZhipin);
//            for (Job job : jobs) {
//                String detail = detailCrawler.query(job.getDetailUrlPath());
//                if (detail != null) {
//                    job.setDetail(detail);
//                }
//            }
//            logger.debug("获取到" + jobs.size() + "条数据");
//            // 过滤在数据库中已经存在的item
//            List<String> jobIds = jobs.stream().map(job -> job.getJobId()).collect(Collectors.toList());
//            List<String> existsJobIds = DBUtils.getInstance().selectJobIdList(jobIds);
//            if (existsJobIds != null && existsJobIds.size() > 0) {
//                jobs = jobs.stream().filter(job -> !existsJobIds.contains(job.getJobId())).collect(Collectors.toList());
//            }
//            // 插入数据库
//            logger.debug("向db增加" + jobs.size() + "条数据");
//            DBUtils.getInstance().insertBatch(jobs);
//        } finally {
//            httpClient.close();
//        }
//    }
}

class CrawleTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(CrawleTask.class);

    private SourceType sourceType;
    private String cityId;
    private String jobId;

    private int basePageIndex = 1;
    private Lock lock = new ReentrantLock();

    private PageCrawleTaskCallback callback;

    public CrawleTask(SourceType sourceType, String cityId, String jobId) {
        this.sourceType = sourceType;
        this.cityId = cityId;
        this.jobId = jobId;
    }

    public void setCallback(PageCrawleTaskCallback callback) {
        this.callback = callback;
    }

    @Override
    public void run() {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        IUrlBuilder urlBuilder = UrlBuilderFactory.newUrlBuild(sourceType);
        assert urlBuilder != null;
        String url = urlBuilder.buildPageUrl(cityId, jobId, basePageIndex);
        lock.lock();
        basePageIndex++;
        lock.unlock();
        try {
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader(HttpHeaders.USER_AGENT, HttpHeaderValueConstants.UserAgent);
            httpGet.setHeader(HttpHeaders.ACCEPT, HttpHeaderValueConstants.Accept);

            final ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
                public String handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {
                    int status = httpResponse.getStatusLine().getStatusCode();
                    if (status != HttpStatus.SC_OK) {
                        logger.debug("request failed: " + httpResponse.getStatusLine());
                    }
                    if (status >= HttpStatus.SC_OK && status < HttpStatus.SC_MULTIPLE_CHOICES) {
                        HttpEntity entity = httpResponse.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: n" + status);
                    }
                }
            };
            logger.info("开始爬取:" + url);
            String responseBody = httpClient.execute(httpGet, responseHandler);
            IResponseParser responseParser = new ZhipinResponseParserImpl();
            List<Job> jobs = responseParser.parse(responseBody);
            logger.info("爬取 " + url + " 获取到" + jobs.size() + "条数据" );
            if (callback != null) {
                callback.callback(jobs, basePageIndex >= 11);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}

interface PageCrawleTaskCallback {
    void callback(List<Job> jobList, boolean stop);
}
