import { initializeApp } from "firebase/app";
import { getAnalytics } from "firebase/analytics";
import { getAuth } from "firebase/auth";
import { getFirestore } from "firebase/firestore";

const firebaseConfig = {
  apiKey: "AIzaSyCfcQznl_Vc4679jYdKu7Imj2uxGIlYNoU",
  authDomain: "ovulitos-ffc21.firebaseapp.com",
  projectId: "ovulitos-ffc21",
  storageBucket: "ovulitos-ffc21.firebasestorage.app",
  messagingSenderId: "178201442511",
  appId: "1:178201442511:web:89ee105a8011781cb25fba",
  measurementId: "G-GPE5VQ404R"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const analytics = getAnalytics(app);
const auth = getAuth(app); 
const db = getFirestore(app);

export {app, analytics, auth, db};