package com.eagle.jobcrawler.database;

import com.eagle.jobcrawler.JobMapper;
import com.eagle.jobcrawler.model.Job;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DBUtils {
    static int jid = 40;

    private SqlSessionFactory sqlSessionFactory = null;

    private static class SingletonHolder {
        private static final DBUtils instance = new DBUtils();
    }

    private DBUtils() {
        try {
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsStream("database.xml"));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static final DBUtils getInstance() {
        return SingletonHolder.instance;
    }

    public void insertBatch(List<Job> jobList) {
        if (jobList == null) {
            return;
        }
        SqlSession session = sqlSessionFactory.openSession();
        try {
            JobMapper mapper = session.getMapper(JobMapper.class);
            mapper.insertJobBatch(jobList);
            session.commit();
        } finally {
            session.close();
        }
    }

    public void insertOne(Job job) {
        if (job == null) {
            return;
        }
        SqlSession session = sqlSessionFactory.openSession();
        try {
            JobMapper mapper = session.getMapper(JobMapper.class);
            mapper.insertOne(job);
            session.commit();
        } finally {
            session.close();
        }
    }

    public List<String> selectJobIdList(List<String> jobIdList) {
        if (jobIdList == null || jobIdList.size() == 0) {
            return null;
        }
        SqlSession session = sqlSessionFactory.openSession();
        try {
            JobMapper mapper = session.getMapper(JobMapper.class);
            return mapper.selectJobIdList(jobIdList);
        } finally {
            session.close();
        }
    }

    public void foo() throws IOException {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            JobMapper mapper = session.getMapper(JobMapper.class);
            List<Job> list = randJobList();
            mapper.insertJobBatch(list);
            session.commit();
        } finally {
            session.close();
        }
    }

    private static Job randamJob() {
        Job job = new Job();
        job.setJobId(String.valueOf(jid));
        job.setJobName("tmp" + jid);
        job.setCity("");
        job.setCompany("company" + jid);
        job.setDegree("hahah");
        jid++;
        return job;
    }

    private static List<Job> randJobList() {
        List<Job> jobs = new ArrayList<Job>(5);
        for (int i = 0; i < 5; i++) {
            jobs.add(randamJob());
        }
        return jobs;
    }
}
