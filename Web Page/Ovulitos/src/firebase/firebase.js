// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getAnalytics } from "firebase/analytics";

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