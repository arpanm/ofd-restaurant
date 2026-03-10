import { useState } from "react";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Badge } from "@/components/ui/badge";
import { Trash2, Plus, Search } from "lucide-react";
import { format } from "date-fns";
import type { Meal, FoodItem } from "./MealPlanCalendar";

interface MealEditDialogProps {
  meal: Meal;
  date: Date;
  onSave: (items: FoodItem[]) => void;
  onClose: () => void;
}

// Sample food database for search
const foodDatabase: FoodItem[] = [
  { id: "1", name: "Protein Oatmeal Bowl", calories: 450, restaurant: "Healthy Bowls" },
  { id: "2", name: "Avocado Toast with Eggs", calories: 380, restaurant: "FreshEats" },
  { id: "3", name: "Greek Yogurt Parfait", calories: 320, restaurant: "Healthy Bowls" },
  { id: "4", name: "Grilled Chicken Salad", calories: 550, restaurant: "FreshEats" },
  { id: "5", name: "Quinoa Buddha Bowl", calories: 480, restaurant: "Healthy Bowls" },
  { id: "6", name: "Salmon with Veggies", calories: 600, restaurant: "FreshEats" },
  { id: "7", name: "Greek Yogurt & Nuts", calories: 250, restaurant: "Healthy Bowls" },
  { id: "8", name: "Fruit Smoothie", calories: 200, restaurant: "FreshEats" },
  { id: "9", name: "Protein Bar", calories: 220, restaurant: "Healthy Bowls" },
  { id: "10", name: "Tandoori Paneer with Veggies", calories: 650, restaurant: "Biryani Blues" },
  { id: "11", name: "Grilled Fish Tacos", calories: 580, restaurant: "FreshEats" },
  { id: "12", name: "Chicken Stir Fry", calories: 620, restaurant: "Biryani Blues" },
  { id: "13", name: "Turkey Wrap", calories: 420, restaurant: "FreshEats" },
  { id: "14", name: "Veggie Burger", calories: 380, restaurant: "Healthy Bowls" },
  { id: "15", name: "Pasta Primavera", calories: 520, restaurant: "Biryani Blues" },
  { id: "16", name: "Chicken Caesar Salad", calories: 480, restaurant: "FreshEats" },
  { id: "17", name: "Paneer Tikka", calories: 400, restaurant: "Biryani Blues" },
  { id: "18", name: "Smoothie Bowl", calories: 350, restaurant: "Healthy Bowls" },
];

export const MealEditDialog = ({ meal, date, onSave, onClose }: MealEditDialogProps) => {
  const [items, setItems] = useState<FoodItem[]>(meal.items);
  const [searchQuery, setSearchQuery] = useState("");
  const [showSearch, setShowSearch] = useState(false);

  const filteredFoods = searchQuery
    ? foodDatabase.filter(food =>
        food.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
        food.restaurant.toLowerCase().includes(searchQuery.toLowerCase())
      )
    : foodDatabase.slice(0, 6);

  const handleAddItem = (food: FoodItem) => {
    if (!items.find(item => item.id === food.id)) {
      setItems([...items, food]);
    }
    setSearchQuery("");
    setShowSearch(false);
  };

  const handleRemoveItem = (id: string) => {
    setItems(items.filter(item => item.id !== id));
  };

  const totalCalories = items.reduce((sum, item) => sum + item.calories, 0);

  return (
    <Dialog open onOpenChange={onClose}>
      <DialogContent className="max-w-2xl max-h-[80vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>
            Edit {meal.type.charAt(0).toUpperCase() + meal.type.slice(1)} - {format(date, "MMM d, yyyy")}
          </DialogTitle>
        </DialogHeader>

        <div className="space-y-6">
          {/* Current Items */}
          <div className="space-y-3">
            <Label>Current Items ({items.length})</Label>
            {items.length === 0 ? (
              <p className="text-sm text-muted-foreground italic p-4 text-center border-2 border-dashed rounded-lg">
                No items selected. Add items below.
              </p>
            ) : (
              <div className="space-y-2">
                {items.map((item) => (
                  <div
                    key={item.id}
                    className="flex items-center justify-between p-3 rounded-lg border bg-card"
                  >
                    <div className="flex-1">
                      <div className="font-medium">{item.name}</div>
                      <div className="text-sm text-muted-foreground flex items-center gap-2">
                        <span>{item.calories} cal</span>
                        <span>•</span>
                        <span>{item.restaurant}</span>
                      </div>
                    </div>
                    <Button
                      variant="ghost"
                      size="icon"
                      onClick={() => handleRemoveItem(item.id)}
                      className="text-destructive hover:text-destructive"
                    >
                      <Trash2 className="h-4 w-4" />
                    </Button>
                  </div>
                ))}
                <div className="flex justify-end pt-2">
                  <Badge variant="secondary" className="text-base px-4 py-2">
                    Total: {totalCalories} calories
                  </Badge>
                </div>
              </div>
            )}
          </div>

          {/* Add Items */}
          <div className="space-y-3">
            <Label>Add Items</Label>
            <div className="relative">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
              <Input
                placeholder="Search food items..."
                value={searchQuery}
                onChange={(e) => {
                  setSearchQuery(e.target.value);
                  setShowSearch(true);
                }}
                onFocus={() => setShowSearch(true)}
                className="pl-9"
              />
            </div>

            {showSearch && (
              <div className="grid gap-2 max-h-64 overflow-y-auto border rounded-lg p-2">
                {filteredFoods.map((food) => {
                  const isAdded = items.find(item => item.id === food.id);
                  return (
                    <button
                      key={food.id}
                      onClick={() => handleAddItem(food)}
                      disabled={!!isAdded}
                      className={`flex items-center justify-between p-3 rounded-lg border text-left transition-colors ${
                        isAdded
                          ? "bg-muted/50 opacity-60 cursor-not-allowed"
                          : "bg-card hover:bg-accent hover:border-primary"
                      }`}
                    >
                      <div>
                        <div className="font-medium">{food.name}</div>
                        <div className="text-sm text-muted-foreground flex items-center gap-2">
                          <span>{food.calories} cal</span>
                          <span>•</span>
                          <span>{food.restaurant}</span>
                        </div>
                      </div>
                      {isAdded ? (
                        <Badge variant="secondary">Added</Badge>
                      ) : (
                        <Plus className="h-4 w-4 text-primary" />
                      )}
                    </button>
                  );
                })}
              </div>
            )}
          </div>
        </div>

        <DialogFooter>
          <Button variant="outline" onClick={onClose}>
            Cancel
          </Button>
          <Button onClick={() => onSave(items)} disabled={items.length === 0}>
            Save Changes
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};