import React, { useState, useEffect } from 'react';
import { Box, Paper, Typography, Skeleton, Fade } from '@mui/material';
import { Lightbulb, Info } from '@mui/icons-material';
import { db } from '../firebase/firebase';
import { collection, query, where, orderBy, limit, getDocs } from 'firebase/firestore';
import dayjs from 'dayjs';

interface Tip {
  dia: number;
  titulo: string;
  contenido: string;
}

const DailyTip = ({ currentUser }: { currentUser: any }) => {
  const [tip, setTip] = useState<Tip | null>(null);
  const [loading, setLoading] = useState(true);
  const [cycleDay, setCycleDay] = useState<number | null>(null);

  useEffect(() => {
    const fetchCycleData = async () => {
      if (!currentUser?.email) return;

      try {
        // 1. Encontrar el inicio de regla más reciente
        const datosRef = collection(db, 'usuarios', currentUser.email, 'Datos');
        const q = query(
          datosRef, 
          where('tipo', '==', 'period_start'), 
          orderBy('fecha', 'desc'), 
          limit(1)
        );
        
        const querySnapshot = await getDocs(q);
        
        if (!querySnapshot.empty) {
          const lastStart = dayjs(querySnapshot.docs[0].data().fecha);
          const today = dayjs();
          const diff = today.diff(lastStart, 'day') + 1;
          
          // Suponemos un ciclo máximo de 35 días para los consejos
          const normalizedDay = diff > 0 && diff <= 35 ? diff : null;
          setCycleDay(normalizedDay);

          if (normalizedDay) {
            // 2. Buscar el consejo para ese día en la colección 'consejos'
            const consejosRef = collection(db, 'consejos');
            const cq = query(consejosRef, where('dia', '==', normalizedDay));
            const cSnapshot = await getDocs(cq);

            if (!cSnapshot.empty) {
              setTip(cSnapshot.docs[0].data() as Tip);
            } else {
              // Si no hay consejo específico, mostrar uno genérico o el día
              setTip({
                dia: normalizedDay,
                titulo: `Día ${normalizedDay} de tu ciclo`,
                contenido: "Hoy es un gran día para cuidar de ti. Mantente hidratada y escucha a tu cuerpo."
              });
            }
          }
        } else {
          setTip({
            dia: 0,
            titulo: "¡Bienvenida!",
            contenido: "Marca el inicio de tu regla en el calendario para recibir consejos personalizados sobre tu ciclo."
          });
        }
      } catch (error) {
        console.error("Error al obtener el consejo:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchCycleData();
  }, [currentUser]);

  if (loading) return <Skeleton variant="rounded" height={100} sx={{ mb: 3, borderRadius: 4 }} />;

  return (
    <Fade in={true} timeout={800}>
      <Paper
        elevation={0}
        sx={{
          p: 3,
          mb: 3,
          borderRadius: 5,
          backgroundColor: '#FFFDF0',
          border: '2px solid #9B5354',
          display: 'flex',
          alignItems: 'flex-start',
          gap: 2,
          position: 'relative',
          overflow: 'hidden',
          '&::before': {
            content: '""',
            position: 'absolute',
            top: 0,
            left: 0,
            width: '6px',
            height: '100%',
            backgroundColor: '#9B5354'
          }
        }}
      >
        <Box sx={{ 
          backgroundColor: 'rgba(155, 83, 84, 0.1)', 
          p: 1.5, 
          borderRadius: 3,
          display: 'flex'
        }}>
          <Lightbulb sx={{ color: '#9B5354', fontSize: '2rem' }} />
        </Box>
        <Box>
          <Typography variant="overline" sx={{ fontWeight: 900, color: '#9B5354', letterSpacing: 2 }}>
            CONSEJO DEL DÍA {cycleDay ? `• DÍA ${cycleDay}` : ''}
          </Typography>
          <Typography variant="h6" sx={{ fontWeight: 800, color: '#69393A', mb: 0.5 }}>
            {tip?.titulo}
          </Typography>
          <Typography variant="body2" sx={{ color: '#69393A', opacity: 0.8, lineHeight: 1.6 }}>
            {tip?.contenido}
          </Typography>
        </Box>
      </Paper>
    </Fade>
  );
};

export default DailyTip;
