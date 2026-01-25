package com.edulearn.kelompok3.Utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.edulearn.kelompok3.Model.AssignmentModel;
import com.edulearn.kelompok3.Model.ClassModel;
import com.edulearn.kelompok3.Model.QuestionModel;
import com.edulearn.kelompok3.Model.ScheduleModel;
import com.edulearn.kelompok3.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

/**
 * Helper class untuk Firebase Realtime Database
 */
public class FirebaseHelper {

    private static final String TAG = "FirebaseHelper";
    private final DatabaseReference database;
    private final FirebaseAuth auth;

    public FirebaseHelper() {
        database = FirebaseDatabase.getInstance("https://db-lms-edulearn-default-rtdb.asia-southeast1.firebasedatabase.app"
        ).getReference();
//        FirebaseDatabase database = FirebaseDatabase.getInstance(
//                "https://db-lms-edulearn-default-rtdb.asia-southeast1.firebasedatabase.app"
//        );

//        DatabaseReference ref = database.getReference();
        auth = FirebaseAuth.getInstance();
    }

    // ==================== USER OPERATIONS ====================

    public void createUser(User user, OnCompleteListener listener) {
        database.child("users").child(user.getUserId())
                .setValue(user)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User berhasil disimpan");
                    listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error menyimpan user", e);
                    listener.onFailure(e.getMessage());
                });
    }

    public void getUser(String userId, OnUserLoadListener listener) {
        database.child("users").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User user = snapshot.getValue(User.class);
                            if (user != null) {
                                user.setUserId(userId);
                                listener.onUserLoaded(user);
                            } else {
                                listener.onError("Data user tidak valid");
                            }
                        } else {
                            listener.onError("User tidak ditemukan");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.onError(error.getMessage());
                    }
                });
    }

    public void updateUser(String userId, Map<String, Object> updates, OnCompleteListener listener) {
        database.child("users").child(userId)
                .updateChildren(updates)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    // ==================== CLASS OPERATIONS ====================

    public void createClass(ClassModel classModel, OnCompleteListener listener) {

        String classId = database.child("classes").push().getKey();
        if (classId == null) {
            listener.onFailure("Gagal generate ID kelas");
            return;
        }

        classModel.setClassId(classId);

        database.child("classes").child(classId)
                .setValue(classModel)
                .addOnSuccessListener(aVoid -> {
                    addClassToUser(classModel.getTeacherId(), classId);
                    listener.onSuccess();
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public void joinClassByCode(String code, String userId, OnCompleteListener listener) {
        database.child("classes")
                .orderByChild("code")
                .equalTo(code)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot classSnapshot : snapshot.getChildren()) {
                                String classId = classSnapshot.getKey();

                                Map<String, Object> updates = new HashMap<>();
                                updates.put("studentIds/" + userId, true);

                                database.child("classes").child(classId)
                                        .updateChildren(updates)
                                        .addOnSuccessListener(aVoid -> {
                                            addClassToUser(userId, classId);
                                            listener.onSuccess();
                                        })
                                        .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
                                return;
                            }
                        } else {
                            listener.onFailure("Kode kelas tidak ditemukan");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.onFailure(error.getMessage());
                    }
                });
    }

    private void addClassToUser(String userId, String classId) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("classIds/" + classId, true);

        database.child("users").child(userId)
                .updateChildren(updates);
    }

    public void getUserClasses(String userId, OnClassListLoadListener listener) {
        database.child("users").child(userId).child("classIds")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        List<ClassModel> classList = new ArrayList<>();

                        if (!snapshot.exists()) {
                            listener.onClassesLoaded(classList);
                            return;
                        }

                        long total = snapshot.getChildrenCount();
                        final long[] loaded = {0};

                        for (DataSnapshot classIdSnap : snapshot.getChildren()) {
                            String classId = classIdSnap.getKey();

                            database.child("classes").child(classId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot classSnap) {
                                            if (classSnap.exists()) {
                                                ClassModel model = classSnap.getValue(ClassModel.class);
                                                if (model != null) {
                                                    model.setClassId(classId);
                                                    classList.add(model);
                                                }
                                            }

                                            loaded[0]++;
                                            if (loaded[0] == total) {
                                                listener.onClassesLoaded(classList);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            loaded[0]++;
                                            if (loaded[0] == total) {
                                                listener.onClassesLoaded(classList);
                                            }
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.onError(error.getMessage());
                    }
                });
    }

    // ==================== SCHEDULE ====================

    public void getTodaySchedules(List<String> classIds, OnScheduleListLoadListener listener) {

        if (classIds == null || classIds.isEmpty()) {
            listener.onSchedulesLoaded(new ArrayList<>());
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = sdf.format(new Date());

        List<ScheduleModel> list = new ArrayList<>();
        final long[] loaded = {0};

        for (String classId : classIds) {
            database.child("schedules")
                    .orderByChild("classId")
                    .equalTo(classId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot snap : snapshot.getChildren()) {
                                ScheduleModel s = snap.getValue(ScheduleModel.class);
                                if (s != null && today.equals(s.getDate())) {
                                    s.setScheduleId(snap.getKey());
                                    list.add(s);
                                }
                            }

                            loaded[0]++;
                            if (loaded[0] == classIds.size()) {
                                listener.onSchedulesLoaded(list);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            loaded[0]++;
                            if (loaded[0] == classIds.size()) {
                                listener.onSchedulesLoaded(list);
                            }
                        }
                    });
        }
    }

    // ==================== ASSIGNMENT ====================

    public void createAssignment(AssignmentModel assignment,
                                 List<QuestionModel> questions,
                                 OnCompleteListener listener) {

        String assignmentId = database.child("assignments").push().getKey();
        if (assignmentId == null) {
            listener.onFailure("Gagal generate ID assignment");
            return;
        }

        assignment.setAssignmentId(assignmentId);
        assignment.setCreatedAt(System.currentTimeMillis());
        assignment.setTotalQuestions(questions.size());

        database.child("assignments").child(assignmentId)
                .setValue(assignment)
                .addOnSuccessListener(aVoid -> {

                    for (int i = 0; i < questions.size(); i++) {
                        QuestionModel q = questions.get(i);
                        q.setOrder(i);

                        String qId = database.child("questions")
                                .child(assignmentId)
                                .push().getKey();

                        if (qId != null) {
                            q.setQuestionId(qId);
                            database.child("questions")
                                    .child(assignmentId)
                                    .child(qId)
                                    .setValue(q);
                        }
                    }
                    listener.onSuccess();
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public void getQuestions(String assignmentId, OnQuestionListLoadListener listener) {
        database.child("questions").child(assignmentId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        List<QuestionModel> list = new ArrayList<>();

                        for (DataSnapshot snap : snapshot.getChildren()) {
                            QuestionModel q = snap.getValue(QuestionModel.class);
                            if (q != null) {
                                q.setQuestionId(snap.getKey());
                                list.add(q);
                            }
                        }
                        listener.onQuestionsLoaded(list);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.onError(error.getMessage());
                    }
                });
    }

    // ==================== UTIL ====================

    public static String generateClassCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }

    // ==================== INTERFACES ====================

    public interface OnCompleteListener {
        void onSuccess();
        void onFailure(String error);
    }

    public interface OnUserLoadListener {
        void onUserLoaded(User user);
        void onError(String error);
    }

    public interface OnClassListLoadListener {
        void onClassesLoaded(List<ClassModel> classes);
        void onError(String error);
    }

    public interface OnScheduleListLoadListener {
        void onSchedulesLoaded(List<ScheduleModel> schedules);
        void onError(String error);
    }

    public interface OnQuestionListLoadListener {
        void onQuestionsLoaded(List<QuestionModel> questions);
        void onError(String error);
    }
}
