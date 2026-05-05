import React, { useEffect, useState } from 'react';
import { 
  Box, 
  Typography, 
  Paper, 
  CircularProgress, 
  Menu, 
  MenuItem, 
  ListItemIcon,
  ListItemText,
  Divider
} from '@mui/material';
import { 
  Favorite, 
  WaterDrop, 
  CheckCircle, 
  Delete,
  Check,
  AutoAwesome
} from '@mui/icons-material';
import { LocalizationProvider, DateCalendar, PickerDay, PickerDayProps } from '@mui/x-date-pickers';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { Badge } from '@mui/material';
import dayjs, { Dayjs } from 'dayjs';
import { db } from '../firebase/firebase';
import { collection, query, onSnapshot, doc, setDoc, deleteDoc } from 'firebase/firestore';
import { useAuth } from '../context/authContext/index.jsx';

const CalendarComponent: React.FC = () => {
  const { currentUser } = useAuth();
  const [dayData, setDayData] = useState<Record<string, string[]>>({}); // { "2024-03-27": ["kiki", "period_start"] }
  const [loading, setLoading] = useState(true);
  const [predictedDate, setPredictedDate] = useState<string | null>(null);
  const [avgCycleDays, setAvgCycleDays] = useState<number>(28);
  
  // Menu state
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const [selectedDate, setSelectedDate] = useState<Dayjs | null>(null);

  useEffect(() => {
    if (!currentUser) return;

    const q = query(collection(db, 'usuarios', currentUser.email, 'Datos'));
    
    const unsubscribe = onSnapshot(q, (querySnapshot) => {
      const dataMap: Record<string, string[]> = {};
      const periodStarts: string[] = [];

      querySnapshot.forEach((doc) => {
        const data = doc.data();
        const dateKey = data.fecha || doc.id;
        
        if (dateKey.match(/^\d{4}-\d{2}-\d{2}$/)) {
          // Soporte para datos antiguos (string) y nuevos (array)
          let types: string[] = [];
          if (Array.isArray(data.tipos)) {
            types = data.tipos;
          } else if (data.tipo) {
            types = [data.tipo];
          }
          
          if (types.length > 0) {
            dataMap[dateKey] = types;
            if (types.includes('period_start')) {
              periodStarts.push(dateKey);
            }
          }
        }
      });

      setDayData(dataMap);
      calculatePrediction(periodStarts);
      setLoading(false);
    });

    return () => unsubscribe();
  }, [currentUser]);

  const calculatePrediction = async (starts: string[]) => {
    if (starts.length < 1) return;

    // Ordenar fechas
    const sortedStarts = starts.sort((a, b) => dayjs(a).unix() - dayjs(b).unix());
    
    let avgCycle = 28; // Default
    if (sortedStarts.length >= 2) {
      const intervals = [];
      for (let i = 1; i < sortedStarts.length; i++) {
        const diff = dayjs(sortedStarts[i]).diff(dayjs(sortedStarts[i-1]), 'day');
        // Filtramos intervalos irreales (menos de 15 días o más de 45 días) para mejorar la media
        if (diff >= 15 && diff <= 45) {
          intervals.push(diff);
        }
      }
      
      if (intervals.length > 0) {
        avgCycle = Math.round(intervals.reduce((a, b) => a + b, 0) / intervals.length);
      }
    }

    setAvgCycleDays(avgCycle);
    
    // Predecir desde la última fecha
    const lastStart = dayjs(sortedStarts[sortedStarts.length - 1]);
    const nextStart = lastStart.add(avgCycle, 'day');
    const nextStartDateStr = nextStart.format('YYYY-MM-DD');
    setPredictedDate(nextStartDateStr);

    // Guardar estos metadatos en el documento del usuario para persistencia
    if (currentUser?.email) {
      try {
        const userRef = doc(db, 'usuarios', currentUser.email);
        await setDoc(userRef, {
          cicloMedio: avgCycle,
          proximaReglaPrevista: nextStartDateStr,
          ultimaActualización: new Date().toISOString()
        }, { merge: true });
      } catch (error) {
        console.error("Error al guardar metadatos de ciclo:", error);
      }
    }
  };

  const handleDateClick = (event: React.MouseEvent<HTMLButtonElement>, date: Dayjs) => {
    setAnchorEl(event.currentTarget);
    setSelectedDate(date);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
    setSelectedDate(null);
  };

  const handleToggleType = async (type: string) => {
    if (!currentUser || !selectedDate) return;

    const dateStr = selectedDate.format('YYYY-MM-DD');
    const docRef = doc(db, 'usuarios', currentUser.email, 'Datos', dateStr);
    
    const currentTypes = dayData[dateStr] || [];
    let newTypes: string[] = [];

    if (currentTypes.includes(type)) {
      // Quitar tipo
      newTypes = currentTypes.filter(t => t !== type);
    } else {
      // Añadir tipo
      newTypes = [...currentTypes, type];
    }

    if (newTypes.length === 0) {
      await deleteDoc(docRef);
    } else {
      await setDoc(docRef, {
        fecha: dateStr,
        tipos: newTypes,
        tipo: newTypes[0], // Retrocompatibilidad
        updatedAt: new Date().toISOString()
      });
    }
    // No cerramos el menú para permitir múltiples selecciones
  };

  const handleClearDay = async () => {
    if (!currentUser || !selectedDate) return;
    const dateStr = selectedDate.format('YYYY-MM-DD');
    const docRef = doc(db, 'usuarios', currentUser.email, 'Datos', dateStr);
    await deleteDoc(docRef);
    handleMenuClose();
  };

  const getMarker = (types: string[], isPredicted: boolean) => {
    if (isPredicted) return '⭕';
    
    // Combinar emojis si hay múltiples
    let emojis = '';
    if (types.includes('kiki')) emojis += '❤️';
    if (types.includes('period_start')) emojis += '🩸';
    if (types.includes('period_end')) emojis += '🏁';
    
    return emojis || null;
  };

  const ServerDay = (props: PickerDayProps<Dayjs>) => {
    const { day, outsideCurrentMonth, ...other } = props;
    const dateStr = day.format('YYYY-MM-DD');
    const types = !outsideCurrentMonth ? dayData[dateStr] : null;
    const isPredicted = !outsideCurrentMonth && dateStr === predictedDate;

    return (
      <Badge
        key={day.toString()}
        overlap="circular"
        badgeContent={types ? getMarker(types, false) : (isPredicted ? getMarker([], true) : undefined)}
        sx={{
          '& .MuiBadge-badge': {
            fontSize: '0.8rem',
            right: 4,
            top: 4,
            pointerEvents: 'none'
          }
        }}
      >
        <PickerDay 
          {...other} 
          outsideCurrentMonth={outsideCurrentMonth} 
          day={day} 
          onClick={(e: any) => handleDateClick(e, day)}
          sx={{
            ...(isPredicted && {
              border: '2px dashed #9B5354',
              backgroundColor: 'rgba(155, 83, 84, 0.05)'
            })
          }}
        />
      </Badge>
    );
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', p: 4, flexGrow: 1 }}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <LocalizationProvider dateAdapter={AdapterDayjs}>
      <Paper 
        elevation={0} 
        sx={{ 
          p: { xs: 2, md: 3 }, 
          pb: { xs: 4, md: 6 },
          borderRadius: 8, 
          backgroundColor: 'rgba(255, 253, 240, 0.9)',
          backdropFilter: 'blur(25px)',
          border: '1px solid rgba(105, 57, 58, 0.2)',
          width: '100%',
          maxWidth: 1100,
          margin: '0 auto',
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          boxShadow: '0 20px 50px rgba(105, 57, 58, 0.08)'
        }}
      >
        <Typography variant="h5" align="center" sx={{ mb: 2, color: '#69393A', fontWeight: 900, letterSpacing: 2 }}>
          REGISTRO DE ACTIVIDAD
        </Typography>

        {predictedDate && (
          <Box sx={{ 
            mb: 2, 
            p: 1.5, 
            px: 3, 
            borderRadius: 4, 
            backgroundColor: 'white', 
            border: '1px solid rgba(155, 83, 84, 0.2)',
            display: 'flex',
            alignItems: 'center',
            gap: 1
          }}>
            <AutoAwesome sx={{ color: '#9B5354', fontSize: '1.2rem' }} />
            <Typography sx={{ color: '#69393A', fontWeight: 700, fontSize: '0.9rem' }}>
              Próxima regla prevista: <Box component="span" sx={{ color: '#9B5354' }}>{dayjs(predictedDate).format('D [de] MMMM')}</Box> (Ciclo medio: {avgCycleDays} días)
            </Typography>
          </Box>
        )}
        
        <DateCalendar
          slots={{
            day: ServerDay,
          }}
          sx={{
            width: '100%',
            maxWidth: 850,
            height: 'auto',
            '& .MuiPickersFadeTransitionGroup-root': {
              minHeight: 'auto',
            },
            '& .MuiDayCalendar-header': {
              justifyContent: 'space-around',
              px: 4,
              mb: 2,
              '& .MuiTypography-root': {
                fontSize: '1.1rem',
                fontWeight: 900,
                color: '#9B5354',
                width: 64
              }
            },
            '& .MuiDayCalendar-weekContainer': {
              justifyContent: 'space-around',
              px: 4,
              my: 0.2
            },
            '& .MuiDayCalendar-monthContainer': {
              width: '100%'
            },
            '& .MuiPickerDay-root': {
              fontSize: '1.2rem',
              width: 64,
              height: 64,
              borderRadius: '50%',
              fontWeight: 900,
              '&:hover': {
                backgroundColor: 'rgba(155, 83, 84, 0.2)',
              }
            },
            '& .MuiPickerDay-root.Mui-selected': {
              backgroundColor: '#9B5354 !important',
              color: 'white'
            },
            '& .MuiPickerDay-today': {
              borderColor: '#9B5354',
              borderWidth: '2px'
            },
            '& .MuiPickersCalendarHeader-root': {
              paddingLeft: 10,
              paddingRight: 10,
              mb: 1,
              '& .MuiPickersCalendarHeader-label': {
                fontSize: '1.6rem',
                fontWeight: 900,
                color: '#69393A'
              },
              '& .MuiIconButton-root': {
                transform: 'scale(1.2)'
              }
            }
          }}
        />

        {/* Action Menu */}
        <Menu
          anchorEl={anchorEl}
          open={Boolean(anchorEl)}
          onClose={handleMenuClose}
          anchorOrigin={{
            vertical: 'center',
            horizontal: 'right',
          }}
          transformOrigin={{
            vertical: 'center',
            horizontal: 'left',
          }}
          PaperProps={{
            sx: {
              borderRadius: 3,
              ml: 1,
              boxShadow: '0 10px 30px rgba(0,0,0,0.1)',
              minWidth: 220,
              backgroundColor: 'rgba(255, 255, 255, 0.95)',
              backdropFilter: 'blur(10px)',
            }
          }}
        >
          <Box sx={{ px: 2, py: 1 }}>
            <Typography variant="caption" sx={{ fontWeight: 'bold', color: '#9B5354', textTransform: 'uppercase' }}>
              Día: {selectedDate?.format('DD/MM')}
            </Typography>
          </Box>
          <MenuItem onClick={() => handleToggleType('kiki')}>
            <ListItemIcon>
              {selectedDate && dayData[selectedDate.format('YYYY-MM-DD')]?.includes('kiki') ? (
                <Check sx={{ color: '#E91E63' }} />
              ) : (
                <Favorite sx={{ color: '#E91E63' }} />
              )}
            </ListItemIcon>
            <ListItemText primary="Kiki ❤️" secondary={selectedDate && dayData[selectedDate.format('YYYY-MM-DD')]?.includes('kiki') ? "Marcado" : ""} />
          </MenuItem>
          <MenuItem onClick={() => handleToggleType('period_start')}>
            <ListItemIcon>
              {selectedDate && dayData[selectedDate.format('YYYY-MM-DD')]?.includes('period_start') ? (
                <Check sx={{ color: '#D32F2F' }} />
              ) : (
                <WaterDrop sx={{ color: '#D32F2F' }} />
              )}
            </ListItemIcon>
            <ListItemText primary="Inicio Regla 🩸" secondary={selectedDate && dayData[selectedDate.format('YYYY-MM-DD')]?.includes('period_start') ? "Marcado" : ""} />
          </MenuItem>
          <MenuItem onClick={() => handleToggleType('period_end')}>
            <ListItemIcon>
              {selectedDate && dayData[selectedDate.format('YYYY-MM-DD')]?.includes('period_end') ? (
                <Check sx={{ color: '#4CAF50' }} />
              ) : (
                <CheckCircle sx={{ color: '#4CAF50' }} />
              )}
            </ListItemIcon>
            <ListItemText primary="Fin Regla 🏁" secondary={selectedDate && dayData[selectedDate.format('YYYY-MM-DD')]?.includes('period_end') ? "Marcado" : ""} />
          </MenuItem>
          
          <Divider sx={{ my: 1 }} />
          
          <MenuItem onClick={handleClearDay} sx={{ color: 'error.main' }}>
            <ListItemIcon><Delete color="error" /></ListItemIcon>
            <ListItemText primary="Eliminar todo" />
          </MenuItem>
        </Menu>
        
      </Paper>
    </LocalizationProvider>
  );
};

export default CalendarComponent;
