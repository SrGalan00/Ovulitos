import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import LoginCircle from './components/LoginCircle';
import Dashboard from './components/Dashboard';


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
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Dashboard />
    </ThemeProvider>
  );
}

export default App;