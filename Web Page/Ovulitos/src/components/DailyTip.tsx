import { useState, useEffect } from 'react';
import { Box, Paper, Typography, Skeleton, Fade } from '@mui/material';
import { Lightbulb } from '@mui/icons-material';
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
          p: 3, 
          borderRadius: 5, 
          backgroundColor: 'rgba(255, 253, 240, 0.95)',
          backdropFilter: 'blur(20px)',
          border: '2px solid rgba(155, 83, 84, 0.2)',
          boxShadow: '0 15px 35px rgba(105, 57, 58, 0.04)',
          display: 'flex',
          flexDirection: 'column',
          gap: 2.5,
          position: 'relative',
          overflow: 'hidden',
          width: '100%',
          boxSizing: 'border-box'
        }}
      >
        {/* Contador de días para la regla y estatus */}
        {cycleDay && (
          <Box sx={{ 
            display: 'flex', 
            alignItems: 'center', 
            justifyContent: 'space-between',
            p: 2,
            borderRadius: 4,
            backgroundColor: 'rgba(155, 83, 84, 0.05)',
            border: '1px solid rgba(155, 83, 84, 0.1)'
          }}>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
              <Box sx={{ 
                backgroundColor: 'rgba(155, 83, 84, 0.12)', 
                p: 1,
                borderRadius: 2.5,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center'
              }}>
                <Lightbulb sx={{ color: '#9B5354', fontSize: '1.2rem' }} />
              </Box>
              <Typography sx={{ fontWeight: 900, color: '#69393A', fontSize: '0.75rem', letterSpacing: 0.5 }}>
                DÍA {cycleDay} DEL CICLO
              </Typography>
            </Box>
            
            <Box sx={{ 
              display: 'flex', 
              alignItems: 'center', 
              gap: 1
            }}>
              <Typography variant="h4" sx={{ fontWeight: 950, color: '#9B5354', fontSize: '1.75rem', lineHeight: 1 }}>
                {Math.max(0, avgCycle - cycleDay + 1)}
              </Typography>
              <Typography variant="caption" sx={{ fontWeight: 800, color: '#9B5354', textAlign: 'left', fontSize: '0.58rem', textTransform: 'uppercase', lineHeight: 1.1 }}>
                días para<br/>la regla
              </Typography>
            </Box>
          </Box>
        )}

        {/* Contenido del Consejo */}
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1.5 }}>
          <Typography variant="overline" sx={{ fontWeight: 900, color: '#9B5354', letterSpacing: 1.5, fontSize: '0.65rem' }}>
            CONSEJO DEL DÍA
          </Typography>
          <Typography variant="h6" sx={{ 
            fontWeight: 800, 
            color: '#69393A', 
            lineHeight: 1.3, 
            fontSize: '1.25rem',
            fontFamily: "'Poppins', sans-serif"
          }}>
            {tip?.titulo || "Cargando consejo..."}
          </Typography>
          <Typography variant="body2" sx={{ 
            color: '#69393A', 
            opacity: 0.85, 
            lineHeight: 1.6, 
            fontSize: '0.85rem',
            letterSpacing: 0.1
          }}>
            {tip?.contenido || "Escucha a tu cuerpo y mantente hidratada hoy."}
          </Typography>
        </Box>
      </Paper>
    </Fade>
  );
};

export default DailyTip;
