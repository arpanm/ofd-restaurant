/**
 * Restaurant Menu Page - Display restaurant details and menu with add to cart
 */

import { useEffect, useState, useMemo } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from '@/store/hooks';
import {
  fetchRestaurantById,
  clearCurrentRestaurant,
} from '@/store/slices/restaurantSlice';
import { fetchCategories, fetchMenuItems } from '@/store/slices/menuSlice';
import { useCart } from '@/hooks/useCart';
import { useAuth } from '@/hooks/useAuth';
import { Navbar } from '@/components/Navbar';
import { CartDrawer } from '@/components/CartDrawer';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Input } from '@/components/ui/input';
import { Skeleton } from '@/components/ui/skeleton';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { ScrollArea } from '@/components/ui/scroll-area';
import {
  Star,
  Clock,
  MapPin,
  Search,
  Heart,
  Plus,
  Minus,
  Leaf,
  Flame,
  ShoppingCart,
  ChevronRight,
  X,
} from 'lucide-react';
import type { MenuItem, MenuCategory, SelectedCustomization, SelectedAddon } from '@/types/api.types';

interface ItemCustomizationDialogProps {
  item: MenuItem | null;
  isOpen: boolean;
  onClose: () => void;
  onAddToCart: (
    item: MenuItem,
    quantity: number,
    customizations: SelectedCustomization[],
    addons: SelectedAddon[],
    instructions?: string
  ) => void;
}

