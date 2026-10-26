# ATAPRIAL

Aplicación móvil Android para el **monitoreo de granjas y galpones**: permite registrar granjas, galpones y mediciones de temperatura y humedad, revisar el historial de lecturas y recibir alertas cuando un galpón supera un umbral crítico. El acceso a cada función depende del rol del usuario.

Proyecto desarrollado para la asignatura **DSY1105 – Desarrollo de Aplicaciones Móviles** (DUOC UC), Evaluación Parcial 2.

## Integrantes

| Nombre |
|---|
| Benjamín Antileo |
| Matías Caro |

## Tecnologías

- Kotlin
- Jetpack Compose + Material 3
- Navigation Compose
- Arquitectura MVVM (ViewModel + StateFlow)
- Room (SQLite) como persistencia local, con KSP
- Android Studio, minSdk 24

## Funcionalidades implementadas

- **Inicio de sesión** con correo y contraseña, validado contra la base de datos local.
- **Roles de usuario** (`JEFATURA`, `SUPERVISOR`, `OPERARIO`) que controlan qué puede ver y hacer cada persona.
- **Registro y gestión de usuarios**: formulario con validación por campo (nombre sin números, correo válido, contraseña de al menos 6 caracteres, rol obligatorio). Solo Jefatura puede crear y eliminar cuentas.
- **Gestión de granjas**: crear, listar y eliminar (Jefatura).
- **Gestión de galpones por granja**: crear galpones con temperatura y humedad, y calcular su estado (`NORMAL`, `ADVERTENCIA`, `CRÍTICO`).
- **Registro de mediciones** por galpón, que actualiza el estado actual y guarda el historial.
- **Historial de sensores**: consulta de las últimas lecturas de cada galpón.
- **Animaciones**: aparición de tarjetas con desplazamiento y desvanecimiento, reordenamiento animado de listas y animación de eliminación (escala, rotación y transparencia).
- **Recursos nativos del dispositivo**:
  - **Notificaciones** (con solicitud de permiso `POST_NOTIFICATIONS` en tiempo de ejecución) cuando un galpón supera los 35 °C.
  - **Vibración** al registrar una alerta crítica y al eliminar un usuario.
- **Persistencia local** con Room: usuarios, granjas, galpones y mediciones se conservan al cerrar la aplicación.

## Estructura del proyecto

```
app/src/main/java/com/example/ataprial/
├── MainActivity.kt
├── data/
│   ├── dao/          # Acceso a datos (Room DAOs)
│   ├── database/     # AppDatabase y conversores
│   └── model/        # Entidades y modelos de estado
└── ui/
    ├── navigation/   # Rutas y NavHost
    ├── screen/       # Pantallas (Compose)
    ├── theme/        # Colores, tipografía y tema
    └── viewmodel/    # ViewModels (estado y lógica)
```

## Cómo ejecutar el proyecto

1. Instalar **Android Studio** (versión reciente con soporte para Compose).
2. Clonar el repositorio:
   ```bash
   git clone <URL-DEL-REPOSITORIO>
   ```
3. Abrir la carpeta del proyecto en Android Studio y esperar a que termine la sincronización de Gradle.
4. Crear o seleccionar un emulador (Android 7.0 / API 24 o superior) o conectar un dispositivo físico con depuración USB.
5. Presionar **Run ▶** sobre el módulo `app`.
6. Al aceptar el permiso de notificaciones (Android 13 o superior) se podrán ver las alertas.

### Usuarios de prueba

La base de datos se crea con estas cuentas la primera vez que se abre la aplicación:

| Rol | Correo | Contraseña |
|---|---|---|
| Jefatura | jefe@mail.com | 123456 |
| Supervisor | supervisor@mail.com | 654321 |
| Operario | operario@mail.com | papoi123 |

> Si cambias la estructura de la base de datos y la app falla al abrir, desinstala la aplicación del emulador y vuelve a ejecutarla para que se recree.

## Planificación y control de versiones

- Planificación de tareas: **Trello** — `<ENLACE-AL-TABLERO>`
- Repositorio: **GitHub** — `<URL-DEL-REPOSITORIO>`
