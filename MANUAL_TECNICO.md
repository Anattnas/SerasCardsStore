# MANUAL TÉCNICO - Seras Cards Store

**Versión:** 1.0  
**Autor:** Juess Ansanta  
**Fecha:** 22/11/2025  
**Estado:** En desarrollo

---

## 1. Introducción

El presente manual técnico documenta la arquitectura, estructura y funcionamiento del proyecto *Seras Cards Store*, una aplicación de venta y gestión de cartas coleccionables.  
Su objetivo es servir como guía para desarrolladores y administradores del proyecto, describiendo los módulos implementados, la organización de los archivos y las funcionalidades disponibles hasta la fecha.

---

## 2. Alcance

Este manual cubre:

- Estructura del proyecto en Android Studio.
- Descripción de módulos funcionales implementados.
- Procedimientos de uso de la aplicación para pruebas y desarrollo.
- Información sobre recursos, layouts, y CI relevantes para auditoría.

Queda fuera del alcance:

- Funcionalidades planificadas pero no implementadas.
- Configuración del repositorio remoto y control de versiones (cubierto en CONFIG_ITEMS.md).
- Guías de usuario final detalladas (manual de usuario separado).

---

## 3. Requisitos del Sistema

- **Sistema Operativo:** Windows 10 o superior, macOS, Linux compatible con Android Studio.
- **Software:** Android Studio (versión recomendada: Arctic Fox o superior), SDK Android API 33.
- **Dependencias:** Firebase SDK para autenticación y base de datos, librerías de soporte de AndroidX y Material Design.
- **Cuenta de Firebase:** Configurada para autenticación y almacenamiento de datos.

---

## 4. Estructura del Proyecto

### 4.1 Directorios principales

- `app/src/main/java/com/serascardsstore/` → Código fuente Kotlin, organizado por paquetes funcionales.
- `app/src/main/res/layout/` → Layouts XML de actividades y fragmentos.
- `app/src/main/res/drawable/` → Recursos gráficos (iconos, imágenes).
- `app/src/main/res/mipmap-*/` → Iconos de la aplicación para distintas resoluciones.
- `app/src/main/res/font/` → Fuentes utilizadas en la interfaz.
- `app/src/main/res/values/` → Colores, temas, dimensiones y strings.
- `app/src/test/java/com/serascardsstore/` → Tests unitarios.

### 4.2 Archivos clave

- `MainActivity.kt` → Contiene el contenedor de fragmentos y el menú principal.
- `HomeFragment.kt` → Pantalla principal tras login, permite navegación a módulos.
- `LoginActivity.kt` / `RegisterActivity.kt` → Gestión de autenticación de usuarios.
- `ProfileFragment.kt` → Visualización y edición de perfil.
- `CreatePostFragment.kt`, `FeedFragment.kt` → Publicaciones de cartas (en desarrollo).
- `FavoritesFragment.kt` → Gestión de favoritos (en desarrollo).
- `README.md` → Información general del proyecto.
- `CONFIG_ITEMS.md` → Inventario de CIs y trazabilidad para auditoría.
- `MANUAL_TECNICO.md` → Este documento.

---

## 5. Módulos Implementados

### 5.1 Autenticación

- Login con correo electrónico.
- Login mediante Google Sign-In.
- Registro de nuevos usuarios.
- Validaciones básicas de campos y manejo de errores.

### 5.2 Perfil de Usuario

- Visualización de datos personales.
- Edición de información básica (nombre, correo, avatar).
- Sincronización con Firebase.

### 5.3 Gestión de Publicaciones

- Creación de publicaciones de venta (fragmento implementado, aún en desarrollo).
- Visualización del feed de publicaciones (en desarrollo).

### 5.4 Favoritos

- Marcado de publicaciones favoritas.
- Visualización de lista de favoritos (en desarrollo).

### 5.5 Navegación

- BottomNavigationView para moverse entre módulos.
- Contenedor de fragmentos central en MainActivity.
- Menú hamburguesa con opciones adicionales (cerrar sesión, perfil, etc.).

---

## 6. Recursos y Layouts

- Cada fragmento tiene un layout XML correspondiente (`activity_*.xml` o `fragment_*.xml`).
- Recursos gráficos: iconos, botones, fondos, imágenes de perfil.
- Fonts personalizadas cargadas desde `res/font/`.

---

## 7. Procedimiento de Ejecución

1. Clonar el repositorio desde GitHub.
2. Abrir el proyecto en Android Studio.
3. Sincronizar Gradle y asegurarse de tener dependencias actualizadas.
4. Configurar Firebase (`google-services.json`) en `app/`.
5. Ejecutar la aplicación en un emulador o dispositivo físico.
6. Navegar por los módulos implementados: login → home → perfil / publicaciones / favoritos.

---

## 8. Notas y Consideraciones

- Algunos módulos están en desarrollo y no contienen todas las funcionalidades planificadas.
- Se recomienda consultar `CONFIG_ITEMS.md` para identificar los CIs y la trazabilidad.
- Los recursos visuales y layouts siguen convenciones de diseño consistentes.
- Para agregar nuevas funcionalidades, crear rama feature/ en Git y seguir la convención de commits.

---

## 9. Futuras Extensiones

- Implementación completa del feed de publicaciones y creación de posts.
- Funcionalidades avanzadas de favoritos y búsqueda.
- Mejoras en UI/UX y personalización del perfil.
- Integración de notificaciones push y geolocalización.

---

## 10. Referencias

- Documentación oficial de [Android Studio](https://developer.android.com/studio)
- [Firebase Authentication](https://firebase.google.com/docs/auth)
- [Material Design Guidelines](https://material.io/design)

<!-- Commit de prueba 2 -->
