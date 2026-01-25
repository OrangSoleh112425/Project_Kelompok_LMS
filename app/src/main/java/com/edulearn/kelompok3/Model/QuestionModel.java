package com.edulearn.kelompok3.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class QuestionModel {
    private String questionId;
    private String questionText;
    private List<String> options;
    private int correctAnswer;
    private int order;

    public QuestionModel() {
        this.options = new ArrayList<>();
    }

    // Getters dan Setters
    public String getQuestionId() { return questionId; }
    public void setQuestionId(String questionId) { this.questionId = questionId; }
    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }
    public int getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(int correctAnswer) { this.correctAnswer = correctAnswer; }
    public int getOrder() { return order; }
    public void setOrder(int order) { this.order = order; }
}
