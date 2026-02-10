import './App.css'
import { Login } from './components/Login'
import { Register } from './components/Register'
import { Home } from './components/Home'
import { Routes, Route, Navigate } from 'react-router-dom'
import { useAuth } from './context/authContext'

function App() {
  const { currentUser, loading } = useAuth();

  if (loading) return <div>Loading...</div>;

  const ProtectedRoute = ({ children }: { children: JSX.Element }) => {
    return currentUser ? children : <Navigate to="/login" />;
  };

  return (
    <Routes>
      <Route path="/login" element={!currentUser ? <Login /> : <Navigate to="/" />} />
      <Route path="/register" element={!currentUser ? <Register /> : <Navigate to="/" />} />
      <Route
        path="/"
        element={
          <ProtectedRoute>
            <Home />
          </ProtectedRoute>
        }
      />
    </Routes>
  )
}

export default App
