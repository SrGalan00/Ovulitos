import { initializeApp } from "firebase/app";
import { getFirestore, collection, getDocs } from "firebase/firestore";

const firebaseConfig = {
  apiKey: "AIzaSyCfcQznl_Vc4679jYdKu7Imj2uxGIlYNoU",
  authDomain: "ovulitos-ffc21.firebaseapp.com",
  projectId: "ovulitos-ffc21",
  storageBucket: "ovulitos-ffc21.firebasestorage.app",
  messagingSenderId: "178201442511",
  appId: "1:178201442511:web:89ee105a8011781cb25fba",
  measurementId: "G-GPE5VQ404R"
};

const app = initializeApp(firebaseConfig);
const db = getFirestore(app);

const testQuery = async () => {
  try {
    const querySnapshot = await getDocs(collection(db, "noticias_semanales"));
    console.log("Documents found:", querySnapshot.size);
    querySnapshot.forEach((doc) => {
      console.log(doc.id, " => ", doc.data());
    });
    process.exit(0);
  } catch (e) {
    console.error("Error querying db:", e);
    process.exit(1);
  }
};

testQuery();
