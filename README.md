#Sistema de Votación Electoral

Sistema web de votación electrónica desarrollado con **Spring Boot**, **MySQL** y **Thymeleaf**, que permite gestionar elecciones, candidatos y votantes de forma segura.

---

## Integrantes del Grupo

| Nombre | Rol |
|---|---|
| Huaynalaya Montes Pedro David Jorge | Lider |
| Saire Flores Guadalupe Dayana | Desarrollador Modulo A |
| Canales Palomino Luciana Graciela | Revisor/ Desarrollador Modulo B |

---

## Descripción del Proyecto

El Sistema de Votación Electoral es una aplicación web que permite:

- Registro y autenticación de votantes mediante DNI y OTP
- Gestión de elecciones y candidatos por parte del administrador
- Emisión de voto único por elección con comprobante
- Visualización de resultados en tiempo real
- Registro de eventos mediante Logback

---

## Tecnologías Utilizadas

| Tecnología | Versión | Uso |
|---|---|---|
| Java | 21 | Lenguaje principal |
| Spring Boot | 3.5.14 | Framework backend |
| Spring Security | 6.5.10 | Seguridad y autenticación |
| Spring Data JPA | - | Acceso a datos |
| Hibernate | 6.6.49 | ORM |
| MySQL | 8.0.46 | Base de datos |
| Thymeleaf | 3.1.5 | Motor de plantillas |
| Logback | - | Registro de eventos |
| Maven | 3.x | Gestión de dependencias |

---

## Estructura del Proyecto

```
sistema-votacion/
├── src/
│   ├── main/
│   │   ├── java/com/votacion/sistema_votacion/
│   │   │   ├── controller/
│   │   │   │   ├── AdministradorController.java
│   │   │   │   ├── CandidatoController.java
│   │   │   │   ├── EleccionController.java
│   │   │   │   ├── ResultadosController.java
│   │   │   │   ├── VotanteController.java
│   │   │   │   └── VotoController.java
│   │   │   ├── model/
│   │   │   │   ├── Administrador.java
│   │   │   │   ├── Candidato.java
│   │   │   │   ├── Comprobante.java
│   │   │   │   ├── Eleccion.java
│   │   │   │   ├── Otp.java
│   │   │   │   ├── PartidoPolitico.java
│   │   │   │   ├── Votante.java
│   │   │   │   └── Voto.java
│   │   │   ├── repository/
│   │   │   │   ├── AdministradorRepository.java
│   │   │   │   ├── CandidatoRepository.java
│   │   │   │   ├── ComprobanteRepository.java
│   │   │   │   ├── EleccionRepository.java
│   │   │   │   ├── OtpRepository.java
│   │   │   │   ├── PartidoPoliticoRepository.java
│   │   │   │   ├── VotanteRepository.java
│   │   │   │   └── VotoRepository.java
│   │   │   ├── SecurityConfig.java
│   │   │   └── SistemaVotacionApplication.java
│   │   └── resources/
│   │       ├── templates/
│   │       │   ├── admin/
│   │       │   └── votante/
│   │       ├── application.properties
│   │       └── logback-spring.xml
└── pom.xml
```

---

## Requisitos Previos

- Java 21
- MySQL 8.0
- Maven 3.x

---

## Instalación y Configuración

### 1. Clonar el repositorio
```bash
git clone https://github.com/tu-usuario/sistema-votacion.git
cd sistema-votacion
```

### 2. Crear la base de datos
```sql
CREATE DATABASE sistema_votacion;
```

### 3. Configurar application.properties
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/sistema_votacion?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=tu_contraseña
spring.jpa.hibernate.ddl-auto=update
```

### 4. Insertar datos iniciales
```sql
USE sistema_votacion;
INSERT INTO administrador (nombre, usuario, password) VALUES ('Administrador', 'admin', '1234');
INSERT INTO partido_politico (nombre, simbolo) VALUES ('Partido Ejemplo', 'PE');
INSERT INTO votante (dni, nombres, apellidos, celular) VALUES ('12345678', 'Juan', 'Perez', '987654321');
INSERT INTO eleccion (nombre, descripcion, fecha_inicio, fecha_fin, estado)
VALUES ('Elecciones 2026', 'Elección general 2026', '2026-01-01 00:00:00', '2026-12-31 23:59:59', 'ACTIVA');
INSERT INTO candidato (nombres, apellidos, propuestas_pdf, id_partido, id_eleccion)
VALUES ('Juan', 'García', null, 1, 1);
```

### 5. Ejecutar la aplicación
```bash
mvn spring-boot:run
```

---

## Rutas Principales

| Ruta | Descripción |
|---|---|
| `/admin/login` | Login del administrador |
| `/admin/dashboard` | Panel de control admin |
| `/elecciones` | Gestión de elecciones |
| `/candidatos/admin` | Gestión de candidatos |
| `/votante/login` | Login del votante (por DNI) |
| `/voto/elecciones` | Lista de elecciones activas |
| `/voto/comprobante` | Comprobante de voto |

---

## Avance del Proyecto (40%)

| Funcionalidad | Estado |
|---|---|
| Login de administrador | Completado |
| Login de votante con OTP | Completado |
| Gestión de elecciones | Completado |
| Gestión de candidatos | Completado |
| Emisión de voto único | Completado |
| Comprobante de voto | Completado |
| Integración Logback | Completado |
| Pruebas de Software | Pendiente |
| Pruebas de Seguridad | Pendiente |
| Despliegue con Maven | Pendiente |

---

## Diagrama de Casos de Uso

```
                    ┌─────────────────────────────────────────┐
                    │         Sistema de Votación              │
                    │                                         │
  ┌──────────┐      │  ┌─────────────────────────────────┐   │
  │          │      │  │  Administrador                  │   │
  │  Admin   │─────────►  - Gestionar elecciones         │   │
  │          │      │  │  - Gestionar candidatos         │   │
  └──────────┘      │  │  - Ver resultados               │   │
                    │  └─────────────────────────────────┘   │
                    │                                         │
  ┌──────────┐      │  ┌─────────────────────────────────┐   │
  │          │      │  │  Votante                        │   │
  │ Votante  │─────────►  - Autenticarse con DNI+OTP     │   │
  │          │      │  │  - Ver elecciones activas       │   │
  └──────────┘      │  │  - Emitir voto                  │   │
                    │  │  - Obtener comprobante          │   │
                    │  └─────────────────────────────────┘   │
                    └─────────────────────────────────────────┘
```

---

## Recursos Java Integrados

- **Logback** — Registro de eventos y auditoría del sistema
- **Spring Security** — Protección de rutas y gestión de sesiones
- **HikariCP** — Pool de conexiones a base de datos

---