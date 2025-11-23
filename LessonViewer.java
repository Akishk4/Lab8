package FrontEnd;

import BackEnd.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;

public class LessonViewer {
    private final Student student;
    private final Course course;
    private final JsonDatabaseManager db;
    private final StudentDashboard dashboard;

    public LessonViewer(Student s, Course c, JsonDatabaseManager db, StudentDashboard dashboard) {
        this.student = s;
        this.course = c;
        this.db = db;
        this.dashboard = dashboard;
    }

    public void show() {
        JFrame f = new JFrame("Course: " + course.getTitle());
        f.setSize(750, 500);
        f.setLocationRelativeTo(null);
        f.setLayout(new BorderLayout());

        DefaultListModel<Lesson> model = new DefaultListModel<>();
        for(Lesson l : course.getLessons()) {
            model.addElement(l);
        }
        JList<Lesson> list = new JList<>(model);
        JScrollPane pane = new JScrollPane(list);
        pane.setPreferredSize(new Dimension(200, 0));
        pane.setBorder(BorderFactory.createTitledBorder("Lessons"));
        f.add(pane, BorderLayout.WEST);

        JTextArea content = new JTextArea();
        content.setEditable(false);
        content.setLineWrap(true);
        content.setWrapStyleWord(true);
        content.setFont(new Font("Serif", Font.PLAIN, 16));
        content.setMargin(new Insets(10,10,10,10));
        f.add(new JScrollPane(content), BorderLayout.CENTER);

        JButton quizBtn = new JButton("Select a Lesson");
        quizBtn.setEnabled(false);
        quizBtn.setFont(new Font("Arial", Font.BOLD, 14));
        f.add(quizBtn, BorderLayout.SOUTH);

        list.addListSelectionListener(e -> {
            Lesson l = list.getSelectedValue();
            if(l != null) {
                content.setText(l.getContent());
                
                boolean hasQuiz = (l.getQuiz() != null && !l.getQuiz().getQuestions().isEmpty());
                boolean taken = student.getQuizScores().containsKey(l.getLessonId());

                if(taken) {
                    quizBtn.setText("Completed (Score: " + student.getQuizScores().get(l.getLessonId()) + ")");
                    quizBtn.setEnabled(false);
                } else if(hasQuiz) {
                    quizBtn.setText("Take Quiz");
                    quizBtn.setEnabled(true);
                } else {
                    quizBtn.setText("No Quiz");
                    quizBtn.setEnabled(false);
                }
            }
        });

        quizBtn.addActionListener(e -> {
            Lesson l = list.getSelectedValue();
            doQuiz(f, l);
            
            quizBtn.setText("Score: " + student.getQuizScores().get(l.getLessonId()));
            quizBtn.setEnabled(false);
            
            checkForCert(f);
            
            if(dashboard != null) dashboard.refresh();
        });

        f.setVisible(true);
    }

 

    private void checkForCert(JFrame p) {
        boolean doneAll = true;
        for(Lesson l : course.getLessons()) {
            if(l.getQuiz() != null && !l.getQuiz().getQuestions().isEmpty()) {
                if(!student.getQuizScores().containsKey(l.getLessonId())) doneAll = false;
            }
        }
        
        if(doneAll) {
            String cert = "CERTIFICATE: " + course.getTitle();
            if(!student.getCertificates().contains(cert)) {
                student.addCertificate(cert);
                db.updateUser(student);
                JOptionPane.showMessageDialog(p, "You earned a Certificate!");
            }
        }
    }
}