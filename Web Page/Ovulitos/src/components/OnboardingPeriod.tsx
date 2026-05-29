import { useState } from 'react';
import { Box, Paper, Typography, Button, Fade } from '@mui/material';
import { StaticDatePicker } from '@mui/x-date-pickers/StaticDatePicker';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { db } from '../firebase/firebase';
import { doc, setDoc } from 'firebase/firestore';
import dayjs, { Dayjs } from 'dayjs';

interface OnboardingPeriodProps {
  currentUser: any;
  onComplete: () => void;
}

const OnboardingPeriod: React.FC<OnboardingPeriodProps> = ({ currentUser, onComplete }) => {
  const [selectedDate, setSelectedDate] = useState<Dayjs | null>(dayjs());
  const [loading, setLoading] = useState(false);

  const handleSave = async () => {
    if (!selectedDate || !currentUser?.email) return;

    setLoading(true);
    try {
      const dateStr = selectedDate.format('YYYY-MM-DD');
      const docRef = doc(db, 'usuarios', currentUser.email, 'Datos', dateStr);
      
      await setDoc(docRef, {
        fecha: dateStr,
        tipo: 'period_start',
        timestamp: new Date()
      });

      // Calcular predicciones iniciales y guardarlas en usuarios/{email}
      const userRef = doc(db, 'usuarios', currentUser.email);
      const nextRegla = selectedDate.add(28, 'day').format('YYYY-MM-DD');
      const nextRegla2 = selectedDate.add(56, 'day').format('YYYY-MM-DD');
      
      await setDoc(userRef, {
        cicloMedio: 28,
        proximaReglaPrevista: nextRegla,
        segundaReglaPrevista: nextRegla2,
        ultimaActualización: new Date().toISOString()
      }, { merge: true });

      onComplete();
    } catch (error) {
      console.error("Error al guardar el periodo inicial:", error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Fade in={true}>
      <Box sx={{ 
        position: 'fixed', 
        top: 0, left: 0, right: 0, bottom: 0, 
        backgroundColor: 'rgba(105, 57, 58, 0.4)', 
        backdropFilter: 'blur(10px)',
        zIndex: 2000,
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        p: 2
      }}>
        <Paper sx={{ 
          maxWidth: 500, 
          width: '100%', 
          p: 4, 
          borderRadius: 6, 
          textAlign: 'center',
          boxShadow: '0 20px 60px rgba(0,0,0,0.2)'
        }}>
          <Typography variant="h4" sx={{ fontWeight: 900, color: '#69393A', mb: 2 }}>
            ¡Hola! 👋
          </Typography>
          <Typography variant="body1" sx={{ color: '#9B5354', mb: 4, fontWeight: 500 }}>
            Para poder darte consejos personalizados y calcular tu ciclo, por favor marca el **primer día** de tu última regla:
          </Typography>

          <LocalizationProvider dateAdapter={AdapterDayjs}>
            <StaticDatePicker
              displayStaticWrapperAs="desktop"
              value={selectedDate}
              onChange={(newValue) => setSelectedDate(newValue)}
              sx={{
                mb: 4,
                backgroundColor: 'transparent',
                '& .MuiPickersLayout-root': {
                  backgroundColor: 'transparent',
                }
              }}
            />
          </LocalizationProvider>

          <Button
            variant="contained"
            fullWidth
            onClick={handleSave}
            disabled={loading}
            sx={{
              py: 2,
              borderRadius: 3,
              backgroundColor: '#69393A',
              fontWeight: 800,
              fontSize: '1rem',
              '&:hover': { backgroundColor: '#9B5354' }
            }}
          >
            {loading ? 'Guardando...' : 'Confirmar y Empezar'}
          </Button>
        </Paper>
      </Box>
    </Fade>
  );
};

export default OnboardingPeriod;
