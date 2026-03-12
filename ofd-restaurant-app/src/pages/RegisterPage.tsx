import { useState, useEffect, useCallback } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import {
  Collapsible,
  CollapsibleContent,
  CollapsibleTrigger,
} from "@/components/ui/collapsible";
import { useToast } from "@/hooks/use-toast";
import { tokenManager } from "@/services/api.config";
import { userService } from "@/services/user.service";
import type { User } from "@/types/api.types";
import { ChevronDown, ChevronUp, Loader2, UserPlus } from "lucide-react";

const schema = z.object({
  firstName: z.string().min(2, "At least 2 characters").max(50),
  lastName: z.string().max(50).optional(),
  email: z.string().email("Invalid email"),
  phone: z.string().regex(/^\+?[1-9]\d{9,14}$/, "Invalid phone (e.g. +919876543210)"),
});

type FormData = z.infer<typeof schema>;

export default function RegisterPage() {
  const navigate = useNavigate();
  const { toast } = useToast();
  const [referralOpen, setReferralOpen] = useState(false);
  const [referralPhone, setReferralPhone] = useState("");
  const [referralUser, setReferralUser] = useState<User | null>(null);
  const [referralSearching, setReferralSearching] = useState(false);
  const [referredBy, setReferredBy] = useState<string | undefined>(undefined);

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<FormData>({
    resolver: zodResolver(schema),
    defaultValues: { firstName: "", lastName: "", email: "", phone: "" },
  });

  const searchReferral = useCallback(async (phone: string) => {
    const trimmed = phone.trim().replace(/\D/g, "");
    if (trimmed.length < 10) {
      setReferralUser(null);
      return;
    }
    setReferralSearching(true);
    try {
      const res = await userService.getUserByPhone(phone.trim());
      const data = "data" in res && res.data ? res.data : null;
      setReferralUser(data as User | null);
    } catch {
      setReferralUser(null);
    } finally {
      setReferralSearching(false);
    }
  }, []);

  useEffect(() => {
    if (!referralPhone.trim()) {
      setReferralUser(null);
      return;
    }
    const t = setTimeout(() => searchReferral(referralPhone), 400);
    return () => clearTimeout(t);
  }, [referralPhone, searchReferral]);

  const onSelectReferral = (user: User) => {
    const code = (user as { referralCode?: string }).referralCode ?? user.id;
    setReferredBy(code);
    setReferralUser(null);
    setReferralPhone("");
    toast({ title: "Referrer set", description: `${user.firstName} ${user.lastName || ""}`.trim() });
  };

  const onSubmit = async (data: FormData) => {
    try {
      const res = await userService.registerAuth({
        firstName: data.firstName,
        lastName: data.lastName ?? "",
        email: data.email,
        phone: data.phone,
        referredBy,
      });
      const auth = res?.data;
      if (auth?.accessToken) {
        tokenManager.setAccessToken(auth.accessToken);
        if (auth.refreshToken) tokenManager.setRefreshToken(auth.refreshToken);
        toast({ title: "Account created", description: "Redirecting to onboarding." });
        navigate("/restaurant-onboarding", { replace: true });
      }
    } catch (e: unknown) {
      const msg =
        e && typeof e === "object" && "message" in e
          ? String((e as { message: unknown }).message)
          : "Registration failed.";
      toast({ title: "Error", description: msg, variant: "destructive" });
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 px-4 py-12">
      <Card className="w-full max-w-md">
        <CardHeader className="space-y-1 text-center">
          <CardTitle className="text-2xl">Register</CardTitle>
          <CardDescription>Create your account and continue to onboarding</CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="firstName">First name</Label>
                <Input
                  id="firstName"
                  {...register("firstName")}
                  placeholder="John"
                  autoComplete="given-name"
                />
                {errors.firstName && (
                  <p className="text-sm text-destructive">{errors.firstName.message}</p>
                )}
              </div>
              <div className="space-y-2">
                <Label htmlFor="lastName">Last name</Label>
                <Input
                  id="lastName"
                  {...register("lastName")}
                  placeholder="Doe"
                  autoComplete="family-name"
                />
                {errors.lastName && (
                  <p className="text-sm text-destructive">{errors.lastName.message}</p>
                )}
              </div>
            </div>
            <div className="space-y-2">
              <Label htmlFor="email">Email</Label>
              <Input
                id="email"
                type="email"
                {...register("email")}
                placeholder="you@example.com"
                autoComplete="email"
              />
              {errors.email && (
                <p className="text-sm text-destructive">{errors.email.message}</p>
              )}
            </div>
            <div className="space-y-2">
              <Label htmlFor="phone">Phone</Label>
              <Input
                id="phone"
                type="tel"
                {...register("phone")}
                placeholder="+919876543210"
                autoComplete="tel"
              />
              {errors.phone && (
                <p className="text-sm text-destructive">{errors.phone.message}</p>
              )}
            </div>

            <Collapsible open={referralOpen} onOpenChange={setReferralOpen}>
              <CollapsibleTrigger asChild>
                <Button type="button" variant="outline" className="w-full justify-between">
                  <span className="flex items-center gap-2">
                    <UserPlus className="w-4 h-4" />
                    Who referred you? (optional)
                  </span>
                  {referralOpen ? (
                    <ChevronUp className="w-4 h-4" />
                  ) : (
                    <ChevronDown className="w-4 h-4" />
                  )}
                </Button>
              </CollapsibleTrigger>
              <CollapsibleContent className="pt-4 space-y-2">
                <Label>Search by phone number</Label>
                <Input
                  placeholder="+919876543210"
                  value={referralPhone}
                  onChange={(e) => setReferralPhone(e.target.value)}
                />
                {referralSearching && (
                  <p className="text-sm text-muted-foreground flex items-center gap-2">
                    <Loader2 className="w-4 h-4 animate-spin" /> Searching…
                  </p>
                )}
                {!referralSearching && referralPhone.trim() && referralUser && (
                  <div className="p-3 rounded-lg border bg-muted/50 flex items-center justify-between">
                    <div>
                      <p className="font-medium">
                        {referralUser.firstName} {referralUser.lastName || ""}
                      </p>
                      <p className="text-sm text-muted-foreground">{referralUser.phone || referralUser.email}</p>
                    </div>
                    <Button
                      type="button"
                      size="sm"
                      onClick={() => onSelectReferral(referralUser)}
                    >
                      Select
                    </Button>
                  </div>
                )}
                {!referralSearching && referralPhone.trim() && !referralUser && referralPhone.replace(/\D/g, "").length >= 10 && (
                  <p className="text-sm text-muted-foreground">No user found with this phone.</p>
                )}
                {referredBy && (
                  <p className="text-sm text-green-600">Referrer selected.</p>
                )}
              </CollapsibleContent>
            </Collapsible>

            <Button type="submit" className="w-full" disabled={isSubmitting}>
              {isSubmitting ? <Loader2 className="w-4 h-4 animate-spin mr-2" /> : null}
              Register & continue to onboarding
            </Button>
          </form>

          <p className="mt-6 text-center text-sm text-muted-foreground">
            Already have an account?{" "}
            <Link to="/login" className="text-primary font-medium hover:underline">
              Login
            </Link>
          </p>
          <p className="text-center text-sm mt-2">
            <Link to="/" className="text-muted-foreground hover:text-primary">
              Back to home
            </Link>
          </p>
        </CardContent>
      </Card>
    </div>
  );
}
