import { useEffect } from "react";
import { Link } from "react-router-dom";
import { Button } from "@/components/ui/button";
import {
  Store,
  UtensilsCrossed,
  ClipboardList,
  Megaphone,
  BarChart3,
  Sparkles,
  Quote,
} from "lucide-react";
import { tokenManager } from "@/services/api.config";

const FEATURES = [
  { icon: Store, title: "Quick onboarding", description: "Get your restaurant live in minutes with our step-by-step flow." },
  { icon: UtensilsCrossed, title: "Menu management", description: "Categories, items, pricing, and availability in one place." },
  { icon: ClipboardList, title: "Orders", description: "View, update status, and manage orders from a single dashboard." },
  { icon: Megaphone, title: "Promotions", description: "Run campaigns, coupons, and reach more customers." },
  { icon: BarChart3, title: "Analytics", description: "Insights and reports to grow your business." },
  { icon: Sparkles, title: "AI suggestions", description: "Smart recommendations to boost orders and engagement." },
];

const STEPS = [
  "Sign up and create your account",
  "Complete onboarding with your menu & docs",
  "Go live and start receiving orders",
  "Grow with promotions and analytics",
];

const TESTIMONIALS = [
  { quote: "We went live in a day. Orders doubled in the first month.", name: "Priya S.", role: "Owner, Spice Route" },
  { quote: "The dashboard is simple and the support team is quick.", name: "Rahul M.", role: "Manager, Urban Bites" },
  { quote: "Promotions and analytics helped us grow without guesswork.", name: "Anita K.", role: "Owner, Green Bowl" },
];

