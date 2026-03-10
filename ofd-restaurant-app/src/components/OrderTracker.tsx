import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { CheckCircle2, Clock, Package, Truck } from "lucide-react";

interface Order {
  id: string;
  items: { item_name: string; quantity: number; price: number }[];
  total: number;
  status: string;
  delivery_address: string;
  created_at: string;
}

interface OrderTrackerProps {
  order: Order;
}

export const OrderTracker = ({ order }: OrderTrackerProps) => {
  const statusConfig = {
    confirmed: { icon: CheckCircle2, label: "Order Confirmed", color: "text-green-500" },
    preparing: { icon: Clock, label: "Preparing", color: "text-yellow-500" },
    out_for_delivery: { icon: Truck, label: "Out for Delivery", color: "text-blue-500" },
    delivered: { icon: Package, label: "Delivered", color: "text-green-600" }
  };

  const currentStatus = statusConfig[order.status as keyof typeof statusConfig] || statusConfig.confirmed;
  const StatusIcon = currentStatus.icon;

  const allStatuses = ["confirmed", "preparing", "out_for_delivery", "delivered"];
  const currentIndex = allStatuses.indexOf(order.status);

  return (
    <Card className="w-full">
      <CardHeader className="pb-3 sm:pb-4 px-3 sm:px-6">
        <CardTitle className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-2">
          <span className="text-sm sm:text-base md:text-lg truncate">Order #{order.id}</span>
          <Badge variant="secondary" className={`${currentStatus.color} text-xs sm:text-sm w-fit`}>
            {currentStatus.label}
          </Badge>
        </CardTitle>
      </CardHeader>
      <CardContent className="space-y-3 sm:space-y-4 px-3 sm:px-6">
        <div className="flex justify-between items-start sm:items-center gap-1 sm:gap-2">
          {allStatuses.map((status, index) => {
            const config = statusConfig[status as keyof typeof statusConfig];
            const Icon = config.icon;
            const isCompleted = index <= currentIndex;
            
            return (
              <div key={status} className="flex flex-col items-center flex-1 min-w-0">
                <div className={`rounded-full p-1 sm:p-1.5 md:p-2 ${isCompleted ? 'bg-primary text-primary-foreground' : 'bg-muted text-muted-foreground'}`}>
                  <Icon className="h-3 w-3 sm:h-4 sm:w-4 md:h-5 md:w-5" />
                </div>
                <p className="text-[9px] sm:text-[10px] md:text-xs mt-1 sm:mt-1.5 md:mt-2 text-center line-clamp-2 px-0.5">
                  {config.label}
                </p>
              </div>
            );
          })}
        </div>
        
        <div className="pt-2 sm:pt-3 md:pt-4 space-y-1.5 sm:space-y-2">
          <p className="text-xs sm:text-sm break-words">
            <span className="font-semibold">Delivery to:</span> {order.delivery_address}
          </p>
          <p className="text-xs sm:text-sm">
            <span className="font-semibold">Total:</span> ₹{order.total}
          </p>
          <p className="text-[10px] sm:text-xs text-muted-foreground">
            Ordered: {new Date(order.created_at).toLocaleString()}
          </p>
        </div>
      </CardContent>
    </Card>
  );
};
