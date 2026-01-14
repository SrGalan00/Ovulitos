import { createUserWithEmailAndPassword, GoogleAuthProvider, signInWithEmailAndPassword, signInWithPopup} from "firebase/auth"
import {auth} from "./firebase" 

export const doCreateUserWithEmailAndPassword = async (email, password) => {
    return createUserWithEmailAndPassword(auth, email, password); // Le enviamos a la autenticación de firebase llamando a la función de auth 
    // del propio firebase mediante la librería. Y vamos a poner dentro de la función en "auth" como una especie de TOKEN (Sin serlo claro) 
    // y además vamos a tener que introducir el email y password para poder crear el usuario
}; 

export const doSignInWithEmailAndPassword = (email, password) => {
    return signInWithEmailAndPassword(auth, email, password); 
}

export const doSignInWithGoogle = async() => {
    const provider = new GoogleAuthProvider(); 
    const result = await signInWithPopup(auth, provider); // Dentro del "auth tenemos todos los datos del usuario en cuestión"
    // result.user

    return result
}; 