export default function HomePage() {
  const isLoggedIn = !!tokenManager.getAccessToken();

  useEffect(() => {
    document.title = "Restaurant App – Dashboard, Menu, Orders & Promotions | FoodAI";
    const metaDesc = document.querySelector('meta[name="description"]');
    if (metaDesc) metaDesc.setAttribute("content", "Restaurant onboarding, dashboard, menu management, orders, promotions, campaigns, reviews and AI suggestions. Grow your restaurant business.");
  }, []);

  useEffect(() => {
    const script = document.createElement("script");
    script.type = "application/ld+json";
    script.text = JSON.stringify({
      "@context": "https://schema.org",
      "@type": "WebSite",
      name: "Restaurant App – FoodAI",
      description: "Restaurant onboarding, dashboard, menu management, orders & promotions.",
      url: typeof window !== "undefined" ? window.location.origin : "",
    });
    document.head.appendChild(script);
    return () => { document.head.removeChild(script); };
  }, []);

  return (
    <div className="min-h-screen bg-white">
      {/* Navbar */}
      <header className="fixed top-0 left-0 right-0 bg-white/95 backdrop-blur border-b z-50">
        <div className="max-w-6xl mx-auto px-4 sm:px-6 h-14 flex items-center justify-between">
          <Link to="/" className="text-xl font-bold text-orange-600">
            🍽️ Restaurant
          </Link>
          <nav className="flex items-center gap-3">
            <Link to="/login">
              <Button variant="ghost">Login</Button>
            </Link>
            <Link to="/register">
              <Button>Get Started</Button>
            </Link>
            {isLoggedIn && (
              <Link to="/dashboard">
                <Button variant="secondary">Dashboard</Button>
              </Link>
            )}
          </nav>
        </div>
      </header>

      <main className="pt-14">
        {/* Hero */}
        <section className="relative py-16 sm:py-24 px-4 bg-gradient-to-b from-orange-50 to-white overflow-hidden">
          <div className="max-w-4xl mx-auto text-center">
            <h1 className="text-4xl sm:text-5xl font-bold text-gray-900 tracking-tight">
              Run your restaurant, simpler.
            </h1>
            <p className="mt-4 text-lg sm:text-xl text-gray-600 max-w-2xl mx-auto">
              Onboarding, menu, orders, and promotions in one place. Go live in minutes and grow with data.
            </p>
            <div className="mt-8 flex flex-col sm:flex-row gap-4 justify-center">
              <Link to="/register">
                <Button size="lg" className="w-full sm:w-auto text-base px-8">
                  Get Started
                </Button>
              </Link>
              <Link to="/login">
                <Button size="lg" variant="outline" className="w-full sm:w-auto text-base px-8">
                  Login
                </Button>
              </Link>
            </div>
          </div>
        </section>

        {/* Stats */}
        <section className="py-12 border-y bg-gray-50/50">
          <div className="max-w-4xl mx-auto px-4 grid grid-cols-2 sm:grid-cols-4 gap-8 text-center">
            <div>
              <div className="text-2xl sm:text-3xl font-bold text-orange-600">2,000+</div>
              <div className="text-sm text-gray-600">Restaurants</div>
            </div>
            <div>
              <div className="text-2xl sm:text-3xl font-bold text-orange-600">50k+</div>
              <div className="text-sm text-gray-600">Orders / day</div>
            </div>
            <div>
              <div className="text-2xl sm:text-3xl font-bold text-orange-600">98%</div>
              <div className="text-sm text-gray-600">Uptime</div>
            </div>
            <div>
              <div className="text-2xl sm:text-3xl font-bold text-orange-600">24/7</div>
              <div className="text-sm text-gray-600">Support</div>
            </div>
          </div>
        </section>

        {/* Features */}
        <section className="py-16 sm:py-24 px-4">
          <div className="max-w-6xl mx-auto">
            <h2 className="text-3xl font-bold text-center text-gray-900">Everything you need</h2>
            <p className="mt-2 text-center text-gray-600 max-w-xl mx-auto">
              One platform for onboarding, menu, orders, promotions, and insights.
            </p>
            <div className="mt-12 grid sm:grid-cols-2 lg:grid-cols-3 gap-6">
              {FEATURES.map((f) => (
                <div
                  key={f.title}
                  className="p-6 rounded-xl border bg-white hover:shadow-md transition-shadow"
                >
                  <div className="w-10 h-10 rounded-lg bg-orange-100 flex items-center justify-center text-orange-600">
                    <f.icon className="w-5 h-5" />
                  </div>
                  <h3 className="mt-4 font-semibold text-gray-900">{f.title}</h3>
                  <p className="mt-2 text-sm text-gray-600">{f.description}</p>
                </div>
              ))}
            </div>
          </div>
        </section>

        {/* How it works */}
        <section className="py-16 sm:py-24 px-4 bg-gray-50/50">
          <div className="max-w-3xl mx-auto">
            <h2 className="text-3xl font-bold text-center text-gray-900">How it works</h2>
            <ul className="mt-10 space-y-6">
              {STEPS.map((step, i) => (
                <li key={step} className="flex gap-4">
                  <span className="flex-shrink-0 w-8 h-8 rounded-full bg-orange-600 text-white flex items-center justify-center text-sm font-medium">
                    {i + 1}
                  </span>
                  <span className="flex items-center text-gray-700">{step}</span>
                </li>
              ))}
            </ul>
          </div>
        </section>

        {/* Testimonials */}
        <section className="py-16 sm:py-24 px-4">
          <div className="max-w-6xl mx-auto">
            <h2 className="text-3xl font-bold text-center text-gray-900">Loved by restaurants</h2>
            <div className="mt-12 grid sm:grid-cols-3 gap-6">
              {TESTIMONIALS.map((t) => (
                <div key={t.name} className="p-6 rounded-xl border bg-white">
                  <Quote className="w-8 h-8 text-orange-200" />
                  <p className="mt-4 text-gray-700">&ldquo;{t.quote}&rdquo;</p>
                  <p className="mt-4 font-medium text-gray-900">{t.name}</p>
                  <p className="text-sm text-gray-500">{t.role}</p>
                </div>
              ))}
            </div>
          </div>
        </section>

        {/* CTA */}
        <section className="py-16 sm:py-24 px-4 bg-orange-600">
          <div className="max-w-2xl mx-auto text-center text-white">
            <h2 className="text-3xl font-bold">Ready to grow your restaurant?</h2>
            <p className="mt-4 opacity-90">Join thousands of restaurants already on the platform.</p>
            <Link to="/register" className="inline-block mt-8">
              <Button size="lg" variant="secondary" className="bg-white text-orange-600 hover:bg-gray-100">
                Get Started
              </Button>
            </Link>
          </div>
        </section>

        {/* Footer */}
        <footer className="py-12 px-4 border-t bg-gray-50">
          <div className="max-w-6xl mx-auto flex flex-col sm:flex-row justify-between items-center gap-6">
            <div className="text-gray-600 text-sm">
              © {new Date().getFullYear()} Restaurant App. All rights reserved.
            </div>
            <div className="flex gap-6 text-sm">
              <Link to="/restaurant-onboarding" className="text-gray-600 hover:text-orange-600">
                Onboarding
              </Link>
              <Link to="/login" className="text-gray-600 hover:text-orange-600">
                Login
              </Link>
              <a href="/privacy" className="text-gray-600 hover:text-orange-600">
                Privacy
              </a>
              <a href="/terms" className="text-gray-600 hover:text-orange-600">
                Terms
              </a>
            </div>
          </div>
          <div className="max-w-6xl mx-auto mt-6 text-center text-sm text-gray-500">
            Available in Bangalore, Mumbai, Delhi, Hyderabad, Chennai & more.
          </div>
        </footer>
      </main>
    </div>
  );
}
