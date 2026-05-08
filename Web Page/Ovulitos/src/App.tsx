import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import LoginCircle from './components/LoginCircle';
import Dashboard from './components/Dashboard';
import { useAuth } from './context/authContext';
import { doSignOut } from './firebase/auth';
import { initializeConsejos } from './utils/firebaseUtils';
import { useEffect } from 'react';


// Crear tema con la paleta de colores
const theme = createTheme({
  palette: {
    primary: {
      main: '#9B5354',
    },
    secondary: {
      main: '#69393A',
    },
    background: {
      default: '#FFF8C9',
    },
  },
  typography: {
    fontFamily: '"Poppins", "Roboto", "Helvetica", "Arial", sans-serif',
  },
});

function App() {
  const { currentUser } = useAuth();
  const isGuest = localStorage.getItem('isGuest') === 'true';

  useEffect(() => {
    initializeConsejos();
  }, []);

  const handleLogout = async () => {
    try {
      if (isGuest) {
        localStorage.removeItem('isGuest');
        window.location.reload();
      } else {
        await doSignOut();
      }
    } catch (error) {
      console.error("Error al cerrar sesión:", error);
    }
  };

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      {(!currentUser && !isGuest) ? (
        <LoginCircle onLogin={() => {}} />
      ) : (
        <Dashboard onLogout={handleLogout} />
      )}
    </ThemeProvider>
  );

}

export default App;