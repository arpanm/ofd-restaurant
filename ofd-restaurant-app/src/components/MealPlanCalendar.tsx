import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Badge } from "@/components/ui/badge";
import { 
  Edit, 
  Trash2,
  Plus,
  MapPin,
  Check,
  MessageSquare
} from "lucide-react";
import { 
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { 
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group";
import { MealEditDialog } from "./MealEditDialog";
import { format, addDays } from "date-fns";
import { useToast } from "@/hooks/use-toast";

export interface FoodItem {
  id: string;
  name: string;
  calories: number;
  restaurant: string;
}

export interface Meal {
  type: "breakfast" | "lunch" | "snacks" | "dinner";
  time: string;
  pincode: string;
  items: FoodItem[];
  hasAddress: boolean;
}

export interface DayPlan {
  date: Date;
  dayName: string;
  totalCalories: number;
  protein: number;
  carbs: number;
  fat: number;
  totalAmount: number;
  meals: Meal[];
}

interface Address {
  id: string;
  label: string;
  fullAddress: string;
  pincode: string;
  isDefault: boolean;
}

interface MealPlanCalendarProps {
  onOrderMeal?: (meal: Meal, date: Date) => void;
  readOnly?: boolean;
}

const getMealLabel = (type: string) => {
  switch (type) {
    case "breakfast": return "Breakfast";
    case "lunch": return "Lunch";
    case "snacks": return "Snacks";
    case "dinner": return "Dinner";
    default: return type;
  }
};

const getDayColor = (dayName: string) => {
  switch (dayName) {
    case "Monday": return "bg-yellow-50 border-yellow-200";
    case "Tuesday": return "bg-green-50 border-green-200";
    case "Wednesday": return "bg-pink-50 border-pink-200";
    case "Thursday": return "bg-blue-50 border-blue-200";
    case "Friday": return "bg-purple-50 border-purple-200";
    case "Saturday": return "bg-red-50 border-red-200";
    case "Sunday": return "bg-orange-50 border-orange-200";
    default: return "bg-gray-50 border-gray-200";
  }
};

const generateInitialMealPlan = (): DayPlan[] => {
  const plans: DayPlan[] = [];
  const today = new Date();

  const dayNames = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"];
  
  // Generate 7 days
  for (let i = 0; i < 7; i++) {
    const date = addDays(today, i);
    const dayName = dayNames[i];
    
    plans.push({
      date,
      dayName,
      totalCalories: 1200,
      protein: 25,
      carbs: 30,
      fat: 20,
      totalAmount: 400,
      meals: [
        {
          type: "breakfast",
          time: "9 AM",
          pincode: "712258",
          items: [
            { id: "b1", name: "Oats Egg Omelette", calories: 300, restaurant: "Healthy Bowls" },
            { id: "b2", name: "Boiled Egg Rice", calories: 200, restaurant: "Healthy Bowls" }
          ],
          hasAddress: true
        },
        {
          type: "lunch",
          time: "1 PM",
          pincode: "712258",
          items: [
            { id: "l1", name: "Oats Egg Omelette", calories: 350, restaurant: "FreshEats" },
            { id: "l2", name: "Boiled Egg Rice", calories: 250, restaurant: "FreshEats" }
          ],
          hasAddress: true
        },
        {
          type: "snacks",
          time: "4 PM",
          pincode: "712258",
          items: [
            { id: "s1", name: "Oats Egg Omelette", calories: 200, restaurant: "Healthy Bowls" },
            { id: "s2", name: "Boiled Egg Rice", calories: 150, restaurant: "Healthy Bowls" }
          ],
          hasAddress: true
        },
        {
          type: "dinner",
          time: "8 PM",
          pincode: "712258",
          items: [
            { id: "d1", name: "Oats Egg Omelette", calories: 400, restaurant: "Biryani Blues" },
            { id: "d2", name: "Boiled Egg Rice", calories: 300, restaurant: "Biryani Blues" }
          ],
          hasAddress: true
        }
      ],
    });
  }

  return plans;
};

export const MealPlanCalendar = ({ onOrderMeal, readOnly = false }: MealPlanCalendarProps) => {
  const { toast } = useToast();
  const [mealPlan, setMealPlan] = useState<DayPlan[]>(generateInitialMealPlan());
  const [selectedPlan, setSelectedPlan] = useState("Meal Plan 1");
  const [startDate, setStartDate] = useState(format(new Date(), "yyyy-MM-dd"));
  const [endDate, setEndDate] = useState(format(addDays(new Date(), 7), "yyyy-MM-dd"));
  const [editingMeal, setEditingMeal] = useState<{ dayIndex: number; mealIndex: number; isNew?: boolean; mealType?: string } | null>(null);
  const [customMealTypes, setCustomMealTypes] = useState<string[]>([]);
  
  // Address management
  const [showAddressDialog, setShowAddressDialog] = useState(false);
  const [editingMealTypeAddress, setEditingMealTypeAddress] = useState<string | null>(null);
  const [addresses, setAddresses] = useState<Address[]>([
    {
      id: "1",
      label: "Home",
      fullAddress: "123 Main Street, Apartment 4B, City Name - 712258",
      pincode: "712258",
      isDefault: true
    },
    {
      id: "2",
      label: "Office",
      fullAddress: "456 Business Park, Floor 5, Corporate Tower - 700001",
      pincode: "700001",
      isDefault: false
    }
  ]);
  const [newAddress, setNewAddress] = useState({
    label: "",
    fullAddress: "",
    pincode: ""
  });
  const [mealTypeAddresses, setMealTypeAddresses] = useState<{ [key: string]: string }>({
    breakfast: "1",
    lunch: "1",
    snacks: "1",
    dinner: "1"
  });
  const [tempSelectedAddress, setTempSelectedAddress] = useState<string>("");
  
  // Checkout flow
  const [showCheckoutDialog, setShowCheckoutDialog] = useState(false);
  const [selectedAddress, setSelectedAddress] = useState<string>("1");
  const [paymentMethod, setPaymentMethod] = useState<string>("card");
  const [orderInstructions, setOrderInstructions] = useState<string>("");

  const handleDeleteDay = (dayIndex: number) => {
    // Clear all meals for this day but keep the day in the grid
    setMealPlan(prev => {
      const newPlan = [...prev];
      newPlan[dayIndex].meals = [];
      return newPlan;
    });
  };

  const handleDeleteMeal = (dayIndex: number, mealIndex: number) => {
    setMealPlan(prev => {
      const newPlan = [...prev];
      newPlan[dayIndex].meals = newPlan[dayIndex].meals.filter((_, index) => index !== mealIndex);
      return newPlan;
    });
  };

  const handleClearMealType = (mealType: string) => {
    // Clear all meals of a specific type across all days
    setMealPlan(prev => {
      const newPlan = prev.map(day => ({
        ...day,
        meals: day.meals.filter(meal => meal.type !== mealType)
      }));
      return newPlan;
    });
  };

  const handleAddNewDay = () => {
    const lastDay = mealPlan[mealPlan.length - 1];
    const newDate = addDays(lastDay.date, 1);
    const dayName = format(newDate, "EEEE");
    
    setMealPlan(prev => [...prev, {
      date: newDate,
      dayName,
      totalCalories: 1200,
      protein: 25,
      carbs: 30,
      fat: 20,
      totalAmount: 400,
      meals: []
    }]);
  };

  const handleAddCustomMeal = () => {
    const mealNumber = customMealTypes.length + 1;
    setCustomMealTypes(prev => [...prev, `custom-meal-${mealNumber}`]);
  };

  const handleDeleteCustomMeal = (customMealType: string) => {
    setCustomMealTypes(prev => prev.filter(type => type !== customMealType));
  };

  const handleUpdateMeal = (items: FoodItem[]) => {
    if (!editingMeal) return;
    
    setMealPlan(prev => {
      const newPlan = [...prev];
      
      if (editingMeal.isNew) {
        // Adding a new meal
        const newMeal: Meal = {
          type: editingMeal.mealType as any,
          time: editingMeal.mealType === 'breakfast' ? '9 AM' : 
                editingMeal.mealType === 'lunch' ? '1 PM' : 
                editingMeal.mealType === 'snacks' ? '4 PM' : '8 PM',
          pincode: '712258',
          items: items,
          hasAddress: true
        };
        newPlan[editingMeal.dayIndex].meals.push(newMeal);
      } else {
        // Editing existing meal
        newPlan[editingMeal.dayIndex].meals[editingMeal.mealIndex].items = items;
      }
      
      return newPlan;
    });
    setEditingMeal(null);
  };

  const handleAddAddress = () => {
    if (!newAddress.label || !newAddress.fullAddress || !newAddress.pincode) {
      toast({
        title: "Missing Information",
        description: "Please fill in all address fields",
        variant: "destructive"
      });
      return;
    }

    const address: Address = {
      id: Date.now().toString(),
      label: newAddress.label,
      fullAddress: newAddress.fullAddress,
      pincode: newAddress.pincode,
      isDefault: addresses.length === 0
    };

    setAddresses(prev => [...prev, address]);
    setNewAddress({ label: "", fullAddress: "", pincode: "" });
    
    // Auto-select the newly added address
    setTempSelectedAddress(address.id);
    
    toast({
      title: "Address Added",
      description: "Your new address has been saved successfully"
    });
  };

  const handleSelectAddress = () => {
    if (!tempSelectedAddress) {
      toast({
        title: "No Address Selected",
        description: "Please select an address",
        variant: "destructive"
      });
      return;
    }

    const wasInCheckoutFlow = showCheckoutDialog === false && editingMealTypeAddress;

    if (editingMealTypeAddress) {
      setMealTypeAddresses(prev => ({
        ...prev,
        [editingMealTypeAddress]: tempSelectedAddress
      }));
      
      const selectedAddr = addresses.find(a => a.id === tempSelectedAddress);
      toast({
        title: "Address Updated",
        description: `${getMealLabel(editingMealTypeAddress)} will be delivered to ${selectedAddr?.label}`
      });
    }

    setShowAddressDialog(false);
    setEditingMealTypeAddress(null);
    setTempSelectedAddress("");

    // Reopen checkout if we came from there
    if (wasInCheckoutFlow) {
      setTimeout(() => setShowCheckoutDialog(true), 100);
    }
  };

  const handleOpenAddressDialog = (mealType: string) => {
    setEditingMealTypeAddress(mealType);
    setTempSelectedAddress(mealTypeAddresses[mealType] || "");
    setShowAddressDialog(true);
  };

  const handleStartAutoOrdering = () => {
    const totalMeals = mealPlan.reduce((sum, day) => sum + day.meals.length, 0);
    
    if (totalMeals === 0) {
      toast({
        title: "No Meals Selected",
        description: "Please add some meals to your plan before proceeding",
        variant: "destructive"
      });
      return;
    }

    if (addresses.length === 0) {
      toast({
        title: "No Address Added",
        description: "Please add a delivery address first",
        variant: "destructive"
      });
      setShowAddressDialog(true);
      return;
    }

    // Check if all meal types with meals have addresses selected
    const mealTypesInPlan = ['breakfast', 'lunch', 'snacks', 'dinner'].filter(mealType =>
      mealPlan.some(day => day.meals.some(meal => meal.type === mealType))
    );

    const missingAddresses = mealTypesInPlan.filter(mealType => 
      !mealTypeAddresses[mealType] || !addresses.find(a => a.id === mealTypeAddresses[mealType])
    );

    if (missingAddresses.length > 0) {
      toast({
        title: "Missing Delivery Addresses",
        description: `Please select delivery addresses for: ${missingAddresses.map(mt => getMealLabel(mt)).join(', ')}`,
        variant: "destructive"
      });
      return;
    }

    setShowCheckoutDialog(true);
  };

  const handleCompleteCheckout = () => {
    // Get unique addresses being used
    const usedAddressIds = Object.values(mealTypeAddresses);
    const uniqueAddresses = [...new Set(usedAddressIds)]
      .map(id => addresses.find(a => a.id === id))
      .filter(Boolean);
    
    const addressSummary = uniqueAddresses.length === 1
      ? `to ${uniqueAddresses[0]?.label}`
      : `to ${uniqueAddresses.length} different addresses`;
    
    toast({
      title: "Order Placed Successfully! 🎉",
      description: `Your meal plan will be delivered ${addressSummary}. Total: ₹${totalAmount}`,
      duration: 5000
    });
    
    setShowCheckoutDialog(false);
    
    // You can add navigation to order confirmation page here
  };

  const totalAmount = mealPlan.reduce((sum, day) => sum + day.totalAmount, 0);

  return (
    <div className="space-y-6">
      {/* Header with Meal Plan Selector */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3 sm:gap-4">
        <h3 className="text-xl sm:text-2xl font-bold">Customize Your Meals</h3>
        <div className="w-full sm:w-64">
          <label className="text-xs sm:text-sm font-medium mb-1.5 sm:mb-2 block">Meal Plan Name</label>
          <Select value={selectedPlan} onValueChange={setSelectedPlan}>
            <SelectTrigger className="h-9 sm:h-10">
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="Meal Plan 1">Meal Plan 1</SelectItem>
              <SelectItem value="Meal Plan 2">Meal Plan 2</SelectItem>
              <SelectItem value="Meal Plan 3">Meal Plan 3</SelectItem>
            </SelectContent>
          </Select>
        </div>
      </div>

      {/* Desktop: Grid Layout with Row/Column Headers */}
      <div className="hidden lg:block overflow-x-auto bg-white rounded-lg border-2 border-gray-200 p-4">
        <div className="grid grid-cols-[180px_repeat(7,1fr)] gap-2 min-w-[1400px]">
          {/* Top-left corner cell with logo */}
          <div className="flex items-center justify-center p-3 bg-yellow-50 rounded-lg border-2 border-yellow-200">
            <div className="text-center">
              <div className="text-3xl mb-1">😋</div>
              <div className="text-xs font-semibold">Weekly Meal Plan</div>
            </div>
          </div>

          {/* Column Headers - Days of the week */}
          {mealPlan.map((day, dayIndex) => (
            <div
              key={`header-${dayIndex}`}
              className={`${getDayColor(day.dayName)} border-2 rounded-lg p-2 text-center relative`}
            >
              <div className="flex items-start justify-between mb-1">
                <h4 className="font-bold text-sm flex-1">{day.dayName}</h4>
                {!readOnly && (
          <Button
                    variant="ghost"
            size="icon"
                    className="h-5 w-5 rounded-full bg-red-500 hover:bg-red-600 text-white p-0 flex-shrink-0"
                    onClick={() => handleDeleteDay(dayIndex)}
                    title={`Clear all meals for ${day.dayName}`}
          >
                    <Trash2 className="h-3 w-3" />
          </Button>
                )}
              </div>
              <div className="text-[9px] leading-tight space-y-0.5 text-left">
                <p className="text-gray-700">
                  Total calories: <span className="font-semibold">{day.totalCalories} Kcal</span>
                </p>
                <p className="text-gray-700">
                  <span className="font-semibold">Protein:</span> {day.protein}%, <span className="font-semibold">Carbs:</span> {day.carbs}%, <span className="font-semibold">Fat:</span> {day.fat}%
                </p>
                <p className="text-gray-700">
                  Total Amount: <span className="font-semibold">{day.totalAmount} Rs</span>
                </p>
              </div>
        </div>
          ))}

          {/* Rows for each meal type */}
          {['breakfast', 'lunch', 'snacks', 'dinner'].map((mealType) => (
            <>
              {/* Row Header - Meal Type */}
              <div
                key={`meal-header-${mealType}`}
                className="bg-gray-50 border-2 border-gray-200 rounded-lg p-3 flex flex-col justify-center relative"
              >
                  <div className="flex items-start justify-between">
                  <div className="flex-1">
                    <h5 className="font-bold text-base mb-1">{getMealLabel(mealType as any)}</h5>
                    <p className="text-xs text-gray-600">
                      Time - {mealType === 'breakfast' ? '9 AM' : mealType === 'lunch' ? '1 PM' : mealType === 'snacks' ? '4 PM' : '8 PM'}
                    </p>
                    {mealTypeAddresses[mealType] && addresses.find(a => a.id === mealTypeAddresses[mealType]) ? (
                      <div className="flex items-center gap-2 mt-1">
                        <div className="flex items-center gap-1">
                          <MapPin className="h-3 w-3 text-green-600" />
                          <span className="text-xs font-semibold text-gray-700">
                            {addresses.find(a => a.id === mealTypeAddresses[mealType])?.label}
                          </span>
                          <span className="text-xs text-gray-500">
                            (Pincode: {addresses.find(a => a.id === mealTypeAddresses[mealType])?.pincode})
                          </span>
                        </div>
                        {!readOnly && (
                          <button 
                            className="text-xs text-blue-600 hover:underline flex items-center gap-1"
                            onClick={() => handleOpenAddressDialog(mealType)}
                          >
                            <Edit className="h-3 w-3" />
                            Edit
                          </button>
                        )}
                      </div>
                    ) : (
                      !readOnly && (
                        <button 
                          className="text-xs text-blue-600 hover:underline flex items-center gap-1 mt-1"
                          onClick={() => handleOpenAddressDialog(mealType)}
                        >
                          <MapPin className="h-3 w-3" />
                          Select Address
                        </button>
                      )
                    )}
                  </div>
                  {!readOnly && (
                    <Button
                      variant="ghost"
                      size="icon"
                      className="h-5 w-5 rounded-full bg-red-500 hover:bg-red-600 text-white p-0 flex-shrink-0"
                      onClick={() => handleClearMealType(mealType)}
                      title={`Clear all ${getMealLabel(mealType as any)} meals`}
                    >
                      <Trash2 className="h-3 w-3" />
                    </Button>
                  )}
      </div>
              </div>

              {/* Meal cells for each day */}
              {mealPlan.map((day, dayIndex) => {
                const meal = day.meals.find(m => m.type === mealType);
                return (
                  <div
                    key={`${mealType}-${dayIndex}`}
                    className={`${getDayColor(day.dayName)} border-2 rounded-lg p-2 min-h-[120px] relative`}
                  >
                    {meal ? (
                      <>
                        {!readOnly && (
                          <div className="flex justify-end gap-0.5 mb-1">
                        <Button
                          variant="ghost"
                          size="icon"
                              className="h-5 w-5 p-0"
                              onClick={() => setEditingMeal({ dayIndex, mealIndex: day.meals.indexOf(meal) })}
                        >
                              <Edit className="h-3 w-3" />
                        </Button>
                        <Button
                          variant="ghost"
                          size="icon"
                              className="h-5 w-5 rounded-full bg-red-500 hover:bg-red-600 text-white p-0"
                              onClick={() => handleDeleteMeal(dayIndex, day.meals.indexOf(meal))}
                        >
                              <Trash2 className="h-2.5 w-2.5" />
                        </Button>
                      </div>
                        )}

                        <div className="space-y-1">
                          {meal.items.slice(0, 2).map((item, itemIndex) => (
                            <div key={item.id} className="text-[10px]">
                              <span className="font-semibold">Item {itemIndex + 1}</span>
                              <p className="text-gray-700 leading-tight">{item.name}</p>
                            </div>
                          ))}
                    </div>
                      </>
                    ) : (
                      !readOnly && (
                        <button 
                          className="flex flex-col items-center justify-center h-full w-full text-gray-400 hover:text-gray-600 hover:bg-white/50 rounded transition-colors group"
                          onClick={() => {
                            // Open edit dialog for adding a new meal
                            setEditingMeal({ 
                              dayIndex, 
                              mealIndex: -1, // -1 indicates new meal
                              isNew: true, 
                              mealType: mealType 
                            });
                          }}
                        >
                          <Plus className="h-8 w-8 mb-1 group-hover:scale-110 transition-transform" />
                          <span className="text-xs">Add meal</span>
                        </button>
                      )
                    )}
                  </div>
                );
              })}
            </>
          ))}

          {/* Custom Meal Rows - Only show when not read-only */}
          {!readOnly && customMealTypes.map((customMealType, index) => (
            <>
              {/* Row Header - Custom Meal */}
              <div
                key={`custom-meal-header-${customMealType}`}
                className="bg-purple-50 border-2 border-purple-200 rounded-lg p-3 flex flex-col justify-center relative"
              >
                <h5 className="font-bold text-base mb-1">Custom Meal {index + 1}</h5>
                <p className="text-xs text-gray-600">Time - Custom</p>
                <p className="text-xs text-gray-600">Pincode - 712258</p>
                <button className="text-xs text-blue-600 hover:underline flex items-center gap-1 mt-1">
                  <Plus className="h-3 w-3" />
                  Select Address
                </button>
                <Button
                  variant="ghost"
                  size="icon"
                  className="h-5 w-5 rounded-full bg-red-500 hover:bg-red-600 text-white p-0 absolute top-2 right-2"
                  onClick={() => handleDeleteCustomMeal(customMealType)}
                >
                  <Trash2 className="h-3 w-3" />
                </Button>
                            </div>

              {/* Custom Meal cells for each day */}
              {mealPlan.map((day, dayIndex) => (
                <div
                  key={`${customMealType}-${dayIndex}`}
                  className={`${getDayColor(day.dayName)} border-2 rounded-lg p-2 min-h-[120px] relative`}
                >
                  <button 
                    className="flex flex-col items-center justify-center h-full w-full text-gray-400 hover:text-gray-600 hover:bg-white/50 rounded transition-colors group"
                    onClick={() => {
                      // Open edit dialog for adding a custom meal
                      setEditingMeal({ 
                        dayIndex, 
                        mealIndex: -1,
                        isNew: true, 
                        mealType: 'snacks' // Default to snacks for custom meals
                      });
                    }}
                  >
                    <Plus className="h-8 w-8 mb-1 group-hover:scale-110 transition-transform" />
                    <span className="text-xs">Add meal</span>
                  </button>
                          </div>
                        ))}
            </>
          ))}

          {/* Add Another Meal Button Row - Only show when not read-only */}
          {!readOnly && (
            <>
              <div className="bg-gray-50 border-2 border-dashed border-gray-300 rounded-lg p-3 flex items-center justify-center">
                <Button
                  variant="ghost"
                  className="text-gray-600 hover:text-gray-900 gap-2"
                  onClick={handleAddCustomMeal}
                >
                  <Plus className="h-4 w-4" />
                  Add Another Meal
                </Button>
              </div>
              
              {/* Empty cells for the Add Another Meal row */}
              {mealPlan.map((day, dayIndex) => (
                <div key={`empty-${dayIndex}`} className="border-2 border-transparent" />
              ))}
            </>
          )}
        </div>
      </div>

      {/* Mobile: Vertical Day Cards */}
      <div className="lg:hidden space-y-3">
        {mealPlan.map((day, dayIndex) => (
          <div key={dayIndex} className={`${getDayColor(day.dayName)} border-2 rounded-lg p-4`}>
            {/* Day Header */}
            <div className="flex items-start justify-between mb-3">
              <div className="flex-1">
                <h4 className="font-bold text-base mb-1">{day.dayName}</h4>
                <div className="text-xs space-y-0.5">
                  <p className="text-gray-700">
                    Total calories: <span className="font-semibold">{day.totalCalories} Kcal</span>
                  </p>
                  <p className="text-gray-700">
                    <span className="font-semibold">Protein:</span> {day.protein}%, <span className="font-semibold">Carbs:</span> {day.carbs}%, <span className="font-semibold">Fat:</span> {day.fat}%
                  </p>
                  <p className="text-gray-700">
                    Total Amount: <span className="font-semibold">{day.totalAmount} Rs</span>
                  </p>
                </div>
              </div>
              {!readOnly && (
                            <Button 
                  variant="ghost"
                  size="icon"
                  className="h-7 w-7 rounded-full bg-red-500 hover:bg-red-600 text-white flex-shrink-0"
                  onClick={() => handleDeleteDay(dayIndex)}
                  title={`Clear all meals for ${day.dayName}`}
                >
                  <Trash2 className="h-3.5 w-3.5" />
                            </Button>
                          )}
                        </div>

            {/* Meals */}
            <div className="space-y-2">
              {day.meals.map((meal, mealIndex) => (
                <div key={mealIndex} className="bg-white/90 rounded-lg p-3 shadow-sm border border-gray-200">
                  <div className="flex items-start justify-between mb-2">
                    <div className="flex-1">
                      <h5 className="font-bold text-sm">{getMealLabel(meal.type)}</h5>
                      <p className="text-xs text-gray-600">Time - {meal.time}</p>
                      <p className="text-xs text-gray-600">Pincode - {meal.pincode}</p>
                    </div>
                    {!readOnly && (
                      <div className="flex gap-1 flex-shrink-0">
                        <Button
                          variant="ghost"
                          size="icon"
                          className="h-7 w-7"
                          onClick={() => setEditingMeal({ dayIndex, mealIndex })}
                        >
                          <Edit className="h-3.5 w-3.5" />
                        </Button>
                        <Button 
                          variant="ghost"
                          size="icon"
                          className="h-7 w-7 rounded-full bg-red-500 hover:bg-red-600 text-white"
                          onClick={() => handleDeleteMeal(dayIndex, mealIndex)}
                        >
                          <Trash2 className="h-3 w-3" />
                        </Button>
                      </div>
                    )}
                  </div>
                  
                  {meal.hasAddress && (
                    <button className="text-xs text-blue-600 hover:underline flex items-center gap-1 mb-2">
                      <Plus className="h-3 w-3" />
                      Select Address
                    </button>
                  )}

                  {/* Meal Items */}
                  <div className="space-y-1.5">
                    {meal.items.slice(0, 2).map((item, itemIndex) => (
                      <div key={item.id} className="text-xs">
                        <span className="font-semibold">Item {itemIndex + 1}</span>
                        <p className="text-gray-600">{item.name}</p>
                      </div>
                    ))}
                  </div>
                  </div>
                ))}
              </div>
          </div>
        ))}

        {/* Custom Meal Types on Mobile - Only show when not read-only */}
        {!readOnly && customMealTypes.map((customMealType, index) => (
          <div key={customMealType} className="bg-purple-50 border-2 border-purple-200 rounded-lg p-4">
            <div className="flex items-start justify-between mb-3">
              <h5 className="font-bold text-base">Custom Meal {index + 1}</h5>
              <Button
                variant="ghost"
                size="icon"
                className="h-7 w-7 rounded-full bg-red-500 hover:bg-red-600 text-white"
                onClick={() => handleDeleteCustomMeal(customMealType)}
              >
                <Trash2 className="h-3.5 w-3.5" />
              </Button>
            </div>
            <p className="text-xs text-gray-600">Time - Custom</p>
            <p className="text-xs text-gray-600 mb-2">Pincode - 712258</p>
            <button className="text-xs text-blue-600 hover:underline flex items-center gap-1">
              <Plus className="h-3 w-3" />
              Select Address
            </button>
          </div>
        ))}

        {/* Add Another Meal Button - Only show when not read-only */}
        {!readOnly && (
          <Button
            variant="outline"
            className="w-full border-dashed"
            onClick={handleAddCustomMeal}
          >
            <Plus className="h-4 w-4 mr-2" />
            Add Another Meal
          </Button>
        )}
      </div>

      {/* Footer with Date Selectors and Payment */}
      <div className="space-y-3 sm:space-y-4 mt-4 sm:mt-6">
        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-3 sm:gap-4">
          {!readOnly && (
            <>
              <div className="space-y-1.5 sm:space-y-2">
                <label className="text-xs sm:text-sm font-medium">Start Date</label>
                <Input
                  type="date"
                  value={startDate}
                  onChange={(e) => setStartDate(e.target.value)}
                  className="h-9 sm:h-10 text-sm"
                />
              </div>
              <div className="space-y-1.5 sm:space-y-2">
                <label className="text-xs sm:text-sm font-medium">End Date</label>
                <Input
                  type="date"
                  value={endDate}
                  onChange={(e) => setEndDate(e.target.value)}
                  className="h-9 sm:h-10 text-sm"
                />
              </div>
            </>
          )}
          <div className={`${readOnly ? 'md:col-span-2 lg:col-span-3' : 'md:col-span-2 lg:col-span-1'} space-y-1 sm:space-y-2`}>
            <div className="text-center sm:text-right mb-2">
              <p className="text-xs sm:text-sm text-gray-600">Total Amount</p>
              <p className="text-2xl sm:text-3xl font-bold text-orange-600">₹ {totalAmount}</p>
            </div>
          </div>
        </div>

        {/* Action Buttons - Only show when not read-only */}
        {!readOnly && (
          <div className="flex flex-col sm:flex-row gap-2 sm:gap-3">
            <Button 
              variant="outline"
              className="flex-1 h-10 sm:h-11 text-sm sm:text-base"
              onClick={() => {
                // Regenerate/modify logic can be implemented
                toast({
                  title: "Feature Coming Soon",
                  description: "Plan modification feature will be available soon"
                });
              }}
            >
              Modify Plan
            </Button>
            <Button 
              variant="outline"
              className="flex-1 h-10 sm:h-11 text-sm sm:text-base"
              onClick={() => {
                if (confirm("Are you sure you want to start over? This will clear your current plan.")) {
                  setMealPlan(generateInitialMealPlan());
                  toast({
                    title: "Plan Reset",
                    description: "Your meal plan has been reset"
                  });
                }
              }}
            >
              Start Over
            </Button>
            <Button 
              className="flex-1 h-10 sm:h-11 text-sm sm:text-base bg-orange-500 hover:bg-orange-600 text-white"
              onClick={handleStartAutoOrdering}
            >
              Start Auto-Ordering
            </Button>
          </div>
        )}
      </div>

      {editingMeal && (
        <MealEditDialog
          meal={
            editingMeal.isNew 
              ? {
                  type: editingMeal.mealType as any,
                  time: editingMeal.mealType === 'breakfast' ? '9 AM' : 
                        editingMeal.mealType === 'lunch' ? '1 PM' : 
                        editingMeal.mealType === 'snacks' ? '4 PM' : '8 PM',
                  pincode: '712258',
                  items: [],
                  hasAddress: true
                }
              : mealPlan[editingMeal.dayIndex].meals[editingMeal.mealIndex]
          }
          date={mealPlan[editingMeal.dayIndex].date}
          onSave={handleUpdateMeal}
          onClose={() => setEditingMeal(null)}
        />
      )}

      {/* Add/Select Address Dialog */}
      <Dialog open={showAddressDialog} onOpenChange={(open) => {
        setShowAddressDialog(open);
        if (!open) {
          setEditingMealTypeAddress(null);
          setTempSelectedAddress("");
          setNewAddress({ label: "", fullAddress: "", pincode: "" });
        }
      }}>
        <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto w-[95vw] sm:w-full p-4 sm:p-6">
          <DialogHeader>
            <DialogTitle className="text-base sm:text-lg">
              {editingMealTypeAddress ? `Select Address for ${getMealLabel(editingMealTypeAddress)}` : 'Manage Addresses'}
            </DialogTitle>
            <DialogDescription className="text-xs sm:text-sm">
              Choose from saved addresses or add a new one
            </DialogDescription>
          </DialogHeader>
          
          <div className="space-y-6 py-4">
            {/* Select from Saved Addresses */}
            {addresses.length > 0 && (
              <div className="space-y-3">
                <Label className="text-base font-semibold">Saved Addresses</Label>
                <RadioGroup value={tempSelectedAddress} onValueChange={setTempSelectedAddress}>
                  <div className="space-y-2 max-h-64 overflow-y-auto">
                    {addresses.map((addr) => (
                      <div key={addr.id} className="flex items-start space-x-3 border rounded-lg p-3 hover:bg-gray-50">
                        <RadioGroupItem value={addr.id} id={`addr-${addr.id}`} className="mt-1" />
                        <label htmlFor={`addr-${addr.id}`} className="flex-1 cursor-pointer">
                          <div className="font-semibold flex items-center gap-2">
                            {addr.label}
                            {addr.isDefault && (
                              <Badge variant="secondary" className="text-xs">Default</Badge>
                            )}
                          </div>
                          <div className="text-sm text-gray-600 mt-1">{addr.fullAddress}</div>
                          <div className="text-xs text-gray-500 mt-1">Pincode: {addr.pincode}</div>
                        </label>
                      </div>
                    ))}
                  </div>
                </RadioGroup>
              </div>
            )}

            {/* Add New Address Section */}
            <div className="border-t pt-4 space-y-4">
              <Label className="text-base font-semibold">Add New Address</Label>
              
              <div className="space-y-2">
                <Label htmlFor="label">Address Label *</Label>
                <Input
                  id="label"
                  placeholder="e.g., Home, Office, Other"
                  value={newAddress.label}
                  onChange={(e) => setNewAddress({ ...newAddress, label: e.target.value })}
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="fullAddress">Full Address *</Label>
                <Textarea
                  id="fullAddress"
                  placeholder="Enter complete address with street, apartment, landmark"
                  value={newAddress.fullAddress}
                  onChange={(e) => setNewAddress({ ...newAddress, fullAddress: e.target.value })}
                  rows={3}
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="pincode">Pincode *</Label>
                <Input
                  id="pincode"
                  placeholder="e.g., 712258"
                  value={newAddress.pincode}
                  onChange={(e) => setNewAddress({ ...newAddress, pincode: e.target.value })}
                />
              </div>

              <Button onClick={handleAddAddress} variant="outline" className="w-full">
                <Plus className="h-4 w-4 mr-2" />
                Add This Address
              </Button>
            </div>
          </div>

          <DialogFooter>
            <Button variant="outline" onClick={() => setShowAddressDialog(false)}>
              Cancel
            </Button>
            <Button onClick={handleSelectAddress} disabled={!tempSelectedAddress}>
              <Check className="h-4 w-4 mr-2" />
              Confirm Selection
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Checkout Dialog */}
      <Dialog open={showCheckoutDialog} onOpenChange={setShowCheckoutDialog}>
        <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto w-[95vw] sm:w-full p-4 sm:p-6">
          <DialogHeader>
            <DialogTitle className="text-base sm:text-lg">Complete Your Order</DialogTitle>
            <DialogDescription className="text-xs sm:text-sm">
              Review your meal plan and complete the payment
            </DialogDescription>
          </DialogHeader>

          <div className="space-y-6 py-4">
            {/* Order Summary */}
            <div className="space-y-3">
              <h3 className="font-semibold text-lg">Order Summary</h3>
              <div className="bg-gray-50 rounded-lg p-4 space-y-2">
                <div className="flex justify-between text-sm">
                  <span>Meal Plan:</span>
                  <span className="font-semibold">{selectedPlan}</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span>Duration:</span>
                  <span className="font-semibold">{format(new Date(startDate), "MMM d")} - {format(new Date(endDate), "MMM d, yyyy")}</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span>Total Days:</span>
                  <span className="font-semibold">{mealPlan.length} days</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span>Total Meals:</span>
                  <span className="font-semibold">{mealPlan.reduce((sum, day) => sum + day.meals.length, 0)} meals</span>
                </div>
                <div className="border-t pt-2 mt-2">
                  <div className="flex justify-between text-lg font-bold">
                    <span>Total Amount:</span>
                    <span className="text-orange-600">₹{totalAmount}</span>
                  </div>
                </div>
              </div>
            </div>

            {/* Delivery Addresses by Meal Type */}
            <div className="space-y-3">
              <div className="flex items-center justify-between">
                <h3 className="font-semibold text-lg">Delivery Addresses by Meal Type</h3>
                <Button 
                  variant="link" 
                  size="sm"
                  onClick={() => {
                    setShowCheckoutDialog(false);
                    setShowAddressDialog(true);
                  }}
                >
                  Add New Address
                </Button>
              </div>
              
              <div className="space-y-3 bg-gray-50 rounded-lg p-4">
                {['breakfast', 'lunch', 'snacks', 'dinner'].map((mealType) => {
                  const addressId = mealTypeAddresses[mealType];
                  const address = addresses.find(a => a.id === addressId);
                  
                  // Check if there are any meals of this type in the plan
                  const hasMealsOfType = mealPlan.some(day => 
                    day.meals.some(meal => meal.type === mealType)
                  );
                  
                  if (!hasMealsOfType) return null;
                  
                  return (
                    <div key={mealType} className="bg-white rounded-lg p-3 border">
                      <div className="flex items-start justify-between">
                        <div className="flex-1">
                          <div className="flex items-center gap-2 mb-2">
                            <h4 className="font-semibold text-sm">{getMealLabel(mealType)}</h4>
                            <Badge variant="outline" className="text-xs">
                              {mealPlan.filter(day => day.meals.some(m => m.type === mealType)).length} days
                            </Badge>
                          </div>
                          {address ? (
                            <div className="space-y-1">
                              <div className="flex items-center gap-2">
                                <MapPin className="h-3 w-3 text-green-600" />
                                <span className="text-sm font-semibold text-gray-700">{address.label}</span>
                                {address.isDefault && (
                                  <Badge variant="secondary" className="text-xs">Default</Badge>
                                )}
                              </div>
                              <div className="text-xs text-gray-600 ml-5">{address.fullAddress}</div>
                              <div className="text-xs text-gray-500 ml-5">Pincode: {address.pincode}</div>
                            </div>
                          ) : (
                            <div className="text-sm text-red-500">No address selected</div>
                          )}
                        </div>
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => {
                            setShowCheckoutDialog(false);
                            handleOpenAddressDialog(mealType);
                          }}
                        >
                          <Edit className="h-3 w-3 mr-1" />
                          Change
                        </Button>
                      </div>
                    </div>
                  );
                })}
              </div>
              
              <div className="text-xs text-gray-500 italic">
                Different meal types can be delivered to different addresses based on your preferences.
              </div>
            </div>

            {/* Payment Method */}
            <div className="space-y-3">
              <h3 className="font-semibold text-lg">Payment Method</h3>
              <RadioGroup value={paymentMethod} onValueChange={setPaymentMethod}>
                <div className="flex items-center space-x-3 border rounded-lg p-3 hover:bg-gray-50">
                  <RadioGroupItem value="card" id="card" />
                  <label htmlFor="card" className="flex-1 cursor-pointer">
                    <div className="font-semibold">Credit / Debit Card</div>
                    <div className="text-sm text-gray-600">Pay securely with your card</div>
                  </label>
                </div>
                <div className="flex items-center space-x-3 border rounded-lg p-3 hover:bg-gray-50">
                  <RadioGroupItem value="upi" id="upi" />
                  <label htmlFor="upi" className="flex-1 cursor-pointer">
                    <div className="font-semibold">UPI</div>
                    <div className="text-sm text-gray-600">Pay via Google Pay, PhonePe, Paytm</div>
                  </label>
                </div>
                <div className="flex items-center space-x-3 border rounded-lg p-3 hover:bg-gray-50">
                  <RadioGroupItem value="cod" id="cod" />
                  <label htmlFor="cod" className="flex-1 cursor-pointer">
                    <div className="font-semibold">Cash on Delivery</div>
                    <div className="text-sm text-gray-600">Pay when you receive</div>
                  </label>
                </div>
              </RadioGroup>
            </div>

            {/* Order Instructions */}
            <div className="space-y-3">
              <h3 className="font-semibold text-lg flex items-center gap-2">
                <MessageSquare className="h-4 w-4" />
                Order Instructions (Optional)
              </h3>
              <Textarea
                placeholder="Add any special instructions for your meal plan (e.g., dietary restrictions, delivery preferences, contactless delivery...)"
                value={orderInstructions}
                onChange={(e) => setOrderInstructions(e.target.value)}
                className="min-h-20"
              />
              <p className="text-xs text-gray-500 italic">
                These instructions will apply to all deliveries in your meal plan
              </p>
            </div>
          </div>

          <DialogFooter className="flex-col sm:flex-row gap-2">
            <Button variant="outline" onClick={() => setShowCheckoutDialog(false)} className="w-full sm:w-auto">
              Cancel
            </Button>
            <Button onClick={handleCompleteCheckout} className="w-full sm:w-auto bg-green-600 hover:bg-green-700">
              <Check className="h-4 w-4 mr-2" />
              Place Order - ₹{totalAmount}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
};