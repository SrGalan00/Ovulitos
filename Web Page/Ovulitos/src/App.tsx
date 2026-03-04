import { ThemeProvider, createTheme } from "@mui/material/styles";
import CssBaseline from "@mui/material/CssBaseline";
import { AnimatePresence, motion } from "framer-motion";
// Usamos la ruta exacta de tu estructura de carpetas
import { AuthProvider, useAuth } from "./context/authContext/index.jsx";
import LoginCircle from "./components/LoginCircle";
import Dashboard from "./components/Dashboard";

const theme = createTheme({
  palette: {
    primary: { main: "#9B5354" },
    secondary: { main: "#69393A" },
    background: { default: "#FFF8C9" },
  },
  typography: {
    fontFamily: '"Poppins", sans-serif',
  },
});

function AppContent() {
  const { currentUser, loading } = useAuth();

  // No mostramos nada mientras Firebase determina si hay un usuario
  if (loading) return null;

  return (
    <AnimatePresence mode="wait">
      {currentUser ? (
        <motion.div
          key="dashboard"
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          exit={{ opacity: 0, y: -20 }}
          transition={{ duration: 0.5 }}
        >
          <Dashboard />
        </motion.div>
      ) : (
        <motion.div
          key="login"
          initial={{ opacity: 0, scale: 0.9 }}
          animate={{ opacity: 1, scale: 1 }}
          exit={{ opacity: 0, scale: 1.1 }}
          transition={{ duration: 0.5 }}
        >
          <LoginCircle />
        </motion.div>
      )}
    </AnimatePresence>
  );
}

export default function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <AuthProvider>
        <AppContent />
      </AuthProvider>
    </ThemeProvider>
  );
}