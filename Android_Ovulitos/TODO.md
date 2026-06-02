# Tareas Pendientes - Ovulitos

Este documento enumera las tareas que faltan por completar o detalles que requieren pulirse en el proyecto **Android_Ovulitos**.

## 1. Lógica y Funcionalidades Incompletas

- [x] **Seguimiento del Ciclo (`InicioFragment.java`)**
  - **Estado actual**: Los días restantes para el periodo están fijos (`tvDias.setText("19 días para tu periodo");`).
  - **Acción**: Implementar la lógica real que calcule los días restantes basándose en la fecha actual y los datos del calendario/ciclo guardados de la usuaria.

- [x] **Sección de Fertilidad / Síntomas (`FertilidadFragment.java`)**
  - **Estado actual**: Los *click listeners* de las tarjetas de información (`cardHinchazon`, `cardParanoia`) están vacíos.
  - **Acción**: Añadir la lógica para que al pulsar se abra el detalle de la información (mediante un *dialog*, expandiendo la vista o navegando a un nuevo fragmento).

- [x] **Sección de Noticias (`NoticiasFragment.java`)**
  - **Estado actual**: Fragmento completamente vacío, solo contiene el boilerplate autogenerado por Android Studio.
  - **Acción**: Diseñar la UI del fragmento (XML) y la lógica para obtener y mostrar noticias relevantes.

## 2. Multimedia e Interfaz (UI)

- [x] **Reproductor de Meditación (`ReproductorMeditacionFragment.java`)**
  - **Estado actual**: El progreso de la meditación está siendo "simulado" mediante un hilo secundario y `Thread.sleep`.
  - **Acción**: 
    - Inicializar el objeto `MediaPlayer` real.
    - Cargar los audios correspondientes.
    - Vincular la duración y el progreso actual al reproductor en lugar del temporizador simulado.
    - Liberar los recursos del reproductor al destruir el fragmento (`mediaPlayer.release()`).

- [x] **Avatares en el Chat (`ChatListAdapter.java`)**
  - **Estado actual**: Hay un comentario indicando que se debe cargar la imagen.
  - **Acción**: Implementar una librería de carga de imágenes, como **Glide** o **Picasso**, para cargar las URL de perfil de los usuarios (`item.getAvatarUrl()`).

## 3. Configuración y Mantenimiento

- [x] **Conexión con la IA (`AiAssistantFragment.java`)**
  - **Estado actual**: La URL base del servidor (`BASE_URL`) está hardcodeada en el código con instrucciones de cambiar la IP local manualmente.
  - **Acción**: Mover la configuración de la IP/URL a `BuildConfig` (desde el `build.gradle`) o a un sistema de configuración centralizado, para evitar tocar el código fuente Java cada vez que se prueba en diferentes entornos o en producción.

- [x] **Limpieza de Recursos (XML)**
  - **Estado actual**: Archivos como `strings.xml` o `data_extraction_rules.xml` mantienen textos y etiquetas predeterminados de creación del proyecto.
  - **Acción**: Eliminar textos como `<!-- TODO: Remove or change this placeholder text -->` y configurar de manera óptima las reglas de extracción.

## 4. Perfil de Usuario y Ajustes Generales

- [x] **Gestión de Foto de Perfil (`PerfilFragment.java` / `AjustesFragment.java`)**
  - **Acción**: Implementar la funcionalidad para que la usuaria pueda seleccionar una foto de su galería, recortarla y actualizarla usando Firebase Storage.

- [x] **Configuración de Seguridad y Permisos**
  - **Acción**: Añadir opciones para cambiar la contraseña, habilitar acceso biométrico (huella dactilar/reconocimiento facial) y gestionar qué permisos tiene la aplicación (cámara, almacenamiento).

- [x] **Gestión de Privacidad**
  - **Acción**: Crear un apartado donde la usuaria pueda revisar los términos de privacidad, configurar la visibilidad de su perfil en el área social (Chat) y decidir cómo se comparten sus datos.

- [x] **Sistema de Notificaciones Push**
  - **Acción**: Implementar los *toggles* en los ajustes y la lógica (ej. con Firebase Cloud Messaging) para activar/desactivar recordatorios: llegada del periodo, ovulación, recordatorios de meditación y nuevos mensajes en el chat.

- [x] **Gestión de Datos de la Cuenta (Extra)**
  - **Acción**: Implementar opciones necesarias hoy en día para aplicaciones de salud: "Exportar mis datos" y "Eliminar mi cuenta permanentemente" (borrando registros en Firebase).
