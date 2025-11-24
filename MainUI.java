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

    private void showSignup() {
        JFrame f = new JFrame("Register");
        f.setSize(400, 400);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLayout(null); 

        JLabel l0 = new JLabel("Role:");
        l0.setBounds(50, 30, 80, 25);
        f.add(l0);

        String[] r = {"student", "instructor", "admin"};
        JComboBox<String> roles = new JComboBox<>(r);
        roles.setBounds(140, 30, 150, 25);
        f.add(roles);

        JLabel l1 = new JLabel("Username:");
        l1.setBounds(50, 70, 80, 25);
        f.add(l1);
        JTextField user = new JTextField();
        user.setBounds(140, 70, 200, 25);
        f.add(user);

        JLabel l2 = new JLabel("Email:");
        l2.setBounds(50, 110, 80, 25);
        f.add(l2);
        JTextField email = new JTextField();
        email.setBounds(140, 110, 200, 25);
        f.add(email);

        JLabel l3 = new JLabel("Password:");
        l3.setBounds(50, 150, 80, 25);
        f.add(l3);
        JPasswordField pass = new JPasswordField();
        pass.setBounds(140, 150, 200, 25);
        f.add(pass);

        JButton create = new JButton("Sign Up");
        create.setBounds(140, 200, 100, 30);
        f.add(create);

        JButton back = new JButton("Back");
        back.setBounds(140, 240, 100, 30);
        f.add(back);

        create.addActionListener(e -> {
            String role = (String) roles.getSelectedItem();
            boolean success = false;
            String pHash = HashUtil.sha256(new String(pass.getPassword()));

            if(role.equals("student")) success = auth.signupStudent(user.getText(), email.getText(), new String(pass.getPassword()));
            else if(role.equals("instructor")) success = auth.signupInstructor(user.getText(), email.getText(), new String(pass.getPassword()));
            else {
                Admin a = new Admin(user.getText(), email.getText(), pHash);
                success = db.addUser(a);
            }

            if(success) {
                JOptionPane.showMessageDialog(f, "Account created! Please login.");
                f.dispose();
                showLogin();
            } else {
                JOptionPane.showMessageDialog(f, "Error: Email might be taken.");
            }
        });

        back.addActionListener(e -> {
            f.dispose();
            showLogin();
        });

        f.setVisible(true);
    }

    public static void main(String[] args) {
        new MainUI();
    }
}