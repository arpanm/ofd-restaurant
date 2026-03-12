import { ReactNode } from "react";
import { Navigate, useLocation } from "react-router-dom";
import { tokenManager } from "@/services/api.config";

interface ProtectedRouteProps {
  children: ReactNode;
}

/**
 * Wraps routes that require authentication. Redirects to /login with ?next= when no token.
 */
export function ProtectedRoute({ children }: ProtectedRouteProps) {
  const location = useLocation();
  const token = tokenManager.getAccessToken();

  if (!token) {
    const next = location.pathname + location.search;
    return <Navigate to={next ? `/login?next=${encodeURIComponent(next)}` : "/login"} state={{ from: location }} replace />;
  }

  return <>{children}</>;
}
