import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;


public class NutriGuideApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginGUI().setVisible(true));
    }
}


class LoginGUI extends JFrame {

    JTextField usernameField;
    JPasswordField passwordField;

    public LoginGUI() {
        setTitle("NutriGuide — Login");
        setSize(300, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10,10));

        JPanel panel = new JPanel(new GridLayout(0,2,10,10));
        panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JLabel lbl1 = new JLabel("Username:");
        JLabel lbl2 = new JLabel("Password:");

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        panel.add(lbl1); panel.add(usernameField);
        panel.add(lbl2); panel.add(passwordField);

        JButton loginBtn = new JButton("Login");
        JButton signupBtn = new JButton("Sign Up");

        JPanel bottom = new JPanel();
        bottom.add(loginBtn);
        bottom.add(signupBtn);

        add(panel, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        // Listeners
        loginBtn.addActionListener(e -> handleLogin());
        signupBtn.addActionListener(e -> {
            dispose();
            new SignupGUI().setVisible(true);
        });
    }

    private void handleLogin() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.");
            return;
        }

        if (checkCredentials(user, pass)) {
            JOptionPane.showMessageDialog(this, "Login successful!");
            dispose();
            new DashboardGUI(user).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Incorrect username or password.");
        }
    }

    private boolean checkCredentials(String u, String p) {
        File file = new File("accounts.txt");
        if (!file.exists()) return false;

        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String[] line = sc.nextLine().split(",");
                if (line.length == 2) {
                    if (line[0].equals(u) && line[1].equals(p)) {
                        return true;
                    }
                }
            }
        } catch (Exception ignored) {}

        return false;
    }
}

