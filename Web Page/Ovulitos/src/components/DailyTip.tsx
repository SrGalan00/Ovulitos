import React, { useState, useEffect } from 'react';
import { Box, Paper, Typography, Skeleton, Fade } from '@mui/material';
import { Lightbulb, Info } from '@mui/icons-material';
import { db } from '../firebase/firebase';
import { collection, query, where, orderBy, limit, getDocs, doc, getDoc } from 'firebase/firestore';
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
  const [avgCycle, setAvgCycle] = useState<number>(28);

  useEffect(() => {
    const fetchCycleData = async () => {
      const isGuest = localStorage.getItem('isGuest') === 'true';
      if (!currentUser?.email && !isGuest) return;

      if (isGuest) {
        setTip({
          dia: 0,
          titulo: "¡Bienvenida!",
          contenido: "Modo invitado activado. Registra tu regla para ver consejos personalizados."
        });
        setLoading(false);
        return;
      }

      try {
        // 1. Obtener metadatos del perfil del usuario (ciclo medio personalizado)
        const userRef = doc(db, 'usuarios', currentUser.email);
        const userSnap = await getDoc(userRef);
        let userAvgCycle = 28;
        if (userSnap.exists()) {
          userAvgCycle = userSnap.data().cicloMedio || 28;
        }
        setAvgCycle(userAvgCycle);

        // 2. Encontrar el inicio de regla más reciente
        const datosRef = collection(db, 'usuarios', currentUser.email, 'Datos');
        // Intentamos buscar por el nuevo campo 'tipos' o el antiguo 'tipo'
        const q = query(
          datosRef, 
          orderBy('fecha', 'desc'), 
          limit(20) // Traemos los últimos para filtrar manualmente y asegurar compatibilidad
        );
        
        const querySnapshot = await getDocs(q);
        
        const lastStartDoc = querySnapshot.docs.find(doc => {
          const data = doc.data();
          return data.tipo === 'period_start' || (Array.isArray(data.tipos) && data.tipos.includes('period_start'));
        });

        if (lastStartDoc) {
          const lastStart = dayjs(lastStartDoc.data().fecha);
          const today = dayjs();
          const diff = today.diff(lastStart, 'day') + 1;
          
          // El día del ciclo se normaliza según el ciclo medio del usuario
          const normalizedDay = diff > 0 && diff <= userAvgCycle + 7 ? diff : null;
          setCycleDay(normalizedDay);

          if (normalizedDay) {
            // 3. Buscar el consejo para ese día en la colección 'consejos'
            // Los consejos suelen estar basados en un ciclo estándar de 28, 
            // así que para el consejo usamos el día normalizado a 28 si el ciclo es muy largo
            const tipDay = normalizedDay > 28 ? 28 : normalizedDay;
            const consejosRef = collection(db, 'consejos');
            const cq = query(consejosRef, where('dia', '==', tipDay));
            const cSnapshot = await getDocs(cq);

            if (!cSnapshot.empty) {
              setTip(cSnapshot.docs[0].data() as Tip);
            } else {
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
          p: 2, // Aumentado de 1.5
          mb: 1.5, // Aumentado de 1
          borderRadius: 4,
          backgroundColor: '#FFFDF0',
          border: '2px solid #9B5354',
          display: 'flex',
          alignItems: 'center',
          gap: 2.5, // Aumentado de 2
          position: 'relative',
          overflow: 'hidden',
          '&::before': {
            content: '""',
            position: 'absolute',
            top: 0,
            left: 0,
            width: '5px', // Aumentado de 4
            height: '100%',
            backgroundColor: '#9B5354'
          }
        }}
      >
        <Box sx={{ 
          backgroundColor: 'rgba(155, 83, 84, 0.1)', 
          p: 1.5, // Aumentado de 1
          borderRadius: 2.5, // Aumentado de 2
          display: 'flex'
        }}>
          <Lightbulb sx={{ color: '#9B5354', fontSize: '1.8rem' }} /> {/* Aumentado de 1.5rem */}
        </Box>
        <Box sx={{ flex: 1 }}>
          <Typography variant="overline" sx={{ fontWeight: 900, color: '#9B5354', letterSpacing: 1.5, fontSize: '0.7rem' }}>
            CONSEJO DEL DÍA {cycleDay ? `• DÍA ${cycleDay}` : ''}
          </Typography>
          <Typography variant="h6" sx={{ fontWeight: 800, color: '#69393A', mb: 0.5, lineHeight: 1.2 }}>
            {tip?.titulo || "Cargando consejo..."}
          </Typography>
          <Typography variant="body2" sx={{ color: '#69393A', opacity: 0.8, lineHeight: 1.4, fontSize: '0.85rem' }}>
            {tip?.contenido || "Escucha a tu cuerpo y mantente hidratada hoy."}
          </Typography>
        </Box>

        {/* Contador de días para la regla */}
        {cycleDay && (
          <Box sx={{ 
            ml: 'auto', 
            display: 'flex', 
            flexDirection: 'column', 
            alignItems: 'center',
            justifyContent: 'center',
            minWidth: 90, // Aumentado de 80
            p: 1.5, // Aumentado de 1
            borderRadius: 3,
            backgroundColor: 'rgba(105, 57, 58, 0.05)',
            border: '1px dashed rgba(105, 57, 58, 0.2)'
          }}>
            <Typography variant="h4" sx={{ fontWeight: 900, color: '#69393A', lineHeight: 1 }}>
              {Math.max(0, avgCycle - cycleDay)}
            </Typography>
            <Typography variant="caption" sx={{ fontWeight: 800, color: '#9B5354', textAlign: 'center', mt: 0.5, fontSize: '0.65rem', lineHeight: 1 }}>
              DÍAS PARA<br/>LA REGLA
            </Typography>
          </Box>
        )}
      </Paper>
    </Fade>
  );
};

export default DailyTip;
