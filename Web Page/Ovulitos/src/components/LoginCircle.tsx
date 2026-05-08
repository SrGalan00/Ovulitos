import React, { useState } from 'react';
import { 
  Box, 
  TextField, 
  Button, 
  Typography, 
  IconButton,
  InputAdornment,
  Link
} from '@mui/material';
import { Google, Visibility, VisibilityOff } from '@mui/icons-material';
import { 
  doSignInWithEmailAndPassword, 
  doSignInWithGoogle,
  doCreateUserWithEmailAndPassword
} from '../firebase/auth';
import SpermBackground from './SpermBackground';

interface LoginProps {
  onLogin: () => void;
}

const LoginCircle: React.FC<LoginProps> = ({ onLogin }) => {
  const [email, setEmail] = useState<string>('');
  const [password, setPassword] = useState<string>('');
  const [name, setName] = useState<string>('');
  const [showPassword, setShowPassword] = useState<boolean>(false);
  const [isRegistering, setIsRegistering] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  const colors = {
    primary: '#FFF8C9',
    secondary: '#69393A',
    accent: '#9B5354',
    lightAccent: '#E38E91',
    softPink: '#F4C7C4',
  };

  const handleAuthAction = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    try {
      if (isRegistering) {
        await doCreateUserWithEmailAndPassword(email, password, name);
      } else {
        await doSignInWithEmailAndPassword(email, password);
      }
    } catch (err: any) {
      setError(isRegistering ? "Error al crear la cuenta." : "Credenciales incorrectas o error de conexión.");
      console.error(err);
    }
  };

  const handleGoogleLogin = async () => {
    setError(null);
    try {
      await doSignInWithGoogle();
    } catch (err: any) {
      setError("Error al iniciar sesión con Google.");
      console.error(err);
    }
  };

  return (
    <Box
      sx={{
        position: 'relative',
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'center',
        alignItems: 'center',
        width: '100vw',
        height: '100vh',
        background: `linear-gradient(135deg, ${colors.primary} 0%, ${colors.softPink} 100%)`,
        overflow: 'hidden',
        margin: 0,
        padding: 0,
        gap: 4
      }}
    >
      <SpermBackground />
      <Typography
        variant="h2"
        sx={{
          color: colors.secondary,
          fontWeight: 'bold',
          fontFamily: "'Poppins', sans-serif",
          textShadow: '2px 2px 4px rgba(0,0,0,0.1)',
          zIndex: 3,
          fontSize: { xs: '2.5rem', sm: '3.75rem' }
        }}
      >
        Ovulitos
      </Typography>

      <Box
        sx={{
          position: 'relative',
          width: { xs: '95vw', sm: 550, md: 650 },
          height: { xs: '95vw', sm: 550, md: 650 },
          maxWidth: { xs: 400, sm: 'none' }, // Limit for very small screens
          maxHeight: { xs: 400, sm: 'none' },
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
        }}
      >
        {/* Outer Ring */}
        <Box
          sx={{
            position: 'absolute',
            width: '100%',
            height: '100%',
            borderRadius: '50%',
            backgroundColor: colors.softPink,
            opacity: 0.8,
            zIndex: 0
          }}
        />

        {/* Inner Circle (The "Ovulito") */}
        <Box
          sx={{
            position: 'relative',
            width: '90%',
            height: '90%',
            borderRadius: '50%',
            backgroundColor: colors.lightAccent,
            boxShadow: 'inset -10px -10px 20px rgba(0,0,0,0.1), 0px 15px 40px rgba(105, 57, 58, 0.3)',
            display: 'flex',
            flexDirection: 'column',
            justifyContent: 'center',
            alignItems: 'center',
            padding: { xs: 3, sm: 6 },
            zIndex: 1,
            overflow: 'hidden'
          }}
        >
          {/* Shine Effect */}
          <Box
            sx={{
              position: 'absolute',
              top: '10%',
              right: '15%',
              width: '18%',
              height: '12%',
              backgroundColor: 'white',
              borderRadius: '50%',
              opacity: 0.5,
              transform: 'rotate(-45deg)',
              filter: 'blur(4px)',
              zIndex: 0
            }}
          />

          <Box
            component="form"
            onSubmit={handleAuthAction}
            sx={{
              width: '100%',
              maxWidth: { xs: 260, sm: 300 },
              display: 'flex',
              flexDirection: 'column',
              gap: { xs: 1, sm: 2 },
              zIndex: 2
            }}
          >
            <Typography 
              variant="h5" 
              sx={{ 
                color: 'white', 
                textAlign: 'center', 
                fontWeight: 'bold',
                mb: 1
              }}
            >
              {isRegistering ? 'Crear Cuenta' : 'Bienvenido'}
            </Typography>

            {isRegistering && (
              <TextField
                placeholder="Nombre"
                variant="outlined"
                fullWidth
                value={name}
                onChange={(e) => setName(e.target.value)}
                sx={{ 
                  backgroundColor: 'white',
                  borderRadius: 2,
                  '& .MuiOutlinedInput-root': {
                    '& fieldset': { border: 'none' },
                  }
                }}
              />
            )}

            <TextField
              placeholder="Email"
              variant="outlined"
              fullWidth
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              sx={{ 
                backgroundColor: 'white',
                borderRadius: 2,
                '& .MuiOutlinedInput-root': {
                  '& fieldset': { border: 'none' },
                }
              }}
            />

            <TextField
              placeholder="Contraseña"
              type={showPassword ? 'text' : 'password'}
              variant="outlined"
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
              sx={{ 
                backgroundColor: 'white',
                borderRadius: 2,
                '& .MuiOutlinedInput-root': {
                  '& fieldset': { border: 'none' },
                }
              }}
            />

            <Button
              type="submit"
              variant="contained"
              sx={{
                backgroundColor: colors.secondary,
                color: 'white',
                textTransform: 'none',
                fontWeight: 'bold',
                py: 1.5,
                borderRadius: 2,
                '&:hover': { backgroundColor: colors.accent },
              }}
            >
              {isRegistering ? 'Registrarse' : 'Iniciar Sesión'}
            </Button>

            {error && (
              <Typography variant="caption" sx={{ color: colors.secondary, textAlign: 'center', fontWeight: 'bold' }}>
                {error}
              </Typography>
            )}
          </Box>

          <Box sx={{ display: 'flex', alignItems: 'center', my: { xs: 0.5, sm: 2 }, width: '70%', zIndex: 2 }}>
            <Box sx={{ flex: 1, height: '1px', backgroundColor: 'rgba(255,255,255,0.5)' }} />
            <Typography variant="caption" sx={{ mx: 1, color: 'white', fontSize: { xs: '0.6rem', sm: '0.75rem' } }}>o</Typography>
            <Box sx={{ flex: 1, height: '1px', backgroundColor: 'rgba(255,255,255,0.5)' }} />
          </Box>

          <Button
            variant="outlined"
            startIcon={<Google />}
            onClick={handleGoogleLogin}
            sx={{
              color: 'white',
              borderColor: 'white',
              textTransform: 'none',
              borderRadius: 2,
              zIndex: 2,
              py: { xs: 0.5, sm: 1 },
              fontSize: { xs: '0.75rem', sm: '0.875rem' },
              '&:hover': { borderColor: colors.primary, backgroundColor: 'rgba(255,255,255,0.1)' }
            }}
          >
            Google
          </Button>

          <Typography variant="caption" sx={{ mt: { xs: 1, sm: 3 }, color: 'white', zIndex: 2, fontSize: { xs: '0.65rem', sm: '0.75rem' } }}>
            {isRegistering ? '¿Ya tienes cuenta?' : '¿No tienes cuenta?'}
            <Link
              component="button"
              variant="caption"
              onClick={() => setIsRegistering(!isRegistering)}
              sx={{ 
                ml: 1, 
                color: colors.secondary, 
                fontWeight: 'bold', 
                textDecoration: 'underline',
                cursor: 'pointer',
                fontSize: { xs: '0.65rem', sm: '0.75rem' }
              }}
            >
              {isRegistering ? 'Inicia Sesión' : 'Regístrate'}
            </Link>
          </Typography>

          <Button
            onClick={() => {
              localStorage.setItem('isGuest', 'true');
              window.location.reload();
            }}
            sx={{
              mt: { xs: 1, sm: 2 },
              color: colors.secondary,
              textTransform: 'none',
              fontWeight: 'bold',
              fontSize: { xs: '0.75rem', sm: '0.85rem' },
              zIndex: 2,
              textDecoration: 'underline',
              '&:hover': { color: colors.accent, backgroundColor: 'transparent' }
            }}
          >
            Continuar como Invitado
          </Button>
        </Box>
      </Box>
    </Box>
  );
};

export default LoginCircle;