package BackEnd;
import java.util.*;
public class Question {
    String questionText;
    List<String> options;
    int correctOptionIndex;

    public Question() {
        options = new ArrayList<>();
    }

    public Question(String q, List<String> opts, int correct) {
        this.questionText = q;
        this.options = opts;
        this.correctOptionIndex = correct;
    }

    public String getQuestionText() { return questionText; }
    public List<String> getOptions() { return options; }
    public int getCorrectOptionIndex() { return correctOptionIndex; }
}
