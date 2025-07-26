# Sistema de Monitoreo UTN

Aplicación Android desarrollada para la materia de **Aplicaciones Móviles** en la **Universidad Técnica del Norte** por **Kevin Calderón**. Esta aplicación recolecta datos GPS, los almacena en una base de datos SQLite, y expone una API REST para consultar los datos y el estado del dispositivo. Incluye una interfaz personalizada con el logo de la UTN y autenticación basada en tokens.

## Características
- Recolección de datos GPS en tiempo real usando un servicio en primer plano.
- Almacenamiento de datos en una base de datos SQLite.
- Servidor HTTP local (NanoHTTPD) con endpoints `/api/sensor_data` y `/api/device_status`.
- Interfaz de usuario para visualizar datos GPS y probar la API.
- Autenticación mediante tokens almacenados en la base de datos.
- Diseño responsivo con colores y estilos personalizados.

## Requisitos
- **Android Studio**: Versión 2023.3.1 o superior.
- **Dispositivo Android**: Mínimo API 21 (Android 5.0) o emulador.
- **Java JDK**: Versión 17 o superior.
- **Permisos**: Ubicación (fina y aproximada) e internet.

## Instalación

1. **Clonar o descargar el proyecto**:
   - Clona el repositorio:
     ```bash
     git clone <URL-del-repositorio>
     ```
   - O descarga el código como ZIP y extráelo.

2. **Abrir en Android Studio**:
   - Abre Android Studio y selecciona **File > Open**.
   - Navega al directorio del proyecto (por ejemplo, `C:\Users\Samy\AndroidStudioProjects\device_monitoring`) y haz clic en **OK**.

3. **Configurar dependencias**:
   - Verifica que `app/build.gradle` contenga:
     ```gradle
     dependencies {
         implementation 'androidx.appcompat:appcompat:1.6.1'
         implementation 'com.google.android.gms:play-services-location:21.0.1'
         implementation 'fi.iki.elonen:nanohttpd:2.3.1'
         implementation 'com.squareup.okhttp3:okhttp:4.12.0'
         implementation 'androidx.recyclerview:recyclerview:1.3.2'
         implementation 'androidx.cardview:cardview:1.0.0'
     }
     ```
   - Haz clic en **Sync Project with Gradle Files** (ícono de elefante o **File > Sync Project with Gradle Files**).

4. **Añadir el logo de la UTN (opcional)**:
   - Descarga el logo oficial de la UTN desde www.utn.edu.ec.
   - Coloca el archivo `utn_logo.png` en `app/src/main/res/drawable/`.
   - Si no tienes el logo, el proyecto usa un placeholder (`@android:drawable/ic_launcher_background`).

5. **Configurar el dispositivo**:
   - **Emulador**:
     - Abre **Device Manager** en Android Studio y crea un emulador (por ejemplo, Pixel 6 con API 34).
   - **Teléfono físico**:
     - Habilita la depuración USB: **Configuración > Acerca del teléfono > Toca "Número de compilación" 7 veces**, luego **Opciones de desarrollador > Depuración USB**.
     - Conecta el teléfono vía USB.

## Ejecución

1. **Compilar el proyecto**:
   - Selecciona el dispositivo en la barra superior de Android Studio.
   - Haz clic en **Run > Run 'app'** (o Shift+F10).

2. **Conceder permisos**:
   - Al iniciar la aplicación, acepta los permisos de ubicación.
   - Verifica en **Configuración > Aplicaciones > Sistema de Monitoreo UTN > Permisos** que la ubicación e internet estén habilitados.

3. **Explorar la aplicación**:
   - **Pantalla principal**: Muestra el logo de la UTN, el título "Sistema de Monitoreo UTN", y los datos del autor (Kevin Calderón, Aplicaciones Móviles). Incluye botones para:
     - **Ver Datos GPS**: Lista los datos GPS almacenados.
     - **Probar API Sensor Data**: Prueba el endpoint `/api/sensor_data`.
     - **Probar API Device Status**: Prueba el endpoint `/api/device_status`.
   - **Probar API Sensor Data**:
     - Navega a la vista correspondiente.
     - Usa los valores predeterminados:
       - Token: `example-token-123`
       - Start Time: `0`
       - End Time: `9999999999999`
     - Haz clic en "Enviar Solicitud" para ver la respuesta JSON.
   - **Probar API Device Status**:
     - Usa el token `example-token-123` y haz clic en "Enviar Solicitud".

## Estructura del Proyecto
- `app/src/main/java/com/example/device_monitoring/`:
  - `MainActivity.java`: Pantalla principal y control del servidor/servicio.
  - `GpsDataActivity.java`: Muestra datos GPS en un `RecyclerView`.
  - `ApiSensorActivity.java`: Prueba el endpoint `/api/sensor_data`.
  - `ApiStatusActivity.java`: Prueba el endpoint `/api/device_status`.
  - `LocationService.java`: Recolecta datos GPS.
  - `ApiServer.java`: Implementa el servidor HTTP.
  - `DatabaseHelper.java`: Gestiona la base de datos SQLite.
- `app/src/main/res/layout/`:
  - `activity_main.xml`: Layout de la pantalla principal.
  - `activity_gps_data.xml`: Layout para datos GPS.
  - `activity_api_sensor.xml`: Layout para probar `/api/sensor_data`.
  - `activity_api_status.xml`: Layout para probar `/api/device_status`.
  - `item_gps_data.xml`: Layout para cada elemento de la lista GPS.
- `app/src/main/res/drawable/`:
  - `utn_logo.png`: Logo de la UTN.
  - `button_background.xml`: Fondo para botones con esquinas redondeadas.

## Base de Datos
- **Nombre**: `DeviceMonitoring.db`
- **Tablas**:
  - `sensor_data`: Almacena datos GPS (`id`, `latitude`, `longitude`, `timestamp`, `device_id`).
  - `credentials`: Almacena tokens (`id`, `token`).

## API
- **Servidor**: NanoHTTPD en `localhost:8080`.
- **Endpoints**:
  - **GET `/api/sensor_data?start_time=<long>&end_time=<long>`**:
    - Autenticación: `Authorization: Bearer example-token-123`
    - Respuesta: Lista JSON de datos GPS.
  - **GET `/api/device_status`**:
    - Autenticación: `Authorization: Bearer example-token-123`
    - Respuesta: JSON con estado del dispositivo.

## Depuración
- Usa **Logcat** (**View > Tool Windows > Logcat**) para inspeccionar logs:
  - Filtra por "LocationService" para datos GPS.
  - Filtra por "ApiServer" para el servidor.
  - Filtra por "ApiSensorActivity" para solicitudes HTTP.
- Si la base de datos está vacía, inserta datos de prueba en `MainActivity.java`:
  ```java
  dbHelper.insertSensorData(-0.2186, -78.5097, System.currentTimeMillis(), "test-device-1");
  ```

## Contribuciones
Este proyecto fue desarrollado como parte de la materia de Aplicaciones Móviles en la UTN. Para contribuir, contacta al autor o abre un issue en el repositorio.

## Licencia
Propiedad de Kevin Calderón y la Universidad Técnica del Norte. Uso restringido a fines académicos.