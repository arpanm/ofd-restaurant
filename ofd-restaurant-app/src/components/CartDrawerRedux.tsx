/**
 * Cart Drawer (Redux) - Shopping cart drawer using Redux state
 */

import { useNavigate } from 'react-router-dom';
import { useCart } from '@/hooks/useCart';
import { Sheet, SheetContent, SheetHeader, SheetTitle } from '@/components/ui/sheet';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { ScrollArea } from '@/components/ui/scroll-area';
import { Separator } from '@/components/ui/separator';
import { Input } from '@/components/ui/input';
import { Skeleton } from '@/components/ui/skeleton';
import {
  ShoppingCart,
  Plus,
  Minus,
  Trash2,
  Tag,
  MapPin,
  X,
  AlertCircle,
} from 'lucide-react';

export const CartDrawerRedux = () => {
  const navigate = useNavigate();
  const {
    cart,
    isLoading,
    isUpdating,
    error,
    isCartOpen,
    isEmpty,
    updateCartItem,
    removeFromCart,
    applyCoupon,
    removeCoupon,
    closeCart,
    clearError,
  } = useCart();

  const handleCheckout = () => {
    closeCart();
    navigate('/checkout');
  };

  const handleUpdateQuantity = async (itemId: string, quantity: number) => {
    if (quantity < 1) {
      await removeFromCart(itemId);
    } else {
      await updateCartItem(itemId, { quantity });
    }
  };

  const handleApplyCoupon = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const formData = new FormData(e.currentTarget);
    const code = formData.get('couponCode') as string;
    if (code) {
      await applyCoupon(code);
      (e.target as HTMLFormElement).reset();
    }
  };

  return (
    <Sheet open={isCartOpen} onOpenChange={(open) => !open && closeCart()}>
      <SheetContent className="w-full sm:max-w-md p-4 sm:p-6 flex flex-col">
        <SheetHeader>
          <SheetTitle className="flex items-center gap-2 text-base sm:text-lg">
            <ShoppingCart className="h-4 w-4 sm:h-5 sm:w-5" />
            Your Cart
            {cart && cart.items.length > 0 && (
              <Badge variant="secondary" className="ml-2">
                {cart.items.reduce((sum, item) => sum + item.quantity, 0)} items
              </Badge>
            )}
          </SheetTitle>
        </SheetHeader>

        {error && (
          <div className="mt-4 p-3 bg-destructive/10 border border-destructive/20 rounded-lg flex items-center gap-2">
            <AlertCircle className="h-4 w-4 text-destructive" />
            <span className="text-sm text-destructive">{error}</span>
            <Button
              variant="ghost"
              size="icon"
              className="ml-auto h-6 w-6"
              onClick={clearError}
            >
              <X className="h-4 w-4" />
            </Button>
          </div>
        )}

        {isLoading ? (
          <div className="flex-1 space-y-4 mt-6">
            {[1, 2, 3].map((i) => (
              <Skeleton key={i} className="h-24 w-full" />
            ))}
          </div>
        ) : isEmpty ? (
          <div className="flex flex-col items-center justify-center flex-1 text-center">
            <ShoppingCart className="h-16 w-16 text-muted-foreground mb-4" />
            <p className="text-lg font-semibold mb-2">Your cart is empty</p>
            <p className="text-sm text-muted-foreground mb-4">
              Add items from restaurants to get started!
            </p>
            <Button variant="outline" onClick={closeCart}>
              Continue Browsing
            </Button>
          </div>
        ) : (
          <div className="flex flex-col flex-1 overflow-hidden">
            {/* Restaurant Info */}
            {cart?.restaurantName && (
              <div className="py-3 border-b">
                <p className="font-semibold">{cart.restaurantName}</p>
                {cart.isMultiRestaurant && (
                  <Badge variant="secondary" className="mt-1">
                    Multi-restaurant order
                  </Badge>
                )}
              </div>
            )}

            {/* Cart Items */}
            <ScrollArea className="flex-1 pr-4 mt-4">
              <div className="space-y-4">
                {cart?.items.map((item) => (
                  <div
                    key={item.id}
                    className="p-3 rounded-lg border bg-card space-y-2"
                  >
                    <div className="flex gap-3">
                      {item.imageUrl && (
                        <img
                          src={item.imageUrl}
                          alt={item.name}
                          className="w-16 h-16 rounded object-cover"
                        />
                      )}
                      <div className="flex-1 min-w-0">
                        <h4 className="font-semibold text-sm truncate">{item.name}</h4>
                        {item.customizations.length > 0 && (
                          <p className="text-xs text-muted-foreground truncate">
                            {item.customizations.map((c) => c.optionName).join(', ')}
                          </p>
                        )}
                        {item.addons.length > 0 && (
                          <p className="text-xs text-muted-foreground truncate">
                            +{item.addons.map((a) => a.addonName).join(', ')}
                          </p>
                        )}
                        <p className="text-sm font-bold text-primary mt-1">
                          ₹{item.totalPrice.toFixed(2)}
                        </p>
                      </div>
                      <div className="flex flex-col items-end gap-2">
                        <Button
                          variant="ghost"
                          size="icon"
                          className="h-7 w-7"
                          onClick={() => removeFromCart(item.id)}
                          disabled={isUpdating}
                        >
                          <Trash2 className="h-4 w-4" />
                        </Button>
                        <div className="flex items-center gap-1 border rounded-md">
                          <Button
                            variant="ghost"
                            size="icon"
                            className="h-7 w-7"
                            onClick={() => handleUpdateQuantity(item.id, item.quantity - 1)}
                            disabled={isUpdating}
                          >
                            <Minus className="h-3 w-3" />
                          </Button>
                          <span className="w-8 text-center text-sm font-semibold">
                            {item.quantity}
                          </span>
                          <Button
                            variant="ghost"
                            size="icon"
                            className="h-7 w-7"
                            onClick={() => handleUpdateQuantity(item.id, item.quantity + 1)}
                            disabled={isUpdating}
                          >
                            <Plus className="h-3 w-3" />
                          </Button>
                        </div>
                      </div>
                    </div>
                    {item.specialInstructions && (
                      <p className="text-xs text-muted-foreground italic">
                        Note: {item.specialInstructions}
                      </p>
                    )}
                  </div>
                ))}
              </div>
            </ScrollArea>

            {/* Coupon Section */}
            <div className="py-4 border-t">
              {cart?.appliedCouponCode ? (
                <div className="flex items-center justify-between p-3 bg-green-50 border border-green-200 rounded-lg">
                  <div className="flex items-center gap-2">
                    <Tag className="h-4 w-4 text-green-600" />
                    <span className="text-sm font-medium text-green-700">
                      {cart.appliedCouponCode} applied
                    </span>
                  </div>
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={() => removeCoupon()}
                    disabled={isUpdating}
                  >
                    Remove
                  </Button>
                </div>
              ) : (
                <form onSubmit={handleApplyCoupon} className="flex gap-2">
                  <Input
                    name="couponCode"
                    placeholder="Enter coupon code"
                    className="flex-1"
                  />
                  <Button type="submit" variant="outline" disabled={isUpdating}>
                    Apply
                  </Button>
                </form>
              )}
            </div>

            {/* Delivery Address */}
            {cart?.deliveryAddress && (
              <div className="py-3 border-t">
                <div className="flex items-start gap-2">
                  <MapPin className="h-4 w-4 text-muted-foreground mt-0.5" />
                  <div className="flex-1">
                    <p className="text-sm font-medium">
                      {cart.deliveryAddress.label}
                    </p>
                    <p className="text-xs text-muted-foreground">
                      {cart.deliveryAddress.street}, {cart.deliveryAddress.city}
                    </p>
                  </div>
                  <Button variant="ghost" size="sm">
                    Change
                  </Button>
                </div>
              </div>
            )}

            {/* Price Breakdown */}
            <div className="py-4 space-y-2 border-t">
              <div className="flex justify-between text-sm">
                <span>Subtotal</span>
                <span>₹{cart?.subtotal.toFixed(2)}</span>
              </div>
              <div className="flex justify-between text-sm">
                <span>Delivery Fee</span>
                <span>₹{cart?.deliveryFee.toFixed(2)}</span>
              </div>
              <div className="flex justify-between text-sm">
                <span>Taxes</span>
                <span>₹{cart?.taxes.toFixed(2)}</span>
              </div>
              {cart && cart.discount > 0 && (
                <div className="flex justify-between text-sm text-green-600">
                  <span>Discount</span>
                  <span>-₹{cart.discount.toFixed(2)}</span>
                </div>
              )}
              <Separator />
              <div className="flex justify-between text-lg font-bold">
                <span>Total</span>
                <span className="text-primary">₹{cart?.total.toFixed(2)}</span>
              </div>
            </div>

            {/* Checkout Button */}
            <Button
              className="w-full"
              size="lg"
              onClick={handleCheckout}
              disabled={isUpdating}
            >
              Proceed to Checkout
            </Button>
          </div>
        )}
      </SheetContent>
    </Sheet>
  );
};

export default CartDrawerRedux;

