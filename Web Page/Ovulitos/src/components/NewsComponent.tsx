import React, { useEffect, useState } from 'react';
import { 
  Box, 
  Typography, 
  Card, 
  CardContent, 
  CardMedia, 
  Button, 
  Grid, 
  CircularProgress, 
  Chip,
  Paper,
  Tabs,
  Tab
} from '@mui/material';
import { Article, OpenInNew } from '@mui/icons-material';
import { db } from '../firebase/firebase';
import { collection, getDocs, query } from 'firebase/firestore';

interface NewsItem {
  id: string;
  titulo: string;
  url: string;
  fase: string;
  imagen: string;
}

const imageMap: Record<string, string> = {
  foto_comida: "https://images.unsplash.com/photo-1540420773420-3366772f4999?auto=format&fit=crop&w=600&q=80",
  flor: "https://images.unsplash.com/photo-1490750967868-88aa4486c946?auto=format&fit=crop&w=600&q=80",
  ovulito_sin_cara: "https://images.unsplash.com/photo-1518156677180-95a2893f3e9f?auto=format&fit=crop&w=600&q=80",
  ovulito: "https://images.unsplash.com/photo-1518156677180-95a2893f3e9f?auto=format&fit=crop&w=600&q=80",
  tarro_emociones: "https://images.unsplash.com/photo-1518199266791-5375a83190b7?auto=format&fit=crop&w=600&q=80",
  alegria: "https://images.unsplash.com/photo-1506126613408-eca07ce68773?auto=format&fit=crop&w=600&q=80",
  foto_embarazada: "https://images.unsplash.com/photo-1516627145497-ae6968895b74?auto=format&fit=crop&w=600&q=80",
  bebe: "https://images.unsplash.com/photo-1519689680058-324335c77eb2?auto=format&fit=crop&w=600&q=80"
};

const defaultImage = "https://images.unsplash.com/photo-1518156677180-95a2893f3e9f?auto=format&fit=crop&w=600&q=80";

const phaseLabels: Record<string, string> = {
  menstrual: "Menstruación 🩸",
  folicular: "Fase Folicular 🌱",
  ovulacion: "Ovulación ⚡",
  lutea: "Fase Lútea 🧘"
};

const phaseColors: Record<string, string> = {
  menstrual: "#D32F2F",
  folicular: "#4CAF50",
  ovulacion: "#FF9800",
  lutea: "#9C27B0"
};

