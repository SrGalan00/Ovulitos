import React, { useState } from 'react';
import { 
  Box, 
  TextField, 
  Button, 
  Typography, 
  IconButton,
  InputAdornment
} from '@mui/material';
import { Google, Visibility, VisibilityOff } from '@mui/icons-material';

interface LoginProps {
  onLogin: () => void;
}

const LoginCircle: React.FC<LoginProps> = ({ onLogin }) => {
  const [email, setEmail] = useState<string>('');
  const [password, setPassword] = useState<string>('');
  const [showPassword, setShowPassword] = useState<boolean>(false);

  const colors = {
    primary: '#FFF8C9',
    secondary: '#69393A',
    accent: '#9B5354',
    lightAccent: '#E38E91',
    softPink: '#F4C7C4',
  };

  const handleEmailLogin = (e: React.FormEvent) => {
    e.preventDefault();
    onLogin();
  };

  return (
    <Box
      sx={{
        display: 'flex',
        justifyContent: 'center', // Centrado horizontal
        alignItems: 'center',     // Centrado vertical
        width: '100vw',           // Ancho total de ventana
        height: '100vh',          // Alto total de ventana
        background: `linear-gradient(135deg, ${colors.primary} 0%, ${colors.softPink} 100%)`,
        overflow: 'hidden',       // Evita scrolls accidentales
        margin: 0,
        padding: 0,
      }}
    >
      <Box
        sx={{
          width: { xs: 350, sm: 450 }, // Responsivo: más pequeño en móviles
          height: { xs: 350, sm: 450 },
          borderRadius: '50%',
          backgroundColor: 'white',
          boxShadow: '0px 15px 40px rgba(105, 57, 58, 0.2)',
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'center',
          alignItems: 'center',
          padding: 4,
          position: 'relative',
          zIndex: 2,
        }}
      >
        <Typography
          variant="h4"
          sx={{
            color: colors.secondary,
            fontWeight: 'bold',
            mb: 2,
            fontFamily: "'Poppins', sans-serif",
          }}
        >
          Ovulitos
        </Typography>

        <Box
          component="form"
          onSubmit={handleEmailLogin}
          sx={{
            width: '100%',
            maxWidth: 260,
            display: 'flex',
            flexDirection: 'column',
            gap: 2,
          }}
        >
          <TextField
            label="Email"
            variant="outlined"
            size="small"
            fullWidth
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            sx={{ '& .MuiInputLabel-root': { fontSize: '0.8rem' } }}
          />

          <TextField
            label="Contraseña"
            type={showPassword ? 'text' : 'password'}
            variant="outlined"
            size="small"
            fullWidth
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            InputProps={{
              endAdornment: (
                <InputAdornment position="end">
                  <IconButton onClick={() => setShowPassword(!showPassword)} edge="end">
                    {showPassword ? <VisibilityOff /> : <Visibility />}
                  </IconButton>
                </InputAdornment>
              ),
            }}
            sx={{ '& .MuiInputLabel-root': { fontSize: '0.8rem' } }}
          />

          <Button
            type="submit"
            variant="contained"
            sx={{
              backgroundColor: colors.accent,
              textTransform: 'none',
              fontWeight: 'bold',
              '&:hover': { backgroundColor: colors.secondary },
            }}
          >
            Iniciar Sesión
          </Button>
        </Box>

        <Box sx={{ display: 'flex', alignItems: 'center', my: 2, width: '70%' }}>
          <Box sx={{ flex: 1, height: '1px', backgroundColor: colors.softPink }} />
          <Typography variant="caption" sx={{ mx: 1, color: colors.secondary }}>o</Typography>
          <Box sx={{ flex: 1, height: '1px', backgroundColor: colors.softPink }} />
        </Box>

        <Button
          variant="outlined"
          startIcon={<Google />}
          onClick={onLogin}
          sx={{
            color: colors.secondary,
            borderColor: colors.softPink,
            textTransform: 'none',
            fontSize: '0.8rem',
            '&:hover': { borderColor: colors.accent }
          }}
        >
          Continuar con Google
        </Button>

        <Typography variant="caption" sx={{ mt: 2, color: colors.secondary, opacity: 0.6 }}>
          Por favor, inicia sesión para continuar
        </Typography>
      </Box>
    </Box>
  );
};

export default LoginCircle;