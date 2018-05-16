package com.eagle.jobcrawler.parser;

import com.eagle.jobcrawler.model.Job;

import java.util.List;

public interface IResponseParser {
    List<Job> parse(String htmlText);
}
