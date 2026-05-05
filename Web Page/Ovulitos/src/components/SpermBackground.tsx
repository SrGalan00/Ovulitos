import React, { useEffect, useState } from 'react';
import { Box } from '@mui/material';
import { keyframes } from '@emotion/react';

const swim = keyframes`
  0% {
    transform: translate(-100px, 0) rotate(0deg);
  }
  25% {
    transform: translate(25vw, 50px) rotate(5deg);
  }
  50% {
    transform: translate(50vw, -50px) rotate(-5deg);
  }
  75% {
    transform: translate(75vw, 50px) rotate(5deg);
  }
  100% {
    transform: translate(110vw, 0) rotate(0deg);
  }
`;

const wiggle = keyframes`
  0%, 100% { d: path("M12 5 Q 9 2, 6 5 T 0 5"); }
  50% { d: path("M12 5 Q 9 8, 6 5 T 0 5"); }
`;

const Spermatozoon = ({ top, delay, duration, size }: { top: string, delay: string, duration: string, size: number }) => (
  <Box
    sx={{
      position: 'absolute',
      top,
      left: -100,
      animation: `${swim} ${duration} linear ${delay} infinite`,
      zIndex: 0,
      opacity: 0.4,
      pointerEvents: 'none',
    }}
  >
    <svg width={size * 4} height={size * 2} viewBox="0 0 20 10">
      <ellipse cx="16" cy="5" rx="3" ry="2.5" fill="white" />
      <path 
        stroke="white" 
        fill="none" 
        strokeWidth="1"
        style={{ animation: `${wiggle} 0.3s ease-in-out infinite` }}
      />
    </svg>
  </Box>
);

const SpermBackground = () => {
  const [sperms, setSperms] = useState<{ id: number, top: string, delay: string, duration: string, size: number }[]>([]);

  useEffect(() => {
    const newSperms = Array.from({ length: 25 }).map((_, i) => ({
      id: i,
      top: `${Math.random() * 100}%`,
      delay: `${Math.random() * 20}s`,
      duration: `${10 + Math.random() * 15}s`,
      size: 5 + Math.random() * 5,
    }));
    setSperms(newSperms);
  }, []);

  return (
    <Box
      sx={{
        position: 'absolute',
        top: 0,
        left: 0,
        width: '100%',
        height: '100%',
        overflow: 'hidden',
        zIndex: 0,
        pointerEvents: 'none',
      }}
    >
      {sperms.map((s) => (
        <Spermatozoon key={s.id} {...s} />
      ))}
    </Box>
  );
};

export default SpermBackground;
