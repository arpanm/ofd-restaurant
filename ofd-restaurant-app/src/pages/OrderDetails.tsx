import { useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Navbar } from "@/components/Navbar";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Separator } from "@/components/ui/separator";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Textarea } from "@/components/ui/textarea";
import { Label } from "@/components/ui/label";
import { toast } from "sonner";
import { 
  ArrowLeft,
  User,
  Phone,
  MapPin,
  Clock,
  CheckCircle2,
  AlertCircle,
  Package,
  Sparkles,
  MessageSquare,
  TrendingUp,
  ChefHat,
  IndianRupee
} from "lucide-react";

interface OrderItem {
  id: string;
  name: string;
  quantity: number;
  price: number;
  image: string;
  customizations?: string[];
  spiceLevel?: number;
  aiCookingInsights?: {
    type: "preference" | "review" | "comment" | "instruction";
    title: string;
    description: string;
    source?: string;
    confidence?: string;
  }[];
}

interface CustomerDetails {
  name: string;
  phone: string;
  email: string;
  address: string;
  deliveryInstructions?: string;
  orderHistory: number;
  avgOrderValue: number;
  preferredItems?: string[];
  dietaryPreferences?: string[];
  allergies?: string[];
  averageRating?: number;
}

interface Order {
  id: string;
  orderNumber: string;
  status: "new" | "confirmed" | "preparing" | "ready" | "out_for_delivery" | "delivered" | "cancelled";
  items: OrderItem[];
  customer: CustomerDetails;
  specialInstructions?: string;
  subtotal: number;
  tax: number;
  deliveryFee: number;
  total: number;
  orderTime: string;
  estimatedTime?: string;
  paymentMethod: string;
}

