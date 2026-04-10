require('dotenv').config();
const express = require('express');
const cors = require('cors');
const rateLimit = require('express-rate-limit');
const { GoogleGenerativeAI, HarmCategory, HarmBlockThreshold } = require('@google/generative-ai');

const app = express();
const port = process.env.PORT || 3000;

// Requerido para que el Rate Limit funcione correctamente en Render (proxy)
app.set('trust proxy', 1);

// Configuración de middlewares
app.use(cors());
app.use(express.json());

// Configuración de Seguridad: Rate Limiting
// Máximo 10 peticiones cada 15 minutos por IP
const apiLimiter = rateLimit({
    windowMs: 15 * 60 * 1000,
    max: 10,
    message: { error: 'Demasiadas peticiones realizadas. Por favor, intenta de nuevo más tarde.' },
    standardHeaders: true,
    legacyHeaders: false,
});

// Aplicar el limitador de solicitudes en la ruta /api/chat
app.use('/api/chat', apiLimiter);

// Instanciar Google Gemini API
const genAI = new GoogleGenerativeAI(process.env.GEMINI_API_KEY);

// Configuración estricta del prompt (System Prompt)
const SYSTEM_PROMPT = `
Eres la asistente especializada en salud femenina de esta aplicación. Tu identidad combina la calidez de una acompañante con el rigor de una especialista clínica.

REGLAS DE TONO SEGÚN EL CONTEXTO:
1. CONSULTAS EMOCIONALES O PERSONALES: (Ej. "Tengo miedo de mi primer examen", "Me siento frustrada por no quedar embarazada"). 
   - Tono: Empático, humano y de escucha activa. 
   - Acción: Valida la emoción brevemente antes de dar información útil. Usa frases como "Es natural sentirse así" o "Entiendo que este proceso sea difícil".
   
2. CONSULTAS TÉCNICAS O MÉDICAS: (Ej. "¿Cómo funciona el DIU?", "Efectos secundarios de la progesterona").
   - Tono: Clínico, preciso, objetivo y directo. 
   - Acción: Elimina adornos. Entrega datos basados en evidencia científica de forma escaneable y rápida.

3. INTERACCIÓN INICIAL:
   - Permite saludos y cortesía básica ("Hola", "Buenos días"). Responde amablemente y redirige de inmediato a la salud femenina.

MURO DE CONTENCIÓN TEMÁTICA (ESTRICTO):
- Tu ámbito es EXCLUSIVAMENTE: Ciclo menstrual, fertilidad, anticoncepción, embarazo/postparto, menopausia, patologías ginecológicas y bienestar íntimo.
- Si la consulta se sale de estos temas (cocina, consejos de vida generales, salud masculina, ocio, etc.):
  - Tono: Seco y cortante.
  - Respuesta: "Mi única función es asistir en consultas de salud y bienestar femenino. No puedo responder a preguntas fuera de este ámbito."

REGLAS DE FORMATO:
- Sé breve: No extiendas tus respuestas más de 2 párrafos cortos o una lista de puntos.
- Seguridad: Siempre termina recomendaciones técnicas con: "Esta información es educativa. Es imprescindible consultar con tu médico para un diagnóstico."
`;

// Configurar los ajustes de seguridad
const safetySettings = [
    {
        category: HarmCategory.HARM_CATEGORY_HARASSMENT,
        threshold: HarmBlockThreshold.BLOCK_MEDIUM_AND_ABOVE,
    },
    {
        category: HarmCategory.HARM_CATEGORY_HATE_SPEECH,
        threshold: HarmBlockThreshold.BLOCK_MEDIUM_AND_ABOVE,
    },
    {
        category: HarmCategory.HARM_CATEGORY_SEXUALLY_EXPLICIT,
        // Al ser preguntas de salud sexual, bajamos la restricción explícita solo para fines médicos
        // Esto permite tratar temas como infecciones, anatomía o métodos anticonceptivos sin ser bloqueado.
        threshold: HarmBlockThreshold.BLOCK_ONLY_HIGH,
    },
    {
        category: HarmCategory.HARM_CATEGORY_DANGEROUS_CONTENT,
        threshold: HarmBlockThreshold.BLOCK_MEDIUM_AND_ABOVE,
    },
];

app.post('/api/chat', async (req, res) => {
    try {
        const { message, userId } = req.body;

        if (!message) {
            return res.status(400).json({ error: 'El mensaje es obligatorio.' });
        }

        // Configuración de generación: limita la longitud y controla la creatividad
        const generationConfig = {
            maxOutputTokens: 300,
            temperature: 0.7,
        };

        // Seleccionar el modelo con los ajustes de seguridad y generación
        const model = genAI.getGenerativeModel({ 
            model: 'gemini-2.5-flash', 
            safetySettings,
            generationConfig 
        });

        // Añadir el system prompt al mensaje del usuario para garantizar el contexto
        const fullPrompt = `${SYSTEM_PROMPT}\n\nPregunta de la usuaria: ${message}\nRespuesta Asistente:`;

        const result = await model.generateContent(fullPrompt);
        const response = await result.response;
        const text = response.text();

        return res.json({ reply: text });

    } catch (error) {
        console.error('Error al generar respuesta:', error);

        // Manejar posibles bloqueos por filtros de seguridad
        if (error.message && error.message.includes('SAFETY')) {
            console.warn('Bloqueo de seguridad detectado:', error.message);
            return res.status(400).json({
                error: 'La pregunta ha sido bloqueada debido a nuestras estrictas políticas de seguridad. Por favor, formula tu pregunta de manera clínica y respetuosa.'
            });
        }

        // Log detallado para otros errores (Error 500)
        console.error('ERROR DETALLADO:', {
            message: error.message,
            stack: error.stack,
            type: error.name
        });

        return res.status(500).json({ error: 'Hubo un error al procesar tu petición.' });
    }
});

app.listen(port, '0.0.0.0', () => {
    console.log(`Servidor Asistente IA escuchando en http://0.0.0.0:${port}`);
    console.log('Estado de la API Key:', process.env.GEMINI_API_KEY ? 'Presente (longitud: ' + process.env.GEMINI_API_KEY.length + ')' : 'FALTANTE');
    if (!process.env.GEMINI_API_KEY) {
        console.warn('¡ADVERTENCIA: GEMINI_API_KEY no está definido en el entorno!');
    }
});
