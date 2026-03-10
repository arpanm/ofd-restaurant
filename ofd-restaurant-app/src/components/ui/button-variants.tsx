import { cva } from "class-variance-authority";

export const buttonVariants = cva(
  "inline-flex items-center justify-center gap-2 whitespace-nowrap rounded-xl text-sm font-semibold transition-all duration-300 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50",
  {
    variants: {
      variant: {
        default: "bg-primary text-primary-foreground shadow-md hover:bg-primary-dark hover:shadow-lg",
        destructive: "bg-destructive text-destructive-foreground shadow-md hover:opacity-90",
        outline: "border-2 border-border bg-transparent hover:bg-muted",
        secondary: "bg-secondary text-secondary-foreground shadow-md hover:bg-secondary/90 hover:shadow-lg",
        ghost: "hover:bg-muted hover:text-foreground",
        link: "text-primary underline-offset-4 hover:underline",
        hero: "bg-gradient-primary text-primary-foreground shadow-xl hover:shadow-2xl hover:scale-105 text-base font-bold",
        gradient: "bg-gradient-secondary text-secondary-foreground shadow-xl hover:shadow-2xl hover:scale-105",
      },
      size: {
        default: "h-11 px-6 py-2",
        sm: "h-9 px-4 text-xs",
        lg: "h-14 px-8 text-base",
        xl: "h-16 px-10 text-lg",
        icon: "h-10 w-10",
      },
    },
    defaultVariants: {
      variant: "default",
      size: "default",
    },
  }
);
