# Inventario de Objetos de Configuración (CI) – Seras Cards Store

**Documento:** Administración de Configuración del Software  
**Versión:** LB-1.0  
**Fecha de Creación:** 22/11/2025  
**Autor / Responsable:** Juess Ansanta  
**Estado:** Versión inicial

## 2. Propósito y Alcance del Documento

**Propósito:**  
El presente documento tiene como objetivo identificar, clasificar y controlar todos los Objetos de Configuración (CI) del proyecto *Seras Cards Store*, estableciendo una línea base inicial (LB-1.0) que sirva como referencia para auditorías, control de versiones y gestión de cambios futuros.

**Alcance:**  
Este inventario abarca todos los artefactos relevantes para la administración de la configuración, incluyendo:

- Código fuente del proyecto (archivos Kotlin, XML y Gradle).
- Documentación técnica y académica existente (README.md, diagramas, manuales).
- Artefactos de diseño como diagramas UML y esquemas de navegación.
- Objetos de pruebas cuando se desarrollen.

Quedan fuera de este inventario elementos temporales o auxiliares que no impactan directamente en el producto final, como archivos de cache, librerías externas descargadas automáticamente, builds locales y otros generados automáticamente por Android Studio.

## 3. Convención de Nombres

Para garantizar uniformidad, trazabilidad y fácil identificación de los Objetos de Configuración (CI) en *Seras Cards Store*, se establece la siguiente convención de nombres:

1. **Nombres descriptivos:**  
   Cada archivo debe tener un nombre claro y descriptivo, reflejando su contenido o funcionalidad. Ejemplos:
    - `LoginActivity.kt` → Actividad principal de inicio de sesión.
    - `PerfilFragment.kt` → Fragmento para gestión de perfil de usuario.
    - `diagrama_clases.png` → Diagrama UML de clases.

2. **Extensiones y tipos de archivos:**
    - Código fuente: `.kt`, `.java`
    - Layouts de UI: `.xml`
    - Documentación: `.md`, `.pdf`, `.png`, `.docx`
    - Configuración del proyecto: `.gradle`, `.xml`

3. **Versionamiento:**
    - La versión inicial se registra como `LB-1.0`.
    - Cambios posteriores se registrarán como `LB-1.1`, `LB-1.2`, etc.
    - Cada modificación debe documentarse en la sección “Historial de Cambios del Documento” y en la bitácora de Git.

4. **Rutas esperadas:**
    - Se indicará la ruta relativa dentro del proyecto para cada archivo, sin depender de rutas absolutas del sistema operativo. Ejemplo:
      ```
      /app/src/main/java/com/serascardsstore/ui/login/LoginActivity.kt
      /docs/diagrama_clases.png
      /README.md
      ```

Esta convención será utilizada para todos los CI listados en las secciones siguientes, asegurando consistencia y claridad en la administración de la configuración.

## 4. Clasificación de Objetos de Configuración

Los Objetos de Configuración (CI) del proyecto *Seras Cards Store* se clasifican de acuerdo con Pressman para facilitar su trazabilidad y control:

| Tipo de Objeto | Descripción |
|----------------|------------|
| Objeto de contenido | Archivos que contienen documentación académica o técnica, incluyendo README.md, manuales, instructivos y guías de usuario. |
| Objeto de diseño | Diagramas UML, esquemas de navegación, mockups y cualquier representación gráfica que describa la arquitectura, flujo o estructura del sistema. |
| Objeto de código | Archivos de implementación del proyecto, incluyendo código fuente (Kotlin/Java), layouts XML, archivos Gradle y otros scripts necesarios para la construcción y ejecución de la aplicación. |
| Objeto de pruebas | Archivos que contienen pruebas unitarias, scripts de test y cualquier artefacto utilizado para validar la funcionalidad del sistema. |

## 5. Inventario de CI Actuales (LB-1.0)

A continuación se presenta el inventario completo de los CIs actuales del proyecto *Seras Cards Store*, organizados por módulo funcional, con versión inicial LB-1.0, clasificación según Pressman y descripción detallada de cada archivo.

---

