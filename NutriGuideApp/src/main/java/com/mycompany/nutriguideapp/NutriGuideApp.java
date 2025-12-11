/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.nutriguideapp;

/**
 *
 * @author Jean Mikhaila
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

// =============================================================
//                      MAIN LAUNCHER
// =============================================================
public class NutriGuideApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginGUI().setVisible(true));
    }
}

// =============================================================
//                      LOGIN GUI
// =============================================================
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

// =============================================================
//                      SIGNUP GUI
// =============================================================
class SignupGUI extends JFrame {

    JTextField usernameField;
    JPasswordField passwordField;

    public SignupGUI() {
        setTitle("NutriGuide — Create Account");
        setSize(300, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10,10));

        JPanel panel = new JPanel(new GridLayout(0,2,10,10));
        panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JLabel lbl1 = new JLabel("Create Username:");
        JLabel lbl2 = new JLabel("Create Password:");

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        panel.add(lbl1); panel.add(usernameField);
        panel.add(lbl2); panel.add(passwordField);

        JButton registerBtn = new JButton("Register");
        JButton backBtn = new JButton("Back");

        JPanel bottom = new JPanel();
        bottom.add(registerBtn);
        bottom.add(backBtn);

        add(panel, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        registerBtn.addActionListener(e -> handleSignup());
        backBtn.addActionListener(e -> {
            dispose();
            new LoginGUI().setVisible(true);
        });
    }

    private void handleSignup() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill all fields.");
            return;
        }

        saveAccount(user, pass);
        JOptionPane.showMessageDialog(this, "Account created!");

        dispose();
        new LoginGUI().setVisible(true);
    }

    private void saveAccount(String u, String p) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("accounts.txt", true))) {
            pw.println(u + "," + p);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving account!");
        }
    }
}

// =============================================================
//                      DASHBOARD WINDOW
// =============================================================
class DashboardGUI extends JFrame {

    public DashboardGUI(String username) {

        setTitle("NutriGuide Dashboard — Welcome " + username);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLayout(new BorderLayout(10,10));

        JLabel title = new JLabel("<html><h1>NutriGuide Dashboard</h1>Welcome, " + username + "</html>");
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JButton plannerBtn = new JButton("Open Meal & Nutrition Planner");
        JButton logoutBtn = new JButton("Logout");

        JPanel center = new JPanel();
        center.add(plannerBtn);
        center.add(logoutBtn);

        add(title, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);

        plannerBtn.addActionListener(e -> {
            dispose();
            new PlannerGUI(new PlannerLogic()).showGUI();
        });

        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginGUI().setVisible(true);
        });
    }
}

// =============================================================
//                      FOOD ITEM MODEL
// =============================================================
class FoodItem {
    String name;
    int calories, protein, carbs, fat;

    FoodItem(String name, int calories, int protein, int carbs, int fat) {
        this.name = name;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
    }

    public Object[] toRow() {
        return new Object[]{name, calories, protein, carbs, fat};
    }

    @Override
    public String toString() {
        return name + " (" + calories + " kcal)";
    }
}

// =============================================================
//                      CORE LOGIC WITH SEARCH & SORT
// =============================================================
class PlannerLogic {

    ArrayList<FoodItem> foodLibrary = new ArrayList<>();
    ArrayList<FoodItem> mealPlan = new ArrayList<>();
    HashMap<String, FoodItem> foodMap = new HashMap<>();

    public PlannerLogic() {
        addFood(new FoodItem("Rice", 200, 4, 45, 1));
        addFood(new FoodItem("Egg", 78, 6, 1, 5));
        addFood(new FoodItem("Chicken Breast", 165, 31, 0, 3));
        addFood(new FoodItem("Apple", 95, 0, 25, 0));
        addFood(new FoodItem("Banana", 105, 1, 27, 0));
    }

    public void addFood(FoodItem f) {
        foodLibrary.add(f);
        foodMap.put(f.name.toLowerCase(), f);
    }

    public FoodItem search(String name) {
        return foodMap.get(name.toLowerCase());
    }

    // ==================== Linear Search ====================
    public FoodItem linearSearch(String name) {
        for (FoodItem f : foodLibrary) {
            if (f.name.equalsIgnoreCase(name)) return f;
        }
        return null;
    }

    // ==================== Insertion Sort by Calories ====================
    public void insertionSortByCalories() {
        for (int i = 1; i < foodLibrary.size(); i++) {
            FoodItem key = foodLibrary.get(i);
            int j = i - 1;

            while (j >= 0 && foodLibrary.get(j).calories > key.calories) {
                foodLibrary.set(j + 1, foodLibrary.get(j));
                j--;
            }
            foodLibrary.set(j + 1, key);
        }
    }

    public void addToMealPlan(FoodItem f) {
        if (!mealPlan.contains(f)) mealPlan.add(f);
    }

    public void removeFromMeal(int index) {
        if (index >= 0 && index < mealPlan.size())
            mealPlan.remove(index);
    }

    public void clearMeal() {
        mealPlan.clear();
    }

    public void exportCSV(File file) throws Exception {
        PrintWriter pw = new PrintWriter(new FileWriter(file));
        pw.println("Name,Calories,Protein,Carbs,Fat");
        for (FoodItem f : mealPlan) {
            pw.println(f.name + "," + f.calories + "," + f.protein + "," + f.carbs + "," + f.fat);
        }
        pw.close();
    }

    public String analytics() {
        int cal=0,p=0,c=0,f=0;
        for (FoodItem x : mealPlan) {
            cal+=x.calories; p+=x.protein; c+=x.carbs; f+=x.fat;
        }
        return "Total: " + cal + " kcal | P:" + p + "g C:" + c + "g F:" + f + "g";
    }
}

// =============================================================
//                      MAIN PLANNER GUI
// =============================================================
class PlannerGUI {

    PlannerLogic logic;

    JFrame frame;

    JTable foodTable, mealTable;
    DefaultTableModel foodModel, mealModel;

    JLabel analyticsLabel;

    String[] HEADERS = {"Name", "Calories", "Protein", "Carbs", "Fat"};

    public PlannerGUI(PlannerLogic logic) {
        this.logic = logic;
    }

    public void showGUI() {
        frame = new JFrame("NutriGuide – Meal & Nutrition Planner");
        frame.setSize(900,600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout(10,10));

        analyticsLabel = new JLabel("Total: 0 kcal");
        analyticsLabel.setHorizontalAlignment(SwingConstants.CENTER);

        main.add(analyticsLabel, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.5);

        // ----- LEFT: FOOD LIBRARY -----
        foodModel = new DefaultTableModel(HEADERS,0);
        foodTable = new JTable(foodModel);

        JPanel left = new JPanel(new BorderLayout());
        left.setBorder(BorderFactory.createTitledBorder("Food Library"));
        left.add(new JScrollPane(foodTable), BorderLayout.CENTER);

        JButton searchBtn = new JButton("Search");
        JButton addBtn = new JButton("Add to Meal Plan");
        JButton addCustomBtn = new JButton("Add Custom");
        JButton sortBtn = new JButton("Sort by Calories");

        JPanel leftBtns = new JPanel();
        leftBtns.add(searchBtn);
        leftBtns.add(addBtn);
        leftBtns.add(addCustomBtn);
        leftBtns.add(sortBtn);
        left.add(leftBtns, BorderLayout.SOUTH);

        // ----- RIGHT: MEAL PLAN -----
        mealModel = new DefaultTableModel(HEADERS,0);
        mealTable = new JTable(mealModel);

        JPanel right = new JPanel(new BorderLayout());
        right.setBorder(BorderFactory.createTitledBorder("Today's Meal Plan"));
        right.add(new JScrollPane(mealTable), BorderLayout.CENTER);

        JButton remove = new JButton("Remove");
        JButton clear = new JButton("Clear");
        JButton export = new JButton("Export CSV");

        JPanel rightBtns = new JPanel();
        rightBtns.add(remove);
        rightBtns.add(clear);
        rightBtns.add(export);

        right.add(rightBtns, BorderLayout.SOUTH);

        split.setLeftComponent(left);
        split.setRightComponent(right);

        main.add(split, BorderLayout.CENTER);

        frame.setContentPane(main);
        frame.setVisible(true);

        refreshFoodTable();

        // ----- LISTENERS -----
        searchBtn.addActionListener(e -> doSearch());
        addBtn.addActionListener(e -> addToMeal());
        addCustomBtn.addActionListener(e -> addCustomFood());
        sortBtn.addActionListener(e -> {
            logic.insertionSortByCalories();
            refreshFoodTable();
        });

        remove.addActionListener(e -> removeMealItem());
        clear.addActionListener(e -> clearMeal());
        export.addActionListener(e -> exportCSV());
    }

    private void refreshFoodTable() {
        foodModel.setRowCount(0);
        for (FoodItem f : logic.foodLibrary) {
            foodModel.addRow(f.toRow());
        }
    }

    private void doSearch() {
        String q = JOptionPane.showInputDialog(frame, "Search food:");
        if (q == null) return;

        FoodItem f = logic.linearSearch(q); // using linear search
        JOptionPane.showMessageDialog(frame,
                f == null ? "Not found." : "Found: " + f);
    }

    private void addToMeal() {
        int r = foodTable.getSelectedRow();
        if (r == -1) { JOptionPane.showMessageDialog(frame, "Select a food."); return; }

        String name = (String) foodModel.getValueAt(r,0);
        FoodItem f = logic.search(name);

        logic.addToMealPlan(f);
        mealModel.addRow(f.toRow());
        analyticsLabel.setText(logic.analytics());
    }

    private void removeMealItem() {
        int r = mealTable.getSelectedRow();
        if (r == -1) return;
        logic.removeFromMeal(r);
        mealModel.removeRow(r);
        analyticsLabel.setText(logic.analytics());
    }

    private void clearMeal() {
        logic.clearMeal();
        mealModel.setRowCount(0);
        analyticsLabel.setText(logic.analytics());
    }

    private void exportCSV() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("meal_plan.csv"));
        if (fc.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
            try {
                logic.exportCSV(fc.getSelectedFile());
                JOptionPane.showMessageDialog(frame, "Exported!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error exporting.");
            }
        }
    }

    private void addCustomFood() {
        JTextField n = new JTextField();
        JTextField ca = new JTextField();
        JTextField pr = new JTextField();
        JTextField cb = new JTextField();
        JTextField fa = new JTextField();

        JPanel p = new JPanel(new GridLayout(0,2));
        p.add(new JLabel("Name:")); p.add(n);
        p.add(new JLabel("Calories:")); p.add(ca);
        p.add(new JLabel("Protein:")); p.add(pr);
        p.add(new JLabel("Carbs:")); p.add(cb);
        p.add(new JLabel("Fat:")); p.add(fa);

        if (JOptionPane.showConfirmDialog(frame, p, "Add Food",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {

            try {
                FoodItem f = new FoodItem(
                        n.getText(),
                        Integer.parseInt(ca.getText()),
                        Integer.parseInt(pr.getText()),
                        Integer.parseInt(cb.getText()),
                        Integer.parseInt(fa.getText())
                );

                logic.addFood(f);
                foodModel.addRow(f.toRow());

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Invalid input.");
            }
        }
    }
}
