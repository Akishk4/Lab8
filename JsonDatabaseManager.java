package BackEnd;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileWriter;
import java.io.File;
import java.nio.file.Files;
import java.util.*;

public class JsonDatabaseManager {

    private static final String USERS_FILE = "users.json";
    private static final String COURSES_FILE = "courses.json";

    private final Map<String, User> usersById = new HashMap<>();
    private final Map<String, Course> coursesById = new LinkedHashMap<>();

    public JsonDatabaseManager() {
        loadUsers();
        loadCourses();
    }

    private void loadUsers() {
        File f = new File(USERS_FILE);
        if (!f.exists()) {
            saveUsers();
            return;
        }

        try {
            String content = Files.readString(f.toPath());
            JSONArray arr = new JSONArray(content);
            usersById.clear();

            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                String role = o.optString("role", "student");

                User u = null;
                if ("student".equals(role)) {
                    Student s = new Student();
                    s.userId = o.optString("userId");
                    s.username = o.optString("username");
                    s.email = o.optString("email");
                    s.passwordHash = o.optString("passwordHash");
                    
                    JSONArray enrolls = o.optJSONArray("enrolledCourseIds");
                    if (enrolls != null) {
                        for (int j = 0; j < enrolls.length(); j++) s.getEnrolledCourseIds().add(enrolls.getString(j));
                    }
                    JSONArray certs = o.optJSONArray("certificates");
                    if (certs != null) {
                        for (int j = 0; j < certs.length(); j++) s.getCertificates().add(certs.getString(j));
                    }
                    JSONObject scores = o.optJSONObject("quizScores");
                    if (scores != null) {
                        for (String key : scores.keySet()) {
                            s.addQuizScore(key, scores.getInt(key));
                        }
                    }
                    u = s;
                } else if ("instructor".equals(role)) {
                    Instructor ins = new Instructor();
                    ins.userId = o.optString("userId");
                    ins.username = o.optString("username");
                    ins.email = o.optString("email");
                    ins.passwordHash = o.optString("passwordHash");
                    
                    JSONArray created = o.optJSONArray("createdCourseIds");
                    if (created != null) {
                        for (int j = 0; j < created.length(); j++) ins.getCreatedCourseIds().add(created.getString(j));
                    }
                    u = ins;
                } else {
                    Admin a = new Admin();
                    a.userId = o.optString("userId");
                    a.username = o.optString("username");
                    a.email = o.optString("email");
                    a.passwordHash = o.optString("passwordHash");
                    u = a;
                }
                
                if (u != null) usersById.put(u.getUserId(), u);
            }
        } catch (Exception e) {
            System.out.println("Error loading users: " + e.getMessage());
        }
    }

   

 
  
   
 


    private void loadCourses() {
        File f = new File(COURSES_FILE);
        if (!f.exists()) {
            saveCourses();
            return;
        }

        try {
            String content = Files.readString(f.toPath());
            JSONArray arr = new JSONArray(content);
            coursesById.clear();

            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                Course c = new Course();
                c.courseId = o.optString("courseId");
                c.title = o.optString("title");
                c.description = o.optString("description");
                c.instructorId = o.optString("instructorId");
                c.status = o.optString("status", "PENDING");

                JSONArray lessonsArr = o.optJSONArray("lessons");
                if (lessonsArr != null) {
                    for (int j = 0; j < lessonsArr.length(); j++) {
                        JSONObject lObj = lessonsArr.getJSONObject(j);
                        Lesson l = new Lesson(lObj.optString("title"), lObj.optString("content"));
                        if (lObj.has("lessonId")) l.lessonId = lObj.getString("lessonId");
                        
                        JSONObject qObj = lObj.optJSONObject("quiz");
                        if (qObj != null) {
                            Quiz q = new Quiz();
                            JSONArray qArr = qObj.optJSONArray("questions");
                            if (qArr != null) {
                                for (int k = 0; k < qArr.length(); k++) {
                                    JSONObject quesObj = qArr.getJSONObject(k);
                                    List<String> options = new ArrayList<>();
                                    JSONArray optArr = quesObj.getJSONArray("options");
                                    for(int m=0; m<optArr.length(); m++) options.add(optArr.getString(m));
                                    
                                    q.addQuestion(new Question(
                                        quesObj.getString("questionText"), 
                                        options, 
                                        quesObj.getInt("correctOptionIndex")
                                    ));
                                }
                            }
                            l.setQuiz(q);
                        }
                        c.addLesson(l);
                    }
                }

                JSONArray studs = o.optJSONArray("studentIds");
                if (studs != null) {
                    for (int j = 0; j < studs.length(); j++) c.getStudentIds().add(studs.getString(j));
                }

                coursesById.put(c.getCourseId(), c);
            }
        } catch (Exception e) {
            System.out.println("Error loading courses: " + e.getMessage());
        }
    }

    public void saveCourses() {
        JSONArray arr = new JSONArray();
        for (Course c : coursesById.values()) {
            JSONObject o = new JSONObject();
            o.put("courseId", c.getCourseId());
            o.put("title", c.getTitle());
            o.put("description", c.getDescription());
            o.put("instructorId", c.getInstructorId());
            o.put("status", c.getStatus());

            JSONArray lArr = new JSONArray();
            for (Lesson l : c.getLessons()) {
                JSONObject lObj = new JSONObject();
                lObj.put("lessonId", l.getLessonId());
                lObj.put("title", l.getTitle());
                lObj.put("content", l.getContent());
                
                if (l.getQuiz() != null) {
                    JSONObject qObj = new JSONObject();
                    JSONArray qArr = new JSONArray();
                    for (Question ques : l.getQuiz().getQuestions()) {
                        JSONObject quesObj = new JSONObject();
                        quesObj.put("questionText", ques.getQuestionText());
                        quesObj.put("options", new JSONArray(ques.getOptions()));
                        quesObj.put("correctOptionIndex", ques.getCorrectOptionIndex());
                        qArr.put(quesObj);
                    }
                    qObj.put("questions", qArr);
                    lObj.put("quiz", qObj);
                }
                lArr.put(lObj);
            }
            o.put("lessons", lArr);
            o.put("studentIds", new JSONArray(c.getStudentIds()));
            arr.put(o);
        }

        try (FileWriter fw = new FileWriter(COURSES_FILE)) {
            fw.write(arr.toString(4));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   

   
    
    public void deleteCourse(String id) {
        coursesById.remove(id);
        saveCourses();
    }

}