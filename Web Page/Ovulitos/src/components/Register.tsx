import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { doCreateUserWithEmailAndPassword } from '../firebase/auth';
import { useAuth } from '../context/authContext';
import { db } from '../firebase/firebase';
import { doc, setDoc } from 'firebase/firestore';

export const Register: React.FC = () => {
  const [email, setEmail] = useState('');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const { currentUser } = useAuth();


  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    try {
      // 1. Crear usuario en Firebase Auth
      const userCredential = await doCreateUserWithEmailAndPassword(email, password);
      const user = userCredential.user;

      // 2. Crear el documento del usuario en Firestore (usuarios/{email})
      // Esto soluciona que el documento aparezca como "no existente" aunque tenga subcolecciones
      if (user && user.email) {
        await setDoc(doc(db, 'usuarios', user.email), {
          uid: user.uid,
          email: user.email,
          nombre: username || user.email.split('@')[0],
          fechaRegistro: new Date().toISOString(),
          rol: 'usuario',
          cicloMedio: 28 // Valor por defecto inicial
        });
      }

      navigate("/");
    } catch (error: any) {
      console.error('Error register: ', error);
      setError(error.message || 'Failed to register');
    }

  };

  useEffect(() => {
    if (currentUser) {
      navigate('/');
    }
  }, [currentUser, navigate]);

  return (
    <div className="min-h-screen flex justify-center items-center bg-[#f7b6c8]">
      <div className="relative w-[800px] h-[800px] flex justify-center items-center">
        {/* Background Image Layer */}
        <div
          className="absolute inset-[40px] rounded-full bg-center bg-cover bg-no-repeat z-10 mix-blend-multiply"
          style={{ backgroundImage: "url('/Ovulito.png')" }}
        ></div>

        {/* Gradient/Color Fallback Layer */}
        <div className="absolute inset-[40px] rounded-full bg-gradient-to-b from-[#f7b6c8] to-[#f3a2b9] -z-10"></div>

        {/* Form Layer */}
        <form className="relative z-20 flex flex-col justify-center items-center gap-[18px]" onSubmit={handleSubmit}>
          <h2 className="text-2xl font-bold text-[#333] mb-1">Crear Cuenta</h2>
          {error && <div className="text-red-600 font-bold bg-white/80 p-2 rounded max-w-[300px] text-center text-sm">{error}</div>}

          <div className="flex flex-col gap-1">
            <label className="text-[#333] text-sm px-1.5 font-medium">Nombre de usuario</label>
            <input
              className="w-[320px] p-[12px] rounded-xl border border-[#ddd] text-base outline-none focus:border-[#c45a7a] transition-colors bg-white/90 backdrop-blur-sm"
              type="text"
              placeholder="Tu nombre"
              value={username}
              required
              onChange={e => setUsername(e.target.value)}
            />
          </div>

          <div className="flex flex-col gap-1">
            <label className="text-[#333] text-sm px-1.5 font-medium">Correo</label>
            <input
              className="w-[320px] p-[12px] rounded-xl border border-[#ddd] text-base outline-none focus:border-[#c45a7a] transition-colors bg-white/90 backdrop-blur-sm"
              type="email"
              placeholder="Correo electrónico"
              value={email}
              required
              onChange={e => setEmail(e.target.value)}
            />
          </div>

          <div className="flex flex-col gap-1">
            <label className="text-[#333] text-sm px-1.5 font-medium">Contraseña</label>
            <input
              className="w-[320px] p-[12px] rounded-xl border border-[#ddd] text-base outline-none focus:border-[#c45a7a] transition-colors bg-white/90 backdrop-blur-sm"
              type="password"
              placeholder="Contraseña"
              value={password}
              required
              onChange={e => setPassword(e.target.value)}
            />
          </div>

          <button
            className="mt-4 w-[200px] p-3.5 rounded-[14px] border border-[#ddd] bg-white font-bold text-[#333] hover:bg-[#f1f1f1] transition-colors cursor-pointer shadow-sm hover:shadow-md active:scale-95 transform duration-100"
            type="submit"
          >
            REGISTRARSE
          </button>

          <div className="mt-2 text-sm font-medium">
            ¿Ya tienes una cuenta? <Link to="/login" className="text-[#c45a7a] hover:underline">Inicia sesión aquí</Link>
          </div>
        </form>
      </div>
    </div>
  );
};
