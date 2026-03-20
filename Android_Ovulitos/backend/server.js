require('dotenv').config();
const express = require('express');
const cors = require('cors');
const rateLimit = require('express-rate-limit');
const { GoogleGenerativeAI, HarmCategory, HarmBlockThreshold } = require('@google/generative-ai');

const app = express();
const port = process.env.PORT || 3000;

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
Eres un asistente médico virtual especializado exclusivamente en salud sexual y reproductiva de la mujer. 
Tu tono debe ser siempre empático, delicado, científico, respetuoso y profesional. 
Bajo ninguna circunstancia debes responder a temas que no sean de salud sexual femenina, ni usar lenguaje vulgar o inapropiado. 
Si el usuario hace una pregunta fuera de este ámbito o inapropiada, responde cordialmente que tu función es únicamente asistir en dudas sobre salud íntima femenina de forma respetuosa.
Enfócate en ofrecer información educativa y recomienda siempre consultar a un profesional médico para diagnósticos concretos.
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

        // Seleccionar el modelo
        const model = genAI.getGenerativeModel({ model: 'gemini-2.5-flash', safetySettings });

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
            return res.status(400).json({ 
                error: 'La pregunta ha sido bloqueada debido a nuestras estrictas políticas de seguridad. Por favor, formula tu pregunta de manera clínica y respetuosa.' 
            });
        }
        
        return res.status(500).json({ error: 'Hubo un error al procesar tu petición.' });
    }
});

app.listen(port, '0.0.0.0', () => {
    console.log(`Servidor Asistente IA escuchando en http://0.0.0.0:${port}`);
    if (!process.env.GEMINI_API_KEY) {
        console.warn('¡ADVERTENCIA: GEMINI_API_KEY no está definido en .env o es el valor por defecto!');
    }
});
