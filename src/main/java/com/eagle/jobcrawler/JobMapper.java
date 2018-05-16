package com.eagle.jobcrawler;

import com.eagle.jobcrawler.model.Job;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface JobMapper {
    List<Job> selectJob();
    boolean insertOne(Job job);
    boolean insertJobBatch(@Param("jobList") List<Job> jobs);
    List<String> selectJobIdList(@Param("jobIdList") List<String> jobIdLists);
}
