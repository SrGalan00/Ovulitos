import React, { useState, useEffect } from 'react';
import { 
  Box, 
  Typography, 
  List, 
  ListItem, 
  ListItemButton, 
  ListItemIcon, 
  ListItemText, 
  Avatar, 
  Divider
} from '@mui/material';
import { 
  Home, 
  Dashboard as DashboardIcon, 
  CalendarMonth, 
  History, 
  Settings, 
  Logout 
} from '@mui/icons-material';
import { useAuth } from '../context/authContext/index.jsx';
import { db } from '../firebase/firebase';
import { doc, onSnapshot } from 'firebase/firestore';

interface SidebarProps {
  onLogout: () => void;
  activeView: string;
  setActiveView: (view: string) => void;
}

const Sidebar: React.FC<SidebarProps> = ({ onLogout, activeView, setActiveView }) => {
  const { currentUser } = useAuth();
  const [userData, setUserData] = useState<{ nombre?: string } | null>(null);
  
  const colors = {
    bg: '#69393A',
    text: '#FFF8C9',
    accent: '#9B5354',
    hover: 'rgba(255, 248, 201, 0.1)'
  };

  // Suscribirse a los datos del perfil del usuario para mostrar el nombre real
  useEffect(() => {
    if (!currentUser?.email || localStorage.getItem('isGuest') === 'true') return;

    const userRef = doc(db, 'usuarios', currentUser.email);
    const unsubscribe = onSnapshot(userRef, (doc) => {
      if (doc.exists()) {
        setUserData(doc.data());
      }
    });

    return () => unsubscribe();
  }, [currentUser]);

  const menuItems = [
    { id: 'home', text: 'HOME', icon: <Home /> },
    { id: 'dashboard', text: 'DASHBOARD', icon: <DashboardIcon /> },
    { id: 'agenda', text: 'AGENDA', icon: <CalendarMonth /> },
    { id: 'history', text: 'HISTORY', icon: <History /> },
  ];

  return (
    <Box sx={{ 
      width: { xs: '100%', md: 280 }, 
      backgroundColor: colors.bg, 
      height: '100%', 
      display: 'flex', 
      flexDirection: 'column',
      borderRight: '1px solid rgba(255, 248, 201, 0.1)',
      color: colors.text,
      borderRadius: { xs: 0, md: 8 }, 
      boxShadow: '0 10px 40px rgba(0,0,0,0.1)',
      overflow: 'hidden', 
      zIndex: 10
    }}>
      {/* Logo Section */}
      <Box sx={{ p: 2, display: 'flex', alignItems: 'center', gap: 1.5 }}>
        <Box sx={{ 
          width: 32, 
          height: 32, 
          borderRadius: 1.5, 
          backgroundColor: colors.accent,
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          color: 'white',
          fontWeight: 'bold',
          fontSize: '1rem'
        }}>
          O
        </Box>
        <Typography variant="subtitle1" sx={{ fontWeight: 800, letterSpacing: 0.5, color: colors.text }}>
          OVULITOS
        </Typography>
      </Box>

      {/* Menu Items */}
      <List sx={{ px: 1.5, py: 0, flexGrow: 1 }}>
        {menuItems.map((item) => (
          <ListItem key={item.id} disablePadding sx={{ mb: 0.5 }}>
            <ListItemButton 
              onClick={() => setActiveView(item.id)}
              sx={{ 
                borderRadius: 2,
                py: 0.8,
                backgroundColor: activeView === item.id ? colors.accent : 'transparent',
                color: colors.text,
                '&:hover': {
                  backgroundColor: activeView === item.id ? colors.accent : colors.hover,
                }
              }}
            >
              <ListItemIcon sx={{ 
                color: colors.text,
                minWidth: 32 
              }}>
                {item.icon}
              </ListItemIcon>
              <ListItemText 
                primary={item.text} 
                primaryTypographyProps={{ 
                  fontSize: '0.75rem', 
                  fontWeight: 700,
                  letterSpacing: 0.5
                }} 
              />
            </ListItemButton>
          </ListItem>
        ))}
      </List>

      <Divider sx={{ backgroundColor: 'rgba(255, 248, 201, 0.1)', mx: 2 }} />

      {/* Download App Call to Action */}
      <Box sx={{ p: 1.5, mt: 0.5 }}>
        <ListItemButton 
          sx={{ 
            backgroundColor: colors.text, 
            color: colors.bg,
            borderRadius: 2,
            py: 1,
            transition: 'all 0.3s ease',
            '&:hover': { 
              backgroundColor: '#fff',
              transform: 'translateY(-1px)',
              boxShadow: '0 3px 10px rgba(0,0,0,0.2)'
            }
          }}
          onClick={() => window.open('https://play.google.com/store', '_blank')}
        >
          <ListItemText 
            primary="DESCARGAR APP" 
            primaryTypographyProps={{ fontSize: '0.75rem', fontWeight: 900, letterSpacing: 0.5 }} 
          />
        </ListItemButton>
      </Box>

      {/* User Profile Section */}
      <Box sx={{ p: 2 }}>
        <Box sx={{ 
          display: 'flex', 
          alignItems: 'center', 
          gap: 1.5, 
          mb: 1.5,
          p: 1.5,
          borderRadius: 2,
          backgroundColor: 'rgba(255, 248, 201, 0.05)',
          border: localStorage.getItem('isGuest') === 'true' ? `1px dashed ${colors.text}` : 'none'
        }}>
          <Avatar sx={{ bgcolor: colors.accent, width: 32, height: 32, fontSize: '0.9rem' }}>
            {localStorage.getItem('isGuest') === 'true' ? 'G' : (userData?.nombre?.charAt(0).toUpperCase() || currentUser?.email?.charAt(0).toUpperCase() || 'U')}
          </Avatar>
          <Box sx={{ overflow: 'hidden' }}>
            <Typography variant="caption" sx={{ fontWeight: 800, color: colors.text, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', display: 'block', maxWidth: 150 }}>
              {localStorage.getItem('isGuest') === 'true' ? 'GUEST USER' : (userData?.nombre || currentUser?.displayName || currentUser?.email?.split('@')[0] || 'usuario')}
            </Typography>
            <Typography variant="caption" sx={{ color: 'rgba(255, 248, 201, 0.6)', cursor: 'pointer', fontSize: '0.65rem', '&:hover': { color: colors.text } }}>
              {localStorage.getItem('isGuest') === 'true' ? 'MODO INVITADO' : 'VIEW PROFILE'}
            </Typography>
          </Box>
        </Box>

        <ListItemButton 
          onClick={() => {
            if (localStorage.getItem('isGuest') === 'true') {
              localStorage.removeItem('isGuest');
              window.location.reload();
            } else {
              onLogout();
            }
          }}
          sx={{ 
            borderRadius: 2,
            py: 0.8,
            color: colors.text,
            '&:hover': { backgroundColor: 'rgba(211, 47, 47, 0.1)', color: '#FFCDD2' }
          }}
        >
          <ListItemIcon sx={{ color: 'inherit', minWidth: 32 }}>
            <Logout sx={{ fontSize: '1.2rem' }} />
          </ListItemIcon>
          <ListItemText 
            primary={localStorage.getItem('isGuest') === 'true' ? "SALIR DE INVITADO" : "CERRAR SESIÓN"} 
            primaryTypographyProps={{ fontSize: '0.75rem', fontWeight: 700 }} 
          />
        </ListItemButton>

        <Box sx={{ mt: 1, display: 'flex', alignItems: 'center', gap: 1, opacity: 0.6, px: 1 }}>
          <Settings sx={{ fontSize: '0.8rem' }} />
          <Typography variant="caption" sx={{ fontWeight: 600, fontSize: '0.65rem' }}>Ovulitos Settings</Typography>
        </Box>
      </Box>
    </Box>
  );
};

export default Sidebar;
