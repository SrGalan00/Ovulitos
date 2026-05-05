import { db } from '../firebase/firebase';
import { collection, setDoc, doc } from 'firebase/firestore';

const sampleTips = [
  { dia: 1, titulo: "Inicio del Ciclo", contenido: "Hoy comienza tu fase folicular. Es normal sentirte con menos energía. Prioriza el descanso y mantente bien hidratada." },
  { dia: 2, titulo: "Cuidado Menstrual", contenido: "Aplica calor en la zona lumbar si sientes molestias. El té de jengibre puede ayudar a reducir la inflamación." },
  { dia: 5, titulo: "Energía en Ascenso", contenido: "Tu estrógeno está empezando a subir. Pronto te sentirás con más claridad mental y energía física." },
  { dia: 10, titulo: "Fase Folicular Tardía", contenido: "Tu piel empieza a verse más radiante. Es un gran momento para actividades sociales y proyectos creativos." },
  { dia: 14, titulo: "Ventana de Ovulación", contenido: "Estás en tu punto máximo de energía y libido. Tu cuerpo está listo para la acción. ¡Disfruta de tu vitalidad!" },
  { dia: 21, titulo: "Fase Lútea", contenido: "La progesterona sube. Es posible que te sientas más introspectiva. Es un buen momento para tareas que requieran detalle y calma." },
  { dia: 25, titulo: "Síndrome Premenstrual", contenido: "Reduce el consumo de sal y cafeína para evitar la hinchazón. El ejercicio suave como yoga puede mejorar mucho tu ánimo." },
  { dia: 28, titulo: "Cerrando el Ciclo", contenido: "Tu cuerpo se prepara para renovarse. Sé amable contigo misma hoy. Un baño relajante o una lectura tranquila son ideales." }
];

export const populateTips = async () => {
  const consejosRef = collection(db, 'consejos');
  
  for (const tip of sampleTips) {
    try {
      await setDoc(doc(consejosRef, `dia_${tip.dia}`), tip);
      console.log(`Consejo día ${tip.dia} guardado.`);
    } catch (error) {
      console.error(`Error guardando consejo ${tip.dia}:`, error);
    }
  }
};
