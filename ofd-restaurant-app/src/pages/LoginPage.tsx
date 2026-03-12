import { useState, useEffect } from "react";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { InputOTP, InputOTPGroup, InputOTPSlot } from "@/components/ui/input-otp";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { useToast } from "@/hooks/use-toast";
import { tokenManager } from "@/services/api.config";
import { userService } from "@/services/user.service";
import { Mail, Phone, Loader2 } from "lucide-react";

type OtpType = "EMAIL" | "PHONE";

export default function LoginPage() {
  const [searchParams] = useSearchParams();
  const next = searchParams.get("next") || "/dashboard";
  const navigate = useNavigate();
  const { toast } = useToast();

  const [otpType, setOtpType] = useState<OtpType>("PHONE");
  const [contact, setContact] = useState("");
  const [otp, setOtp] = useState("");
  const [step, setStep] = useState<"contact" | "verify">("contact");
  const [loading, setLoading] = useState(false);
  const [resendCooldown, setResendCooldown] = useState(0);

  useEffect(() => {
    if (tokenManager.getAccessToken()) {
      navigate(next, { replace: true });
    }
  }, [navigate, next]);

  useEffect(() => {
    if (resendCooldown <= 0) return;
    const t = setInterval(() => setResendCooldown((c) => c - 1), 1000);
    return () => clearInterval(t);
  }, [resendCooldown]);

  const handleSendOtp = async () => {
    const trimmed = contact.trim();
    if (!trimmed) {
      toast({ title: "Required", description: "Enter email or phone number.", variant: "destructive" });
      return;
    }
    if (otpType === "EMAIL" && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(trimmed)) {
      toast({ title: "Invalid email", description: "Enter a valid email address.", variant: "destructive" });
      return;
    }
    setLoading(true);
    try {
      await userService.sendOtp(trimmed, otpType);
      setStep("verify");
      setOtp("");
      setResendCooldown(60);
      toast({ title: "OTP sent", description: "Check your " + (otpType === "EMAIL" ? "email" : "phone") + " for the code." });
    } catch (e: unknown) {
      const msg = e && typeof e === "object" && "message" in e ? String((e as { message: unknown }).message) : "Failed to send OTP.";
      toast({ title: "Error", description: msg, variant: "destructive" });
    } finally {
      setLoading(false);
    }
  };

  const handleVerifyOtp = async () => {
    if (otp.length !== 6) {
      toast({ title: "Enter OTP", description: "Enter the 6-digit code.", variant: "destructive" });
      return;
    }
    setLoading(true);
    try {
      const res = await userService.verifyOtp(contact.trim(), otp);
      const data = res?.data;
      if (data?.accessToken) {
        tokenManager.setAccessToken(data.accessToken);
        if (data.refreshToken) tokenManager.setRefreshToken(data.refreshToken);
        toast({ title: "Welcome back", description: "You are logged in." });
        navigate(next, { replace: true });
      }
    } catch (e: unknown) {
      const msg = e && typeof e === "object" && "message" in e ? String((e as { message: unknown }).message) : "Invalid or expired OTP.";
      toast({ title: "Verification failed", description: msg, variant: "destructive" });
      setOtp("");
    } finally {
      setLoading(false);
    }
  };

  const handleOtpComplete = (value: string) => {
    setOtp(value);
    if (value.length === 6) {
      (async () => {
        setLoading(true);
        try {
          const res = await userService.verifyOtp(contact.trim(), value);
          const data = res?.data;
          if (data?.accessToken) {
            tokenManager.setAccessToken(data.accessToken);
            if (data.refreshToken) tokenManager.setRefreshToken(data.refreshToken);
            toast({ title: "Welcome back", description: "You are logged in." });
            navigate(next, { replace: true });
          }
        } catch (e: unknown) {
          const msg = e && typeof e === "object" && "message" in e ? String((e as { message: unknown }).message) : "Invalid or expired OTP.";
          toast({ title: "Verification failed", description: msg, variant: "destructive" });
          setOtp("");
        } finally {
          setLoading(false);
        }
      })();
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 px-4 py-12">
      <Card className="w-full max-w-md">
        <CardHeader className="space-y-1 text-center">
          <CardTitle className="text-2xl">Login</CardTitle>
          <CardDescription>Sign in with OTP sent to your email or phone</CardDescription>
        </CardHeader>
        <CardContent className="space-y-6">
          {step === "contact" ? (
            <>
              <div className="flex gap-2 p-1 bg-muted rounded-lg">
                <Button
                  type="button"
                  variant={otpType === "PHONE" ? "default" : "ghost"}
                  size="sm"
                  className="flex-1"
                  onClick={() => setOtpType("PHONE")}
                >
                  <Phone className="w-4 h-4 mr-2" />
                  Phone
                </Button>
                <Button
                  type="button"
                  variant={otpType === "EMAIL" ? "default" : "ghost"}
                  size="sm"
                  className="flex-1"
                  onClick={() => setOtpType("EMAIL")}
                >
                  <Mail className="w-4 h-4 mr-2" />
                  Email
                </Button>
              </div>
              <div className="space-y-2">
                <Label htmlFor="contact">
                  {otpType === "PHONE" ? "Phone number" : "Email address"}
                </Label>
                <Input
                  id="contact"
                  type={otpType === "EMAIL" ? "email" : "tel"}
                  placeholder={otpType === "EMAIL" ? "you@example.com" : "+919876543210"}
                  value={contact}
                  onChange={(e) => setContact(e.target.value)}
                  disabled={loading}
                />
              </div>
              <Button className="w-full" onClick={handleSendOtp} disabled={loading}>
                {loading ? <Loader2 className="w-4 h-4 animate-spin mr-2" /> : null}
                Send OTP
              </Button>
            </>
          ) : (
            <>
              <div className="text-sm text-muted-foreground text-center">
                We sent a 6-digit code to <strong>{contact}</strong>
              </div>
              <div className="flex justify-center">
                <InputOTP maxLength={6} value={otp} onChange={setOtp} onComplete={handleOtpComplete}>
                  <InputOTPGroup className="gap-1">
                    {[0, 1, 2, 3, 4, 5].map((i) => (
                      <InputOTPSlot key={i} index={i} />
                    ))}
                  </InputOTPGroup>
                </InputOTP>
              </div>
              <Button className="w-full" onClick={handleVerifyOtp} disabled={loading || otp.length !== 6}>
                {loading ? <Loader2 className="w-4 h-4 animate-spin mr-2" /> : null}
                Verify & Login
              </Button>
              <div className="flex items-center justify-between text-sm">
                <Button
                  type="button"
                  variant="ghost"
                  size="sm"
                  onClick={() => { setStep("contact"); setOtp(""); }}
                >
                  Change number / email
                </Button>
                <Button
                  type="button"
                  variant="ghost"
                  size="sm"
                  onClick={handleSendOtp}
                  disabled={resendCooldown > 0 || loading}
                >
                  {resendCooldown > 0 ? `Resend in ${resendCooldown}s` : "Resend OTP"}
                </Button>
              </div>
            </>
          )}
          <p className="text-center text-sm text-muted-foreground">
            Don&apos;t have an account?{" "}
            <Link to="/register" className="text-primary font-medium hover:underline">
              Register
            </Link>
          </p>
          <p className="text-center text-sm">
            <Link to="/" className="text-muted-foreground hover:text-primary">
              Back to home
            </Link>
          </p>
        </CardContent>
      </Card>
    </div>
  );
}
