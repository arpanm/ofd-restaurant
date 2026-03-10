import { useNavigate } from "react-router-dom";
import { Sheet, SheetContent, SheetHeader, SheetTitle, SheetTrigger } from "@/components/ui/sheet";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { ScrollArea } from "@/components/ui/scroll-area";
import { ShoppingCart, Plus, Minus, Trash2, Sparkles, MessageSquare } from "lucide-react";
import { Separator } from "@/components/ui/separator";
import { Textarea } from "@/components/ui/textarea";
import { Label } from "@/components/ui/label";

interface CartItem {
  id: string;
  item_name: string;
  restaurant_name: string;
  price: number;
  quantity: number;
  instructions?: string;
}

interface MenuItem {
  id: string;
  name: string;
  restaurant: string;
  restaurant_logo?: string;
  price: number;
  image: string;
  description?: string;
  calories?: number;
  tags?: string[];
}

interface CartDrawerProps {
  cart: CartItem[];
  onUpdateQuantity: (id: string, newQuantity: number) => void;
  onRemoveItem: (id: string) => void;
  onUpdateInstructions: (id: string, instructions: string) => void;
  onCheckout: () => void;
  aiSuggestions?: MenuItem[];
  onAddSuggestion?: (item: MenuItem) => void;
}

export const CartDrawer = ({ cart, onUpdateQuantity, onRemoveItem, onUpdateInstructions, onCheckout, aiSuggestions = [], onAddSuggestion }: CartDrawerProps) => {
  const navigate = useNavigate();
  const total = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
  
  const handleCheckout = () => {
    navigate('/checkout', { state: { cart } });
    onCheckout();
  };
  
  return (
    <Sheet>
      <SheetTrigger asChild>
        <Button variant="outline" className="gap-1.5 sm:gap-2 relative h-9 sm:h-11 px-3 sm:px-4 text-sm sm:text-base" data-cart-drawer>
          <ShoppingCart className="h-4 w-4 sm:h-5 sm:w-5" />
          <span className="hidden xs:inline">Cart</span>
          {cart.length > 0 && (
            <Badge variant="destructive" className="absolute -top-2 -right-2 h-5 w-5 sm:h-6 sm:w-6 rounded-full p-0 flex items-center justify-center text-xs">
              {cart.length}
            </Badge>
          )}
        </Button>
      </SheetTrigger>
      <SheetContent className="w-full sm:max-w-md p-4 sm:p-6">
        <SheetHeader>
          <SheetTitle className="flex items-center gap-2 text-base sm:text-lg">
            <ShoppingCart className="h-4 w-4 sm:h-5 sm:w-5" />
            Your Cart ({cart.length} item{cart.length !== 1 ? 's' : ''})
          </SheetTitle>
        </SheetHeader>
        
        {cart.length === 0 ? (
          <div className="flex flex-col items-center justify-center h-[60vh] text-center">
            <ShoppingCart className="h-16 w-16 text-muted-foreground mb-4" />
            <p className="text-lg font-semibold mb-2">Your cart is empty</p>
            <p className="text-sm text-muted-foreground">Add items to get started!</p>
          </div>
        ) : (
          <div className="flex flex-col h-[calc(100vh-8rem)]">
            <ScrollArea className="flex-1 pr-2 sm:pr-4 mt-4 sm:mt-6">
              <div className="space-y-3 sm:space-y-4">
                {cart.map((item) => (
                  <div key={item.id} className="p-2.5 sm:p-3 rounded-lg border bg-card space-y-2 sm:space-y-3">
                    <div className="flex gap-2 sm:gap-3">
                      <div className="flex-1 min-w-0">
                        <h4 className="font-semibold text-sm sm:text-base truncate">{item.item_name}</h4>
                        <p className="text-xs sm:text-sm text-muted-foreground truncate">{item.restaurant_name}</p>
                        <p className="text-xs sm:text-sm font-bold text-primary mt-0.5 sm:mt-1">₹{item.price}</p>
                      </div>
                      <div className="flex flex-col items-end gap-1.5 sm:gap-2 flex-shrink-0">
                        <Button
                          variant="ghost"
                          size="icon"
                          className="h-6 w-6 sm:h-7 sm:w-7"
                          onClick={() => onRemoveItem(item.id)}
                        >
                          <Trash2 className="h-3 w-3 sm:h-3.5 sm:w-3.5" />
                        </Button>
                        <div className="flex items-center gap-0.5 sm:gap-1 border rounded-md">
                          <Button
                            variant="ghost"
                            size="icon"
                            className="h-6 w-6 sm:h-7 sm:w-7"
                            onClick={() => onUpdateQuantity(item.id, Math.max(1, item.quantity - 1))}
                          >
                            <Minus className="h-2.5 w-2.5 sm:h-3 sm:w-3" />
                          </Button>
                          <span className="w-6 sm:w-8 text-center text-xs sm:text-sm font-semibold">{item.quantity}</span>
                          <Button
                            variant="ghost"
                            size="icon"
                            className="h-6 w-6 sm:h-7 sm:w-7"
                            onClick={() => onUpdateQuantity(item.id, item.quantity + 1)}
                          >
                            <Plus className="h-2.5 w-2.5 sm:h-3 sm:w-3" />
                          </Button>
                        </div>
                      </div>
                    </div>
                    
                    {/* Item Instructions */}
                    <div className="space-y-1 sm:space-y-1.5">
                      <Label htmlFor={`instructions-${item.id}`} className="text-[10px] sm:text-xs flex items-center gap-1">
                        <MessageSquare className="h-2.5 w-2.5 sm:h-3 sm:w-3" />
                        Special Instructions (optional)
                      </Label>
                      <Textarea
                        id={`instructions-${item.id}`}
                        placeholder="e.g., Extra spicy, no onions..."
                        value={item.instructions || ''}
                        onChange={(e) => onUpdateInstructions(item.id, e.target.value)}
                        className="text-[10px] sm:text-xs h-14 sm:h-16 resize-none"
                      />
                    </div>
                  </div>
                ))}

                {/* AI Suggestions in Cart */}
                {aiSuggestions && aiSuggestions.length > 0 && (
                  <div className="mt-4 sm:mt-6 p-3 sm:p-4 rounded-lg bg-gradient-to-r from-primary/5 to-secondary/5 border border-primary/20">
                    <div className="flex items-center gap-1.5 sm:gap-2 mb-2 sm:mb-3">
                      <Sparkles className="h-3.5 w-3.5 sm:h-4 sm:w-4 text-primary flex-shrink-0" />
                      <h4 className="font-semibold text-xs sm:text-sm">Complete Your Meal</h4>
                    </div>
                    <p className="text-[10px] sm:text-xs text-muted-foreground mb-2 sm:mb-3">
                      AI suggests these items based on your cart
                    </p>
                    <div className="space-y-1.5 sm:space-y-2">
                      {aiSuggestions.slice(0, 3).map((item) => (
                        <div key={item.id} className="flex gap-1.5 sm:gap-2 p-1.5 sm:p-2 rounded-md bg-background border">
                          <img 
                            src={item.image} 
                            alt={item.name}
                            className="w-10 h-10 sm:w-12 sm:h-12 rounded object-cover flex-shrink-0"
                          />
                          <div className="flex-1 min-w-0">
                            <h5 className="font-medium text-xs sm:text-sm truncate">{item.name}</h5>
                            <p className="text-[10px] sm:text-xs text-muted-foreground truncate">{item.restaurant}</p>
                            <p className="text-[10px] sm:text-xs font-bold text-primary">₹{item.price}</p>
                          </div>
                          <Button 
                            size="sm"
                            variant="outline"
                            className="h-7 w-7 sm:h-8 sm:w-8 p-0 flex-shrink-0"
                            onClick={() => onAddSuggestion && onAddSuggestion(item)}
                          >
                            <Plus className="h-2.5 w-2.5 sm:h-3 sm:w-3" />
                          </Button>
                        </div>
                      ))}
                    </div>
                  </div>
                )}
              </div>
            </ScrollArea>
            
            <div className="pt-3 sm:pt-4 space-y-3 sm:space-y-4">
              <Separator />
              <div className="flex justify-between items-center text-base sm:text-lg font-bold">
                <span>Total:</span>
                <span className="text-primary">₹{total}</span>
              </div>
              <Button 
                variant="hero" 
                className="w-full h-10 sm:h-11 text-sm sm:text-base" 
                onClick={handleCheckout}
              >
                Proceed to Checkout
              </Button>
            </div>
          </div>
        )}
      </SheetContent>
    </Sheet>
  );
};
