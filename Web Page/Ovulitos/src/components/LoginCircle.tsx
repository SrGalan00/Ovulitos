import React, { useState } from 'react';
import { Box, TextField, Button, Typography, IconButton, InputAdornment } from '@mui/material';
import { Google, Visibility, VisibilityOff } from '@mui/icons-material';
import { doSignInWithEmailAndPassword, doSignInWithGoogle } from '../firebase/auth.js';

const LoginCircle = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [isLoggingIn, setIsLoggingIn] = useState(false);

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!isLoggingIn) {
      setIsLoggingIn(true);
      try {
        await doSignInWithEmailAndPassword(email, password);
      } catch (err: any) {
        alert("Error: " + err.message);
        setIsLoggingIn(false);
      }
    }
  };

  return (
    <Box sx={{ 
      display: 'flex', justifyContent: 'center', alignItems: 'center', 
      minHeight: '100vh', width: '100vw',
      background: 'linear-gradient(135deg, #FFF8C9 0%, #F4C7C4 100%)' 
    }}>
      <Box sx={{ 
        width: 450, height: 450, borderRadius: '50%', backgroundColor: 'white',
        display: 'flex', flexDirection: 'column', justifyContent: 'center', 
        alignItems: 'center', p: 4, boxShadow: '0px 10px 30px rgba(105, 57, 58, 0.2)' 
      }}>
        <Typography variant="h4" sx={{ color: '#69393A', fontWeight: 'bold', mb: 3 }}>Ovulitos</Typography>
        
        <Box component="form" onSubmit={handleLogin} sx={{ width: '100%', maxWidth: 260, display: 'flex', flexDirection: 'column', gap: 2 }}>
          <TextField label="Email" size="small" fullWidth value={email} onChange={(e) => setEmail(e.target.value)} required />
          <TextField 
            label="Contraseña" type={showPassword ? 'text' : 'password'} size="small" fullWidth 
            value={password} onChange={(e) => setPassword(e.target.value)} required
            InputProps={{
              endAdornment: (
                <InputAdornment position="end">
                  <IconButton onClick={() => setShowPassword(!showPassword)} edge="end">
                    {showPassword ? <VisibilityOff /> : <Visibility />}
                  </IconButton>
                </InputAdornment>
              ),
            }}
          />
          <Button type="submit" variant="contained" disabled={isLoggingIn} sx={{ backgroundColor: '#9B5354', mt: 1 }}>
            {isLoggingIn ? 'Entrando...' : 'Iniciar Sesión'}
          </Button>
        </Box>

        <Box sx={{ display: 'flex', alignItems: 'center', my: 2, width: '70%' }}>
          <Box sx={{ flex: 1, height: '1px', backgroundColor: '#F4C7C4' }} />
          <Typography variant="caption" sx={{ mx: 1, color: '#69393A' }}>o</Typography>
          <Box sx={{ flex: 1, height: '1px', backgroundColor: '#F4C7C4' }} />
        </Box>

        <Button 
          variant="outlined" startIcon={<Google />} onClick={doSignInWithGoogle} 
          sx={{ color: '#69393A', borderColor: '#9B5354', textTransform: 'none' }}
        >
          Continuar con Google
        </Button>
      </Box>
    </Box>
  );
};

export default LoginCircle;