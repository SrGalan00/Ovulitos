import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { doSignInWithEmailAndPassword } from '../firebase/auth';
import { useAuth } from '../context/authContext';

export const Login: React.FC = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [rememberme, setRememberme] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const { currentUser } = useAuth();


  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    try {
      await doSignInWithEmailAndPassword(email, password);
      navigate("/");
    } catch (error: any) {
      console.error('Error login: ', error);
      setError(error.message || 'Failed to login');
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

        {/* Gradient/Color Fallback Layer (Optional, matches original design) */}
        <div className="absolute inset-[40px] rounded-full bg-gradient-to-b from-[#f7b6c8] to-[#f3a2b9] -z-10"></div>

        {/* Form Layer */}
        <form className="relative z-20 flex flex-col justify-center items-center gap-[22px]" onSubmit={handleSubmit}>
          {error && <div className="text-red-600 font-bold bg-white/80 p-2 rounded">{error}</div>}
          <div className="flex flex-col gap-1">
            <label className="text-[#333] text-base px-1.5 font-medium">Email</label>
            <input
              className="w-[320px] p-[14px] rounded-xl border border-[#ddd] text-base outline-none focus:border-[#c45a7a] transition-colors bg-white/90 backdrop-blur-sm"
              type="email"
              placeholder="Email"
              value={email}
              onChange={e => setEmail(e.target.value)}
            />
          </div>

          <div className="flex flex-col gap-1">
            <label className="text-[#333] text-base px-1.5 font-medium">Password</label>
            <input
              className="w-[320px] p-[14px] rounded-xl border border-[#ddd] text-base outline-none focus:border-[#c45a7a] transition-colors bg-white/90 backdrop-blur-sm"
              type="password"
              placeholder="Password"
              value={password}
              onChange={e => setPassword(e.target.value)}
            />
          </div>

          <label className="flex items-center gap-2.5 text-[#333] text-sm font-medium cursor-pointer">
            <input
              type="checkbox"
              className="w-4 h-4 accent-[#c45a7a]"
              checked={rememberme}
              onChange={e => setRememberme(e.target.checked)}
            />
            Remember me
          </label>

          <button
            className="mt-5 w-[200px] p-3.5 rounded-[14px] border border-[#ddd] bg-white font-bold text-[#333] hover:bg-[#f1f1f1] transition-colors cursor-pointer shadow-sm hover:shadow-md active:scale-95 transform duration-100"
            type="submit"
          >
            LOGIN
          </button>
          <div className="mt-4 text-sm font-medium">
            Not registered yet? <Link to="/register" className="text-[#c45a7a] hover:underline">Register here</Link>
          </div>
        </form>
      </div>
    </div>
  );
};
