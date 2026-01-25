package com.edulearn.kelompok3.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubmissionModel {
    private String submissionId;
    private String assignmentId;
    private String studentId;
    private String studentName;
    private Map<String, Integer> answers; // questionId -> selectedAnswer
    private double score;
    private long submittedAt;
    private boolean isGraded;

    public SubmissionModel() {
        this.answers = new HashMap<>();
    }

    // Getters dan Setters
    public String getSubmissionId() { return submissionId; }
    public void setSubmissionId(String submissionId) { this.submissionId = submissionId; }
    public String getAssignmentId() { return assignmentId; }
    public void setAssignmentId(String assignmentId) { this.assignmentId = assignmentId; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public Map<String, Integer> getAnswers() { return answers; }
    public void setAnswers(Map<String, Integer> answers) { this.answers = answers; }
    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
    public long getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(long submittedAt) { this.submittedAt = submittedAt; }
    public boolean isGraded() { return isGraded; }
    public void setGraded(boolean graded) { isGraded = graded; }
}
