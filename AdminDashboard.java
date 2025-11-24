package FrontEnd;

import BackEnd.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;

public class AdminDashboard {
    private final Admin admin;
    private final JsonDatabaseManager db;
    private JFrame frame;
    private DefaultListModel<Course> model;

    public AdminDashboard(Admin admin, JsonDatabaseManager db) {
        this.admin = admin;
        this.db = db;
    }

    public void show() {
        frame = new JFrame("Admin");
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel top = new JPanel(new BorderLayout());
        JButton logout = new JButton("Logout");
        top.add(new JLabel("  Pending Courses"), BorderLayout.WEST);
        top.add(logout, BorderLayout.EAST);
        frame.add(top, BorderLayout.NORTH);

        logout.addActionListener(e -> { frame.dispose(); new MainUI(); });

        model = new DefaultListModel<>();
        refresh();
        JList<Course> list = new JList<>(model);
        frame.add(new JScrollPane(list), BorderLayout.CENTER);

        JPanel bot = new JPanel();
        JButton app = new JButton("Approve");
        JButton rej = new JButton("Reject");
        bot.add(app); bot.add(rej);
        frame.add(bot, BorderLayout.SOUTH);

        app.addActionListener(e -> {
            Course c = list.getSelectedValue();
            if(c != null) {
                c.setStatus("APPROVED");
                db.updateCourse(c);
                refresh();
            }
        });

        rej.addActionListener(e -> {
            Course c = list.getSelectedValue();
            if(c != null) {
                c.setStatus("REJECTED");
                db.updateCourse(c);
                refresh();
            }
        });

        frame.setVisible(true);
    }

    private void refresh() {
        model.clear();
        for(Course c : db.listAllCourses()) {
            if(c.getStatus().equals("PENDING")) model.addElement(c);
        }
    }
}