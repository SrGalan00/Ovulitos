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

const sampleNews = [
  // Menstrual Phase News
  {
    titulo: "Nutrición menstrual: Qué comer durante la regla",
    url: "https://www.cuerpomente.com/salud-natural/tratamientos/alimentacion-durante-menstruacion_1907",
    fase: "menstrual",
    imagen: "foto_comida"
  },
  {
    titulo: "Ejercicios suaves para aliviar los dolores menstruales",
    url: "https://helloclue.com/es/articulos/ciclo-menstrual/ejercicio-durante-el-periodo",
    fase: "menstrual",
    imagen: "flor"
  },
  {
    titulo: "Entendiendo tu período: Fisiología de la menstruación",
    url: "https://es.wikipedia.org/wiki/Menstruaci%C3%B3n",
    fase: "menstrual",
    imagen: "ovulito_sin_cara"
  },
  {
    titulo: "Autocuidado y descanso en tu fase menstrual",
    url: "https://www.sabervivir.es/salud-y-bienestar/consejos-para-dormir-bien-menstruacion",
    fase: "menstrual",
    imagen: "tarro_emociones"
  },
  // Folicular Phase News
  {
    titulo: "Fase folicular: Incrementa tu productividad",
    url: "https://helloclue.com/es/articulos/ciclo-menstrual/fase-folicular",
    fase: "folicular",
    imagen: "alegria"
  },
  {
    titulo: "Recetas saludables para la fase folicular",
    url: "https://www.cuerpomente.com/alimentacion/recetas-saludables",
    fase: "folicular",
    imagen: "foto_comida"
  },
  {
    titulo: "Entrenamiento de fuerza en la fase folicular",
    url: "https://www.runnersworld.com/es/salud-lesiones/a42211516/entrenamiento-segun-ciclo-menstrual/",
    fase: "folicular",
    imagen: "flor"
  },
  {
    titulo: "Planificando metas: Aprovecha el pico de estrógeno",
    url: "https://www.sabervivir.es/psicologia/consejos-motivacion",
    fase: "folicular",
    imagen: "ovulito"
  },
  // Ovulación Phase News
  {
    titulo: "Identifica los síntomas de la ovulación",
    url: "https://espanol.babycenter.com/a900119/c%C3%B3mo-saber-cu%C3%A1ndo-est%C3%A1s-ovulando",
    fase: "ovulacion",
    imagen: "ovulito"
  },
  {
    titulo: "La ventana fértil y cómo calcularla",
    url: "https://www.plannedparenthood.org/es/temas-de-salud/embarazo/como-quedar-embarazada",
    fase: "ovulacion",
    imagen: "foto_embarazada"
  },
  {
    titulo: "Alimentación rica en antioxidantes para ovular",
    url: "https://www.cuerpomente.com/salud-natural/antioxidantes",
    fase: "ovulacion",
    imagen: "foto_comida"
  },
  {
    titulo: "El papel del flujo cervical en la ovulación",
    url: "https://helloclue.com/es/articulos/ciclo-menstrual/flujo-cervical-y-ovulacion",
    fase: "ovulacion",
    imagen: "ovulito_sin_cara"
  },
  // Lútea Phase News
  {
    titulo: "Fase lútea: Preparando el cuerpo para el descanso",
    url: "https://helloclue.com/es/articulos/ciclo-menstrual/la-fase-lutea",
    fase: "lutea",
    imagen: "bebe"
  },
  {
    titulo: "Cómo combatir el síndrome premenstrual (SPM)",
    url: "https://www.mayoclinic.org/es/diseases-conditions/premenstrual-syndrome/symptoms-causes/syc-20376780",
    fase: "lutea",
    imagen: "tarro_emociones"
  },
  {
    titulo: "Alimentos ricos en magnesio para evitar antojos",
    url: "https://www.cuerpomente.com/alimentacion/alimentos-ricos-en-magnesio",
    fase: "lutea",
    imagen: "foto_comida"
  },
  {
    titulo: "Meditación y yoga para la calma premenstrual",
    url: "https://www.sabervivir.es/yoga-bienestar",
    fase: "lutea",
    imagen: "alegria"
  }
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

export const initializeNoticias = async () => {
  try {
    const noticiasRef = collection(db, 'noticias_semanales');
    const q = query(noticiasRef, limit(1));
    const querySnapshot = await getDocs(q);

    // Solo poblamos si la colección está vacía para evitar duplicados
    if (querySnapshot.empty) {
      console.log("Inicializando colección de noticias_semanales...");
      let index = 1;
      for (const news of sampleNews) {
        await setDoc(doc(noticiasRef, `noticia_${index}`), news);
        index++;
      }
      console.log("Colección 'noticias_semanales' creada con éxito.");
      return true;
    }
    return false;
  } catch (error) {
    console.error("Error al inicializar noticias:", error);
    return false;
  }
};
