import React, { useState } from 'react';
import { 
  Box, 
  Typography, 
  IconButton,
  Drawer,
  useTheme,
  useMediaQuery,
  Grid
} from '@mui/material';
import { Menu as MenuIcon } from '@mui/icons-material';
import { useAuth } from '../context/authContext';
import CalendarComponent from './CalendarComponent';
import Sidebar from './Sidebar';
import DailyTip from './DailyTip';
import OnboardingPeriod from './OnboardingPeriod';
import { db } from '../firebase/firebase';
import { collection, query, limit, getDocs } from 'firebase/firestore';
import { useEffect } from 'react';

interface DashboardProps {
  onLogout: () => void;
}

const Dashboard: React.FC<DashboardProps> = ({ onLogout }) => {
  const { currentUser } = useAuth();
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));
  const [activeView, setActiveView] = useState('dashboard');
  const [needsOnboarding, setNeedsOnboarding] = useState(false);
  const [refreshKey, setRefreshKey] = useState(0);
  const [mobileOpen, setMobileOpen] = useState(false);

  const handleDrawerToggle = () => {
    setMobileOpen(!mobileOpen);
  };

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

      {/* Sidebar for Desktop */}
      {!isMobile && (
        <Sidebar onLogout={onLogout} activeView={activeView} setActiveView={setActiveView} />
      )}

      {/* Sidebar for Mobile (Drawer) */}
      <Drawer
        variant="temporary"
        open={mobileOpen}
        onClose={handleDrawerToggle}
        ModalProps={{ keepMounted: true }}
        sx={{
          display: { xs: 'block', md: 'none' },
          '& .MuiDrawer-paper': { 
            boxSizing: 'border-box', 
            width: 280, 
            backgroundColor: 'transparent',
            boxShadow: 'none',
            border: 'none'
          },
        }}
      >
        <Sidebar 
          onLogout={onLogout} 
          activeView={activeView} 
          setActiveView={(view) => {
            setActiveView(view);
            setMobileOpen(false);
          }} 
        />
      </Drawer>

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
        p: { xs: 1.5, md: 3 }, // Aumentado de 1/2 a 1.5/3
        zIndex: 1, 
        overflowY: 'auto',
        backgroundColor: 'rgba(255, 255, 255, 0.4)',
        backdropFilter: 'blur(10px)',
        borderRadius: 8, // Restaurado
        border: '1px solid rgba(105, 57, 58, 0.1)',
        boxShadow: '0 10px 40px rgba(105, 57, 58, 0.05)',
        display: 'flex',
        flexDirection: 'column'
      }}>
        <Box sx={{ 
          textAlign: 'center', 
          mb: 2, 
          mt: 1.5,
          position: 'relative' // Para posicionar el botón de menú
        }}>
          {isMobile && (
            <IconButton
              color="inherit"
              aria-label="open drawer"
              edge="start"
              onClick={handleDrawerToggle}
              sx={{ 
                position: 'absolute', 
                left: 0, 
                top: '50%', 
                transform: 'translateY(-50%)',
                color: colors.secondary 
              }}
            >
              <MenuIcon />
            </IconButton>
          )}
          <Typography variant="overline" sx={{ color: colors.textMuted, letterSpacing: 2, fontWeight: 'bold', fontSize: '0.8rem' }}>
            PERSONAL SPACE
          </Typography>
          <Typography variant="h3" sx={{ 
            color: colors.secondary, 
            fontWeight: 800, 
            fontFamily: "'Poppins', sans-serif",
            mt: 0.5,
            fontSize: { xs: '1.75rem', md: '3rem' } // Responsivo
          }}>
            Your Dashboard<Box component="span" sx={{ color: colors.accent }}>.</Box>
          </Typography>
        </Box>

        <Grid container spacing={2} justifyContent="center" sx={{ flexGrow: 1, overflow: 'auto' }}>
          {/* Daily Tip - Side by side on md+ */}
          <Grid size={{ xs: 12, md: 4, lg: 3 }}>
             <DailyTip currentUser={currentUser} key={refreshKey} />
          </Grid>
          {/* Calendar Section */}
          <Grid size={{ xs: 12, md: 8, lg: 8 }} sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
            <CalendarComponent />
          </Grid>
        </Grid>
      </Box>
    </Box>
  );
};

export default Dashboard;