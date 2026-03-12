import { Provider } from "react-redux";
import { Toaster } from "@/components/ui/toaster";
import { Toaster as Sonner } from "@/components/ui/sonner";
import { TooltipProvider } from "@/components/ui/tooltip";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { ScrollToTop } from "@/components/ScrollToTop";
import { ProtectedRoute } from "@/components/auth/ProtectedRoute";
import { store } from "@/store";
import HomePage from "./pages/HomePage";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
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
            <Route path="/" element={<HomePage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/dashboard" element={<ProtectedRoute><Restaurant /></ProtectedRoute>} />
            <Route path="/restaurant" element={<ProtectedRoute><Restaurant /></ProtectedRoute>} />
            <Route path="/restaurant-onboarding" element={<RestaurantOnboarding />} />
            <Route path="/restaurant/order/:orderId" element={<ProtectedRoute><OrderDetails /></ProtectedRoute>} />
            <Route path="/restaurant/:restaurantId" element={<ProtectedRoute><RestaurantMenu /></ProtectedRoute>} />
            <Route path="*" element={<NotFound />} />
          </Routes>
        </BrowserRouter>
      </TooltipProvider>
    </QueryClientProvider>
  </Provider>
);

export default App;
