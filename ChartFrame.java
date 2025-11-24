package FrontEnd;

import BackEnd.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class ChartFrame extends JFrame {
    private final Instructor instructor;
    private final JsonDatabaseManager db;

    public ChartFrame(Instructor instructor, JsonDatabaseManager db) {
        this.instructor = instructor;
        this.db = db;
        
        setTitle("Course Analytics");
        setSize(800, 650);
        setLocationRelativeTo(null);
        
        JPanel p = new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                drawCharts(g2);
            }
        };
        p.setBackground(Color.WHITE);
        add(p);
    }

    private void drawCharts(Graphics g) {
        List<Course> myCourses = new ArrayList<>();
        for(Course c : db.listAllCourses()) {
            if(c.getInstructorId().equals(instructor.getUserId())) {
                myCourses.add(c);
            }
        }

        if(myCourses.isEmpty()) {
            g.drawString("No courses to show.", 50, 50);
            return;
        }

        int chart1Top = 80;
        int chart1Base = 280;
        
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Average Quiz Scores per Course", 50, 40);
        
        g.setColor(Color.DARK_GRAY);
        g.drawLine(50, chart1Base, 700, chart1Base);
        g.drawLine(50, chart1Base, 50, chart1Top - 20);
        
        int x = 80;
        for(Course c : myCourses) {
            double total = 0;
            int count = 0;

            for(String sid : c.getStudentIds()) {
                User u = db.findUserById(sid);
                if(u instanceof Student) {
                    Student s = (Student)u;
                    for(Lesson l : c.getLessons()) {
                         if(l.getQuiz() != null && s.getQuizScores().containsKey(l.getLessonId())) {
                             int score = s.getQuizScores().get(l.getLessonId());
                             int max = l.getQuiz().getQuestions().size();
                             if(max > 0) {
                                 total += ((double)score/max) * 100;
                                 count++;
                             }
                         }
                    }
                }
            }
            
            int avgHeight = 0;
            if(count > 0) {
                avgHeight = (int)(total / count);
            } else {
                avgHeight = 0;
            }

            g.setColor(new Color(65, 105, 225));
            int barPixelHeight = avgHeight * 2;
            g.fillRect(x, chart1Base - barPixelHeight, 50, barPixelHeight);
            
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 12));
            g.drawString(avgHeight + "%", x + 10, chart1Base - barPixelHeight - 5);
            
            g.setFont(new Font("Arial", Font.PLAIN, 12));
            String shortName = c.getTitle();
            if(shortName.length() > 12) {
                shortName = shortName.substring(0, 10) + "..";
            }
            g.drawString(shortName, x, chart1Base + 20);
            
            x += 100;
        }

        int chart2Start = 380;
        
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Course Completion Rates", 50, chart2Start - 30);

        int y = chart2Start;
        for(Course c : myCourses) {
            int totalStuds = c.getStudentIds().size();
            int doneStuds = 0;

            for(String sid : c.getStudentIds()) {
                User u = db.findUserById(sid);
                if(u instanceof Student) {
                    Student s = (Student)u;
                    boolean allDone = true;
                    for(Lesson l : c.getLessons()) {
                        if(l.getQuiz() != null && !l.getQuiz().getQuestions().isEmpty()) {
                            if(!s.getQuizScores().containsKey(l.getLessonId())) {
                                allDone = false;
                            }
                        }
                    }
                    if(allDone) {
                        doneStuds++;
                    }
                }
            }

            int percent = 0;
            if(totalStuds > 0) {
                percent = (int)(((double)doneStuds / totalStuds) * 100);
            } else {
                percent = 0;
            }

            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.PLAIN, 14));
            String title = c.getTitle();
            if(title.length() > 20) {
                title = title.substring(0, 20) + "...";
            }
            g.drawString(title, 50, y + 15);

            g.setColor(new Color(220, 220, 220));
            g.fillRect(250, y, 300, 20);

            if(percent > 0) {
                g.setColor(new Color(46, 139, 87));
                g.fillRect(250, y, percent * 3, 20);
            }

            g.setColor(Color.GRAY);
            g.drawRect(250, y, 300, 20);

            g.setColor(Color.BLACK);
            g.drawString(percent + "%", 560, y + 15);

            y += 50;
        }
    }
}