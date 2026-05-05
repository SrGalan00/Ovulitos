import React, { useEffect, useState } from 'react';
import { 
  Box, 
  Typography, 
  Paper, 
  CircularProgress, 
  Menu, 
  MenuItem, 
  ListItemIcon,
  ListItemText
} from '@mui/material';
import { 
  Favorite, 
  WaterDrop, 
  CheckCircle, 
  Delete 
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
  const [dayData, setDayData] = useState<Record<string, string>>({}); // { "2024-03-27": "kiki" }
  const [loading, setLoading] = useState(true);
  
  // Menu state
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const [selectedDate, setSelectedDate] = useState<Dayjs | null>(null);

  useEffect(() => {
    if (!currentUser) return;

    const q = query(collection(db, 'usuarios', currentUser.email, 'Datos'));
    
    const unsubscribe = onSnapshot(q, (querySnapshot) => {
      const dataMap: Record<string, string> = {};
      querySnapshot.forEach((doc) => {
        const data = doc.data();
        const dateKey = data.fecha || doc.id;
        if (dateKey.match(/^\d{4}-\d{2}-\d{2}$/)) {
          dataMap[dateKey] = data.tipo || 'kiki';
        }
      });
      setDayData(dataMap);
      setLoading(false);
    });

    return () => unsubscribe();
  }, [currentUser]);

  const handleDateClick = (event: React.MouseEvent<HTMLButtonElement>, date: Dayjs) => {
    setAnchorEl(event.currentTarget);
    setSelectedDate(date);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
    setSelectedDate(null);
  };

  const handleSelectType = async (type: string | null) => {
    if (!currentUser || !selectedDate) return;

    const dateStr = selectedDate.format('YYYY-MM-DD');
    const docRef = doc(db, 'usuarios', currentUser.email, 'Datos', dateStr);

    if (type === null) {
      await deleteDoc(docRef);
    } else {
      await setDoc(docRef, {
        fecha: dateStr,
        tipo: type,
        updatedAt: new Date().toISOString()
      });
    }
    handleMenuClose();
  };

  const getMarker = (type: string) => {
    switch (type) {
      case 'kiki': return '❤️';
      case 'period_start': return '🩸';
      case 'period_end': return '🏁';
      default: return '📍';
    }
  };

  const ServerDay = (props: PickerDayProps<Dayjs>) => {
    const { day, outsideCurrentMonth, ...other } = props;
    const dateStr = day.format('YYYY-MM-DD');
    const type = !outsideCurrentMonth ? dayData[dateStr] : null;

    return (
      <Badge
        key={day.toString()}
        overlap="circular"
        badgeContent={type ? getMarker(type) : undefined}
        sx={{
          '& .MuiBadge-badge': {
            fontSize: '0.9rem',
            right: 4,
            top: 4
          }
        }}
      >
        <PickerDay 
          {...other} 
          outsideCurrentMonth={outsideCurrentMonth} 
          day={day} 
          onClick={(e: any) => handleDateClick(e, day)}
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
          pb: { xs: 4, md: 6 }, // Padding inferior consistente pero no excesivo
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
        
        <DateCalendar
          slots={{
            day: ServerDay,
          }}
          sx={{
            width: '100%',
            maxWidth: 850,
            height: 'auto',
            '& .MuiPickersFadeTransitionGroup-root': {
              minHeight: 'auto', // Dejamos que ajuste al contenido
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
              minWidth: 200,
              backgroundColor: 'rgba(255, 255, 255, 0.95)',
              backdropFilter: 'blur(10px)',
            }
          }}
        >
          <Box sx={{ px: 2, py: 1 }}>
            <Typography variant="caption" sx={{ fontWeight: 'bold', color: '#9B5354', textTransform: 'uppercase' }}>
              Marcar día: {selectedDate?.format('DD/MM')}
            </Typography>
          </Box>
          <MenuItem onClick={() => handleSelectType('kiki')}>
            <ListItemIcon><Favorite sx={{ color: '#E91E63' }} /></ListItemIcon>
            <ListItemText primary="Marcar Kiki" />
          </MenuItem>
          <MenuItem onClick={() => handleSelectType('period_start')}>
            <ListItemIcon><WaterDrop sx={{ color: '#D32F2F' }} /></ListItemIcon>
            <ListItemText primary="Inicio de Regla" />
          </MenuItem>
          <MenuItem onClick={() => handleSelectType('period_end')}>
            <ListItemIcon><CheckCircle sx={{ color: '#4CAF50' }} /></ListItemIcon>
            <ListItemText primary="Fin de Regla" />
          </MenuItem>
          <MenuItem onClick={() => handleSelectType(null)} sx={{ color: 'error.main' }}>
            <ListItemIcon><Delete color="error" /></ListItemIcon>
            <ListItemText primary="Eliminar Registro" />
          </MenuItem>
        </Menu>
        
      </Paper>
    </LocalizationProvider>
  );
};

export default CalendarComponent;
