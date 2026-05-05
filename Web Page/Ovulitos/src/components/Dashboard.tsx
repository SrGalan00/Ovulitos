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

const Dashboard: React.FC<DashboardProps> = ({ onLogout }) => {
  const { currentUser } = useAuth();
  const [activeView, setActiveView] = useState('dashboard');
  
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
      minHeight: '100vh', 
      backgroundColor: colors.background,
      backgroundImage: `radial-gradient(at 0% 0%, ${colors.primary} 0%, transparent 50%), 
                        radial-gradient(at 100% 0%, ${colors.primary} 0%, transparent 50%)`,
      position: 'relative',
      overflow: 'hidden'
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

      <Box component="main" sx={{ flexGrow: 1, p: { xs: 2, md: 3 }, zIndex: 1, overflowY: 'auto' }}>
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
          <Grid item xs={12} md={12} lg={10}>
            <DailyTip currentUser={currentUser} />
            <CalendarComponent />
          </Grid>
        </Grid>
      </Box>
    </Box>
  );
};

export default Dashboard;