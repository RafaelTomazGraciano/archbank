import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { ArrowRight, QrCode, Zap, ShieldCheck, type LucideIcon } from "lucide-react";
import { buttonVariants } from "@/shared/components/ui/button";
import {
  Card,
  CardHeader,
  CardTitle,
  CardDescription,
} from "@/shared/components/ui/card";
import { Separator } from "@/shared/components/ui/separator";
import { cn } from "@/shared/lib/utils";

import logo from "@/assets/archbank-logo.png";

type Feature = {
  icon: LucideIcon;
  title: string;
  text: string;
  tone: "primary" | "accent";
};

const features: Feature[] = [
  {
    icon: QrCode,
    title: "Instant Pix",
    text: "Send and receive instantly, at no cost.",
    tone: "primary",
  },
  {
    icon: Zap,
    title: "No monthly fees",
    text: "No maintenance costs, no fine print.",
    tone: "accent",
  },
  {
    icon: ShieldCheck,
    title: "Bank-grade security",
    text: "End-to-end encryption on every transaction.",
    tone: "primary",
  },
];

export default function HomePage() {
  const [mounted, setMounted] = useState(false);
  useEffect(() => setMounted(true), []);

  return (
    <div className="min-h-screen bg-background text-foreground">
      {/* Nav */}
      <header className="max-w-5xl mx-auto flex items-center justify-between px-6 py-8">
        <img src={logo} alt="ArchBank" className="h-12 w-auto" />
        <Link to="/login">
          Log in
        </Link>
      </header>

      <main className="max-w-5xl mx-auto px-6">
        {/* Hero */}
        <section
          className={cn(
            "relative grid md:grid-cols-2 items-center gap-10 pt-8 pb-24 md:pt-14 md:pb-32 transition-all duration-700",
            mounted ? "opacity-100 translate-y-0" : "opacity-0 translate-y-3"
          )}
        >
          <div>
            <h1 className="text-4xl md:text-5xl font-semibold leading-[1.1] tracking-tight">
              Your money, the way it should be:{" "}
              <span className="text-primary">simple</span>.
            </h1>
            <p className="mt-5 text-base md:text-lg text-muted-foreground leading-relaxed max-w-md text-accent">
              <span className="text-accent font-medium">Pix</span>,
              transfers, and payments in one place — no hidden fees, no red
              tape to open an account.
            </p>

            <div className="mt-8 flex flex-wrap items-center gap-2">
              <Link
                to="/signup"
                className={cn(
                  buttonVariants({ size: "lg" }),
                  "inline-flex items-center gap-2"
                )}
              >
                Create free account
                <ArrowRight className="h-4 w-4" />
              </Link>
              <Link to="/login" className={buttonVariants({ variant: "link" })}>
                I already have an account
              </Link>
            </div>
          </div>

          <div className="hidden md:flex items-center justify-center h-full">
            <img src={logo} alt="" aria-hidden className="w-full max-w-xl" />
          </div>
        </section>

        <Separator />

        {/* Feature strip */}
        <section className="py-12 grid grid-cols-1 sm:grid-cols-3 gap-6 items-stretch">
          {features.map((feature) => (
            <Card key={feature.title} className="h-full">
              <CardHeader className="gap-3">
                <div
                  className={cn(
                    "h-9 w-9 flex items-center justify-center rounded-md",
                    feature.tone === "primary"
                      ? "bg-primary/10 text-primary"
                      : "bg-accent/10 text-accent"
                  )}
                >
                  <feature.icon className="h-5 w-5" />
                </div>
                <CardTitle className="text-sm">{feature.title}</CardTitle>
                <CardDescription>{feature.text}</CardDescription>
              </CardHeader>
            </Card>
          ))}
        </section>
      </main>

      <footer className="px-6 py-10 text-center text-xs text-muted-foreground">
        <span className="font-semibold">
          ©{" "}
          <span className="text-primary">Arch</span>
          <span className="text-accent">Bank</span>
        </span>{" "}
        {new Date().getFullYear()}
      </footer>
    </div>
  );
}