import { useState, useContext, useEffect } from "react";
import { auth} from "../../firebase/firebase"; 
import {onAuthStateChanged} from "firebase/auth"

const AuthContext = React.createContext(); 

export function useAuth(){
    return useContext(AuthContext); 
}

export function AuthProvider({children}){
    const [currentUser, setCurrentUser] = useState(null); 
    const [userLoggedIn, setUserLoggedIn] = useState(false); 
    const [loading, setLoding] = useState(true); 

    useEffect(() => {
        const unsubscribe = onAuthStateChanged(auth, initializeUser); 
        return unsubscribe
    }, [])

    async function initializeUser(user){
        if(user){
            setCurrentUser(...user); 
            setUserLoggedIn(true); 
        }else{
            setCurrentUser(null);
            setUserLoggedIn(false);  
        }
    }

    const value = { // Le vamos a enviar como autenticación los siguientes parámetros 
        currentUser, // El usuario actual 
        userLoggedIn, // El usuario con el que hemos iniciado sesión 
        loading // La carga de la autenticación 
    }

    return (
        <AuthProvider.Provider value={value}> 
            {!loading && children}
        </AuthProvider.Provider> 
    )
}

export {AuthContext}