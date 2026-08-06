import { SignupForm } from "../components/SignupForm";

export default function SignUpPage(){
    return(
    <div className="flex min-h-screen items-center justify-center bg-background p-6">
      <div className="w-full max-w-sm">
        <SignupForm />
      </div>
    </div>
  )
}