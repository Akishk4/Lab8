package FrontEnd;

import BackEnd.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

public class InstructorDashboard {
    private final Instructor instructor;
    private final JsonDatabaseManager db;
    private JFrame frame;
    private DefaultListModel<Course> courseModel;
    private JList<Course> courseList;

    public InstructorDashboard(Instructor inst, JsonDatabaseManager db) {
        this.instructor = inst;
        this.db = db;
    }

    public void show() {
        frame = new JFrame("Instructor: " + instructor.getUsername());
        frame.setSize(800, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel top = new JPanel();
        top.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        JLabel lbl = new JLabel("Welcome, " + instructor.getUsername() + " | ");
        lbl.setFont(new Font("Arial", Font.BOLD, 14));
        
        JButton insightsBtn = new JButton("View Analytics");
        insightsBtn.setBackground(new Color(255, 255, 200));
        
        JButton logoutBtn = new JButton("Logout");

        top.add(lbl);
        top.add(insightsBtn);
        top.add(logoutBtn);
        frame.add(top, BorderLayout.NORTH);

        courseModel = new DefaultListModel<>();
        refreshCourses();
        courseList = new JList<>(courseModel);
        JScrollPane scroll = new JScrollPane(courseList);
        scroll.setBorder(BorderFactory.createTitledBorder("My Courses"));
        frame.add(scroll, BorderLayout.CENTER);

        JPanel bot = new JPanel();
        JButton addC = new JButton("Create Course");
        JButton addQ = new JButton("Add Quiz");
        
        bot.add(addC);
        bot.add(addQ);
        frame.add(bot, BorderLayout.SOUTH);

        logoutBtn.addActionListener(e -> {
            frame.dispose();
            new MainUI();
        });

        insightsBtn.addActionListener(e -> {
            ChartFrame cf = new ChartFrame(instructor, db);
            cf.setVisible(true);
        });

        addC.addActionListener(e -> {
            String t = JOptionPane.showInputDialog(frame, "Course Title:");
            if(t != null && !t.isEmpty()) {
                Course c = new Course(t, "Description", instructor.getUserId());
                db.addCourse(c);
                instructor.addCourse(c.getCourseId());
                db.updateUser(instructor);
                refreshCourses();
            }
        });

        addQ.addActionListener(e -> {
            Course c = courseList.getSelectedValue();
            if(c == null || c.getLessons().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Select a course with lessons first.");
                return;
            }
            
            Lesson[] lArr = c.getLessons().toArray(new Lesson[0]);
            Lesson l = (Lesson) JOptionPane.showInputDialog(frame, "Pick Lesson:", "Add Quiz", JOptionPane.PLAIN_MESSAGE, null, lArr, lArr[0]);
            
            if(l != null) {
                String q = JOptionPane.showInputDialog("Question:");
                String o1 = JOptionPane.showInputDialog("Option 1:");
                String o2 = JOptionPane.showInputDialog("Option 2:");
                String ans = JOptionPane.showInputDialog("Correct (0 or 1):");
                
                if(q != null && ans != null) {
                    if(l.getQuiz() == null) l.setQuiz(new Quiz());
                    List<String> opts = new ArrayList<>(); opts.add(o1); opts.add(o2);
                    try {
                        l.getQuiz().addQuestion(new Question(q, opts, Integer.parseInt(ans)));
                        db.updateCourse(c);
                        JOptionPane.showMessageDialog(frame, "Quiz updated!");
                    } catch(Exception ex) {
                        JOptionPane.showMessageDialog(frame, "Error: Answer must be 0 or 1");
                    }
                }
            }
        });

        frame.setVisible(true);
    }

    private void refreshCourses() {
        courseModel.clear();
        for(Course c : db.listAllCourses()) {
            if(c.getInstructorId().equals(instructor.getUserId())) {
                courseModel.addElement(c);
            }
        }
    }
}