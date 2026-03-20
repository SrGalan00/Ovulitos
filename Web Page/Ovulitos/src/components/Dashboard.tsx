import React from 'react';
import { Box, Typography, Button, AppBar, Toolbar, Container } from '@mui/material';
import { ExitToApp } from '@mui/icons-material';
import { doSignOut } from '../firebase/auth.js';

const Dashboard = () => {
  return (
    <Box sx={{ minHeight: '100vh', width: '100vw', backgroundColor: '#FFF8C9', display: 'flex', flexDirection: 'column' }}>
      <AppBar position="static" sx={{ backgroundColor: '#69393A' }}>
        <Toolbar sx={{ justifyContent: 'space-between' }}>
          <Typography variant="h6" sx={{ fontWeight: 'bold' }}>Ovulitos Dashboard</Typography>
          <Button color="inherit" onClick={() => doSignOut()} startIcon={<ExitToApp />}>
  Cerrar Sesión
</Button>
        </Toolbar>
      </AppBar>

      <Container maxWidth={false} sx={{ mt: 4, flexGrow: 1, display: 'flex', justifyContent: 'center' }}>
        <Box sx={{ backgroundColor: 'white', borderRadius: 4, p: 4, width: '100%', maxWidth: '1200px', boxShadow: '0px 10px 30px rgba(0,0,0,0.05)' }}>
          <Typography variant="h4" sx={{ color: '#69393A', fontWeight: 'bold', mb: 2 }}>
            ¡Bienvenido al Proyecto Ovulitos!
          </Typography>
          <Typography variant="body1" sx={{ color: '#69393A' }}>
            Este es el dashboard principal de la aplicación. Aquí puedes empezar a construir tu interfaz.
          </Typography>
          
          <Box sx={{ mt: 4, p: 5, border: '2px dashed #F4C7C4', borderRadius: 2, textAlign: 'center' }}>
            <Typography sx={{ color: '#9B5354' }}>Área de trabajo lista para nuevos componentes</Typography>
          </Box>
        </Box>
      </Container>
    </Box>
  );
};

export default Dashboard;