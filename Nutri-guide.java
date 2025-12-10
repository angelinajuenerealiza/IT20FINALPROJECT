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

/* =============================================================
                        LOGIN GUI
   ============================================================= */
class LoginGUI extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginGUI() {
        setTitle("NutriGuide - Login");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("NutriGuide Login", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(4, 1, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        form.add(new JLabel("Username:"));
        form.add(usernameField);

        form.add(new JLabel("Password:"));
        form.add(passwordField);

        add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(2, 1, 10, 10));

        JButton loginBtn = new JButton("Login");
        JButton signupBtn = new JButton("Create Account");

        loginBtn.addActionListener(e -> authenticate());
        signupBtn.addActionListener(e -> {
            dispose();
            new SignupGUI().setVisible(true);
        });

        buttons.add(loginBtn);
        buttons.add(signupBtn);

        add(buttons, BorderLayout.SOUTH);
    }

    private void authenticate() {
        String user = usernameField.getText().trim();
        String pass = String.valueOf(passwordField.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill up all fields.");
            return;
        }

        if (user.equals("admin") && pass.equals("admin")) {
            dispose();
            new DashboardGUI().setVisible(true);
            return;
        }

        File file = new File("users.txt");
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "No accounts exist. Please create one.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean found = false;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 2 && data[0].equals(user) && data[1].equals(pass)) {
                    found = true;
                    break;
                }
            }

            if (found) {
                dispose();
                new DashboardGUI().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.");
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

/* =============================================================
                        SIGNUP GUI
   ============================================================= */
class SignupGUI extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public SignupGUI() {
        setTitle("NutriGuide - Create Account");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Create Your NutriGuide Account", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(4, 1, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        form.add(new JLabel("Choose Username:"));
        form.add(usernameField);

        form.add(new JLabel("Choose Password:"));
        form.add(passwordField);

        add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(2, 1, 10, 10));

        JButton createBtn = new JButton("Create Account");
        JButton backBtn = new JButton("Back to Login");

        createBtn.addActionListener(e -> createAccount());
        backBtn.addActionListener(e -> {
            dispose();
            new LoginGUI().setVisible(true);
        });

        buttons.add(createBtn);
        buttons.add(backBtn);

        add(buttons, BorderLayout.SOUTH);
    }

    private void createAccount() {
        String user = usernameField.getText().trim();
        String pass = String.valueOf(passwordField.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill up all fields.");
            return;
        }

        File file = new File("users.txt");

        try {
            // If user already exists
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;

                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data[0].equals(user)) {
                        JOptionPane.showMessageDialog(this, "Username already exists!");
                        reader.close();
                        return;
                    }
                }
                reader.close();
            }

            // Save new user
            FileWriter fw = new FileWriter(file, true);
            fw.write(user + "," + pass + "\n");
            fw.close();

            JOptionPane.showMessageDialog(this, "Account created successfully!");

            dispose();
            new LoginGUI().setVisible(true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

/* =============================================================
                        DASHBOARD GUI
   ============================================================= */
class DashboardGUI extends JFrame {

    private PlannerLogic plannerLogic = new PlannerLogic();

    public DashboardGUI() {
        setTitle("NutriGuide Dashboard");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 1, 10, 10));

        JLabel title = new JLabel("Welcome to NutriGuide!", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        add(title);

        JButton plannerBtn = new JButton("Meal Planner");
        JButton dsaBtn = new JButton("DSA: Sorting, Searching & Classification");
        JButton logoutBtn = new JButton("Logout");

        plannerBtn.addActionListener(e -> new PlannerGUI(plannerLogic).setVisible(true));

        dsaBtn.addActionListener(e -> new DSAModuleGUI(plannerLogic).setVisible(true));

        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginGUI().setVisible(true);
        });

        add(plannerBtn);
        add(dsaBtn);
        add(logoutBtn);
    }
}

/* =============================================================
                        FOOD ITEM CLASS
   ============================================================= */
class FoodItem {
    String name;
    double calories;

    FoodItem(String name, double calories) {
        this.name = name;
        this.calories = calories;
    }
}

/* =============================================================
                        PLANNER LOGIC
   ============================================================= */
class PlannerLogic {

    ArrayList<FoodItem> foodLibrary = new ArrayList<>();

    public PlannerLogic() {
        loadDefaultFoods();
    }

    private void loadDefaultFoods() {
        foodLibrary.add(new FoodItem("Apple", 95));
        foodLibrary.add(new FoodItem("Banana", 105));
        foodLibrary.add(new FoodItem("Chicken Breast", 165));
        foodLibrary.add(new FoodItem("Egg", 78));
        foodLibrary.add(new FoodItem("Rice (1 cup)", 206));
        foodLibrary.add(new FoodItem("Milk (1 cup)", 149));
        foodLibrary.add(new FoodItem("Bread Slice", 66));
        foodLibrary.add(new FoodItem("Orange", 62));
    }

    public void addCustomFood(String name, double calories) {
        foodLibrary.add(new FoodItem(name, calories));
    }

    public ArrayList<FoodItem> getFoodLibrary() {
        return foodLibrary;
    }
}