### Módulo 1: Autenticación
| Nombre / Ruta | Versión | Tipo Pressman | Descripción |
|---------------|---------|---------------|-------------|
| app/src/main/java/com/serascardsstore/ui/login/LoginActivity.kt | LB-1.0 | Código | Pantalla de inicio de sesión con correo y Google Sign-In. |
| app/src/main/java/com/serascardsstore/ui/login/LoginViewModel.kt | LB-1.0 | Código | Lógica de validación y comunicación con Firebase para autenticación. |
| app/src/main/res/layout/activity_login_email.xml | LB-1.0 | Diseño | Layout XML de la pantalla de login por email. |
| app/src/main/java/com/serascardsstore/ui/login/RegisterActivity.kt | LB-1.0 | Código | Pantalla de registro de nuevos usuarios. |
| app/src/main/res/layout/activity_registro_email.xml | LB-1.0 | Diseño | Layout XML de registro de usuarios. |
| app/src/main/res/drawable/email.png | LB-1.0 | Diseño | Icono para indicar email en login/registro. |
| app/src/main/res/drawable/google.png | LB-1.0 | Diseño | Icono de Google Sign-In. |

---

### Módulo 2: Perfil de Usuario
| Nombre / Ruta | Versión | Tipo Pressman | Descripción |
|---------------|---------|---------------|-------------|
| app/src/main/java/com/serascardsstore/ui/profile/ProfileFragment.kt | LB-1.0 | Código | Fragmento que muestra y permite editar los datos del usuario. |
| app/src/main/res/layout/fragment_digi_perfil.xml | LB-1.0 | Diseño | Layout XML para el perfil de usuario. |
| app/src/main/java/com/serascardsstore/ui/profile/ProfileViewModel.kt | LB-1.0 | Código | Lógica de comunicación con Firebase para CRUD de usuario. |
| app/src/main/res/drawable/img_perfil.png | LB-1.0 | Diseño | Imagen de perfil por defecto. |

---

### Módulo 3: Gestión de Publicaciones
| Nombre / Ruta | Versión | Tipo Pressman | Descripción |
|---------------|---------|---------------|-------------|
| app/src/main/java/com/serascardsstore/ui/posts/CreatePostFragment.kt | LB-1.0 | Código | Fragmento para crear publicaciones de venta. |
| app/src/main/res/layout/activity_crear_anuncio.xml | LB-1.0 | Diseño | Layout XML para crear publicaciones. |
| app/src/main/java/com/serascardsstore/ui/posts/FeedFragment.kt | LB-1.0 | Código | Fragmento para mostrar publicaciones de todos los usuarios. |
| app/src/main/res/layout/fragment_digi_inicio.xml | LB-1.0 | Diseño | Layout XML del feed de publicaciones inicial. |

---

### Módulo 4: Favoritos
| Nombre / Ruta | Versión | Tipo Pressman | Descripción |
|---------------|---------|---------------|-------------|
| app/src/main/java/com/serascardsstore/ui/favorites/FavoritesFragment.kt | LB-1.0 | Código | Fragmento para mostrar publicaciones marcadas como favoritas. |
| app/src/main/res/layout/fragment_digi_favoritos.xml | LB-1.0 | Diseño | Layout XML para la sección de favoritos. |
| app/src/main/res/drawable/favorito.png | LB-1.0 | Diseño | Icono de marcador de favorito. |

---

### Módulo 5: Navegación / Pantalla Inicio
| Nombre / Ruta | Versión | Tipo Pressman | Descripción |
|---------------|---------|---------------|-------------|
| app/src/main/java/com/serascardsstore/ui/home/HomeFragment.kt | LB-1.0 | Código | Fragmento principal después del login, con opciones de navegación. |
| app/src/main/res/layout/fragment_digi_inicio.xml | LB-1.0 | Diseño | Layout XML de la pantalla de inicio principal. |
| app/src/main/java/com/serascardsstore/ui/main/MainActivity.kt | LB-1.0 | Código | Actividad que contiene el menú principal y fragment container. |
| app/src/main/res/layout/activity_main.xml | LB-1.0 | Diseño | Layout XML de la actividad principal con BottomNavigationView. |
| app/src/main/res/menu/menu_digi_main.xml | LB-1.0 | Diseño | Menu XML para la navegación principal. |
| app/src/main/res/menu/menu_hamburgesa.xml | LB-1.0 | Diseño | Menu XML para el menú hamburguesa lateral. |

---

### Recursos / Documentación
| Nombre / Ruta | Versión | Tipo Pressman | Descripción |
|---------------|---------|---------------|-------------|
| README.md | LB-1.0 | Contenido | Documento principal de presentación y guía del proyecto. |
| Mis_notas/notas-activity_editar_perfil.txt | LB-1.0 | Contenido | Notas de desarrollo y observaciones para el fragmento de perfil. |
| Mis_notas/notas-fragment_digi_perfil.txt | LB-1.0 | Contenido | Notas de desarrollo y observaciones para el fragmento de perfil. |
| docs/diagrama_clases.png | LB-1.0 | Diseño | Diagrama UML de clases del sistema. |
| docs/diagrama_navegacion.png | LB-1.0 | Diseño | Diagrama de navegación entre pantallas de la app. |
| app/src/main/res/font/comic_jungle.ttf | LB-1.0 | Diseño | Fuente tipográfica para textos y títulos. |
| app/src/main/res/font/comic_jungle_extrude.ttf | LB-1.0 | Diseño | Fuente tipográfica extra para efectos. |

