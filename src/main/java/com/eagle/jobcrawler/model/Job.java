package com.eagle.jobcrawler.model;

public class Job {
    private int id;
    private String jobId;
    private String jobName;
    private String city;
    private String workExperience;
    private String degree;
    private String treatment;
    private String detail;
    private String company;
    private String source;
    private String detailUrlPath;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getWorkExperience() {
        return workExperience;
    }

    public void setWorkExperience(String workExperience) {
        this.workExperience = workExperience;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDetailUrlPath() {
        return detailUrlPath;
    }

    public void setDetailUrlPath(String detailUrlPath) {
        this.detailUrlPath = detailUrlPath;
    }

    @Override
    public String toString() {
        return "Job{" +
                "jobId='" + jobId + '\'' +
                ", jobName='" + jobName + '\'' +
                ", city='" + city + '\'' +
                ", workExperience='" + workExperience + '\'' +
                ", degree='" + degree + '\'' +
                ", treatment='" + treatment + '\'' +
                ", company='" + company + '\'' +
                ", source='" + source + '\'' +
                ", detailUrlpath='" + detailUrlPath + '\'' +
                '}';
    }
}
