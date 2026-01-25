package com.edulearn.kelompok3.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleModel {
    private String scheduleId;
    private String classId;
    private String subjectId;
    private String subjectName;
    private String date;
    private String time;
    private String room;
    private String teacherName;

    public ScheduleModel() {}

    public ScheduleModel(String scheduleId, String classId, String subjectId, String subjectName,
                    String date, String time, String room, String teacherName) {
        this.scheduleId = scheduleId;
        this.classId = classId;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.date = date;
        this.time = time;
        this.room = room;
        this.teacherName = teacherName;
    }

    // Getters dan Setters
    public String getScheduleId() { return scheduleId; }
    public void setScheduleId(String scheduleId) { this.scheduleId = scheduleId; }
    public String getClassId() { return classId; }
    public void setClassId(String classId) { this.classId = classId; }
    public String getSubjectId() { return subjectId; }
    public void setSubjectId(String subjectId) { this.subjectId = subjectId; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }
    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
}
