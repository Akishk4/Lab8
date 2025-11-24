package BackEnd;

import java.util.*;

public class Quiz {

    List<Question> questions = new ArrayList<>();

    public Quiz() {
    }

    public void addQuestion(Question q) {
        questions.add(q);
    }

    public List<Question> getQuestions() {
        return questions;
    }
}
