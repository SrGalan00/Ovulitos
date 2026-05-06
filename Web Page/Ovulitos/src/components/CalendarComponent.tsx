import React, { useEffect, useState } from 'react';
import { 
  Badge, 
  Menu, 
  MenuItem, 
  ListItemIcon,
  ListItemText,
  Divider,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  FormControl,
  FormLabel,
  RadioGroup,
  FormControlLabel,
  Radio,
  TextField,
  Chip,
  Box,
  Typography,
  Button,
  CircularProgress,
  Paper
} from '@mui/material';
import { 
  Favorite, 
  WaterDrop, 
  CheckCircle, 
  Delete,
  Check,
  AutoAwesome,
  Save,
  Close
} from '@mui/icons-material';
import { LocalizationProvider, DateCalendar, PickerDay, PickerDayProps } from '@mui/x-date-pickers';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import dayjs, { Dayjs } from 'dayjs';
import { db } from '../firebase/firebase';
import { collection, doc, setDoc, onSnapshot, query, deleteDoc, getDoc } from 'firebase/firestore';
import { useAuth } from '../context/authContext/index.jsx';

const CalendarComponent: React.FC = () => {
  const { currentUser } = useAuth();
  const [dayData, setDayData] = useState<Record<string, string[]>>({}); // { "2024-03-27": ["kiki", "period_start"] }
  const [loading, setLoading] = useState(true);
  const [predictedDates, setPredictedDates] = useState<string[]>([]);
  const [avgCycleDays, setAvgCycleDays] = useState<number>(28);
  
  // Menu state
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const [selectedDate, setSelectedDate] = useState<Dayjs | null>(null);

  // Details Dialog state
  const [openDetails, setOpenDetails] = useState(false);
  const [intensidad, setIntensidad] = useState('Leve');
  const [sintoma, setSintoma] = useState('');
  const [isKiki, setIsKiki] = useState(false);

  useEffect(() => {
    const isGuest = localStorage.getItem('isGuest') === 'true';
    if (!currentUser && !isGuest) return;

    if (isGuest) {
      setLoading(false);
      return;
    }

    if (!currentUser) return;

    // 1. Suscribirse a Datos (Regla, Síntomas)
    const datosRef = collection(db, 'usuarios', currentUser.email, 'Datos');
    const qDatos = query(datosRef);
    
    // 2. Suscribirse a kikiData (Colección aparte)
    const kikiRef = collection(db, 'usuarios', currentUser.email, 'kikiData');
    const qKiki = query(kikiRef);

    const unsubDatos = onSnapshot(qDatos, (querySnapshot) => {
      setDayData(prev => {
        const newData = { ...prev };
        const periodStarts: string[] = [];

        querySnapshot.forEach((doc) => {
          const data = doc.data();
          const dateKey = data.fecha || doc.id;
          
          if (dateKey.match(/^\d{4}-\d{2}-\d{2}$/)) {
            let types: string[] = [];
            if (Array.isArray(data.tipos)) types = data.tipos;
            else if (data.tipo) types = [data.tipo];
            
            if (types.length > 0) {
              // Mantener lo que ya hay de kiki si existe en este día
              const isKiki = prev[dateKey]?.includes('kiki');
              newData[dateKey] = isKiki ? Array.from(new Set([...types, 'kiki'])) : types;
              if (types.includes('period_start')) periodStarts.push(dateKey);
            }
          }
        });

        calculatePrediction(periodStarts);
        return newData;
      });
      setLoading(false);
    });

    const unsubKiki = onSnapshot(qKiki, (querySnapshot) => {
      setDayData(prev => {
        const newData = { ...prev };
        querySnapshot.forEach((doc) => {
          const dateKey = doc.id;
          if (dateKey.match(/^\d{4}-\d{2}-\d{2}$/)) {
            const existing = newData[dateKey] || [];
            newData[dateKey] = Array.from(new Set([...existing, 'kiki']));
          }
        });
        return newData;
      });
    });

    return () => {
      unsubDatos();
      unsubKiki();
    };
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
    
    // Predecir las siguientes dos reglas
    const lastStart = dayjs(sortedStarts[sortedStarts.length - 1]);
    const p1 = lastStart.add(avgCycle, 'day');
    const p2 = p1.add(avgCycle, 'day');
    const dates = [p1.format('YYYY-MM-DD'), p2.format('YYYY-MM-DD')];
    setPredictedDates(dates);

    // Guardar estos metadatos en el documento del usuario para persistencia
    if (currentUser?.email) {
      try {
        const userRef = doc(db, 'usuarios', currentUser.email);
        await setDoc(userRef, {
          cicloMedio: avgCycle,
          proximaReglaPrevista: dates[0],
          segundaReglaPrevista: dates[1],
          ultimaActualización: new Date().toISOString()
        }, { merge: true });
      } catch (error) {
        console.error("Error al guardar metadatos de ciclo:", error);
      }
    }
  };

  const isInPredictionRange = (dateStr: string) => {
    if (predictedDates.length === 0) return false;
    const dDate = dayjs(dateStr);
    return predictedDates.some(pDateStr => {
      const pDate = dayjs(pDateStr);
      const diff = Math.abs(dDate.diff(pDate, 'day'));
      return diff <= 3;
    });
  };

  const handleDateClick = (event: React.MouseEvent<HTMLButtonElement>, date: Dayjs) => {
    setAnchorEl(event.currentTarget);
    setSelectedDate(date);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
    setSelectedDate(null);
  };

  const handleSaveDetails = async () => {
    if (!currentUser || !selectedDate) return;

    const dateStrFormatted = selectedDate.format('DD/MM/YYYY');
    const dateStrKey = selectedDate.format('YYYY-MM-DD');

    try {
      // 1. Guardar en datosCalendario (sobreescribe el último registro según la app)
      const datosCalRef = doc(db, 'usuarios', currentUser.email, 'Datos', 'datosCalendario');
      await setDoc(datosCalRef, {
        fechaSelected: dateStrFormatted,
        'intensidad del sangrado': intensidad,
        'Síntomas': sintoma
      });

      // 2. Guardar en kikiData (Colección aparte - Un documento por día)
      if (isKiki) {
        const kikiDocRef = doc(db, 'usuarios', currentUser.email, 'kikiData', dateStrKey);
        await setDoc(kikiDocRef, {
          fecha: dateStrKey,
          registradoEn: new Date().toISOString()
        });
        
        // También marcar en el calendario localmente si no estaba
        await handleToggleType('kiki', true); 
      }

      setOpenDetails(false);
      handleMenuClose();
    } catch (error) {
      console.error("Error al guardar detalles:", error);
    }
  };

  const handleToggleType = async (type: string, forceAdd: boolean = false) => {
    if (!currentUser || !selectedDate) return;

    const dateStr = selectedDate.format('YYYY-MM-DD');
    
    // Lógica especial para Kiki en su propia colección
    if (type === 'kiki') {
      const kikiDocRef = doc(db, 'usuarios', currentUser.email, 'kikiData', dateStr);
      const current = dayData[dateStr] || [];
      
      if (!forceAdd && current.includes('kiki')) {
        // Si ya existe y no estamos forzando, lo quitamos
        await deleteDoc(kikiDocRef);
      } else {
        // Si no existe o forzamos, lo añadimos
        await setDoc(kikiDocRef, {
          fecha: dateStr,
          registradoEn: new Date().toISOString()
        });
      }
      return;
    }

    const docRef = doc(db, 'usuarios', currentUser.email, 'Datos', dateStr);
    const currentTypes = dayData[dateStr] || [];
    let newTypes: string[] = [];

    if (!forceAdd && currentTypes.includes(type)) {
      newTypes = currentTypes.filter(t => t !== type);
    } else {
      newTypes = Array.from(new Set([...currentTypes, type]));
    }

    if (newTypes.length === 0) {
      await deleteDoc(docRef);
    } else {
      await setDoc(docRef, {
        tipos: newTypes,
        tipo: newTypes[0], 
        updatedAt: new Date().toISOString(),
        fecha: dateStr
      });
    }
  };

  const handleClearDay = async () => {
    if (!currentUser || !selectedDate) return;
    const dateStr = selectedDate.format('YYYY-MM-DD');
    
    // Eliminar de Datos
    const docRef = doc(db, 'usuarios', currentUser.email, 'Datos', dateStr);
    await deleteDoc(docRef);

    // Eliminar de kikiData
    const kikiDocRef = doc(db, 'usuarios', currentUser.email, 'kikiData', dateStr);
    await deleteDoc(kikiDocRef);

    handleMenuClose();
  };

  const getMarker = (types: string[], dateStr: string) => {
    // Combinar emojis si hay múltiples
    let emojis = '';
    if (types.includes('kiki')) emojis += '❤️';
    if (types.includes('period_start')) emojis += '🩸';
    if (types.includes('period_end')) emojis += '🏁';
    
    if (emojis) return emojis;

    // Si no hay datos pero está en el rango de predicción
    if (isInPredictionRange(dateStr)) return '⭕';
    
    return null;
  };

  const ServerDay = (props: PickerDayProps<Dayjs>) => {
    const { day, outsideCurrentMonth, ...other } = props;
    const dateStr = day.format('YYYY-MM-DD');
    const types = !outsideCurrentMonth ? dayData[dateStr] : null;
    const inRange = !outsideCurrentMonth && isInPredictionRange(dateStr);
    const isExactPrediction = !outsideCurrentMonth && predictedDates.includes(dateStr);

    return (
      <Badge
        key={dateStr}
        overlap="circular"
        badgeContent={getMarker(types || [], dateStr)}
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
            ...(inRange && {
              border: isExactPrediction ? '2px solid #9B5354' : '2px dashed rgba(155, 83, 84, 0.4)',
              backgroundColor: isExactPrediction ? 'rgba(155, 83, 84, 0.12)' : 'rgba(155, 83, 84, 0.06)',
              '&:hover': {
                backgroundColor: 'rgba(155, 83, 84, 0.2)',
              }
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
          p: { xs: 1.5, md: 2.5 }, 
          pb: { xs: 2, md: 3 },
          borderRadius: 5, 
          backgroundColor: 'rgba(255, 253, 240, 0.9)',
          backdropFilter: 'blur(25px)',
          border: '1px solid rgba(105, 57, 58, 0.2)',
          width: '100%',
          maxWidth: 950,
          margin: '0 auto',
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          boxShadow: '0 20px 50px rgba(105, 57, 58, 0.08)'
        }}
      >
        <Typography variant="h6" align="center" sx={{ mb: 1.5, color: '#69393A', fontWeight: 900, letterSpacing: 1.5 }}>
          REGISTRO DE ACTIVIDAD
        </Typography>

        {predictedDates.length > 0 && (
          <Box sx={{ 
            mb: 0.5, 
            p: 0.5, 
            px: 1.5, 
            borderRadius: 2, 
            backgroundColor: 'white', 
            border: '1px solid rgba(155, 83, 84, 0.2)',
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            gap: 0
          }}>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
              <AutoAwesome sx={{ color: '#9B5354', fontSize: '0.9rem' }} />
              <Typography sx={{ color: '#69393A', fontWeight: 700, fontSize: '0.7rem' }}>
                Próximas reglas previstas:
              </Typography>
            </Box>
            <Typography sx={{ color: '#9B5354', fontWeight: 800, fontSize: '0.65rem' }}>
              1ª: {dayjs(predictedDates[0]).format('D MMM')} • 2ª: {dayjs(predictedDates[1]).format('D MMM')}
            </Typography>
          </Box>
        )}
        
        <DateCalendar
          slots={{
            day: ServerDay,
          }}
          sx={{
            width: '100%',
            maxWidth: 750, // Aumentado de 600
            height: 'auto',
            '& .MuiPickersFadeTransitionGroup-root': {
              minHeight: 'auto',
            },
            '& .MuiDayCalendar-header': {
              justifyContent: 'space-around',
              px: 3,
              mb: 1.5,
              '& .MuiTypography-root': {
                fontSize: '1rem', // Aumentado de 0.8
                fontWeight: 900,
                color: '#9B5354',
                width: 54 // Aumentado de 40
              }
            },
            '& .MuiDayCalendar-weekContainer': {
              justifyContent: 'space-around',
              px: 3,
              my: 0.2
            },
            '& .MuiDayCalendar-monthContainer': {
              width: '100%'
            },
            '& .MuiPickerDay-root': {
              fontSize: '1.1rem', // Aumentado de 0.85
              width: 54, // Aumentado de 40
              height: 54, // Aumentado de 40
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
              paddingLeft: 4,
              paddingRight: 4,
              mb: 1,
              mt: 0.5,
              minHeight: 48,
              '& .MuiPickersCalendarHeader-label': {
                fontSize: '1.4rem', // Aumentado de 1
                fontWeight: 900,
                color: '#69393A'
              },
              '& .MuiIconButton-root': {
                transform: 'scale(1.1)' // Aumentado de 0.9
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
            horizontal: 'center',
          }}
          transformOrigin={{
            vertical: 'center',
            horizontal: 'center',
          }}
          disableScrollLock
          PaperProps={{
            sx: {
              borderRadius: 3,
              ml: 0,
              mt: 0,
              boxShadow: '0 10px 30px rgba(105, 57, 58, 0.2)',
              minWidth: 220,
              backgroundColor: 'rgba(255, 255, 255, 0.98)',
              backdropFilter: 'blur(10px)',
              border: '1px solid rgba(105, 57, 58, 0.1)',
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
          
          <MenuItem onClick={() => {
            setOpenDetails(true);
            setIsKiki(dayData[selectedDate?.format('YYYY-MM-DD') || '']?.includes('kiki') || false);
          }}>
            <ListItemIcon><AutoAwesome sx={{ color: '#9B5354' }} /></ListItemIcon>
            <ListItemText primary="Registro Detallado ✨" secondary="Síntomas e Intensidad" />
          </MenuItem>
          
          <Divider sx={{ my: 1 }} />
          
          <MenuItem onClick={handleClearDay} sx={{ color: 'error.main' }}>
            <ListItemIcon><Delete color="error" /></ListItemIcon>
            <ListItemText primary="Eliminar todo" />
          </MenuItem>
        </Menu>

        {/* Dialog para Registro Detallado */}
        <Dialog 
          open={openDetails} 
          onClose={() => setOpenDetails(false)}
          PaperProps={{
            sx: { borderRadius: 5, p: 1, maxWidth: 450, width: '100%' }
          }}
        >
          <DialogTitle sx={{ fontWeight: 900, color: '#69393A', display: 'flex', alignItems: 'center', gap: 1 }}>
            <AutoAwesome /> Detalle del día: {selectedDate?.format('DD/MM')}
          </DialogTitle>
          <DialogContent>
            <Box sx={{ mt: 2, display: 'flex', flexDirection: 'column', gap: 3 }}>
              
              <FormControl>
                <FormLabel sx={{ fontWeight: 700, color: '#9B5354', mb: 1 }}>Intensidad del Sangrado</FormLabel>
                <RadioGroup 
                  row 
                  value={intensidad} 
                  onChange={(e) => setIntensidad(e.target.value)}
                >
                  <FormControlLabel value="Leve" control={<Radio sx={{ color: '#9B5354', '&.Mui-checked': { color: '#9B5354' } }} />} label="Leve" />
                  <FormControlLabel value="Moderado" control={<Radio sx={{ color: '#9B5354', '&.Mui-checked': { color: '#9B5354' } }} />} label="Moderado" />
                  <FormControlLabel value="Abundante" control={<Radio sx={{ color: '#9B5354', '&.Mui-checked': { color: '#9B5354' } }} />} label="Abundante" />
                </RadioGroup>
              </FormControl>

              <TextField
                label="Síntomas"
                placeholder="Ej: Cólicos, dolor de cabeza..."
                fullWidth
                variant="outlined"
                value={sintoma}
                onChange={(e) => setSintoma(e.target.value)}
                InputProps={{
                  sx: { borderRadius: 3 }
                }}
              />

              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', p: 2, backgroundColor: 'rgba(233, 30, 99, 0.05)', borderRadius: 3 }}>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                  <Favorite sx={{ color: '#E91E63' }} />
                  <Typography sx={{ fontWeight: 700, color: '#69393A' }}>¿Hubo Kiki? ❤️</Typography>
                </Box>
                <Button 
                  variant={isKiki ? "contained" : "outlined"}
                  color="secondary"
                  onClick={() => setIsKiki(!isKiki)}
                  sx={{ borderRadius: 2, textTransform: 'none' }}
                >
                  {isKiki ? "Sí, registrado" : "No"}
                </Button>
              </Box>

            </Box>
          </DialogContent>
          <DialogActions sx={{ p: 3 }}>
            <Button onClick={() => setOpenDetails(false)} sx={{ color: '#9B5354', fontWeight: 700 }}>Cancelar</Button>
            <Button 
              onClick={handleSaveDetails} 
              variant="contained" 
              startIcon={<Save />}
              sx={{ 
                backgroundColor: '#69393A', 
                borderRadius: 3, 
                px: 3,
                '&:hover': { backgroundColor: '#9B5354' }
              }}
            >
              Guardar Registro
            </Button>
          </DialogActions>
        </Dialog>
        
      </Paper>
    </LocalizationProvider>
  );
};

export default CalendarComponent;
