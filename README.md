## Manual técnico

### Requisitos del sistema
- **Versión mínima de Android (Min SDK):** API 24 (Android 7.0 Nougat) o superior.
- **Versión objetivo (Target SDK):** API 34 (Android 14).
- **Versión de Gradle:** 8.2 (Android Gradle Plugin 8.1.4).
- **Espacio requerido:** Aproximadamente 50-100 MB de almacenamiento en el dispositivo para la instalación, y el espacio estándar requerido por Android Studio para la compilación local.
- **Conexión a internet:** Requerida para la autenticación de usuarios y la lectura/escritura de datos en Firebase.

### Instrucciones de compilación e instalación
1. **Clonar el repositorio:**
   Abre una terminal y ejecuta el siguiente comando para descargar el proyecto:
   ```bash
   git clone https://github.com/SrGalan00/Ovulitos.git
   ```
2. **Abrir el proyecto en Android Studio:**
   Inicia Android Studio, selecciona "Open an existing project" (o simplemente "Open") y selecciona la carpeta `Android_Ovulitos` que se encuentra dentro del repositorio clonado.
3. **Sincronización de Gradle:**
   Espera a que Android Studio finalice la sincronización (`Gradle Sync`). Descargará automáticamente todas las dependencias necesarias indicadas en los archivos `build.gradle.kts` (como Lottie, Firebase BOM, Material, etc.).
4. **Configuración de Servicios (Firebase):**
   La aplicación utiliza los servicios de Firebase (Auth, Firestore, Realtime Database). El archivo de configuración necesario (`google-services.json`) **ya está incluido** en el repositorio dentro del directorio `app/`. Por lo tanto, no es necesario añadir claves de API adicionales manualmente para que la aplicación funcione.
5. **Ejecutar la aplicación:**
   Selecciona un emulador (AVD) configurado con un nivel de API 24 o superior, o conecta un dispositivo físico Android. Haz clic en el botón de "Run" en Android Studio o pulsa `Shift + F10` para compilar e instalar la app.

### Credenciales de prueba
Para facilitar la evaluación por parte del tribunal y poder entrar directamente a la aplicación sin tener que pasar por el proceso de registro, podéis utilizar el siguiente usuario de pruebas:

- **Email:** ``
- **Contraseña:** ``


