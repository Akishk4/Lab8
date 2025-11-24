package BackEnd;

import java.util.*;

public class Lesson {

    String lessonId;
    String title;
    String content;
    Quiz quiz;

    public Lesson() {
    }

    public Lesson(String title, String content) {
        this.lessonId = UUID.randomUUID().toString();
        this.title = title;
        this.content = content;
    }

    public String getLessonId() {
        return lessonId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz q) {
        this.quiz = q;
    }

    public String toString() {
        return title;
    }
}
