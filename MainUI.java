package FrontEnd;

import BackEnd.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;

public class MainUI {
    private final JsonDatabaseManager db;
    private final AuthService auth;

    public MainUI() {
        db = new JsonDatabaseManager();
        auth = new AuthService(db);
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(this::showLogin);
    }

    private void showLogin() {
        JFrame f = new JFrame("SkillForge Login");
        f.setSize(400, 350); 
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLayout(null); 

        JLabel title = new JLabel("Welcome to SkillForge");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setBounds(100, 20, 200, 30);
        f.add(title);

        JLabel l1 = new JLabel("Email:");
        l1.setBounds(50, 70, 80, 25);
        f.add(l1);

        JTextField email = new JTextField();
        email.setBounds(140, 70, 200, 25);
        f.add(email);

        JLabel l2 = new JLabel("Password:");
        l2.setBounds(50, 110, 80, 25);
        f.add(l2);

        JPasswordField pass = new JPasswordField();
        pass.setBounds(140, 110, 200, 25);
        f.add(pass);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(140, 160, 90, 30);
        f.add(loginBtn);

        JButton regBtn = new JButton("Register New Account");
        regBtn.setBounds(100, 210, 180, 30);
        f.add(regBtn);

        loginBtn.addActionListener(e -> {
            String eText = email.getText();
            String pText = new String(pass.getPassword());
            User u = auth.login(eText, pText);
            
            if(u != null) {
                f.dispose(); 
                if(u.getRole().equals("admin")) {
                    new AdminDashboard((Admin)u, db).show();
                } else if(u.getRole().equals("student")) {
                    new StudentDashboard((Student)u, db).show();
                } else {
                    new InstructorDashboard((Instructor)u, db).show();
                }
            } else {
                JOptionPane.showMessageDialog(f, "Wrong email or password!");
            }
        });

        regBtn.addActionListener(e -> {
            f.dispose();
            showSignup();
        });

        f.setVisible(true);
    }

    
    public static void main(String[] args) {
        new MainUI();
    }
}