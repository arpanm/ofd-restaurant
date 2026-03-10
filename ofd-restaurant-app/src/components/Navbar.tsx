import { useState, useEffect, useRef } from "react";
import { Link, useLocation } from "react-router-dom";
import { Menu, X } from "lucide-react";

export const Navbar = () => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const location = useLocation();
  const menuRef = useRef<HTMLDivElement>(null);

  const toggleMenu = () => {
    setIsMenuOpen(!isMenuOpen);
  };

  const closeMenu = () => {
    setIsMenuOpen(false);
  };

  // Close menu when clicking outside
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
        closeMenu();
      }
    };

    if (isMenuOpen) {
      document.addEventListener("mousedown", handleClickOutside);
    }

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [isMenuOpen]);

  const isActive = (path: string) => {
    return location.pathname === path;
  };

  const appLinks = [
    { label: "Restaurant Dashboard", path: "/restaurant" },
    { label: "Restaurant Onboarding", path: "/restaurant-onboarding" },
  ];

  return (
    <nav className="fixed top-0 left-0 right-0 bg-white shadow-md z-50 h-[70px]">
      <div className="max-w-[1400px] mx-auto px-4 sm:px-6 lg:px-8 h-full">
        <div className="flex items-center justify-between h-full">
          {/* Brand */}
          <Link
            to="/"
            className="text-xl sm:text-2xl font-bold text-primary whitespace-nowrap"
            onClick={closeMenu}
          >
            🍽️ Restaurant
          </Link>

          {/* Hamburger Button */}
          <button
            onClick={toggleMenu}
            className="lg:hidden p-2 text-gray-600 hover:text-primary transition-colors"
            aria-label="Toggle menu"
          >
            {isMenuOpen ? <X className="h-6 w-6" /> : <Menu className="h-6 w-6" />}
          </button>

          {/* Desktop & Mobile Menu */}
          <div
            ref={menuRef}
            className={`
              lg:flex lg:items-center lg:gap-2
              ${isMenuOpen ? "flex" : "hidden"}
              fixed lg:relative top-[70px] lg:top-0 left-0 right-0 lg:left-auto lg:right-auto
              flex-col lg:flex-row
              bg-white lg:bg-transparent
              shadow-md lg:shadow-none
              p-4 lg:p-0
              max-h-[calc(100vh-70px)] lg:max-h-none
              overflow-y-auto lg:overflow-visible
              transition-all duration-300
            `}
          >
            {/* Home / Dashboard */}
            <Link
              to="/"
              className={`
                px-3 sm:px-4 py-2 rounded-lg font-medium transition-all whitespace-nowrap text-sm sm:text-base
                ${
                  isActive("/") || isActive("/restaurant")
                    ? "bg-primary text-white"
                    : "text-gray-600 hover:bg-gray-100 hover:text-primary"
                }
              `}
              onClick={closeMenu}
            >
              Dashboard
            </Link>

            {appLinks.map((link) => (
              <Link
                key={link.path}
                to={link.path}
                className={`
                  px-3 sm:px-4 py-2 rounded-lg font-medium transition-all whitespace-nowrap text-sm sm:text-base
                  ${
                    isActive(link.path)
                      ? "bg-primary text-white"
                      : "text-gray-600 hover:bg-gray-100 hover:text-primary"
                  }
                `}
                onClick={closeMenu}
              >
                {link.label}
              </Link>
            ))}
          </div>
        </div>
      </div>
    </nav>
  );
};
