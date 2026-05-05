import React from 'react';
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

interface SidebarProps {
  onLogout: () => void;
  activeView: string;
  setActiveView: (view: string) => void;
}

const Sidebar: React.FC<SidebarProps> = ({ onLogout, activeView, setActiveView }) => {
  const { currentUser } = useAuth();
  
  const colors = {
    bg: '#69393A',
    text: '#FFF8C9',
    accent: '#9B5354',
    hover: 'rgba(255, 248, 201, 0.1)'
  };

  const menuItems = [
    { id: 'home', text: 'HOME', icon: <Home /> },
    { id: 'dashboard', text: 'DASHBOARD', icon: <DashboardIcon /> },
    { id: 'agenda', text: 'AGENDA', icon: <CalendarMonth /> },
    { id: 'history', text: 'HISTORY', icon: <History /> },
  ];

  return (
    <Box sx={{ 
      width: 280, 
      backgroundColor: colors.bg, 
      height: '100vh', 
      display: 'flex', 
      flexDirection: 'column',
      borderRight: '1px solid rgba(255, 248, 201, 0.1)',
      color: colors.text,
      position: 'sticky',
      top: 0,
      zIndex: 10
    }}>
      {/* Logo Section */}
      <Box sx={{ p: 4, display: 'flex', alignItems: 'center', gap: 2 }}>
        <Box sx={{ 
          width: 40, 
          height: 40, 
          borderRadius: 2, 
          backgroundColor: colors.accent,
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          color: 'white',
          fontWeight: 'bold',
          fontSize: '1.2rem'
        }}>
          O
        </Box>
        <Typography variant="h6" sx={{ fontWeight: 800, letterSpacing: 1, color: colors.text }}>
          OVULITOS
        </Typography>
      </Box>

      {/* Menu Items */}
      <List sx={{ px: 2, flexGrow: 1 }}>
        {menuItems.map((item) => (
          <ListItem key={item.id} disablePadding sx={{ mb: 1 }}>
            <ListItemButton 
              onClick={() => setActiveView(item.id)}
              sx={{ 
                borderRadius: 2,
                backgroundColor: activeView === item.id ? colors.accent : 'transparent',
                color: colors.text,
                '&:hover': {
                  backgroundColor: activeView === item.id ? colors.accent : colors.hover,
                }
              }}
            >
              <ListItemIcon sx={{ 
                color: colors.text,
                minWidth: 40 
              }}>
                {item.icon}
              </ListItemIcon>
              <ListItemText 
                primary={item.text} 
                primaryTypographyProps={{ 
                  fontSize: '0.8rem', 
                  fontWeight: 700,
                  letterSpacing: 1
                }} 
              />
            </ListItemButton>
          </ListItem>
        ))}
      </List>

      <Divider sx={{ backgroundColor: 'rgba(255, 248, 201, 0.1)', mx: 2 }} />

      {/* Download App Call to Action */}
      <Box sx={{ p: 2, mt: 1 }}>
        <ListItemButton 
          sx={{ 
            backgroundColor: colors.text, 
            color: colors.bg,
            borderRadius: 3,
            py: 1.5,
            transition: 'all 0.3s ease',
            '&:hover': { 
              backgroundColor: '#fff',
              transform: 'translateY(-2px)',
              boxShadow: '0 5px 15px rgba(0,0,0,0.2)'
            }
          }}
          onClick={() => window.open('https://play.google.com/store', '_blank')}
        >
          <ListItemIcon sx={{ color: colors.bg, minWidth: 35 }}>
            <Box component="span" sx={{ fontSize: '1.4rem' }}>📱</Box>
          </ListItemIcon>
          <ListItemText 
            primary="DESCARGAR APP" 
            primaryTypographyProps={{ fontSize: '0.85rem', fontWeight: 900, letterSpacing: 1 }} 
          />
        </ListItemButton>
      </Box>

      {/* User Profile Section */}
      <Box sx={{ p: 3 }}>
        <Box sx={{ 
          display: 'flex', 
          alignItems: 'center', 
          gap: 2, 
          mb: 3,
          p: 2,
          borderRadius: 3,
          backgroundColor: 'rgba(255, 248, 201, 0.05)',
          border: localStorage.getItem('isGuest') === 'true' ? `1px dashed ${colors.text}` : 'none'
        }}>
          <Avatar sx={{ bgcolor: colors.accent, width: 40, height: 40 }}>
            {localStorage.getItem('isGuest') === 'true' ? 'G' : (currentUser?.email?.charAt(0).toUpperCase() || 'U')}
          </Avatar>
          <Box>
            <Typography variant="body2" sx={{ fontWeight: 800, color: colors.text, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', maxWidth: 150 }}>
              {localStorage.getItem('isGuest') === 'true' ? 'GUEST USER' : (currentUser?.displayName || currentUser?.email?.split('@')[0] || 'usuario')}
            </Typography>
            <Typography variant="caption" sx={{ color: 'rgba(255, 248, 201, 0.6)', cursor: 'pointer', '&:hover': { color: colors.text } }}>
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
            color: colors.text,
            '&:hover': { backgroundColor: 'rgba(211, 47, 47, 0.1)', color: '#FFCDD2' }
          }}
        >
          <ListItemIcon sx={{ color: 'inherit', minWidth: 40 }}>
            <Logout />
          </ListItemIcon>
          <ListItemText 
            primary={localStorage.getItem('isGuest') === 'true' ? "SALIR DE INVITADO" : "CERRAR SESIÓN"} 
            primaryTypographyProps={{ fontSize: '0.8rem', fontWeight: 700 }} 
          />
        </ListItemButton>

        <Box sx={{ mt: 2, display: 'flex', alignItems: 'center', gap: 1, opacity: 0.6, px: 2 }}>
          <Settings sx={{ fontSize: '1rem' }} />
          <Typography variant="caption" sx={{ fontWeight: 600 }}>Ovulitos Settings</Typography>
        </Box>
      </Box>
    </Box>
  );
};

export default Sidebar;
