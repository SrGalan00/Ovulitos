import React from 'react';
import { doSignOut } from '../firebase/auth';
import { useAuth } from '../context/authContext';

export const Home: React.FC = () => {
    const { currentUser } = useAuth();

    return (
        <div className="flex flex-col items-center justify-center min-h-screen bg-[#FFF8C9] gap-4">
            <h1 className="text-3xl font-bold text-[#c45a7a]">Welcome Home!</h1>
            <p className="text-lg">Hello, {currentUser?.email}</p>
            <button
                onClick={() => doSignOut()}
                className="px-6 py-2 bg-[#c45a7a] text-white rounded-xl hover:bg-[#a04660] transition-colors"
            >
                Logout
            </button>
        </div>
    );
};
