package com.edulearn.kelompok3.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassModel {
    private String classId;
    private String name;
    private String code;
    private String description;
    private String teacherId;
    private String teacherName;
    private long createdAt;
    private Map<String, Boolean> studentIds; // Gunakan Map untuk Realtime DB

    public ClassModel() {
        this.studentIds = new HashMap<>();
    }

    public ClassModel(String classId, String name, String code, String description, String teacherId, String teacherName) {
        this.classId = classId;
        this.name = name;
        this.code = code;
        this.description = description;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.createdAt = System.currentTimeMillis();
        this.studentIds = new HashMap<>();
    }

    // Getters dan Setters
    public String getClassId() { return classId; }
    public void setClassId(String classId) { this.classId = classId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getTeacherId() { return teacherId; }
    public void setTeacherId(String teacherId) { this.teacherId = teacherId; }
    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public Map<String, Boolean> getStudentIds() { return studentIds; }
    public void setStudentIds(Map<String, Boolean> studentIds) { this.studentIds = studentIds; }
}
