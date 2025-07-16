# Guía de Migración de Maven a Gradle - ArkaValenzuela

## 📋 Resumen
Esta guía documenta la migración del proyecto ArkaValenzuela de Maven a Gradle, manteniendo toda la funcionalidad y configuración existente.

## 🎯 Objetivos de la Migración
- Migrar de Maven a Gradle manteniendo la funcionalidad completa
- Mejorar los tiempos de construcción
- Aprovechar las características avanzadas de Gradle
- Mantener compatibilidad con Spring Boot 3.5.3 y Java 21

## 📁 Estructura del Proyecto

### Antes (Maven)
```
proyecto/
├── pom.xml
├── mvnw
├── mvnw.cmd
└── src/
    ├── main/
    └── test/
```

### Después (Gradle)
```
proyecto/
├── build.gradle
├── settings.gradle
├── gradlew
├── gradlew.bat
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
└── src/
    ├── main/
    └── test/
```

## 🔧 Archivos de Configuración

### 1. build.gradle
Equivalente al `pom.xml` de Maven, contiene:
- Plugins utilizados
- Dependencias del proyecto
- Configuración de tareas
- Versiones y propiedades

```gradle
plugins {
    id 'java'
    id 'war'
    id 'org.springframework.boot' version '3.5.3'
    id 'io.spring.dependency-management' version '1.1.7'
}

group = 'com.arka'
version = '0.0.1-SNAPSHOT'
description = 'Proyecto Spring Boot con Gradle - ArkaValenzuela'

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    runtimeOnly 'com.mysql:mysql-connector-j'
    testRuntimeOnly 'com.h2database:h2'
    providedRuntime 'org.springframework.boot:spring-boot-starter-tomcat'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
}
```

### 2. settings.gradle
Define el nombre del proyecto:
```gradle
rootProject.name = 'arkavalenzuela'
```

### 3. gradle-wrapper.properties
Especifica la versión de Gradle a utilizar:
```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-8.14.3-bin.zip
```

## 🔄 Equivalencias Maven vs Gradle

| Maven | Gradle | Descripción |
|-------|--------|-------------|
| `mvn clean` | `./gradlew clean` | Limpia el proyecto |
| `mvn compile` | `./gradlew compileJava` | Compila el código fuente |
| `mvn test` | `./gradlew test` | Ejecuta las pruebas |
| `mvn package` | `./gradlew build` | Construye el proyecto |
| `mvn spring-boot:run` | `./gradlew bootRun` | Ejecuta la aplicación Spring Boot |
| `mvn install` | `./gradlew publishToMavenLocal` | Instala en repositorio local |

## 📦 Gestión de Dependencias

### Maven (pom.xml)
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

### Gradle (build.gradle)
```gradle
implementation 'org.springframework.boot:spring-boot-starter-web'
```

### Scopes de Dependencias
| Maven | Gradle | Uso |
|-------|--------|-----|
| `compile` | `implementation` | Dependencias de compilación |
| `runtime` | `runtimeOnly` | Solo en tiempo de ejecución |
| `test` | `testImplementation` | Solo para pruebas |
| `provided` | `providedRuntime` | Proporcionado por el contenedor |

## 🚀 Comandos de Construcción

### Construcción Completa
```bash
# Limpia y construye el proyecto
./gradlew clean build

# Solo construcción
./gradlew build
```

### Ejecutar la Aplicación
```bash
# Ejecutar con Spring Boot
./gradlew bootRun

# Ejecutar con parámetros
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### Pruebas
```bash
# Ejecutar todas las pruebas
./gradlew test

# Ejecutar pruebas con reporte detallado
./gradlew test --info

# Ejecutar pruebas específicas
./gradlew test --tests "com.arka.arkavalenzuela.*"
```

### Generar WAR
```bash
# Generar WAR tradicional
./gradlew war

# Generar WAR ejecutable de Spring Boot
./gradlew bootWar
```

## 🔍 Verificación de la Migración

### 1. Compilación
```bash
./gradlew compileJava
```

### 2. Pruebas
```bash
./gradlew test
```

### 3. Construcción Completa
```bash
./gradlew clean build
```

### 4. Ejecutar Aplicación
```bash
./gradlew bootRun
```

## 📊 Ventajas de Gradle sobre Maven

### Rendimiento
- **Construcción Incremental**: Solo recompila lo que ha cambiado
- **Cache de Dependencias**: Reutiliza artefactos entre proyectos
- **Paralelización**: Ejecuta tareas en paralelo automáticamente

### Flexibilidad
- **DSL Expressivo**: Configuración más concisa y legible
- **Plugins Poderosos**: Ecosistema rico de plugins
- **Tareas Personalizadas**: Fácil creación de tareas custom

### Herramientas
- **Gradle Wrapper**: Garantiza la misma versión en todos los entornos
- **Build Scan**: Análisis detallado de las construcciones
- **Dependency Insight**: Mejor resolución de conflictos de dependencias

## 🛠️ Configuración del IDE

### IntelliJ IDEA
1. Abrir el proyecto
2. IntelliJ detectará automáticamente que es un proyecto Gradle
3. Importar cuando se solicite
4. Configurar el JDK 21 en Project Structure

### Visual Studio Code
1. Instalar la extensión "Extension Pack for Java"
2. Abrir el proyecto
3. VS Code detectará automáticamente la configuración Gradle

## 🔧 Tareas Personalizadas

Ejemplo de tarea personalizada en `build.gradle`:

```gradle
task showDependencies {
    doLast {
        configurations.compileClasspath.each { println it.name }
    }
}
```

Ejecutar con:
```bash
./gradlew showDependencies
```

## 📝 Archivos a Eliminar (Opcional)

Después de verificar que la migración funciona correctamente, puedes eliminar:
- `pom.xml`
- `mvnw`
- `mvnw.cmd`
- `.mvn/` (directorio)

## ⚠️ Consideraciones Importantes

1. **Gradle Wrapper**: Siempre usar `./gradlew` en lugar de `gradle` instalado globalmente
2. **Versiones**: Mantener sincronizadas las versiones de dependencias
3. **Profiles**: Los profiles de Maven se manejan diferente en Gradle
4. **Cache**: Gradle mantiene un cache global que mejora el rendimiento

## 🎉 Migración Completada

✅ **El proyecto ha sido migrado exitosamente de Maven a Gradle**

### Resultados de la Verificación
- ✅ **Compilación**: `./gradlew compileJava` - EXITOSA
- ✅ **Pruebas**: `./gradlew test` - 3 pruebas ejecutadas, todas EXITOSAS
- ✅ **Construcción**: `./gradlew build` - EXITOSA
- ✅ **Generación WAR**: 
  - `arkavalenzuela-0.0.1-SNAPSHOT.war` (44.3 MB)
  - `arkavalenzuela-0.0.1-SNAPSHOT-boot.war` (54.0 MB)

### Archivos Generados
- `build.gradle` - Configuración principal del proyecto
- `settings.gradle` - Configuración del proyecto raíz
- `gradle.properties` - Propiedades de optimización de Gradle
- `gradle/wrapper/` - Gradle Wrapper para consistencia de versiones

### Siguientes Pasos
1. ✅ Verificar que todas las pruebas pasen: `./gradlew test`
2. ✅ Construir el proyecto: `./gradlew build`
3. 🔄 Ejecutar la aplicación: `./gradlew bootRun`
4. 📝 Actualizar la documentación del proyecto
5. 🚀 Configurar CI/CD para usar Gradle

-