import { useState, useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { restaurantService } from "@/services/restaurant.service";
import type { OnboardingDraftResponse } from "@/types/api.types";

const ONBOARDING_DRAFT_ID_KEY = "onboarding_draft_id";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Badge } from "@/components/ui/badge";
import { Progress } from "@/components/ui/progress";
import { Checkbox } from "@/components/ui/checkbox";
import { useToast } from "@/hooks/use-toast";
import { 
  ArrowRight, 
  ArrowLeft, 
  CheckCircle2, 
  Upload, 
  FileText, 
  Store,
  MapPin,
  Phone,
  Mail,
  DollarSign,
  FileCheck,
  ChefHat,
  Package,
  Megaphone,
  Building2,
  CreditCard,
  Shield,
  Clock,
  Users
} from "lucide-react";

interface RestaurantDetails {
  name: string;
  description: string;
  cuisineTypes: string[];
  address: string;
  city: string;
  state: string;
  pincode: string;
  phone: string;
  email: string;
  ownerName: string;
  ownerPhone: string;
  ownerEmail: string;
  fssaiNumber: string;
  gstNumber: string;
  panNumber: string;
  bankName: string;
  accountNumber: string;
  ifscCode: string;
  accountHolderName: string;
}

interface UploadedDocument {
  name: string;
  size: number;
  type: string;
  uploadedAt: Date;
  url?: string;
}

