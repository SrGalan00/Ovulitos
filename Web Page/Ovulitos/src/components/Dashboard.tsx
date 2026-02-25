import React from 'react';
import { 
  Box, 
  Typography, 
  Button, 
  AppBar, 
  Toolbar, 
  Container 
} from '@mui/material';
import { ExitToApp } from '@mui/icons-material';

interface DashboardProps {
  onLogout: () => void;
}

const Dashboard: React.FC<DashboardProps> = ({ onLogout }) => {
  const colors = {
    primary: '#FFF8C9',
    secondary: '#69393A',
    accent: '#9B5354',
    lightAccent: '#E38E91',
    softPink: '#F4C7C4',
  };

  return (
    <Box sx={{ 
      minHeight: '100vh', 
      width: '100vw', 
      backgroundColor: colors.primary,
      display: 'flex',
      flexDirection: 'column',
      m: 0,
      p: 0
    }}>
      <AppBar 
        position="static" 
        sx={{ 
          backgroundColor: colors.secondary,
          boxShadow: '0px 4px 20px rgba(105, 57, 58, 0.2)',
        }}
      >
        <Toolbar sx={{ justifyContent: 'space-between' }}>
          <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
            Ovulitos
          </Typography>
          <Button
            color="inherit"
            onClick={onLogout}
            startIcon={<ExitToApp />}
            sx={{ '&:hover': { backgroundColor: colors.accent } }}
          >
            Cerrar Sesión
          </Button>
        </Toolbar>
      </AppBar>

      <Container 
        maxWidth={false} 
        disableGutters 
        sx={{ 
          flexGrow: 1, 
          display: 'flex', 
          flexDirection: 'column',
          p: { xs: 2, md: 4 } // Margen responsivo para el cuadro blanco
        }}
      >
        <Box
          sx={{
            backgroundColor: 'white',
            borderRadius: 4,
            p: 4,
            boxShadow: '0px 10px 30px rgba(105, 57, 58, 0.1)',
            flexGrow: 1,
          }}
        >
          <Typography variant="h4" gutterBottom sx={{ color: colors.secondary, fontWeight: 'bold' }}>
            ¡Bienvenido al Proyecto Ovulitos!
          </Typography>
          
          <Typography variant="body1" sx={{ color: colors.secondary, mb: 3 }}>
            Esta es la pantalla principal del proyecto Ovulitos. Aqui puedes registrarte y obtener informacion sobre nuestra aplicacion.
          </Typography>

          <Box sx={{ 
            mt: 4, 
            p: 5, 
            border: `2px dashed ${colors.softPink}`, 
            borderRadius: 2, 
            textAlign: 'center' 
          }}>
            <Typography variant="subtitle1" sx={{ color: colors.accent }}>
              
            </Typography>
          </Box>
        </Box>
      </Container>
    </Box>
  );
};

export default Dashboard;