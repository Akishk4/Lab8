/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package BackEnd;

import java.util.*;

public class Student extends User {
    List<String> enrolledCourseIds = new ArrayList<>();
    Map<String, Integer> quizScores = new HashMap<>();
    List<String> certificates = new ArrayList<>();

    public Student() { super(); this.role = "student"; }

    public Student(String username, String email, String passwordHash) {
        super("student", username, email, passwordHash);
    }

    public List<String> getEnrolledCourseIds() { return enrolledCourseIds; }
    public Map<String, Integer> getQuizScores() { return quizScores; }
    public List<String> getCertificates() { return certificates; }

    public void enrollCourse(String cid) {
        if (!enrolledCourseIds.contains(cid)) enrolledCourseIds.add(cid);
    }

    public void addQuizScore(String lessonId, int score) {
        quizScores.put(lessonId, score);
    }

    public void addCertificate(String cert) {
        if (!certificates.contains(cert)) certificates.add(cert);
    }
}