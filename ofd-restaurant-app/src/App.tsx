import { Provider } from "react-redux";
import { Toaster } from "@/components/ui/toaster";
import { Toaster as Sonner } from "@/components/ui/sonner";
import { TooltipProvider } from "@/components/ui/tooltip";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { ScrollToTop } from "@/components/ScrollToTop";
import { store } from "@/store";
import Restaurant from "./pages/Restaurant";
import RestaurantOnboarding from "./pages/RestaurantOnboarding";
import OrderDetails from "./pages/OrderDetails";
import RestaurantMenu from "./pages/RestaurantMenu";
import NotFound from "./pages/NotFound";

const queryClient = new QueryClient();

const App = () => (
  <Provider store={store}>
    <QueryClientProvider client={queryClient}>
      <TooltipProvider>
        <Toaster />
        <Sonner />
        <BrowserRouter>
          <ScrollToTop />
          <Routes>
            <Route path="/" element={<Restaurant />} />
            <Route path="/restaurant" element={<Restaurant />} />
            <Route path="/restaurant-onboarding" element={<RestaurantOnboarding />} />
            <Route path="/restaurant/order/:orderId" element={<OrderDetails />} />
            <Route path="/restaurant/:restaurantId" element={<RestaurantMenu />} />
            <Route path="*" element={<NotFound />} />
          </Routes>
        </BrowserRouter>
      </TooltipProvider>
    </QueryClientProvider>
  </Provider>
);

export default App;
