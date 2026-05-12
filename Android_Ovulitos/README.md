# Manual Técnico - Ovulitos

Este documento está dirigido a desarrolladores y evaluadores que deseen compilar, instalar y probar la aplicación Ovulitos.

## 1. Requisitos del Sistema
Para asegurar un correcto funcionamiento y compilación, el sistema debe cumplir con:

- **Plataforma**: Android
- **Versión de SDK Android**:
    - **Mínima (minSdk)**: 24 (Android 7.0 Nougat)
    - **Objetivo (targetSdk)**: 34 (Android 14)
- **Lenguaje**: Java 8 (Versión 1.8)
- **Gradle**: Versión 8.9 (o superior compatible)
- **Espacio en disco requerido**: ~1.5 GB libres (para el código fuente, caché de Gradle y build generada).
- **Conectividad**: Imprescindible conexión a Internet para sincronización con Firebase y consumo de la API de IA.

## 2. Instrucciones de Compilación e Instalación

Sigue estos pasos para configurar el entorno de desarrollo:

### Paso 1: Clonar el Repositorio
Clona el proyecto en tu máquina local:
```bash
git clone https://github.com/SrGalan00/Ovulitos.git
```

### Paso 2: Importar en Android Studio
1. Abre Android Studio.
2. Selecciona **File > Open** y elige la carpeta `Android_Ovulitos`.
3. Espera a que Gradle sincronice todas las dependencias (este proceso puede tardar unos minutos).

### Paso 3: Configuración de Firebase y Claves
1. Asegúrate de que el archivo `google-services.json` esté ubicado en la carpeta `app/`. Sin este archivo, los servicios de autenticación y base de datos no funcionarán.
2. **Backend e IA**: La aplicación apunta actualmente a `https://ovulitos-1.onrender.com`. Si deseas ejecutar el backend localmente:
   - Ve a la carpeta `backend/`.
   - Ejecuta `npm install`.
   - Crea un archivo `.env` con tu `GEMINI_API_KEY`.
   - Cambia la `BASE_URL` en `AiAssistantFragment.java` a la IP de tu servidor local.

### Paso 4: Ejecución
- Conecta un dispositivo físico con depuración USB activada o inicia un emulador.
- Pulsa el botón **Run 'app'** (índice verde) en la barra superior.

## 3. Credenciales de Prueba para el Tribunal

Para acceder a todas las funcionalidades de la aplicación sin realizar un registro manual, utilice los siguientes datos:

- **Email**: `test@gmail.com`
- **Contraseña**: `password123`

> [!IMPORTANT]
> Estas credenciales están habilitadas en el entorno de desarrollo de Firebase para facilitar la evaluación rápida por parte del tribunal.
