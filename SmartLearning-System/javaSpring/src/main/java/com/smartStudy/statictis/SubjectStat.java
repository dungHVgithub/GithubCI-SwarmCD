package com.smartStudy.statictis;

public class SubjectStat {
    private String subjectName;
    private Long totalCount;

    public  SubjectStat(String subjectName, Long totalCount)
    {
        this.subjectName = subjectName;
        this.totalCount = totalCount;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }
}
