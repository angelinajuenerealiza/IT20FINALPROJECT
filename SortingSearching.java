public FoodItem linearSearch(String name) {
    for (FoodItem f : foodLibrary) {
        if (f.name.equalsIgnoreCase(name)) {
            return f;
        }
    }
    return null; // not found
}

// Insertion Sort Implementation (sort by calories)
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
// Insertion Sort Implementation (sort by calories)
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
