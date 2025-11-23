/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package BackEnd;

import java.util.*;

public class Instructor extends User {
    List<String> createdCourseIds = new ArrayList<>();

    public Instructor() { super(); this.role = "instructor"; }

    public Instructor(String username, String email, String passwordHash) {
        super("instructor", username, email, passwordHash);
    }

    public List<String> getCreatedCourseIds() { return createdCourseIds; }

    public void addCourse(String cid) {
        if (!createdCourseIds.contains(cid)) createdCourseIds.add(cid);
    }
}