const OrderDetails = () => {
  const { orderId } = useParams();
  const navigate = useNavigate();

  // Mock data - In real app, fetch from API based on orderId
  const [order, setOrder] = useState<Order>({
    id: orderId || "1",
    orderNumber: "#2847",
    status: "preparing",
    orderTime: "2:45 PM",
    estimatedTime: "3:15 PM",
    paymentMethod: "UPI",
    customer: {
      name: "Rahul Kumar",
      phone: "+91 98765 43210",
      email: "rahul.k@example.com",
      address: "Flat 402, Prestige Towers, Koramangala 4th Block, Bangalore - 560034",
      deliveryInstructions: "Please ring the bell twice. Leave at door if no response.",
      orderHistory: 23,
      avgOrderValue: 425,
      preferredItems: ["Biryani", "Tikka", "Naan"],
      dietaryPreferences: ["Less Spicy", "Well Cooked Rice"],
      allergies: ["Peanuts"],
      averageRating: 4.8
    },
    items: [
      {
        id: "1",
        name: "Chicken Biryani",
        quantity: 2,
        price: 220,
        image: "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=400",
        customizations: ["Extra raita", "Less spicy"],
        spiceLevel: 2,
        aiCookingInsights: [
          {
            type: "preference",
            title: "Customer Prefers Less Spicy Food",
            description: "In 8 out of last 10 orders, customer requested reduced spice level",
            source: "Order history analysis",
            confidence: "High (80%)"
          },
          {
            type: "review",
            title: "Loves Well-Cooked Rice",
            description: "Previous 5-star review: 'Perfect texture, rice was not mushy'",
            source: "Review from order #2801",
            confidence: "Medium (65%)"
          },
          {
            type: "instruction",
            title: "Cook Rice Al Dente",
            description: "Customer consistently prefers slightly firm rice texture",
            source: "Pattern from 5 orders",
            confidence: "High (85%)"
          },
          {
            type: "comment",
            title: "Separate Raita Packing",
            description: "Requested in 3 recent orders to keep raita container separate",
            source: "Order #2801, #2756, #2698",
            confidence: "Very High (90%)"
          }
        ]
      },
      {
        id: "2",
        name: "Paneer Tikka",
        quantity: 1,
        price: 180,
        image: "https://images.unsplash.com/photo-1599487488170-d11ec9c172f0?w=400",
        customizations: ["Extra mint chutney"],
        aiCookingInsights: [
          {
            type: "preference",
            title: "Extra Char Preferred",
            description: "Customer rated 5 stars when paneer had good char marks",
            source: "Review analysis",
            confidence: "Medium (70%)"
          },
          {
            type: "review",
            title: "Loves Tangy Mint Chutney",
            description: "Review: 'Mint chutney was amazing, please add extra'",
            source: "Order #2756",
            confidence: "High (80%)"
          },
          {
            type: "instruction",
            title: "Cook Until Golden Brown",
            description: "Best ratings when tikka is well-grilled with visible char",
            source: "Rating pattern analysis",
            confidence: "Medium (65%)"
          }
        ]
      },
      {
        id: "3",
        name: "Mango Lassi",
        quantity: 2,
        price: 80,
        image: "https://images.unsplash.com/photo-1623065422902-30a2d299bbe4?w=400",
        aiCookingInsights: [
          {
            type: "preference",
            title: "Prefers Less Sweet",
            description: "Customer mentioned 'too sweet' in previous comment",
            source: "Order #2801",
            confidence: "High (75%)"
          }
        ]
      }
    ],
    specialInstructions: "Please make the biryani less spicy as it's for kids. Also, pack the raita separately.",
    subtotal: 780,
    tax: 39,
    deliveryFee: 30,
    total: 849
  });

  const [internalNotes, setInternalNotes] = useState("");

  const statusOptions: Order["status"][] = ["new", "confirmed", "preparing", "ready", "out_for_delivery", "delivered", "cancelled"];

  const getStatusColor = (status: Order["status"]) => {
    const colors = {
      new: "bg-blue-500",
      confirmed: "bg-purple-500",
      preparing: "bg-yellow-500",
      ready: "bg-green-500",
      out_for_delivery: "bg-indigo-500",
      delivered: "bg-gray-500",
      cancelled: "bg-red-500"
    };
    return colors[status] || "bg-gray-500";
  };

  const getStatusIcon = (status: Order["status"]) => {
    if (status === "new") return <AlertCircle className="h-5 w-5" />;
    if (status === "preparing") return <Clock className="h-5 w-5" />;
    if (status === "ready" || status === "delivered") return <CheckCircle2 className="h-5 w-5" />;
    return <Clock className="h-5 w-5" />;
  };

  const updateOrderStatus = (newStatus: Order["status"]) => {
    setOrder({ ...order, status: newStatus });
    toast.success(`Order ${order.orderNumber} updated to ${newStatus}`);
  };

  const saveNotes = () => {
    toast.success("Internal notes saved");
  };

  return (
    <div className="min-h-screen bg-gradient-hero">
      <Navbar />
      
      <div className="pt-24 pb-12 px-4">
        <div className="container mx-auto max-w-7xl">
          {/* Header */}
          <div className="mb-8">
            <Button 
              variant="ghost" 
              onClick={() => navigate("/restaurant")}
              className="mb-4 text-sm"
            >
              <ArrowLeft className="h-4 w-4 mr-2" />
              Back to Dashboard
            </Button>
            
            <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-4">
              <div className="min-w-0 flex-1">
                <h1 className="text-2xl sm:text-3xl lg:text-4xl font-bold mb-2 truncate">Order {order.orderNumber}</h1>
                <p className="text-sm sm:text-base text-muted-foreground">Placed at {order.orderTime} • Estimated: {order.estimatedTime}</p>
              </div>
              
              <div className="flex flex-col gap-3 w-full lg:w-auto lg:flex-shrink-0">
                <Select value={order.status} onValueChange={(value) => updateOrderStatus(value as Order["status"])}>
                  <SelectTrigger className="w-full lg:w-64">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    {statusOptions.map((status) => (
                      <SelectItem key={status} value={status}>
                        {status.replace('_', ' ').toUpperCase()}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
                <Badge className={`${getStatusColor(order.status)} text-white justify-center py-2 text-sm sm:text-base`}>
                  <span className="mr-2">{getStatusIcon(order.status)}</span>
                  {order.status.toUpperCase()}
                </Badge>
              </div>
            </div>
          </div>

          <div className="grid lg:grid-cols-3 gap-6">
            {/* Left Column - Order Items & Instructions */}
            <div className="lg:col-span-2 space-y-6">
              {/* Order Items */}
              <Card>
                <CardHeader>
                  <CardTitle className="flex items-center gap-2 text-base sm:text-lg">
                    <Package className="h-4 w-4 sm:h-5 sm:w-5 text-primary flex-shrink-0" />
                    <span className="truncate">Order Items ({order.items.length})</span>
                  </CardTitle>
                </CardHeader>
                <CardContent className="space-y-4">
                  {order.items.map((item) => (
                    <div key={item.id}>
                      <div className="flex flex-col sm:flex-row gap-4">
                        <img 
                          src={item.image} 
                          alt={item.name}
                          className="w-full sm:w-24 h-48 sm:h-24 object-cover rounded-lg flex-shrink-0"
                        />
                        <div className="flex-1 min-w-0">
                          <div className="flex flex-col sm:flex-row sm:justify-between sm:items-start gap-2">
                            <div className="min-w-0 flex-1">
                              <h3 className="font-bold text-base sm:text-lg truncate">{item.name}</h3>
                              <p className="text-xs sm:text-sm text-muted-foreground">Quantity: {item.quantity}</p>
                              {item.spiceLevel && (
                                <p className="text-xs sm:text-sm">🔥 Spice Level: {item.spiceLevel}/5</p>
                              )}
                            </div>
                            <p className="font-bold text-base sm:text-lg flex-shrink-0">₹{item.price * item.quantity}</p>
                          </div>
                          
                          {item.customizations && item.customizations.length > 0 && (
                            <div className="mt-2">
                              <p className="text-xs sm:text-sm font-semibold mb-1">Customizations:</p>
                              <div className="flex flex-wrap gap-2">
                                {item.customizations.map((custom, idx) => (
                                  <Badge key={idx} variant="outline" className="text-xs">
                                    {custom}
                                  </Badge>
                                ))}
                              </div>
                            </div>
                          )}

                          {/* AI Cooking Insights */}
                          {item.aiCookingInsights && item.aiCookingInsights.length > 0 && (
                            <div className="mt-3 space-y-2">
                              <p className="text-xs sm:text-sm font-semibold flex items-center gap-1">
                                <Sparkles className="h-4 w-4 text-primary flex-shrink-0" />
                                <span className="truncate">AI Cooking Insights for This Customer:</span>
                              </p>
                              {item.aiCookingInsights.map((insight, idx) => {
                                const getBgColor = () => {
                                  if (insight.type === "preference") return "bg-blue-50 dark:bg-blue-950 border-blue-200 dark:border-blue-800";
                                  if (insight.type === "review") return "bg-green-50 dark:bg-green-950 border-green-200 dark:border-green-800";
                                  if (insight.type === "comment") return "bg-yellow-50 dark:bg-yellow-950 border-yellow-200 dark:border-yellow-800";
                                  return "bg-purple-50 dark:bg-purple-950 border-purple-200 dark:border-purple-800";
                                };

                                const getIcon = () => {
                                  if (insight.type === "preference") return "👤";
                                  if (insight.type === "review") return "⭐";
                                  if (insight.type === "comment") return "💬";
                                  return "👨‍🍳";
                                };

                                return (
                                  <div key={idx} className={`p-2 sm:p-3 rounded-lg border ${getBgColor()}`}>
                                    <div className="flex items-start gap-2">
                                      <span className="text-base sm:text-lg flex-shrink-0">{getIcon()}</span>
                                      <div className="flex-1 min-w-0">
                                        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-1 sm:gap-2">
                                          <p className="font-semibold text-xs sm:text-sm truncate flex-1 min-w-0">{insight.title}</p>
                                          {insight.confidence && (
                                            <Badge variant="outline" className="text-[10px] sm:text-xs whitespace-nowrap flex-shrink-0">
                                              {insight.confidence}
                                            </Badge>
                                          )}
                                        </div>
                                        <p className="text-[10px] sm:text-xs text-muted-foreground mt-1 break-words">{insight.description}</p>
                                        {insight.source && (
                                          <p className="text-[10px] sm:text-xs text-muted-foreground italic mt-1 break-words">
                                            Source: {insight.source}
                                          </p>
                                        )}
                                      </div>
                                    </div>
                                  </div>
                                );
                              })}
                            </div>
                          )}
                        </div>
                      </div>
                      {order.items.indexOf(item) !== order.items.length - 1 && (
                        <Separator className="my-4" />
                      )}
                    </div>
                  ))}
                </CardContent>
              </Card>

              {/* Special Instructions */}
              {order.specialInstructions && (
                <Card>
                  <CardHeader>
                    <CardTitle className="flex items-center gap-2 text-base sm:text-lg">
                      <MessageSquare className="h-4 w-4 sm:h-5 sm:w-5 text-primary flex-shrink-0" />
                      <span className="truncate">Special Instructions from Customer</span>
                    </CardTitle>
                  </CardHeader>
                  <CardContent>
                    <div className="p-3 sm:p-4 bg-yellow-50 dark:bg-yellow-950 border border-yellow-200 dark:border-yellow-800 rounded-lg">
                      <p className="text-xs sm:text-sm break-words">{order.specialInstructions}</p>
                    </div>
                  </CardContent>
                </Card>
              )}

              {/* Internal Notes */}
              <Card>
                <CardHeader>
                  <CardTitle className="flex items-center gap-2 text-base sm:text-lg">
                    <ChefHat className="h-4 w-4 sm:h-5 sm:w-5 text-primary flex-shrink-0" />
                    <span className="truncate">Internal Kitchen Notes</span>
                  </CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="space-y-3">
                    <Textarea
                      placeholder="Add internal notes for kitchen staff..."
                      value={internalNotes}
                      onChange={(e) => setInternalNotes(e.target.value)}
                      rows={4}
                      className="w-full resize-none text-xs sm:text-sm"
                    />
                    <Button onClick={saveNotes} variant="outline" className="w-full text-sm">
                      Save Notes
                    </Button>
                  </div>
                </CardContent>
              </Card>
            </div>

            {/* Right Column - Customer & Payment Details */}
            <div className="space-y-6">
              {/* Customer Details */}
              <Card>
                <CardHeader>
                  <CardTitle className="flex items-center gap-2 text-base sm:text-lg">
                    <User className="h-4 w-4 sm:h-5 sm:w-5 text-primary flex-shrink-0" />
                    <span className="truncate">Customer Details</span>
                  </CardTitle>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div>
                    <p className="font-bold text-base sm:text-lg truncate">{order.customer.name}</p>
                    <div className="flex items-center gap-2 text-xs sm:text-sm text-muted-foreground mt-1">
                      <TrendingUp className="h-3 w-3 sm:h-4 sm:w-4 flex-shrink-0" />
                      <span className="truncate">{order.customer.orderHistory} orders • ₹{order.customer.avgOrderValue} avg</span>
                    </div>
                  </div>

                  <Separator />

                  <div className="space-y-3">
                    <div className="flex items-start gap-3">
                      <Phone className="h-4 w-4 text-primary mt-1 flex-shrink-0" />
                      <div className="min-w-0 flex-1">
                        <p className="text-xs sm:text-sm font-medium">Phone</p>
                        <p className="text-xs sm:text-sm text-muted-foreground break-words">{order.customer.phone}</p>
                      </div>
                    </div>

                    <div className="flex items-start gap-3">
                      <MapPin className="h-4 w-4 text-primary mt-1 flex-shrink-0" />
                      <div className="min-w-0 flex-1">
                        <p className="text-xs sm:text-sm font-medium">Delivery Address</p>
                        <p className="text-xs sm:text-sm text-muted-foreground break-words">{order.customer.address}</p>
                      </div>
                    </div>

                    {order.customer.deliveryInstructions && (
                      <div className="p-2 sm:p-3 bg-blue-50 dark:bg-blue-950 border border-blue-200 dark:border-blue-800 rounded-lg">
                        <p className="text-[10px] sm:text-xs font-semibold mb-1">Delivery Instructions:</p>
                        <p className="text-xs sm:text-sm break-words">{order.customer.deliveryInstructions}</p>
                      </div>
                    )}
                  </div>

                  <Separator />

                  {/* AI Customer Insights */}
                  <div className="space-y-3">
                    <div>
                      <p className="text-xs sm:text-sm font-semibold mb-2 flex items-center gap-1">
                        <Sparkles className="h-3 w-3 sm:h-4 sm:w-4 text-primary flex-shrink-0" />
                        <span className="truncate">Frequently Orders:</span>
                      </p>
                      {order.customer.preferredItems && (
                        <div className="flex flex-wrap gap-1.5 sm:gap-2">
                          {order.customer.preferredItems.map((item, idx) => (
                            <Badge key={idx} variant="secondary" className="text-xs">
                              {item}
                            </Badge>
                          ))}
                        </div>
                      )}
                    </div>

                    {order.customer.dietaryPreferences && order.customer.dietaryPreferences.length > 0 && (
                      <div>
                        <p className="text-xs sm:text-sm font-semibold mb-2">🥗 Dietary Preferences:</p>
                        <div className="flex flex-wrap gap-1.5 sm:gap-2">
                          {order.customer.dietaryPreferences.map((pref, idx) => (
                            <Badge key={idx} className="bg-green-500 text-xs">
                              {pref}
                            </Badge>
                          ))}
                        </div>
                      </div>
                    )}

                    {order.customer.allergies && order.customer.allergies.length > 0 && (
                      <div className="p-2 sm:p-3 bg-red-50 dark:bg-red-950 border border-red-200 dark:border-red-800 rounded-lg">
                        <p className="text-xs sm:text-sm font-bold text-red-700 dark:text-red-300 mb-1">
                          ⚠️ ALLERGIES:
                        </p>
                        <div className="flex flex-wrap gap-1.5 sm:gap-2">
                          {order.customer.allergies.map((allergy, idx) => (
                            <Badge key={idx} variant="destructive" className="text-xs">
                              {allergy}
                            </Badge>
                          ))}
                        </div>
                        <p className="text-[10px] sm:text-xs text-red-600 dark:text-red-400 mt-2 break-words">
                          Please ensure no cross-contamination
                        </p>
                      </div>
                    )}

                    {order.customer.averageRating && (
                      <div>
                        <p className="text-xs sm:text-sm font-semibold mb-1">⭐ Customer Rating:</p>
                        <div className="flex flex-wrap items-center gap-2">
                          <Badge variant="outline" className="text-xs sm:text-sm">
                            {order.customer.averageRating} / 5.0
                          </Badge>
                          <span className="text-[10px] sm:text-xs text-muted-foreground">
                            (Valued customer)
                          </span>
                        </div>
                      </div>
                    )}
                  </div>
                </CardContent>
              </Card>

              {/* Order Summary */}
              <Card>
                <CardHeader>
                  <CardTitle className="flex items-center gap-2 text-base sm:text-lg">
                    <IndianRupee className="h-4 w-4 sm:h-5 sm:w-5 text-primary flex-shrink-0" />
                    <span className="truncate">Order Summary</span>
                  </CardTitle>
                </CardHeader>
                <CardContent className="space-y-3">
                  <div className="flex justify-between gap-2">
                    <span className="text-xs sm:text-sm text-muted-foreground">Subtotal</span>
                    <span className="font-medium text-xs sm:text-sm flex-shrink-0">₹{order.subtotal}</span>
                  </div>
                  <div className="flex justify-between gap-2">
                    <span className="text-xs sm:text-sm text-muted-foreground">Tax (5%)</span>
                    <span className="font-medium text-xs sm:text-sm flex-shrink-0">₹{order.tax}</span>
                  </div>
                  <div className="flex justify-between gap-2">
                    <span className="text-xs sm:text-sm text-muted-foreground">Delivery Fee</span>
                    <span className="font-medium text-xs sm:text-sm flex-shrink-0">₹{order.deliveryFee}</span>
                  </div>
                  <Separator />
                  <div className="flex justify-between gap-2">
                    <span className="font-bold text-base sm:text-lg">Total</span>
                    <span className="font-bold text-base sm:text-lg text-primary flex-shrink-0">₹{order.total}</span>
                  </div>
                  <div className="pt-2">
                    <Badge variant="outline" className="w-full justify-center text-xs sm:text-sm">
                      Paid via {order.paymentMethod}
                    </Badge>
                  </div>
                </CardContent>
              </Card>

              {/* Quick Actions */}
              <Card>
                <CardHeader>
                  <CardTitle className="text-base sm:text-lg">Quick Actions</CardTitle>
                </CardHeader>
                <CardContent className="space-y-2">
                  <Button className="w-full text-sm" variant="outline">
                    <Phone className="h-4 w-4 mr-2" />
                    Call Customer
                  </Button>
                  <Button className="w-full text-sm" variant="outline">
                    <MessageSquare className="h-4 w-4 mr-2" />
                    Send Message
                  </Button>
                  <Button className="w-full text-sm" variant="outline">
                    <Package className="h-4 w-4 mr-2" />
                    Print Bill
                  </Button>
                  <Button className="w-full text-sm" variant="destructive">
                    Cancel Order
                  </Button>
                </CardContent>
              </Card>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default OrderDetails;

