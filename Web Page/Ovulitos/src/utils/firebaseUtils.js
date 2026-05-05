import { db } from '../firebase/firebase';
import { collection, setDoc, doc, getDocs, query, limit } from 'firebase/firestore';

const sampleTips = [
  { dia: 1, titulo: "Día 1: Inicio del Ciclo", contenido: "Hoy comienza tu fase folicular. Es normal sentirte con menos energía. Prioriza el descanso y mantente bien hidratada. El hierro es tu mejor aliado hoy." },
  { dia: 2, titulo: "Día 2: Alivio Menstrual", contenido: "Aplica calor en la zona lumbar si sientes molestias. El té de jengibre o canela puede ayudar a reducir la inflamación y los calambres." },
  { dia: 3, titulo: "Día 3: Autocuidado", contenido: "Sigue escuchando a tu cuerpo. Una caminata suave o estiramientos ligeros pueden ayudar a mejorar la circulación y el ánimo." },
  { dia: 5, titulo: "Día 5: Energía en Ascenso", contenido: "Tu estrógeno está empezando a subir. Pronto te sentirás con más claridad mental y energía física. ¡Tu piel empieza a brillar!" },
  { dia: 7, titulo: "Día 7: Fin de la Regla", contenido: "Con el fin del sangrado, tu energía aumenta. Es un momento ideal para planificar la semana y empezar nuevos proyectos." },
  { dia: 10, titulo: "Día 10: Fase Folicular Tardía", contenido: "Tus niveles de estrógeno están altos. Te sientes más sociable y comunicativa. Es un gran momento para reuniones importantes." },
  { dia: 14, titulo: "Día 14: Ventana de Ovulación", contenido: "Estás en tu punto máximo de vitalidad y libido. Tu confianza está por las nubes. ¡Aprovecha este impulso de energía!" },
  { dia: 16, titulo: "Día 16: Transición Lútea", contenido: "La ovulación ha pasado y la progesterona empieza a subir. Es posible que sientas un deseo de estar más tranquila y en casa." },
  { dia: 21, titulo: "Día 21: Pico de Progesterona", contenido: "Tu metabolismo está un poco más acelerado. Opta por comidas nutritivas y saciantes. Es un buen momento para tareas de enfoque." },
  { dia: 25, titulo: "Día 25: Fase Premenstrual", contenido: "Reduce el consumo de sal y cafeína para evitar la retención de líquidos. El magnesio puede ayudarte a dormir mejor y reducir la ansiedad." },
  { dia: 28, titulo: "Día 28: Reflexión y Calma", contenido: "Tu cuerpo se prepara para renovarse. Sé amable contigo misma. Un baño relajante o meditación te ayudarán a cerrar el ciclo con paz." }
];

export const initializeConsejos = async () => {
  try {
    const consejosRef = collection(db, 'consejos');
    const q = query(consejosRef, limit(1));
    const querySnapshot = await getDocs(q);

    // Solo poblamos si la colección está vacía para evitar duplicados
    if (querySnapshot.empty) {
      console.log("Inicializando colección de consejos...");
      for (const tip of sampleTips) {
        await setDoc(doc(consejosRef, `dia_${tip.dia}`), tip);
      }
      console.log("Colección 'consejos' creada con éxito.");
      return true;
    }
    return false;
  } catch (error) {
    console.error("Error al inicializar consejos:", error);
    return false;
  }
};
