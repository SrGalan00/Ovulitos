import React, { useState } from 'react';
import { 
  Box, 
  Typography, 
  Button, 
  Paper,
  Grid
} from '@mui/material';
import { Schedule, Refresh, ArrowForward } from '@mui/icons-material';
import { useAuth } from '../context/authContext/index.jsx';
import CalendarComponent from './CalendarComponent';
import Sidebar from './Sidebar';
import DailyTip from './DailyTip';
import OnboardingPeriod from './OnboardingPeriod';
import { db } from '../firebase/firebase';
import { collection, query, limit, getDocs } from 'firebase/firestore';
import { useEffect } from 'react';

const Dashboard: React.FC<DashboardProps> = ({ onLogout }) => {
  const { currentUser } = useAuth();
  const [activeView, setActiveView] = useState('dashboard');
  const [needsOnboarding, setNeedsOnboarding] = useState(false);
  const [refreshKey, setRefreshKey] = useState(0);

  useEffect(() => {
    const checkData = async () => {
      if (!currentUser?.email) return;
      if (localStorage.getItem('isGuest') === 'true') return;

      const datosRef = collection(db, 'usuarios', currentUser.email, 'Datos');
      const q = query(datosRef, limit(1));
      const snapshot = await getDocs(q);
      
      setNeedsOnboarding(snapshot.empty);
    };

    checkData();
  }, [currentUser, refreshKey]);
  
  const colors = {
    primary: '#FFF8C9',
    secondary: '#69393A',
    accent: '#9B5354',
    background: '#FDF8F0',
    cardBg: '#FFFFFF',
    textMuted: 'rgba(105, 57, 58, 0.6)'
  };

  return (
    <Box sx={{ 
      display: 'flex', 
      height: '100vh', 
      backgroundColor: colors.background,
      backgroundImage: `radial-gradient(at 0% 0%, ${colors.primary} 0%, transparent 50%), 
                        radial-gradient(at 100% 0%, ${colors.primary} 0%, transparent 50%)`,
      position: 'relative',
      overflow: 'hidden',
      p: 2,
      gap: 2
    }}>
      {/* Background Wavy Lines Effect */}
      <Box sx={{
        position: 'absolute',
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        opacity: 0.1,
        pointerEvents: 'none',
        backgroundImage: 'repeating-linear-gradient(45deg, #69393A 0, #69393A 1px, transparent 0, transparent 50%)',
        backgroundSize: '100px 100px',
        maskImage: 'radial-gradient(circle, black 0%, transparent 80%)'
      }} />

      <Sidebar onLogout={onLogout} activeView={activeView} setActiveView={setActiveView} />

      {needsOnboarding && (
        <OnboardingPeriod 
          currentUser={currentUser} 
          onComplete={() => {
            setNeedsOnboarding(false);
            setRefreshKey(prev => prev + 1);
          }} 
        />
      )}

      <Box component="main" sx={{ 
        flexGrow: 1, 
        p: { xs: 2, md: 4 }, 
        zIndex: 1, 
        overflowY: 'auto',
        backgroundColor: 'rgba(255, 255, 255, 0.4)', // Fondo semitransparente para el contenido
        backdropFilter: 'blur(10px)',
        borderRadius: 8, // Bordes redondeados para el panel de contenido
        border: '1px solid rgba(105, 57, 58, 0.1)',
        boxShadow: '0 10px 40px rgba(105, 57, 58, 0.05)'
      }}>
        <Box sx={{ textAlign: 'center', mb: 3, mt: 2 }}>
          <Typography variant="overline" sx={{ color: colors.textMuted, letterSpacing: 3, fontWeight: 'bold' }}>
            PERSONAL SPACE
          </Typography>
          <Typography variant="h3" sx={{ 
            color: colors.secondary, 
            fontWeight: 800, 
            fontFamily: "'Poppins', sans-serif",
            mt: 0.5
          }}>
            Your Dashboard<Box component="span" sx={{ color: colors.accent }}>.</Box>
          </Typography>
        </Box>

        <Grid container spacing={4} justifyContent="center">
          {/* Calendar Section */}
          <Grid item xs={12} md={12} lg={11}>
            <DailyTip currentUser={currentUser} key={refreshKey} />
            <CalendarComponent />
          </Grid>
        </Grid>
      </Box>
    </Box>
  );
};

export default Dashboard;