import java.io.*;
import java.util.*;

// -------------------- 2.1 FoodItem Class --------------------
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

    public Object[] toRow() { return new Object[]{name, calories, protein, carbs, fat}; }
    @Override
    public String toString() { return name + " (" + calories + " kcal)"; }
}

// -------------------- 2.2 PlannerLogic Class --------------------
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

    // -------------------- 2.3 Searching --------------------
    public FoodItem search(String name) { return foodMap.get(name.toLowerCase()); }
    public FoodItem linearSearch(String name) {
        for (FoodItem f : foodLibrary) if (f.name.equalsIgnoreCase(name)) return f;
        return null;
    }

    // -------------------- 2.4 Sorting --------------------
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

    // -------------------- 2.5 Meal Plan Management --------------------
    public void addToMealPlan(FoodItem f) { if (!mealPlan.contains(f)) mealPlan.add(f); }
    public void removeFromMeal(int index) { if (index >= 0 && index < mealPlan.size()) mealPlan.remove(index); }
    public void clearMeal() { mealPlan.clear(); }

    // -------------------- 2.6 Export and Analytics --------------------
    public void exportCSV(File file) throws Exception {
        PrintWriter pw = new PrintWriter(new FileWriter(file));
        pw.println("Name,Calories,Protein,Carbs,Fat");
        for (FoodItem f : mealPlan) pw.println(f.name + "," + f.calories + "," + f.protein + "," + f.carbs + "," + f.fat);
        pw.close();
    }

    public String analytics() {
        int cal=0,p=0,c=0,f=0;
        for (FoodItem x : mealPlan) { cal+=x.calories; p+=x.protein; c+=x.carbs; f+=x.fat; }
        return "Total: " + cal + " kcal | P:" + p + "g C:" + c + "g F:" + f + "g";
    }
}