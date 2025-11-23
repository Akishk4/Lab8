package FrontEnd;

import BackEnd.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.FileWriter;
import java.util.*;
import org.json.JSONObject;

public class StudentDashboard {
    private final Student student;
    private final JsonDatabaseManager db;
    private JFrame frame;
    
    private DefaultListModel<Course> myCoursesModel;
    private DefaultListModel<Course> catalogModel;
    private DefaultListModel<String> certModel;

    public StudentDashboard(Student s, JsonDatabaseManager db) {
        this.student = s;
        this.db = db;
    }

    public void show() {
        frame = new JFrame("Student Portal - " + student.getUsername());
        frame.setSize(850, 550);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(new EmptyBorder(10, 10, 10, 10));
        JButton logout = new JButton("Logout");
        
        JLabel welcome = new JLabel("Welcome, " + student.getUsername());
        welcome.setFont(new Font("Arial", Font.BOLD, 16));
        
        top.add(welcome, BorderLayout.WEST);
        top.add(logout, BorderLayout.EAST);
        frame.add(top, BorderLayout.NORTH);

        logout.addActionListener(e -> {
            frame.dispose();
            new MainUI();
        });

        JTabbedPane tabs = new JTabbedPane();

        JPanel myPanel = new JPanel(new BorderLayout());
        myCoursesModel = new DefaultListModel<>();
        JList<Course> myList = new JList<>(myCoursesModel);
        JButton openBtn = new JButton("Continue Learning");
        
        openBtn.addActionListener(e -> {
            Course c = myList.getSelectedValue();
            if(c != null) {
                new LessonViewer(student, c, db, this).show();
            } else {
                JOptionPane.showMessageDialog(frame, "Select a course.");
            }
        });

        myPanel.add(new JScrollPane(myList), BorderLayout.CENTER);
        myPanel.add(openBtn, BorderLayout.SOUTH);
        tabs.addTab("Enrolled Courses", myPanel);

        JPanel catPanel = new JPanel(new BorderLayout());
        catalogModel = new DefaultListModel<>();
        JList<Course> catList = new JList<>(catalogModel);
        JButton enrollBtn = new JButton("Enroll");
        
        enrollBtn.addActionListener(e -> {
            Course c = catList.getSelectedValue();
            if(c != null) {
                student.enrollCourse(c.getCourseId());
                c.enrollStudent(student.getUserId());
                db.updateUser(student);
                db.updateCourse(c);
                JOptionPane.showMessageDialog(frame, "Enrolled!");
                refresh();
            } else {
                JOptionPane.showMessageDialog(frame, "Select a course.");
            }
        });

        catPanel.add(new JScrollPane(catList), BorderLayout.CENTER);
        catPanel.add(enrollBtn, BorderLayout.SOUTH);
        tabs.addTab("Available Courses", catPanel);

        JPanel certPanel = new JPanel(new BorderLayout());
        certModel = new DefaultListModel<>();
        JList<String> certList = new JList<>(certModel);
        JButton downBtn = new JButton("Download JSON Certificate");

        downBtn.addActionListener(e -> {
            String cert = certList.getSelectedValue();
            if(cert != null) {
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("Student", student.getUsername());
                    obj.put("Certificate", cert);
                    
                    String name = "Certificate_" + System.currentTimeMillis() + ".json";
                    FileWriter fw = new FileWriter(name);
                    fw.write(obj.toString(4));
                    fw.close();
                    JOptionPane.showMessageDialog(frame, "Saved: " + name);
                } catch(Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error saving file.");
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Select a certificate.");
            }
        });

        certPanel.add(new JScrollPane(certList), BorderLayout.CENTER);
        certPanel.add(downBtn, BorderLayout.SOUTH);
        tabs.addTab("My Certificates", certPanel);

        frame.add(tabs, BorderLayout.CENTER);
        refresh();
        frame.setVisible(true);
    }

    public void refresh() {
        myCoursesModel.clear();
        catalogModel.clear();
        certModel.clear();

        java.util.List<Course> all = db.listAllCourses();
        for(Course c : all) {
            if("APPROVED".equals(c.getStatus())) {
                if(student.getEnrolledCourseIds().contains(c.getCourseId())) {
                    myCoursesModel.addElement(c);
                } else {
                    catalogModel.addElement(c);
                }
            }
        }

        for(String s : student.getCertificates()) {
            certModel.addElement(s);
        }
    }
}
