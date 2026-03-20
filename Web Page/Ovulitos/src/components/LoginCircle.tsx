import React, { useState } from 'react';
import { Box, TextField, Button, Typography, IconButton, InputAdornment } from '@mui/material';
import { Google, Visibility, VisibilityOff } from '@mui/icons-material';
import { motion, AnimatePresence } from 'framer-motion';
// Importamos las funciones de tu compañero
import { 
  doSignInWithEmailAndPassword, 
  doCreateUserWithEmailAndPassword, 
  doSignInWithGoogle 
} from '../firebase/auth.js'; 

const LoginCircle = () => {
  const [isRegistering, setIsRegistering] = useState(false);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    try {
      if (isRegistering) {
        // Registro nuevo usuario
        await doCreateUserWithEmailAndPassword(email, password);
      } else {
        // Inicio de sesión normal
        await doSignInWithEmailAndPassword(email, password);
      }
    } catch (err: any) {
      alert("Error: " + err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box sx={{ 
      display: 'flex', justifyContent: 'center', alignItems: 'center', 
      width: '100vw', height: '100vh', 
      background: 'linear-gradient(135deg, #FFF8C9 0%, #F4C7C4 100%)' 
    }}>
      <motion.div
        initial={{ scale: 0.8, opacity: 0 }}
        animate={{ scale: 1, opacity: 1 }}
        transition={{ duration: 0.5 }}
      >
        <Box sx={{ 
          width: 450, height: 450, borderRadius: '50%', backgroundColor: 'white',
          boxShadow: '0px 15px 40px rgba(105, 57, 58, 0.2)', display: 'flex',
          flexDirection: 'column', justifyContent: 'center', alignItems: 'center', p: 4
        }}>
          
          <Typography variant="h4" sx={{ color: '#69393A', fontWeight: 'bold', mb: 2 }}>
            {isRegistering ? 'Crear Cuenta' : 'Ovulitos'}
          </Typography>

          <Box component="form" onSubmit={handleSubmit} sx={{ width: '100%', maxWidth: 260, display: 'flex', flexDirection: 'column', gap: 2 }}>
            <TextField 
              label="Email" size="small" fullWidth required
              value={email} onChange={(e) => setEmail(e.target.value)} 
            />
            <TextField 
              label="Contraseña" type={showPassword ? 'text' : 'password'} size="small" fullWidth required
              value={password} onChange={(e) => setPassword(e.target.value)}
              InputProps={{
                endAdornment: (
                  <InputAdornment position="end">
                    <IconButton onClick={() => setShowPassword(!showPassword)}>
                      {showPassword ? <VisibilityOff /> : <Visibility />}
                    </IconButton>
                  </InputAdornment>
                ),
              }}
            />
            
            <Button 
              type="submit" variant="contained" disabled={loading}
              sx={{ backgroundColor: '#9B5354', textTransform: 'none', fontWeight: 'bold' }}
            >
              {loading ? 'Procesando...' : (isRegistering ? 'Registrarse' : 'Iniciar Sesión')}
            </Button>

            {/* BOTÓN PARA CAMBIAR ENTRE LOGIN Y REGISTRO */}
            <Button 
              variant="text" 
              onClick={() => setIsRegistering(!isRegistering)}
              sx={{ color: '#69393A', textTransform: 'none', fontSize: '0.8rem', mt: -1 }}
            >
              {isRegistering ? '¿Ya tienes cuenta? Entra aquí' : '¿No tienes cuenta? Regístrate aquí'}
            </Button>
          </Box>

          <Box sx={{ display: 'flex', alignItems: 'center', my: 2, width: '70%' }}>
            <Box sx={{ flex: 1, height: '1px', backgroundColor: '#F4C7C4' }} />
            <Typography variant="caption" sx={{ mx: 1, color: '#69393A' }}>o</Typography>
            <Box sx={{ flex: 1, height: '1px', backgroundColor: '#F4C7C4' }} />
          </Box>

          <Button 
            variant="outlined" startIcon={<Google />} onClick={doSignInWithGoogle}
            sx={{ color: '#69393A', borderColor: '#F4C7C4', textTransform: 'none' }}
          >
            Continuar con Google
          </Button>

        </Box>
      </motion.div>
    </Box>
  );
};

export default LoginCircle;