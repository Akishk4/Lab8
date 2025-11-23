package BackEnd;

import java.util.*;

public class Course {

    String courseId;
    String title;
    String description;
    String instructorId;
    String status;

    List<Lesson> lessons = new ArrayList<>();
    List<String> studentIds = new ArrayList<>();

    public Course() {
    }

    public Course(String title, String description, String instructorId) {
        this.courseId = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.instructorId = instructorId;
        this.status = "PENDING";
    }

    public String getCourseId() {
        return courseId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getInstructorId() {
        return instructorId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String s) {
        this.status = s;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public List<String> getStudentIds() {
        return studentIds;
    }
        public void addLesson(Lesson l) {
        if (l != null) {
            lessons.add(l);
        }
    }

    public void enrollStudent(String studentId) {
        if (!studentIds.contains(studentId)) {
            studentIds.add(studentId);
        }
    }

    public String toString() {
        return title + " [" + status + "]";
    }
}
