import { 
    createUserWithEmailAndPassword, 
    GoogleAuthProvider,
    signInWithEmailAndPassword, 
    signInWithPopup,
    signOut,
    updateProfile 
  } from "firebase/auth";
  import { auth } from "./firebase"; 
  
  export const doCreateUserWithEmailAndPassword = async (email, password, displayName) => {
      const userCredential = await createUserWithEmailAndPassword(auth, email, password);
      if (displayName) {
          await updateProfile(userCredential.user, { displayName });
      }
      return userCredential;
  }; 
  
  export const doSignInWithEmailAndPassword = async (email, password) => {
      return await signInWithEmailAndPassword(auth, email, password); 
  }; 
  
  export const doSignInWithGoogle = async () => {
      const provider = new GoogleAuthProvider(); 
      return await signInWithPopup(auth, provider); 
  }; 
  
  export const doSignOut = async () => {
      return await signOut(auth); 
  };