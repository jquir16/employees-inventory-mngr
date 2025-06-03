# Sistema de Gestión de Incorporación de Empleados

## Tecnologías Utilizadas

### Backend
- **Framework**: Spring Boot 3.4.6
- **Lenguaje**: Java 21
- **Seguridad**: Spring Security + JWT
- **Base de datos**: PostgreSQL
- **ORM**: Spring Data JPA + Hibernate
- **Herramienta de construcción**: Maven
- **Pruebas**: JUnit, Spring Test, Mockito
- **Dependencias principales**:
  - Lombok
  - JJWT (Tokens JWT)
  - Spring Web
  - Spring Data JPA
  - Spring Security

### Frontend
- **Framework**: Next.js 15.3.3
- **Lenguaje**: TypeScript 5
- **Estilos**: Tailwind CSS 4.1.8
- **Gestión de estado**: Zustand + React Query
- **Formularios**: Formik + Yup
- **Pruebas**: Jest, React Testing Library
- **Dependencias principales**:
  - React 19
  - Axios
  - React Hot Toast
  - React Icons
  - React Table

### Infraestructura
- **Contenedores**: Docker + Docker Compose
- **CI/CD**: GitHub Actions
- **Base de datos**: PostgreSQL 15
- **Entorno de ejecución**: Node.js 18, Java 21

## Configuración del Proyecto

### Requisitos previos
- Docker 20.10+
- Docker Compose 2.0+
- Node.js 18+
- JDK Java 21
- Maven 3.9+

### Ejecución con Docker Compose

1. **Clonar el repositorio**:
   ```bash
   git clone https://github.com/tu-repositorio/employee-onboarding.git
   cd employee-onboarding
   ```

2. **Construir e iniciar los contenedores**:
   ```bash
   docker-compose up -d --build
   ```

3. **Servicios disponibles**:
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080
   - Base de datos: localhost:15432 (usuario: postgres, contraseña: admin)

4. **Detener los servicios**:
   ```bash
   docker-compose down
   ```

### Comandos de desarrollo

#### Backend
```bash
# Ejecutar aplicación Spring Boot
mvn spring-boot:run

# Ejecutar pruebas
mvn test

# Generar reporte de cobertura de código
mvn test jacoco:report

# Ejecutar análisis de estilo de código
mvn checkstyle:checkstyle

# Construir paquete JAR
mvn clean package
```

#### Frontend
```bash
# Instalar dependencias
npm install

# Iniciar servidor de desarrollo
npm run dev

# Ejecutar pruebas
npm test

# Ejecutar pruebas con cobertura
npm test -- --coverage

# Construir para producción
npm run build

# Iniciar servidor de producción
npm start
```

## Pruebas y Calidad

### Pruebas del Backend
- Pruebas unitarias con JUnit 5
- Pruebas de integración con Spring Test
- Pruebas de seguridad con Spring Security Test
- Cobertura de código con JaCoCo
- Análisis estático con Checkstyle

Ejecutar todas las pruebas del backend:
```bash
cd backend/employee.onboarding.mngr
mvn test
```

### Pruebas del Frontend
- Pruebas de componentes con React Testing Library
- Pruebas de integración con Jest
- Pruebas end-to-end (por implementar)

Ejecutar todas las pruebas del frontend:
```bash
cd frontend/employees-onboarding-front
npm test
```

## Pipeline de CI/CD

### CI para Backend (GitHub Actions)
- Se activa con cambios en el código del backend
- Configura un contenedor de PostgreSQL para pruebas
- Ejecuta la construcción con Maven
- Ejecuta todas las pruebas
- Los reportes de cobertura están disponibles como artefactos

### CI para Frontend (Por implementar)
- Incluirá:
  - Verificación de construcción
  - Ejecución de pruebas
  - Análisis de código
  - Escaneo de seguridad

## Estructura del Proyecto

```
employee-onboarding/
├── backend/
│   └── employee.onboarding.mngr/
│       ├── src/
│       │   ├── main/java/... # Aplicación Spring Boot
│       │   └── test/java/... # Clases de prueba
│       └── pom.xml # Configuración de Maven
├── frontend/
│   └── employees-onboarding-front/
│       ├── src/ # Aplicación Next.js
│       ├── tests/ # Archivos de prueba
│       └── package.json # Configuración de NPM
├── postgresql/
│   └── Dockerfile # Configuración de la base de datos
├── docker-compose.yml # Definición del stack completo
└── .github/
    └── workflows/
        ├── ci-backend.yml # CI para backend
        └── ci-frontend.yml # CI para frontend (por agregar)
```