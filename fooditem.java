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