const NewsComponent: React.FC = () => {
  const [news, setNews] = useState<NewsItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState('todas');

  useEffect(() => {
    const fetchNews = async () => {
      try {
        const newsRef = collection(db, 'noticias_semanales');
        const q = query(newsRef);
        const snapshot = await getDocs(q);
        
        const fetchedNews: NewsItem[] = [];
        snapshot.forEach((docSnap) => {
          const data = docSnap.data();
          fetchedNews.push({
            id: docSnap.id,
            titulo: data.titulo || 'Sin título',
            url: data.url || '#',
            fase: data.fase || 'general',
            imagen: data.imagen || ''
          });
        });
        
        setNews(fetchedNews);
      } catch (error) {
        console.error("Error al obtener noticias:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchNews();
  }, []);

  const filteredNews = filter === 'todas' 
    ? news 
    : news.filter(item => item.fase === filter);

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', p: 8, flexGrow: 1 }}>
        <CircularProgress sx={{ color: '#9B5354' }} />
      </Box>
    );
  }

  return (
    <Paper 
      elevation={0} 
      sx={{ 
        p: { xs: 2, md: 3 }, 
        borderRadius: 5, 
        backgroundColor: 'rgba(255, 253, 240, 0.9)',
        backdropFilter: 'blur(25px)',
        border: '1px solid rgba(105, 57, 58, 0.2)',
        width: '100%',
        maxWidth: 1100,
        margin: '0 auto',
        display: 'flex',
        flexDirection: 'column',
        boxShadow: '0 20px 50px rgba(105, 57, 58, 0.08)',
        flexGrow: 1,
        overflow: 'hidden'
      }}
    >
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5, mb: 3 }}>
        <Article sx={{ color: '#9B5354', fontSize: '2rem' }} />
        <Typography variant="h5" sx={{ color: '#69393A', fontWeight: 900, letterSpacing: 1 }}>
          Artículos y Noticias del Ciclo
        </Typography>
      </Box>

      {/* Tabs para Filtrar por Fase */}
      <Box sx={{ borderBottom: 1, borderColor: 'rgba(105, 57, 58, 0.1)', mb: 4 }}>
        <Tabs 
          value={filter} 
          onChange={(_, newValue) => setFilter(newValue)} 
          variant="scrollable"
          scrollButtons="auto"
          textColor="primary"
          indicatorColor="primary"
          sx={{
            '& .MuiTab-root': {
              fontWeight: 800,
              color: 'rgba(105, 57, 58, 0.6)',
              fontSize: '0.9rem',
              '&.Mui-selected': {
                color: '#9B5354'
              }
            },
            '& .MuiTabs-indicator': {
              backgroundColor: '#9B5354',
              height: 3
            }
          }}
        >
          <Tab value="todas" label="Todas ✨" />
          <Tab value="menstrual" label="Menstruación 🩸" />
          <Tab value="folicular" label="Fase Folicular 🌱" />
          <Tab value="ovulacion" label="Ovulación ⚡" />
          <Tab value="lutea" label="Fase Lútea 🧘" />
        </Tabs>
      </Box>

      {filteredNews.length === 0 ? (
        <Box sx={{ py: 8, textAlign: 'center' }}>
          <Typography variant="body1" sx={{ color: 'rgba(105, 57, 58, 0.6)', fontWeight: 700 }}>
            No hay artículos disponibles para esta fase en este momento.
          </Typography>
        </Box>
      ) : (
        <Box sx={{ flexGrow: 1, overflowY: 'auto', pr: 1 }}>
          <Grid container spacing={3}>
            {filteredNews.map((item) => (
              <Grid key={item.id} item xs={12} sm={6} md={4} sx={{ display: 'flex' }}>
                <Card 
                  sx={{ 
                    display: 'flex', 
                    flexDirection: 'column', 
                    width: '100%',
                    borderRadius: 4,
                    border: '1px solid rgba(105, 57, 58, 0.1)',
                    boxShadow: '0 8px 24px rgba(105, 57, 58, 0.03)',
                    backgroundColor: '#FFFFFF',
                    transition: 'all 0.3s ease',
                    cursor: 'pointer',
                    '&:hover': {
                      transform: 'translateY(-6px)',
                      boxShadow: '0 12px 30px rgba(105, 57, 58, 0.1)',
                      borderColor: 'rgba(155, 83, 84, 0.4)'
                    }
                  }}
                  onClick={() => window.open(item.url, '_blank')}
                >
                  <CardMedia
                    component="img"
                    height="180"
                    image={imageMap[item.imagen] || defaultImage}
                    alt={item.titulo}
                    sx={{
                      borderTopLeftRadius: 16,
                      borderTopRightRadius: 16
                    }}
                  />
                  <CardContent sx={{ flexGrow: 1, display: 'flex', flexDirection: 'column', gap: 1.5, p: 2.5 }}>
                    <Box>
                      <Chip 
                        label={phaseLabels[item.fase] || "General 💡"} 
                        size="small"
                        sx={{ 
                          fontWeight: 800,
                          fontSize: '0.7rem',
                          backgroundColor: `${phaseColors[item.fase] || '#9B5354'}12`,
                          color: phaseColors[item.fase] || '#9B5354',
                          border: `1px solid ${phaseColors[item.fase] || '#9B5354'}30`
                        }}
                      />
                    </Box>
                    <Typography 
                      variant="subtitle1" 
                      sx={{ 
                        color: '#69393A', 
                        fontWeight: 900, 
                        lineHeight: 1.4,
                        fontSize: '0.95rem',
                        fontFamily: "'Poppins', sans-serif",
                        display: '-webkit-box',
                        WebkitLineClamp: 3,
                        WebkitBoxOrient: 'vertical',
                        overflow: 'hidden',
                        height: '4.2em'
                      }}
                    >
                      {item.titulo}
                    </Typography>
                    
                    <Box sx={{ mt: 'auto', pt: 1.5 }}>
                      <Button 
                        variant="outlined" 
                        color="primary"
                        endIcon={<OpenInNew />}
                        fullWidth
                        sx={{ 
                          borderRadius: 3, 
                          textTransform: 'none',
                          fontWeight: 800,
                          fontSize: '0.8rem',
                          borderColor: 'rgba(155, 83, 84, 0.4)',
                          color: '#9B5354',
                          '&:hover': {
                            backgroundColor: '#9B5354',
                            color: '#FFFFFF',
                            borderColor: '#9B5354'
                          }
                        }}
                      >
                        Leer Artículo
                      </Button>
                    </Box>
                  </CardContent>
                </Card>
              </Grid>
            ))}
          </Grid>
        </Box>
      )}
    </Paper>
  );
};

export default NewsComponent;