const RestaurantOnboarding = () => {
  const navigate = useNavigate();
  const { toast } = useToast();

  const [draftId, setDraftId] = useState<string | null>(null);
  const [draftLoading, setDraftLoading] = useState(true);
  const [step, setStep] = useState(1);
  const [assistanceRequested, setAssistanceRequested] = useState(false);
  const [contractSigned, setContractSigned] = useState(false);
  const [signatureText, setSignatureText] = useState("");

  // Document uploads
  const [fssaiDocument, setFssaiDocument] = useState<UploadedDocument | null>(null);
  const [gstDocument, setGstDocument] = useState<UploadedDocument | null>(null);
  const [panDocument, setPanDocument] = useState<UploadedDocument | null>(null);
  const [cancelledCheque, setCancelledCheque] = useState<UploadedDocument | null>(null);
  const [menuExcelFile, setMenuExcelFile] = useState<UploadedDocument | null>(null);

  const [details, setDetails] = useState<RestaurantDetails>({
    name: "",
    description: "",
    cuisineTypes: [],
    address: "",
    city: "",
    state: "",
    pincode: "",
    phone: "",
    email: "",
    ownerName: "",
    ownerPhone: "",
    ownerEmail: "",
    fssaiNumber: "",
    gstNumber: "",
    panNumber: "",
    bankName: "",
    accountNumber: "",
    ifscCode: "",
    accountHolderName: ""
  });

  const totalSteps = 6;
  const progressPercentage = (step / totalSteps) * 100;

  const prefillFromDraft = useCallback((draft: OnboardingDraftResponse) => {
    setStep(Math.min(6, Math.max(1, draft.currentStep || 1)));
    setDetails({
      name: draft.name ?? "",
      description: draft.description ?? "",
      cuisineTypes: draft.cuisineTypes ?? [],
      address: draft.address ?? "",
      city: draft.city ?? "",
      state: draft.state ?? "",
      pincode: draft.pincode ?? "",
      phone: draft.phone ?? "",
      email: draft.email ?? "",
      ownerName: draft.ownerName ?? "",
      ownerPhone: draft.ownerPhone ?? "",
      ownerEmail: draft.ownerEmail ?? "",
      fssaiNumber: draft.fssaiNumber ?? "",
      gstNumber: draft.gstNumber ?? "",
      panNumber: draft.panNumber ?? "",
      bankName: draft.bankName ?? "",
      accountNumber: draft.accountNumber ?? "",
      ifscCode: draft.ifscCode ?? "",
      accountHolderName: draft.accountHolderName ?? "",
    });
    setContractSigned(!!draft.contractSigned);
    setSignatureText(draft.signatureText ?? "");
    setAssistanceRequested(!!draft.assistanceRequested);
    if (draft.fssaiDocumentUrl) {
      setFssaiDocument({ name: "Saved document", size: 0, type: "", uploadedAt: new Date(), url: draft.fssaiDocumentUrl });
    }
    if (draft.gstDocumentUrl) {
      setGstDocument({ name: "Saved document", size: 0, type: "", uploadedAt: new Date(), url: draft.gstDocumentUrl });
    }
    if (draft.panDocumentUrl) {
      setPanDocument({ name: "Saved document", size: 0, type: "", uploadedAt: new Date(), url: draft.panDocumentUrl });
    }
    if (draft.cancelledChequeDocumentUrl) {
      setCancelledCheque({ name: "Saved document", size: 0, type: "", uploadedAt: new Date(), url: draft.cancelledChequeDocumentUrl });
    }
    if (draft.menuFileUrl) {
      setMenuExcelFile({ name: "Saved menu", size: 0, type: "", uploadedAt: new Date(), url: draft.menuFileUrl });
    }
  }, []);

  useEffect(() => {
    let cancelled = false;
    (async () => {
      const stored = localStorage.getItem(ONBOARDING_DRAFT_ID_KEY);
      if (stored) {
        try {
          const res = await restaurantService.getOnboardingDraft(stored);
          const data = res?.data;
          if (cancelled) return;
          if (data && data.status !== "SUBMITTED") {
            setDraftId(stored);
            prefillFromDraft(data);
          } else {
            localStorage.removeItem(ONBOARDING_DRAFT_ID_KEY);
            const createRes = await restaurantService.createOnboardingDraft();
            const id = createRes?.data?.draftId;
            if (cancelled) return;
            if (id) {
              setDraftId(id);
              localStorage.setItem(ONBOARDING_DRAFT_ID_KEY, id);
            }
          }
        } catch {
          if (cancelled) return;
          localStorage.removeItem(ONBOARDING_DRAFT_ID_KEY);
          try {
            const createRes = await restaurantService.createOnboardingDraft();
            const id = createRes?.data?.draftId;
            if (cancelled) return;
            if (id) {
              setDraftId(id);
              localStorage.setItem(ONBOARDING_DRAFT_ID_KEY, id);
            }
          } catch (e) {
            toast({ title: "Could not load or create draft", description: "Please refresh and try again.", variant: "destructive" });
          }
        }
      } else {
        try {
          const createRes = await restaurantService.createOnboardingDraft();
          const id = createRes?.data?.draftId;
          if (cancelled) return;
          if (id) {
            setDraftId(id);
            localStorage.setItem(ONBOARDING_DRAFT_ID_KEY, id);
          }
        } catch (e) {
          toast({ title: "Could not create draft", description: "Please refresh and try again.", variant: "destructive" });
        }
      }
      if (!cancelled) setDraftLoading(false);
    })();
    return () => { cancelled = true; };
  }, [prefillFromDraft, toast]);

  const saveDraft = useCallback(async (nextStep: number) => {
    if (!draftId) return;
    const payload = {
      step: nextStep,
      name: details.name || undefined,
      description: details.description || undefined,
      cuisineTypes: details.cuisineTypes?.length ? details.cuisineTypes : undefined,
      address: details.address || undefined,
      city: details.city || undefined,
      state: details.state || undefined,
      pincode: details.pincode || undefined,
      phone: details.phone || undefined,
      email: details.email || undefined,
      ownerName: details.ownerName || undefined,
      ownerPhone: details.ownerPhone || undefined,
      ownerEmail: details.ownerEmail || undefined,
      fssaiNumber: details.fssaiNumber || undefined,
      gstNumber: details.gstNumber || undefined,
      panNumber: details.panNumber || undefined,
      fssaiDocumentUrl: fssaiDocument ? (fssaiDocument.url ?? `pending://${fssaiDocument.name}`) : undefined,
      gstDocumentUrl: gstDocument ? (gstDocument.url ?? `pending://${gstDocument.name}`) : undefined,
      panDocumentUrl: panDocument ? (panDocument.url ?? `pending://${panDocument.name}`) : undefined,
      cancelledChequeDocumentUrl: cancelledCheque ? (cancelledCheque.url ?? `pending://${cancelledCheque.name}`) : undefined,
      bankName: details.bankName || undefined,
      accountNumber: details.accountNumber || undefined,
      ifscCode: details.ifscCode || undefined,
      accountHolderName: details.accountHolderName || undefined,
      contractSigned: contractSigned || undefined,
      contractSignedBy: signatureText || undefined,
      contractSignedAt: contractSigned ? new Date().toISOString() : undefined,
      signatureText: signatureText || undefined,
      menuFileUrl: menuExcelFile?.url || undefined,
      assistanceRequested: assistanceRequested || undefined,
    };
    try {
      await restaurantService.saveOnboardingDraft(draftId, payload);
    } catch (e) {
      toast({ title: "Progress could not be saved", description: "You can continue; try again later.", variant: "destructive" });
    }
  }, [draftId, details, fssaiDocument, gstDocument, panDocument, cancelledCheque, menuExcelFile, contractSigned, signatureText, assistanceRequested, toast]);

  const cuisineOptions = [
    "North Indian", "South Indian", "Chinese", "Italian", "Continental",
    "Mexican", "Thai", "Japanese", "Fast Food", "Desserts", "Beverages",
    "Bakery", "Street Food", "Healthy", "Vegan", "Organic"
  ];

  const handleInputChange = (field: keyof RestaurantDetails, value: string) => {
    setDetails(prev => ({ ...prev, [field]: value }));
  };

  const handleCuisineToggle = (cuisine: string) => {
    setDetails(prev => ({
      ...prev,
      cuisineTypes: prev.cuisineTypes.includes(cuisine)
        ? prev.cuisineTypes.filter(c => c !== cuisine)
        : [...prev.cuisineTypes, cuisine]
    }));
  };

  const handleFileUpload = (
    event: React.ChangeEvent<HTMLInputElement>,
    documentType: "fssai" | "gst" | "pan" | "cheque" | "menu"
  ) => {
    const file = event.target.files?.[0];
    if (!file) return;

    // Validate file size (max 5MB)
    if (file.size > 5 * 1024 * 1024) {
      toast({
        title: "File Too Large",
        description: "Please upload a file smaller than 5MB",
        variant: "destructive"
      });
      return;
    }

    // Validate file type
    const allowedTypes = documentType === "menu" 
      ? ["application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"]
      : ["image/jpeg", "image/jpg", "image/png", "application/pdf"];
    
    if (!allowedTypes.includes(file.type)) {
      toast({
        title: "Invalid File Type",
        description: documentType === "menu" 
          ? "Please upload an Excel file (.xlsx or .xls)"
          : "Please upload a PDF, JPG, or PNG file",
        variant: "destructive"
      });
      return;
    }

    const uploadedDoc: UploadedDocument = {
      name: file.name,
      size: file.size,
      type: file.type,
      uploadedAt: new Date()
    };

    // Set the appropriate state
    switch (documentType) {
      case "fssai":
        setFssaiDocument(uploadedDoc);
        break;
      case "gst":
        setGstDocument(uploadedDoc);
        break;
      case "pan":
        setPanDocument(uploadedDoc);
        break;
      case "cheque":
        setCancelledCheque(uploadedDoc);
        break;
      case "menu":
        setMenuExcelFile(uploadedDoc);
        break;
    }

    toast({
      title: "File Uploaded Successfully",
      description: `${file.name} has been uploaded`
    });
  };

  const removeDocument = (documentType: "fssai" | "gst" | "pan" | "cheque" | "menu") => {
    switch (documentType) {
      case "fssai":
        setFssaiDocument(null);
        break;
      case "gst":
        setGstDocument(null);
        break;
      case "pan":
        setPanDocument(null);
        break;
      case "cheque":
        setCancelledCheque(null);
        break;
      case "menu":
        setMenuExcelFile(null);
        break;
    }
    
    toast({
      title: "Document Removed",
      description: "The document has been removed"
    });
  };

  const formatFileSize = (bytes: number): string => {
    if (bytes === 0) return "0 Bytes";
    const k = 1024;
    const sizes = ["Bytes", "KB", "MB"];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + " " + sizes[i];
  };

  const requestAssistance = () => {
    setAssistanceRequested(true);
    toast({
      title: "Assistance Requested!",
      description: "Our business operations team will contact you within 24 hours to help complete your onboarding.",
    });
  };

  const handleSignContract = () => {
    if (!signatureText.trim()) {
      toast({
        title: "Signature Required",
        description: "Please enter your full name as signature",
        variant: "destructive"
      });
      return;
    }
    setContractSigned(true);
    toast({
      title: "Contract Signed!",
      description: "Your contract has been digitally signed and saved.",
    });
  };

  const completeOnboarding = async () => {
    if (!draftId) {
      toast({ title: "Session expired", description: "Please refresh and complete the form again.", variant: "destructive" });
      return;
    }
    try {
      await saveDraft(6);
      const res = await restaurantService.submitOnboardingByDraft({
        draftId,
        createdBy: "onboarding-user",
      });
      localStorage.removeItem(ONBOARDING_DRAFT_ID_KEY);
      if (res?.data?.id) {
        toast({
          title: "Onboarding complete!",
          description: "Check your email and phone for verification to log in. Redirecting to dashboard...",
        });
        setTimeout(() => navigate("/restaurant?tab=dashboard"), 2000);
      } else {
        toast({
          title: "Onboarding submitted",
          description: "Check your email and phone for verification. Redirecting...",
          variant: "default",
        });
        setTimeout(() => navigate("/restaurant?tab=dashboard"), 2000);
      }
    } catch (err: unknown) {
      const message = err && typeof err === "object" && "message" in err ? String((err as { message: string }).message) : "Onboarding submission failed";
      toast({
        title: "Submission failed",
        description: message,
        variant: "destructive",
      });
    }
  };

  const validateStep = (currentStep: number): boolean => {
    switch (currentStep) {
      case 1:
        if (!details.name || !details.description || details.cuisineTypes.length === 0) {
          toast({
            title: "Incomplete Information",
            description: "Please fill in all required fields",
            variant: "destructive"
          });
          return false;
        }
        return true;
      case 2:
        if (!details.address || !details.city || !details.pincode || !details.phone || !details.email) {
          toast({
            title: "Incomplete Information",
            description: "Please fill in all required fields",
            variant: "destructive"
          });
          return false;
        }
        return true;
      case 3:
        if (!details.ownerName || !details.ownerPhone || !details.fssaiNumber) {
          toast({
            title: "Incomplete Information",
            description: "Please fill in all required fields",
            variant: "destructive"
          });
          return false;
        }
        if (!fssaiDocument) {
          toast({
            title: "FSSAI Certificate Required",
            description: "Please upload your FSSAI license certificate",
            variant: "destructive"
          });
          return false;
        }
        return true;
      case 4:
        if (!details.bankName || !details.accountNumber || !details.ifscCode) {
          toast({
            title: "Incomplete Information",
            description: "Please fill in all required fields",
            variant: "destructive"
          });
          return false;
        }
        if (!cancelledCheque) {
          toast({
            title: "Bank Proof Required",
            description: "Please upload a cancelled cheque or bank statement",
            variant: "destructive"
          });
          return false;
        }
        return true;
      case 5:
        if (!contractSigned) {
          toast({
            title: "Contract Not Signed",
            description: "Please review and sign the contract to proceed",
            variant: "destructive"
          });
          return false;
        }
        return true;
      default:
        return true;
    }
  };

  const nextStep = async () => {
    if (!validateStep(step)) return;
    const next = Math.min(step + 1, totalSteps);
    await saveDraft(next);
    setStep(next);
  };

  const prevStep = () => {
    setStep(prev => Math.max(prev - 1, 1));
  };

  if (draftLoading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-orange-50 via-white to-red-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-orange-600 mx-auto mb-4" />
          <p className="text-gray-600">Loading your progress...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-orange-50 via-white to-red-50">
      <div className="container mx-auto px-4 py-8">
        {/* Header */}
        <div className="mb-8 text-center">
          <div className="flex items-center justify-center gap-2 mb-4">
            <Store className="h-10 w-10 text-orange-600" />
            <h1 className="text-3xl md:text-4xl font-bold">Restaurant Onboarding</h1>
          </div>
          <p className="text-gray-600 text-sm md:text-base">Join our marketplace and grow your business</p>
        </div>

        {/* Progress Bar */}
        <div className="mb-8 max-w-3xl mx-auto">
          <div className="flex justify-between mb-2">
            <span className="text-sm font-semibold text-gray-700">Step {step} of {totalSteps}</span>
            <span className="text-sm font-semibold text-orange-600">{Math.round(progressPercentage)}%</span>
          </div>
          <Progress value={progressPercentage} className="h-3" />
          
          {/* Step Labels */}
          <div className="grid grid-cols-6 gap-1 mt-4 text-xs">
            <div className={`text-center ${step >= 1 ? 'text-orange-600 font-semibold' : 'text-gray-400'}`}>
              Basic Info
            </div>
            <div className={`text-center ${step >= 2 ? 'text-orange-600 font-semibold' : 'text-gray-400'}`}>
              Contact
            </div>
            <div className={`text-center ${step >= 3 ? 'text-orange-600 font-semibold' : 'text-gray-400'}`}>
              Legal
            </div>
            <div className={`text-center ${step >= 4 ? 'text-orange-600 font-semibold' : 'text-gray-400'}`}>
              Banking
            </div>
            <div className={`text-center ${step >= 5 ? 'text-orange-600 font-semibold' : 'text-gray-400'}`}>
              Contract
            </div>
            <div className={`text-center ${step >= 6 ? 'text-orange-600 font-semibold' : 'text-gray-400'}`}>
              Setup
            </div>
          </div>
        </div>

        {/* Assistance Request Banner */}
        {!assistanceRequested && (
          <div className="max-w-3xl mx-auto mb-6">
            <Card className="border-2 border-blue-200 bg-blue-50">
              <CardContent className="p-4">
                <div className="flex items-start justify-between gap-4">
                  <div className="flex items-start gap-3">
                    <Users className="h-5 w-5 text-blue-600 mt-1 flex-shrink-0" />
                    <div>
                      <h3 className="font-semibold text-blue-900">Need Help?</h3>
                      <p className="text-sm text-blue-700">
                        Our business operations team can assist you with the onboarding process
                      </p>
                    </div>
                  </div>
                  <Button size="sm" onClick={requestAssistance} className="bg-blue-600 hover:bg-blue-700 flex-shrink-0">
                    Request Assistance
                  </Button>
                </div>
              </CardContent>
            </Card>
          </div>
        )}

        {assistanceRequested && (
          <div className="max-w-3xl mx-auto mb-6">
            <Card className="border-2 border-green-200 bg-green-50">
              <CardContent className="p-4">
                <div className="flex items-center gap-3">
                  <CheckCircle2 className="h-5 w-5 text-green-600" />
                  <div>
                    <h3 className="font-semibold text-green-900">Assistance Requested</h3>
                    <p className="text-sm text-green-700">
                      We'll contact you within 24 hours. You can continue the onboarding process in the meantime.
                    </p>
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>
        )}

        {/* Main Content */}
        <div className="max-w-3xl mx-auto">
          <Card className="shadow-xl">
            <CardHeader className="bg-gradient-to-r from-orange-500 to-red-500 text-white">
              <CardTitle className="flex items-center gap-2">
                {step === 1 && <><Store className="h-6 w-6" /> Basic Information</>}
                {step === 2 && <><MapPin className="h-6 w-6" /> Contact Details</>}
                {step === 3 && <><Shield className="h-6 w-6" /> Legal Information</>}
                {step === 4 && <><CreditCard className="h-6 w-6" /> Banking Details</>}
                {step === 5 && <><FileCheck className="h-6 w-6" /> Contract Agreement</>}
                {step === 6 && <><ChefHat className="h-6 w-6" /> Complete Setup</>}
              </CardTitle>
              <CardDescription className="text-orange-50">
                {step === 1 && "Tell us about your restaurant"}
                {step === 2 && "How can customers reach you?"}
                {step === 3 && "Provide your legal documents"}
                {step === 4 && "Add your payment account details"}
                {step === 5 && "Review and sign the partnership agreement"}
                {step === 6 && "Add menu items and configure your restaurant"}
              </CardDescription>
            </CardHeader>

            <CardContent className="p-6 space-y-6">
              {/* Step 1: Basic Information */}
              {step === 1 && (
                <div className="space-y-4">
                  <div>
                    <Label htmlFor="name">Restaurant Name *</Label>
                    <Input
                      id="name"
                      value={details.name}
                      onChange={(e) => handleInputChange("name", e.target.value)}
                      placeholder="Enter restaurant name"
                      className="mt-1"
                    />
                  </div>

                  <div>
                    <Label htmlFor="description">Description *</Label>
                    <Textarea
                      id="description"
                      value={details.description}
                      onChange={(e) => handleInputChange("description", e.target.value)}
                      placeholder="Describe your restaurant, specialties, and what makes you unique"
                      className="mt-1 min-h-[100px]"
                    />
                  </div>

                  <div>
                    <Label>Cuisine Types * (Select all that apply)</Label>
                    <div className="flex flex-wrap gap-2 mt-2">
                      {cuisineOptions.map((cuisine) => (
                        <Badge
                          key={cuisine}
                          variant={details.cuisineTypes.includes(cuisine) ? "default" : "outline"}
                          className={`cursor-pointer ${
                            details.cuisineTypes.includes(cuisine)
                              ? "bg-orange-500 hover:bg-orange-600"
                              : "hover:bg-gray-100"
                          }`}
                          onClick={() => handleCuisineToggle(cuisine)}
                        >
                          {cuisine}
                        </Badge>
                      ))}
                    </div>
                    {details.cuisineTypes.length > 0 && (
                      <p className="text-sm text-green-600 mt-2">
                        ✓ {details.cuisineTypes.length} cuisine(s) selected
                      </p>
                    )}
                  </div>
                </div>
              )}

              {/* Step 2: Contact Details */}
              {step === 2 && (
                <div className="space-y-4">
                  <div>
                    <Label htmlFor="address">Restaurant Address *</Label>
                    <Textarea
                      id="address"
                      value={details.address}
                      onChange={(e) => handleInputChange("address", e.target.value)}
                      placeholder="Street address, building number, landmark"
                      className="mt-1"
                    />
                  </div>

                  <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                    <div>
                      <Label htmlFor="city">City *</Label>
                      <Input
                        id="city"
                        value={details.city}
                        onChange={(e) => handleInputChange("city", e.target.value)}
                        placeholder="City"
                        className="mt-1"
                      />
                    </div>
                    <div>
                      <Label htmlFor="state">State</Label>
                      <Input
                        id="state"
                        value={details.state}
                        onChange={(e) => handleInputChange("state", e.target.value)}
                        placeholder="State"
                        className="mt-1"
                      />
                    </div>
                    <div>
                      <Label htmlFor="pincode">Pincode *</Label>
                      <Input
                        id="pincode"
                        value={details.pincode}
                        onChange={(e) => handleInputChange("pincode", e.target.value)}
                        placeholder="Pincode"
                        className="mt-1"
                      />
                    </div>
                  </div>

                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                      <Label htmlFor="phone">Restaurant Phone *</Label>
                      <div className="flex items-center gap-2 mt-1">
                        <Phone className="h-4 w-4 text-gray-400" />
                        <Input
                          id="phone"
                          value={details.phone}
                          onChange={(e) => handleInputChange("phone", e.target.value)}
                          placeholder="+91 XXXXX XXXXX"
                        />
                      </div>
                    </div>
                    <div>
                      <Label htmlFor="email">Restaurant Email *</Label>
                      <div className="flex items-center gap-2 mt-1">
                        <Mail className="h-4 w-4 text-gray-400" />
                        <Input
                          id="email"
                          type="email"
                          value={details.email}
                          onChange={(e) => handleInputChange("email", e.target.value)}
                          placeholder="restaurant@example.com"
                        />
                      </div>
                    </div>
                  </div>
                </div>
              )}

              {/* Step 3: Legal Information */}
              {step === 3 && (
                <div className="space-y-4">
                  <div className="bg-yellow-50 border-l-4 border-yellow-400 p-4 mb-4">
                    <p className="text-sm text-yellow-800">
                      <strong>Important:</strong> All legal documents must be valid and match the registered business name
                    </p>
                  </div>

                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                      <Label htmlFor="ownerName">Owner Name *</Label>
                      <Input
                        id="ownerName"
                        value={details.ownerName}
                        onChange={(e) => handleInputChange("ownerName", e.target.value)}
                        placeholder="Full name as per documents"
                        className="mt-1"
                      />
                    </div>
                    <div>
                      <Label htmlFor="ownerPhone">Owner Phone *</Label>
                      <Input
                        id="ownerPhone"
                        value={details.ownerPhone}
                        onChange={(e) => handleInputChange("ownerPhone", e.target.value)}
                        placeholder="+91 XXXXX XXXXX"
                        className="mt-1"
                      />
                    </div>
                  </div>

                  <div>
                    <Label htmlFor="ownerEmail">Owner Email</Label>
                    <Input
                      id="ownerEmail"
                      type="email"
                      value={details.ownerEmail}
                      onChange={(e) => handleInputChange("ownerEmail", e.target.value)}
                      placeholder="owner@example.com"
                      className="mt-1"
                    />
                  </div>

                  <div>
                    <Label htmlFor="fssaiNumber">FSSAI License Number *</Label>
                    <Input
                      id="fssaiNumber"
                      value={details.fssaiNumber}
                      onChange={(e) => handleInputChange("fssaiNumber", e.target.value)}
                      placeholder="14-digit FSSAI number"
                      className="mt-1"
                    />
                    <p className="text-xs text-gray-500 mt-1">Required for food business operations</p>
                  </div>

                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                      <Label htmlFor="gstNumber">GST Number (Optional)</Label>
                      <Input
                        id="gstNumber"
                        value={details.gstNumber}
                        onChange={(e) => handleInputChange("gstNumber", e.target.value)}
                        placeholder="GST registration number"
                        className="mt-1"
                      />
                    </div>
                    <div>
                      <Label htmlFor="panNumber">PAN Number (Optional)</Label>
                      <Input
                        id="panNumber"
                        value={details.panNumber}
                        onChange={(e) => handleInputChange("panNumber", e.target.value)}
                        placeholder="PAN card number"
                        className="mt-1"
                      />
                    </div>
                  </div>

                  {/* Document Uploads */}
                  <div className="border-t pt-4 mt-4">
                    <h3 className="font-semibold text-lg mb-4 flex items-center gap-2">
                      <Upload className="h-5 w-5 text-orange-600" />
                      Upload Documents
                    </h3>
                    <p className="text-sm text-gray-600 mb-4">
                      Upload clear, legible copies of your documents (PDF, JPG, or PNG format, max 5MB each)
                    </p>

                    <div className="space-y-4">
                      {/* FSSAI Certificate */}
                      <div className="border rounded-lg p-4 bg-gray-50">
                        <Label className="font-semibold mb-2 block">FSSAI License Certificate *</Label>
                        {!fssaiDocument ? (
                          <div>
                            <input
                              type="file"
                              id="fssai-upload"
                              accept=".pdf,.jpg,.jpeg,.png"
                              className="hidden"
                              onChange={(e) => handleFileUpload(e, "fssai")}
                            />
                            <label htmlFor="fssai-upload">
                              <Button
                                type="button"
                                variant="outline"
                                className="cursor-pointer"
                                onClick={() => document.getElementById("fssai-upload")?.click()}
                              >
                                <Upload className="h-4 w-4 mr-2" />
                                Choose File
                              </Button>
                            </label>
                            <p className="text-xs text-gray-500 mt-2">PDF, JPG, PNG • Max 5MB</p>
                          </div>
                        ) : (
                          <div className="flex items-center justify-between bg-white p-3 rounded border">
                            <div className="flex items-center gap-3">
                              <FileText className="h-8 w-8 text-green-600" />
                              <div>
                                <div className="font-medium text-sm">{fssaiDocument.name}</div>
                                <div className="text-xs text-gray-500">{formatFileSize(fssaiDocument.size)}</div>
                              </div>
                            </div>
                            <Button
                              variant="ghost"
                              size="sm"
                              onClick={() => removeDocument("fssai")}
                              className="text-red-600 hover:text-red-700 hover:bg-red-50"
                            >
                              Remove
                            </Button>
                          </div>
                        )}
                      </div>

                      {/* GST Certificate */}
                      <div className="border rounded-lg p-4 bg-gray-50">
                        <Label className="font-semibold mb-2 block">GST Registration Certificate (Optional)</Label>
                        {!gstDocument ? (
                          <div>
                            <input
                              type="file"
                              id="gst-upload"
                              accept=".pdf,.jpg,.jpeg,.png"
                              className="hidden"
                              onChange={(e) => handleFileUpload(e, "gst")}
                            />
                            <label htmlFor="gst-upload">
                              <Button
                                type="button"
                                variant="outline"
                                className="cursor-pointer"
                                onClick={() => document.getElementById("gst-upload")?.click()}
                              >
                                <Upload className="h-4 w-4 mr-2" />
                                Choose File
                              </Button>
                            </label>
                            <p className="text-xs text-gray-500 mt-2">PDF, JPG, PNG • Max 5MB</p>
                          </div>
                        ) : (
                          <div className="flex items-center justify-between bg-white p-3 rounded border">
                            <div className="flex items-center gap-3">
                              <FileText className="h-8 w-8 text-green-600" />
                              <div>
                                <div className="font-medium text-sm">{gstDocument.name}</div>
                                <div className="text-xs text-gray-500">{formatFileSize(gstDocument.size)}</div>
                              </div>
                            </div>
                            <Button
                              variant="ghost"
                              size="sm"
                              onClick={() => removeDocument("gst")}
                              className="text-red-600 hover:text-red-700 hover:bg-red-50"
                            >
                              Remove
                            </Button>
                          </div>
                        )}
                      </div>

                      {/* PAN Card */}
                      <div className="border rounded-lg p-4 bg-gray-50">
                        <Label className="font-semibold mb-2 block">PAN Card (Optional)</Label>
                        {!panDocument ? (
                          <div>
                            <input
                              type="file"
                              id="pan-upload"
                              accept=".pdf,.jpg,.jpeg,.png"
                              className="hidden"
                              onChange={(e) => handleFileUpload(e, "pan")}
                            />
                            <label htmlFor="pan-upload">
                              <Button
                                type="button"
                                variant="outline"
                                className="cursor-pointer"
                                onClick={() => document.getElementById("pan-upload")?.click()}
                              >
                                <Upload className="h-4 w-4 mr-2" />
                                Choose File
                              </Button>
                            </label>
                            <p className="text-xs text-gray-500 mt-2">PDF, JPG, PNG • Max 5MB</p>
                          </div>
                        ) : (
                          <div className="flex items-center justify-between bg-white p-3 rounded border">
                            <div className="flex items-center gap-3">
                              <FileText className="h-8 w-8 text-green-600" />
                              <div>
                                <div className="font-medium text-sm">{panDocument.name}</div>
                                <div className="text-xs text-gray-500">{formatFileSize(panDocument.size)}</div>
                              </div>
                            </div>
                            <Button
                              variant="ghost"
                              size="sm"
                              onClick={() => removeDocument("pan")}
                              className="text-red-600 hover:text-red-700 hover:bg-red-50"
                            >
                              Remove
                            </Button>
                          </div>
                        )}
                      </div>
                    </div>
                  </div>
                </div>
              )}

              {/* Step 4: Banking Details */}
              {step === 4 && (
                <div className="space-y-4">
                  <div className="bg-blue-50 border-l-4 border-blue-400 p-4 mb-4">
                    <p className="text-sm text-blue-800">
                      <strong>Secure:</strong> Your banking details are encrypted and used only for payment settlements
                    </p>
                  </div>

                  <div>
                    <Label htmlFor="bankName">Bank Name *</Label>
                    <Input
                      id="bankName"
                      value={details.bankName}
                      onChange={(e) => handleInputChange("bankName", e.target.value)}
                      placeholder="Name of your bank"
                      className="mt-1"
                    />
                  </div>

                  <div>
                    <Label htmlFor="accountHolderName">Account Holder Name *</Label>
                    <Input
                      id="accountHolderName"
                      value={details.accountHolderName}
                      onChange={(e) => handleInputChange("accountHolderName", e.target.value)}
                      placeholder="Name as per bank account"
                      className="mt-1"
                    />
                  </div>

                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                      <Label htmlFor="accountNumber">Account Number *</Label>
                      <Input
                        id="accountNumber"
                        value={details.accountNumber}
                        onChange={(e) => handleInputChange("accountNumber", e.target.value)}
                        placeholder="Bank account number"
                        className="mt-1"
                      />
                    </div>
                    <div>
                      <Label htmlFor="ifscCode">IFSC Code *</Label>
                      <Input
                        id="ifscCode"
                        value={details.ifscCode}
                        onChange={(e) => handleInputChange("ifscCode", e.target.value)}
                        placeholder="Bank IFSC code"
                        className="mt-1"
                      />
                    </div>
                  </div>

                  {/* Cancelled Cheque Upload */}
                  <div className="border-t pt-4 mt-4">
                    <h3 className="font-semibold text-lg mb-4 flex items-center gap-2">
                      <Upload className="h-5 w-5 text-blue-600" />
                      Upload Bank Proof
                    </h3>
                    <div className="border rounded-lg p-4 bg-blue-50">
                      <Label className="font-semibold mb-2 block">Cancelled Cheque or Bank Statement *</Label>
                      <p className="text-xs text-gray-600 mb-3">
                        Upload a cancelled cheque or bank statement showing account number and IFSC code
                      </p>
                      {!cancelledCheque ? (
                        <div>
                          <input
                            type="file"
                            id="cheque-upload"
                            accept=".pdf,.jpg,.jpeg,.png"
                            className="hidden"
                            onChange={(e) => handleFileUpload(e, "cheque")}
                          />
                          <label htmlFor="cheque-upload">
                            <Button
                              type="button"
                              variant="outline"
                              className="cursor-pointer bg-white"
                              onClick={() => document.getElementById("cheque-upload")?.click()}
                            >
                              <Upload className="h-4 w-4 mr-2" />
                              Choose File
                            </Button>
                          </label>
                          <p className="text-xs text-gray-500 mt-2">PDF, JPG, PNG • Max 5MB</p>
                        </div>
                      ) : (
                        <div className="flex items-center justify-between bg-white p-3 rounded border">
                          <div className="flex items-center gap-3">
                            <FileText className="h-8 w-8 text-green-600" />
                            <div>
                              <div className="font-medium text-sm">{cancelledCheque.name}</div>
                              <div className="text-xs text-gray-500">{formatFileSize(cancelledCheque.size)}</div>
                            </div>
                          </div>
                          <Button
                            variant="ghost"
                            size="sm"
                            onClick={() => removeDocument("cheque")}
                            className="text-red-600 hover:text-red-700 hover:bg-red-50"
                          >
                            Remove
                          </Button>
                        </div>
                      )}
                    </div>
                  </div>
                </div>
              )}

              {/* Step 5: Contract Agreement */}
              {step === 5 && (
                <div className="space-y-4">
                  <div className="bg-gray-50 border border-gray-200 rounded-lg p-6 max-h-96 overflow-y-auto">
                    <h3 className="text-lg font-bold mb-4">Partnership Agreement</h3>
                    
                    <div className="space-y-4 text-sm text-gray-700">
                      <section>
                        <h4 className="font-semibold text-orange-600 mb-2">1. Commission Structure</h4>
                        <ul className="list-disc pl-5 space-y-1">
                          <li>Platform Commission: <strong>15% on order value</strong></li>
                          <li>Payment Gateway Charges: <strong>2% on order value</strong></li>
                          <li>GST applicable as per current rates</li>
                        </ul>
                      </section>

                      <section>
                        <h4 className="font-semibold text-orange-600 mb-2">2. Delivery Charges</h4>
                        <ul className="list-disc pl-5 space-y-1">
                          <li>Base Delivery Fee: <strong>₹40</strong> (up to 3 km)</li>
                          <li>Additional: <strong>₹10 per km</strong> beyond 3 km</li>
                          <li>Surge pricing during peak hours (20% additional)</li>
                          <li>70% of delivery charges go to delivery partners</li>
                        </ul>
                      </section>

                      <section>
                        <h4 className="font-semibold text-orange-600 mb-2">3. Payment Settlement</h4>
                        <ul className="list-disc pl-5 space-y-1">
                          <li>Weekly payment settlements (every Monday)</li>
                          <li>2-day settlement cycle after order delivery</li>
                          <li>Direct bank transfer to registered account</li>
                          <li>Detailed payment reports via dashboard</li>
                        </ul>
                      </section>

                      <section>
                        <h4 className="font-semibold text-orange-600 mb-2">4. Restaurant Obligations</h4>
                        <ul className="list-disc pl-5 space-y-1">
                          <li>Maintain valid FSSAI license at all times</li>
                          <li>Provide accurate menu and pricing information</li>
                          <li>Ensure food quality and hygiene standards</li>
                          <li>Respond to orders within 2 minutes</li>
                          <li>Maintain preparation time accuracy</li>
                          <li>Handle customer complaints professionally</li>
                        </ul>
                      </section>

                      <section>
                        <h4 className="font-semibold text-orange-600 mb-2">5. Platform Services</h4>
                        <ul className="list-disc pl-5 space-y-1">
                          <li>Customer acquisition and marketing</li>
                          <li>Order management system</li>
                          <li>Payment processing and reconciliation</li>
                          <li>Customer support</li>
                          <li>Analytics and business insights</li>
                          <li>Promotional campaigns and offers</li>
                        </ul>
                      </section>

                      <section>
                        <h4 className="font-semibold text-orange-600 mb-2">6. SLA Requirements</h4>
                        <ul className="list-disc pl-5 space-y-1">
                          <li>Order acceptance rate: Minimum 85%</li>
                          <li>Order rejection penalty: ₹50 per rejection</li>
                          <li>Late preparation penalty: ₹20 per 5 minutes delay</li>
                          <li>Customer rating requirement: Minimum 4.0/5.0</li>
                        </ul>
                      </section>

                      <section>
                        <h4 className="font-semibold text-orange-600 mb-2">7. Term and Termination</h4>
                        <ul className="list-disc pl-5 space-y-1">
                          <li>Initial term: 1 year from date of signing</li>
                          <li>Auto-renewal unless terminated with 30 days notice</li>
                          <li>Either party may terminate with written notice</li>
                          <li>Immediate termination for fraud or legal violations</li>
                        </ul>
                      </section>

                      <section>
                        <h4 className="font-semibold text-orange-600 mb-2">8. Data and Privacy</h4>
                        <ul className="list-disc pl-5 space-y-1">
                          <li>Customer data remains confidential</li>
                          <li>No unauthorized use of customer information</li>
                          <li>Compliance with data protection regulations</li>
                          <li>Secure handling of payment information</li>
                        </ul>
                      </section>
                    </div>
                  </div>

                  {!contractSigned ? (
                    <div className="space-y-4">
                      <div className="flex items-start gap-2 bg-yellow-50 p-4 rounded-lg border border-yellow-200">
                        <Clock className="h-5 w-5 text-yellow-600 flex-shrink-0 mt-0.5" />
                        <p className="text-sm text-yellow-800">
                          Please review the entire contract before signing. By signing, you agree to all terms and conditions.
                        </p>
                      </div>

                      <div>
                        <Label htmlFor="signature">Digital Signature (Type your full name) *</Label>
                        <Input
                          id="signature"
                          value={signatureText}
                          onChange={(e) => setSignatureText(e.target.value)}
                          placeholder="Type your full name as signature"
                          className="mt-1 font-serif text-lg"
                        />
                      </div>

                      <div className="flex items-center gap-2">
                        <Checkbox id="terms" />
                        <label htmlFor="terms" className="text-sm">
                          I have read and agree to the terms and conditions of this partnership agreement
                        </label>
                      </div>

                      <Button onClick={handleSignContract} className="w-full bg-green-600 hover:bg-green-700">
                        <FileCheck className="h-4 w-4 mr-2" />
                        Sign Contract
                      </Button>
                    </div>
                  ) : (
                    <div className="bg-green-50 border-2 border-green-300 rounded-lg p-6">
                      <div className="flex items-center gap-3 mb-4">
                        <CheckCircle2 className="h-8 w-8 text-green-600" />
                        <div>
                          <h3 className="font-bold text-green-900">Contract Signed Successfully</h3>
                          <p className="text-sm text-green-700">Signed on {new Date().toLocaleDateString()}</p>
                        </div>
                      </div>
                      <div className="bg-white p-4 rounded border border-green-200">
                        <p className="text-sm text-gray-600 mb-1">Digital Signature:</p>
                        <p className="text-2xl font-serif text-gray-900">{signatureText}</p>
                      </div>
                    </div>
                  )}
                </div>
              )}

              {/* Step 6: Complete Setup */}
              {step === 6 && (
                <div className="space-y-6">
                  <div className="text-center mb-6">
                    <div className="inline-flex items-center justify-center w-16 h-16 bg-green-100 rounded-full mb-4">
                      <CheckCircle2 className="h-10 w-10 text-green-600" />
                    </div>
                    <h3 className="text-2xl font-bold text-gray-900 mb-2">Onboarding Complete!</h3>
                    <p className="text-gray-600">Your restaurant has been successfully registered on our platform</p>
                  </div>

                  <div className="bg-blue-50 border-l-4 border-blue-400 p-4">
                    <h4 className="font-semibold text-blue-900 mb-2">Next Steps:</h4>
                    <p className="text-sm text-blue-800 mb-3">
                      Complete your restaurant setup to start receiving orders
                    </p>
                  </div>

                  {/* Quick Menu Upload */}
                  <Card className="border-2 border-green-200 bg-gradient-to-br from-green-50 to-emerald-50">
                    <CardContent className="p-6">
                      <div className="flex items-start gap-4 mb-4">
                        <div className="h-14 w-14 rounded-xl bg-green-100 flex items-center justify-center flex-shrink-0">
                          <Upload className="h-7 w-7 text-green-600" />
                        </div>
                        <div className="flex-1">
                          <h3 className="text-xl font-bold text-gray-900 mb-2">Quick Start: Upload Your Menu</h3>
                          <p className="text-sm text-gray-600 mb-1">
                            Save time by uploading all your menu items at once using our Excel template
                          </p>
                          <p className="text-xs text-gray-500">
                            Includes: Item names, prices, categories, descriptions, and inventory details
                          </p>
                        </div>
                      </div>

                      {!menuExcelFile ? (
                        <div className="space-y-3">
                          <div className="flex flex-wrap gap-2">
                            <Button
                              type="button"
                              variant="outline"
                              size="sm"
                              className="border-green-300 text-green-700 hover:bg-green-50"
                            >
                              <FileText className="h-4 w-4 mr-2" />
                              Download Excel Template
                            </Button>
                            <Button
                              type="button"
                              variant="outline"
                              size="sm"
                              className="border-blue-300 text-blue-700 hover:bg-blue-50"
                            >
                              <FileText className="h-4 w-4 mr-2" />
                              View Sample Menu
                            </Button>
                          </div>

                          <div className="border-2 border-dashed border-green-300 rounded-lg p-6 bg-white text-center">
                            <input
                              type="file"
                              id="menu-upload"
                              accept=".xlsx,.xls"
                              className="hidden"
                              onChange={(e) => handleFileUpload(e, "menu")}
                            />
                            <label htmlFor="menu-upload" className="cursor-pointer">
                              <div className="flex flex-col items-center">
                                <Upload className="h-12 w-12 text-green-400 mb-3" />
                                <div className="text-sm font-semibold text-gray-900 mb-1">
                                  Click to upload or drag and drop
                                </div>
                                <div className="text-xs text-gray-500">
                                  Excel files only (.xlsx or .xls) • Max 5MB
                                </div>
                              </div>
                            </label>
                          </div>

                          <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-3">
                            <div className="flex gap-2">
                              <FileText className="h-4 w-4 text-yellow-600 flex-shrink-0 mt-0.5" />
                              <div className="text-xs text-yellow-800">
                                <strong>Template Format:</strong> Category | Item Name | Description | Price | Veg/Non-Veg | Prep Time | Stock Quantity | Image URL
                              </div>
                            </div>
                          </div>
                        </div>
                      ) : (
                        <div className="space-y-3">
                          <div className="flex items-center justify-between bg-white p-4 rounded-lg border-2 border-green-300">
                            <div className="flex items-center gap-3">
                              <div className="h-12 w-12 rounded-lg bg-green-100 flex items-center justify-center">
                                <FileText className="h-6 w-6 text-green-600" />
                              </div>
                              <div>
                                <div className="font-semibold text-gray-900">{menuExcelFile.name}</div>
                                <div className="text-sm text-gray-500">{formatFileSize(menuExcelFile.size)}</div>
                              </div>
                            </div>
                            <Button
                              variant="ghost"
                              size="sm"
                              onClick={() => removeDocument("menu")}
                              className="text-red-600 hover:text-red-700 hover:bg-red-50"
                            >
                              Remove
                            </Button>
                          </div>

                          <div className="bg-green-50 border border-green-200 rounded-lg p-4">
                            <div className="flex items-start gap-3">
                              <CheckCircle2 className="h-5 w-5 text-green-600 flex-shrink-0 mt-0.5" />
                              <div>
                                <div className="font-semibold text-green-900 mb-1">File uploaded successfully!</div>
                                <p className="text-sm text-green-700 mb-3">
                                  Your menu will be imported and available in your dashboard after approval
                                </p>
                                <Button
                                  size="sm"
                                  className="bg-green-600 hover:bg-green-700"
                                  onClick={() => {
                                    toast({
                                      title: "Menu Uploaded!",
                                      description: "Your menu items will be processed and imported to your restaurant dashboard."
                                    });
                                  }}
                                >
                                  <CheckCircle2 className="h-4 w-4 mr-2" />
                                  Confirm Menu Upload
                                </Button>
                              </div>
                            </div>
                          </div>

                          <p className="text-xs text-center text-gray-500">
                            You can also add or edit items individually from your restaurant dashboard
                          </p>
                        </div>
                      )}
                    </CardContent>
                  </Card>

                  <div className="space-y-3">
                    <Card className="border-2 border-orange-200 hover:border-orange-400 transition-all cursor-pointer">
                      <CardContent className="p-4">
                        <div className="flex items-center gap-4">
                          <div className="h-12 w-12 rounded-lg bg-orange-100 flex items-center justify-center flex-shrink-0">
                            <ChefHat className="h-6 w-6 text-orange-600" />
                          </div>
                          <div className="flex-1">
                            <h4 className="font-semibold text-gray-900">Add Menu Items</h4>
                            <p className="text-sm text-gray-600">Add items individually or upload via Excel</p>
                          </div>
                          <Badge className="bg-red-500">Required</Badge>
                        </div>
                      </CardContent>
                    </Card>

                    <Card className="border-2 border-gray-200 hover:border-gray-400 transition-all cursor-pointer">
                      <CardContent className="p-4">
                        <div className="flex items-center gap-4">
                          <div className="h-12 w-12 rounded-lg bg-purple-100 flex items-center justify-center flex-shrink-0">
                            <DollarSign className="h-6 w-6 text-purple-600" />
                          </div>
                          <div className="flex-1">
                            <h4 className="font-semibold text-gray-900">Set Pricing</h4>
                            <p className="text-sm text-gray-600">Configure prices and discounts</p>
                          </div>
                          <Badge className="bg-red-500">Required</Badge>
                        </div>
                      </CardContent>
                    </Card>

                    <Card className="border-2 border-gray-200 hover:border-gray-400 transition-all cursor-pointer">
                      <CardContent className="p-4">
                        <div className="flex items-center gap-4">
                          <div className="h-12 w-12 rounded-lg bg-green-100 flex items-center justify-center flex-shrink-0">
                            <Package className="h-6 w-6 text-green-600" />
                          </div>
                          <div className="flex-1">
                            <h4 className="font-semibold text-gray-900">Manage Inventory</h4>
                            <p className="text-sm text-gray-600">Track stock and ingredients (Optional)</p>
                          </div>
                          <Badge variant="outline">Optional</Badge>
                        </div>
                      </CardContent>
                    </Card>

                    <Card className="border-2 border-gray-200 hover:border-gray-400 transition-all cursor-pointer">
                      <CardContent className="p-4">
                        <div className="flex items-center gap-4">
                          <div className="h-12 w-12 rounded-lg bg-blue-100 flex items-center justify-center flex-shrink-0">
                            <Megaphone className="h-6 w-6 text-blue-600" />
                          </div>
                          <div className="flex-1">
                            <h4 className="font-semibold text-gray-900">Create Campaigns</h4>
                            <p className="text-sm text-gray-600">Launch promotions and offers (Optional)</p>
                          </div>
                          <Badge variant="outline">Optional</Badge>
                        </div>
                      </CardContent>
                    </Card>
                  </div>

                  <Button onClick={completeOnboarding} className="w-full bg-orange-600 hover:bg-orange-700 text-lg py-6">
                    <Building2 className="h-5 w-5 mr-2" />
                    Go to Restaurant Dashboard
                  </Button>

                  <p className="text-xs text-center text-gray-500">
                    You can always complete these steps later from your dashboard
                  </p>
                </div>
              )}

              {/* Navigation Buttons */}
              {step < 6 && (
                <div className="flex justify-between pt-6 border-t">
                  <Button
                    variant="outline"
                    onClick={prevStep}
                    disabled={step === 1}
                  >
                    <ArrowLeft className="h-4 w-4 mr-2" />
                    Previous
                  </Button>
                  <Button
                    onClick={nextStep}
                    className="bg-orange-600 hover:bg-orange-700"
                  >
                    {step === 5 ? "Continue to Setup" : "Next"}
                    <ArrowRight className="h-4 w-4 ml-2" />
                  </Button>
                </div>
              )}
            </CardContent>
          </Card>
        </div>

        {/* Footer */}
        <div className="mt-8 text-center text-sm text-gray-500">
          <p>Having trouble? <a href="#" className="text-orange-600 hover:underline">Contact Support</a></p>
          <p className="mt-2">© 2024 Food Marketplace. All rights reserved.</p>
        </div>
      </div>
    </div>
  );
};

export default RestaurantOnboarding;