const ItemCustomizationDialog = ({
  item,
  isOpen,
  onClose,
  onAddToCart,
}: ItemCustomizationDialogProps) => {
  const [quantity, setQuantity] = useState(1);
  const [selectedCustomizations, setSelectedCustomizations] = useState<SelectedCustomization[]>([]);
  const [selectedAddons, setSelectedAddons] = useState<SelectedAddon[]>([]);
  const [instructions, setInstructions] = useState('');

  useEffect(() => {
    if (item) {
      // Pre-select default customizations
      const defaults: SelectedCustomization[] = [];
      item.customizations?.forEach((customization) => {
        const defaultOption = customization.options.find((o) => o.isDefault);
        if (defaultOption) {
          defaults.push({
            customizationId: customization.id,
            customizationName: customization.name,
            optionId: defaultOption.id,
            optionName: defaultOption.name,
            price: defaultOption.price,
          });
        }
      });
      setSelectedCustomizations(defaults);
      setSelectedAddons([]);
      setQuantity(1);
      setInstructions('');
    }
  }, [item]);

  if (!item) return null;

  const handleCustomizationSelect = (
    customization: MenuItem['customizations'][0],
    optionId: string
  ) => {
    const option = customization.options.find((o) => o.id === optionId);
    if (!option) return;

    setSelectedCustomizations((prev) => {
      const filtered = prev.filter((c) => c.customizationId !== customization.id);
      return [
        ...filtered,
        {
          customizationId: customization.id,
          customizationName: customization.name,
          optionId: option.id,
          optionName: option.name,
          price: option.price,
        },
      ];
    });
  };

  const handleAddonToggle = (addon: MenuItem['addons'][0]) => {
    setSelectedAddons((prev) => {
      const existing = prev.find((a) => a.addonId === addon.id);
      if (existing) {
        return prev.filter((a) => a.addonId !== addon.id);
      }
      return [
        ...prev,
        {
          addonId: addon.id,
          addonName: addon.name,
          quantity: 1,
          price: addon.price,
        },
      ];
    });
  };

  const calculateTotal = () => {
    let total = item.currentPrice;
    selectedCustomizations.forEach((c) => (total += c.price));
    selectedAddons.forEach((a) => (total += a.price * a.quantity));
    return total * quantity;
  };

  const handleAddToCart = () => {
    onAddToCart(item, quantity, selectedCustomizations, selectedAddons, instructions || undefined);
    onClose();
  };

  return (
    <Dialog open={isOpen} onOpenChange={(open) => !open && onClose()}>
      <DialogContent className="max-w-md max-h-[90vh] overflow-hidden flex flex-col">
        <DialogHeader>
          <DialogTitle>{item.name}</DialogTitle>
          <DialogDescription>{item.description}</DialogDescription>
        </DialogHeader>

        <ScrollArea className="flex-1 pr-4">
          <div className="space-y-6">
            {/* Item Image */}
            {item.images?.[0] && (
              <img
                src={item.images[0]}
                alt={item.name}
                className="w-full h-48 object-cover rounded-lg"
              />
            )}

            {/* Customizations */}
            {item.customizations?.map((customization) => (
              <div key={customization.id}>
                <div className="flex items-center justify-between mb-2">
                  <h4 className="font-semibold">{customization.name}</h4>
                  {customization.required && (
                    <Badge variant="secondary" className="text-xs">
                      Required
                    </Badge>
                  )}
                </div>
                <div className="space-y-2">
                  {customization.options.map((option) => (
                    <label
                      key={option.id}
                      className={`flex items-center justify-between p-3 border rounded-lg cursor-pointer ${
                        selectedCustomizations.some((c) => c.optionId === option.id)
                          ? 'border-primary bg-primary/5'
                          : 'hover:bg-muted'
                      } ${!option.isAvailable ? 'opacity-50 cursor-not-allowed' : ''}`}
                    >
                      <div className="flex items-center gap-3">
                        <input
                          type="radio"
                          name={customization.id}
                          checked={selectedCustomizations.some((c) => c.optionId === option.id)}
                          onChange={() => handleCustomizationSelect(customization, option.id)}
                          disabled={!option.isAvailable}
                          className="h-4 w-4"
                        />
                        <span>{option.name}</span>
                      </div>
                      {option.price > 0 && (
                        <span className="text-sm text-muted-foreground">+₹{option.price}</span>
                      )}
                    </label>
                  ))}
                </div>
              </div>
            ))}

            {/* Addons */}
            {item.addons && item.addons.length > 0 && (
              <div>
                <h4 className="font-semibold mb-2">Add-ons</h4>
                <div className="space-y-2">
                  {item.addons.map((addon) => (
                    <label
                      key={addon.id}
                      className={`flex items-center justify-between p-3 border rounded-lg cursor-pointer ${
                        selectedAddons.some((a) => a.addonId === addon.id)
                          ? 'border-primary bg-primary/5'
                          : 'hover:bg-muted'
                      } ${!addon.isAvailable ? 'opacity-50 cursor-not-allowed' : ''}`}
                    >
                      <div className="flex items-center gap-3">
                        <input
                          type="checkbox"
                          checked={selectedAddons.some((a) => a.addonId === addon.id)}
                          onChange={() => handleAddonToggle(addon)}
                          disabled={!addon.isAvailable}
                          className="h-4 w-4"
                        />
                        <span>{addon.name}</span>
                      </div>
                      <span className="text-sm text-muted-foreground">+₹{addon.price}</span>
                    </label>
                  ))}
                </div>
              </div>
            )}

            {/* Special Instructions */}
            <div>
              <h4 className="font-semibold mb-2">Special Instructions</h4>
              <Input
                placeholder="Any special requests? (optional)"
                value={instructions}
                onChange={(e) => setInstructions(e.target.value)}
              />
            </div>
          </div>
        </ScrollArea>

        <DialogFooter className="flex items-center gap-4 pt-4 border-t">
          {/* Quantity Selector */}
          <div className="flex items-center gap-2">
            <Button
              variant="outline"
              size="icon"
              onClick={() => setQuantity(Math.max(1, quantity - 1))}
              disabled={quantity <= 1}
            >
              <Minus className="h-4 w-4" />
            </Button>
            <span className="w-8 text-center font-semibold">{quantity}</span>
            <Button
              variant="outline"
              size="icon"
              onClick={() => setQuantity(quantity + 1)}
            >
              <Plus className="h-4 w-4" />
            </Button>
          </div>

          {/* Add to Cart Button */}
          <Button className="flex-1" onClick={handleAddToCart}>
            Add to Cart • ₹{calculateTotal().toFixed(2)}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};

const MenuItemCard = ({
  item,
  onAdd,
  isFavorite,
  onToggleFavorite,
}: {
  item: MenuItem;
  onAdd: () => void;
  isFavorite: boolean;
  onToggleFavorite: () => void;
}) => (
  <Card className="overflow-hidden">
    <CardContent className="p-4">
      <div className="flex gap-4">
        <div className="flex-1">
          <div className="flex items-center gap-2 mb-1">
            {item.dietaryInfo.isVegetarian && (
              <span className="w-4 h-4 border-2 border-green-600 flex items-center justify-center">
                <span className="w-2 h-2 rounded-full bg-green-600" />
              </span>
            )}
            {item.dietaryInfo.isVegan && <Leaf className="h-4 w-4 text-green-600" />}
            {item.dietaryInfo.spiceLevel !== 'NONE' && (
              <Flame className="h-4 w-4 text-red-500" />
            )}
          </div>
          <h3 className="font-semibold mb-1">{item.name}</h3>
          <p className="text-sm text-muted-foreground line-clamp-2 mb-2">{item.description}</p>
          <div className="flex items-center gap-2 mb-2">
            <span className="font-semibold">₹{item.currentPrice.toFixed(0)}</span>
            {item.discountPercentage && item.discountPercentage > 0 && (
              <>
                <span className="text-sm text-muted-foreground line-through">
                  ₹{item.basePrice.toFixed(0)}
                </span>
                <Badge variant="secondary" className="text-xs text-green-600">
                  {item.discountPercentage}% OFF
                </Badge>
              </>
            )}
          </div>
          <div className="flex items-center gap-4 text-sm text-muted-foreground">
            {item.rating > 0 && (
              <span className="flex items-center gap-1">
                <Star className="h-3 w-3 fill-yellow-400 text-yellow-400" />
                {item.rating.toFixed(1)}
              </span>
            )}
            {item.preparationTime > 0 && (
              <span className="flex items-center gap-1">
                <Clock className="h-3 w-3" />
                {item.preparationTime} min
              </span>
            )}
          </div>
        </div>
        <div className="flex flex-col items-end gap-2">
          {item.images?.[0] ? (
            <img
              src={item.images[0]}
              alt={item.name}
              className="w-24 h-24 object-cover rounded-lg"
            />
          ) : (
            <div className="w-24 h-24 bg-muted rounded-lg flex items-center justify-center">
              <span className="text-muted-foreground text-xs">No image</span>
            </div>
          )}
          <div className="flex items-center gap-2">
            <Button
              variant="ghost"
              size="icon"
              className="h-8 w-8"
              onClick={onToggleFavorite}
            >
              <Heart
                className={`h-4 w-4 ${isFavorite ? 'fill-red-500 text-red-500' : ''}`}
              />
            </Button>
            <Button
              size="sm"
              className="h-8"
              onClick={onAdd}
              disabled={!item.availability?.isAvailable}
            >
              <Plus className="h-4 w-4" />
              Add
            </Button>
          </div>
        </div>
      </div>
    </CardContent>
  </Card>
);

export default function RestaurantMenu() {
  const { restaurantId } = useParams<{ restaurantId: string }>();
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const { isAuthenticated } = useAuth();
  const { isItemFavorite, toggleFavoriteItem, isRestaurantFavorite, toggleFavoriteRestaurant } = useAuth();
  const { addToCart, openCart, cart, itemCount } = useCart();

  const { currentRestaurant: restaurant, isLoading: restaurantLoading } = useAppSelector(
    (state) => state.restaurant
  );
  const { categories, menuItems, isLoading: menuLoading } = useAppSelector((state) => state.menu);

  const [searchQuery, setSearchQuery] = useState('');
  const [activeCategory, setActiveCategory] = useState<string>('all');
  const [selectedItem, setSelectedItem] = useState<MenuItem | null>(null);

  useEffect(() => {
    if (restaurantId) {
      dispatch(fetchRestaurantById(restaurantId));
      dispatch(fetchCategories(restaurantId));
      dispatch(fetchMenuItems(restaurantId));
    }

    return () => {
      dispatch(clearCurrentRestaurant());
    };
  }, [dispatch, restaurantId]);

  const filteredItems = useMemo(() => {
    let items = menuItems;

    // Filter by category
    if (activeCategory !== 'all') {
      items = items.filter((item) => item.categoryId === activeCategory);
    }

    // Filter by search query
    if (searchQuery.trim()) {
      const query = searchQuery.toLowerCase();
      items = items.filter(
        (item) =>
          item.name.toLowerCase().includes(query) ||
          item.description.toLowerCase().includes(query) ||
          item.tags?.some((tag) => tag.toLowerCase().includes(query))
      );
    }

    return items;
  }, [menuItems, activeCategory, searchQuery]);

  const itemsByCategory = useMemo(() => {
    const grouped: Record<string, MenuItem[]> = {};
    filteredItems.forEach((item) => {
      if (!grouped[item.categoryId]) {
        grouped[item.categoryId] = [];
      }
      grouped[item.categoryId].push(item);
    });
    return grouped;
  }, [filteredItems]);

  const handleAddToCart = async (
    item: MenuItem,
    quantity: number,
    customizations: SelectedCustomization[],
    addons: SelectedAddon[],
    instructions?: string
  ) => {
    if (!isAuthenticated) {
      navigate('/onboarding');
      return;
    }

    await addToCart({
      menuItemId: item.id,
      restaurantId: item.restaurantId,
      quantity,
      customizations,
      addons,
      specialInstructions: instructions,
    });
  };

  const isLoading = restaurantLoading || menuLoading;

  if (isLoading || !restaurant) {
    return (
      <div className="min-h-screen bg-background">
        <Navbar />
        <main className="container mx-auto px-4 py-8">
          <Skeleton className="h-48 w-full rounded-lg mb-6" />
          <Skeleton className="h-8 w-1/3 mb-2" />
          <Skeleton className="h-4 w-1/2 mb-6" />
          <div className="grid md:grid-cols-2 gap-4">
            {[1, 2, 3, 4].map((i) => (
              <Skeleton key={i} className="h-32" />
            ))}
          </div>
        </main>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background">
      <Navbar />
      <main className="pb-24">
        {/* Restaurant Header */}
        <div className="relative">
          {restaurant.images?.banner ? (
            <img
              src={restaurant.images.banner}
              alt={restaurant.name}
              className="w-full h-48 md:h-64 object-cover"
            />
          ) : (
            <div className="w-full h-48 md:h-64 bg-gradient-to-r from-primary/20 to-primary/10" />
          )}
          <div className="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent" />
          <div className="absolute bottom-0 left-0 right-0 p-6 text-white">
            <div className="container mx-auto">
              <div className="flex items-end justify-between">
                <div>
                  <h1 className="text-3xl font-bold mb-2">{restaurant.name}</h1>
                  <p className="text-white/80 mb-2">{restaurant.cuisine.join(' • ')}</p>
                  <div className="flex items-center gap-4 text-sm">
                    <span className="flex items-center gap-1">
                      <Star className="h-4 w-4 fill-yellow-400 text-yellow-400" />
                      {restaurant.rating.toFixed(1)} ({restaurant.totalReviews} reviews)
                    </span>
                    <span className="flex items-center gap-1">
                      <MapPin className="h-4 w-4" />
                      {restaurant.address.city}
                    </span>
                  </div>
                </div>
                <Button
                  variant="secondary"
                  size="icon"
                  onClick={() => toggleFavoriteRestaurant(restaurant.id)}
                >
                  <Heart
                    className={`h-5 w-5 ${
                      isRestaurantFavorite(restaurant.id) ? 'fill-red-500 text-red-500' : ''
                    }`}
                  />
                </Button>
              </div>
            </div>
          </div>
        </div>

        <div className="container mx-auto px-4 py-6">
          {/* Search */}
          <div className="relative mb-6">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
            <Input
              type="text"
              placeholder="Search for dishes..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="pl-10"
            />
            {searchQuery && (
              <Button
                variant="ghost"
                size="icon"
                className="absolute right-2 top-1/2 -translate-y-1/2 h-6 w-6"
                onClick={() => setSearchQuery('')}
              >
                <X className="h-4 w-4" />
              </Button>
            )}
          </div>

          {/* Category Tabs */}
          <Tabs value={activeCategory} onValueChange={setActiveCategory} className="mb-6">
            <ScrollArea className="w-full">
              <TabsList className="w-max">
                <TabsTrigger value="all">All Items</TabsTrigger>
                {categories.map((category) => (
                  <TabsTrigger key={category.id} value={category.id}>
                    {category.name}
                    {category.itemCount > 0 && (
                      <Badge variant="secondary" className="ml-2">
                        {category.itemCount}
                      </Badge>
                    )}
                  </TabsTrigger>
                ))}
              </TabsList>
            </ScrollArea>
          </Tabs>

          {/* Menu Items */}
          {filteredItems.length === 0 ? (
            <div className="text-center py-12">
              <p className="text-muted-foreground">No items found</p>
            </div>
          ) : activeCategory === 'all' ? (
            // Show items grouped by category
            <div className="space-y-8">
              {categories.map((category) => {
                const categoryItems = itemsByCategory[category.id];
                if (!categoryItems || categoryItems.length === 0) return null;

                return (
                  <div key={category.id}>
                    <h2 className="text-xl font-semibold mb-4">
                      {category.name}
                      <span className="text-muted-foreground font-normal text-sm ml-2">
                        ({categoryItems.length} items)
                      </span>
                    </h2>
                    <div className="grid md:grid-cols-2 gap-4">
                      {categoryItems.map((item) => (
                        <MenuItemCard
                          key={item.id}
                          item={item}
                          onAdd={() => setSelectedItem(item)}
                          isFavorite={isItemFavorite(item.id)}
                          onToggleFavorite={() => toggleFavoriteItem(item.id)}
                        />
                      ))}
                    </div>
                  </div>
                );
              })}
            </div>
          ) : (
            // Show filtered items
            <div className="grid md:grid-cols-2 gap-4">
              {filteredItems.map((item) => (
                <MenuItemCard
                  key={item.id}
                  item={item}
                  onAdd={() => setSelectedItem(item)}
                  isFavorite={isItemFavorite(item.id)}
                  onToggleFavorite={() => toggleFavoriteItem(item.id)}
                />
              ))}
            </div>
          )}
        </div>

        {/* Item Customization Dialog */}
        <ItemCustomizationDialog
          item={selectedItem}
          isOpen={!!selectedItem}
          onClose={() => setSelectedItem(null)}
          onAddToCart={handleAddToCart}
        />

        {/* Floating Cart Button */}
        {itemCount > 0 && (
          <div className="fixed bottom-0 left-0 right-0 p-4 bg-background border-t">
            <div className="container mx-auto">
              <Button className="w-full" size="lg" onClick={openCart}>
                <ShoppingCart className="h-5 w-5 mr-2" />
                View Cart ({itemCount} items) • ₹{cart?.total.toFixed(2)}
                <ChevronRight className="h-5 w-5 ml-auto" />
              </Button>
            </div>
          </div>
        )}
      </main>

      <CartDrawer />
    </div>
  );
}