## 6. Inventario de CI Futuros (Planeados)

Esta sección incluye los CIs que se agregarán en próximas iteraciones del proyecto. No forman parte de la línea base LB-1.0, pero se registran para control de cambios y planificación.

| Nombre / Ruta | Versión Planeada | Tipo Pressman | Descripción |
|---------------|----------------|---------------|-------------|
| app/src/main/java/com/serascardsstore/ui/posts/CreatePostFragment.kt | LB-1.1 | Código | Fragmento para crear publicaciones de venta (actualmente en desarrollo). |
| app/src/main/res/layout/fragment_create_post.xml | LB-1.1 | Diseño | Layout XML asociado a la creación de publicaciones. |
| app/src/main/java/com/serascardsstore/ui/posts/FeedFragment.kt | LB-1.1 | Código | Fragmento para mostrar el feed de publicaciones de todos los usuarios. |
| app/src/main/res/layout/fragment_feed.xml | LB-1.1 | Diseño | Layout XML del feed de publicaciones. |
| app/src/main/java/com/serascardsstore/ui/favorites/FavoritesFragment.kt | LB-1.1 | Código | Fragmento para gestionar publicaciones marcadas como favoritas. |
| app/src/main/res/layout/fragment_favorites.xml | LB-1.1 | Diseño | Layout XML de la sección de favoritos. |
| tests/unit_tests/ | LB-1.1 | Pruebas | Tests unitarios pendientes para validar lógica de los módulos de publicaciones y favoritos. |
| docs/MANUAL_TECNICO.md | LB-1.1 | Contenido | Manual técnico placeholder que se completará con instrucciones de uso y arquitectura. |
| docs/diagrama_flujo.png | LB-1.1 | Diseño | Diagrama de flujo de procesos planeado para las nuevas funcionalidades. |

**Notas:**
- La columna "Versión Planeada" indica la línea base o versión futura donde se espera integrar el CI.
- Mantener este inventario actualizado asegura trazabilidad entre lo planeado y lo desarrollado.

## 7. Versionamiento Inicial y Estado

Esta sección documenta las versiones del proyecto y el estado de los Objetos de Configuración (CIs) en cada línea base.

| Línea Base / Versión | Fecha | Responsable(s) | Cambios Principales / Comentarios |
|---------------------|-------|----------------|----------------------------------|
| LB-1.0 | 2025-11-22 | Juess Ansanta | Línea base inicial del proyecto *Seras Cards Store*. Incluye todos los CIs existentes al momento: código fuente de autenticación, perfil, navegación, layouts XML, documentación básica (README.md, diagramas UML). |
| LB-1.1 | Prevista 2025-12-01 | Juess Ansanta | Integración de módulos de publicaciones y favoritos, tests unitarios, manual técnico placeholder y diagramas de flujo. Se planea como actualización de la línea base para auditar cambios y control de versiones. |

**Notas:**
- Cada línea base debe estar etiquetada en el repositorio Git (tag vX.Y) para trazabilidad.
- Las fechas y responsables deben reflejar evidencia de commits y aprobaciones de cambios.
- Esta tabla sirve como referencia para auditoría, facilitando seguimiento de evolución del proyecto.

## 8. Historial de Cambios del Documento

| Fecha | Versión del Documento | Autor | Descripción del Cambio |
|-------|--------------------|-------|-----------------------|
| 2025-11-22 | 1.0 | Juess Ansanta | Creación inicial de `CONFIG_ITEMS.md`. Inclusión de Secciones 1 a 7 con inventario preliminar de CIs y convenciones de nombres. |
| 2025-11-22 | 1.1 | Juess Ansanta | Actualización de inventario de CIs con listado completo de archivos reales del proyecto. Ajuste de descripciones y clasificación Pressman. |
| 2025-11-23 | 1.2 | Juess Ansanta | Planeación de CIs futuros: módulos no terminados, tests y documentación adicional. |

**Notas:**
- Cada cambio registrado debe estar soportado por evidencia en Git (commit, tag, PR).
- Las versiones del documento se correlacionan con la línea base de software LB-X.Y para trazabilidad completa.
