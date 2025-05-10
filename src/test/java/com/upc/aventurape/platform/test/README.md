# Documentación de Pruebas - AventurePe Backend

## 6.1. Testing Suites & Validation

Este documento describe las diferentes suites de pruebas implementadas para validar la calidad y el funcionamiento del backend de AventurePe.

### 6.1.1. Core Entities Unit Tests

Las pruebas unitarias se centran en verificar el comportamiento correcto de los componentes individuales del sistema, principalmente las entidades centrales y sus servicios asociados.

#### Entidades y servicios probados:

- **User Service**: Pruebas para la creación de usuarios, asignación de roles y autenticación.
  - Archivo: `UserTest.java`
  - Funcionalidad probada: Creación de usuarios, asignación de roles, validación de credenciales.

```java
// Ejemplo de prueba unitaria para User
@Test
public void testUserCreation() {
    // Arrange
    String username = "testuser";
    String password = "password123";
    String email = "test@example.com";

    // Act
    User user = new User(username, password, email);

    // Assert
    assertEquals(username, user.getUsername());
    assertEquals(password, user.getPassword());
    assertEquals(email, user.getEmail());
    assertTrue(user.getRoles().isEmpty());
}
```

- **Profile Service**: Pruebas para la creación y gestión de perfiles de usuario.
  - Archivo: `ProfileTest.java`
  - Funcionalidad probada: Creación de perfiles de aventureros y emprendedores, validación de datos.

```java
// Ejemplo de prueba unitaria para ProfileAdventurer
@Test
public void testProfileAdventurerCreation() {
    // Arrange
    Long userId = 1L;
    String firstName = "John";
    String lastName = "Doe";
    String email = "john.doe@example.com";
    String street = "Main St";
    String number = "123";
    String city = "New York";
    String postalCode = "10001";
    String country = "USA";
    String gender = "MALE";

    // Act
    ProfileAdventurer adventurer = new ProfileAdventurer(
            new UserId(userId),
            firstName,
            lastName,
            email,
            street,
            number,
            city,
            postalCode,
            country,
            gender
    );

    // Assert
    assertEquals(userId, adventurer.getUserId().userId());
    assertEquals(firstName, adventurer.getFirstName());
    assertEquals(lastName, adventurer.getLastName());
    assertEquals(gender, adventurer.getGender());
}
```

- **Publication Service**: Pruebas para la gestión de publicaciones de aventuras.
  - Archivo: `PublicationTest.java`
  - Funcionalidad probada: Creación de publicaciones, actualización de costos, cálculo de valoraciones.

```java
// Ejemplo de prueba unitaria para Publication
@Test
public void testPublicationCreation() {
    // Arrange
    Long entrepreneurId = 1L;
    EntrepreneurId entId = new EntrepreneurId(entrepreneurId);
    Adventure adventure = new Adventure();
    adventure.setNameActivity("Aventura en los Andes");
    adventure.setDescription("Una increíble aventura en las montañas");
    adventure.setTimeDuration(3);
    adventure.setCantPeople(5);
    Integer cost = 500;
    String image = "https://example.com/image.jpg";

    // Act
    Publication publication = new Publication(entId, adventure, cost, image);

    // Assert
    assertEquals(entrepreneurId, publication.getEntrepreneurId().entrepreneurId());
    assertEquals("Aventura en los Andes", publication.getAdventure().getNameActivity());
    assertEquals(cost, publication.getCost());
    assertEquals(image, publication.getImage());
}
```

Estas pruebas unitarias permiten detectar errores temprano en el ciclo de desarrollo y facilitan el mantenimiento del código al verificar que cada componente funcione correctamente de forma aislada.

### 6.1.2. Core Integration Tests

Las pruebas de integración verifican que los diferentes componentes del sistema funcionen correctamente en conjunto, especialmente la interacción entre controladores, servicios y repositorios.

#### Controladores probados:

- **User Controller**: Pruebas para las operaciones de registro e inicio de sesión.
  - Archivo: `UserControllerIntegrationTest.java`
  - Endpoints probados: `/api/v1/users/sign-up`, `/api/v1/users/sign-in`, `/api/v1/users`

```java
// Ejemplo de prueba de integración para UserController
@Test
public void testSignUp() throws Exception {
    // Arrange
    SignUpCommand signUpCommand = new SignUpCommand(
            "testuser",
            "test@example.com",
            "password123",
            null
    );

    Map<String, String> response = new HashMap<>();
    response.put("message", "User registered successfully");
    
    doReturn(response).when(userCommandService).handle(any(SignUpCommand.class));

    // Act & Assert
    mockMvc.perform(post("/api/v1/users/sign-up")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(signUpCommand)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("User registered successfully"));
}
```

- **Profile Controller**: Pruebas para la creación y consulta de perfiles.
  - Archivo: `ProfileControllerIntegrationTest.java`
  - Endpoints probados: `/api/v1/profiles/adventurer`, `/api/v1/profiles/entrepreneur`, `/api/v1/profiles/{id}`

```java
// Ejemplo de prueba de integración para ProfileController
@Test
public void testCreateProfileAdventurer() throws Exception {
    // Arrange
    CreateProfileAdventurerCommand command = new CreateProfileAdventurerCommand(
            1L,
            "John",
            "Doe",
            "john.doe@example.com",
            "Main St",
            "123",
            "New York",
            "10001",
            "USA",
            "MALE"
    );

    ProfileAdventurer profile = new ProfileAdventurer();
    profile.setId(1L);
    
    doReturn(profile).when(profileCommandService)
        .handle(ArgumentMatchers.<CreateProfileCommand>any());

    // Act & Assert
    mockMvc.perform(post("/api/v1/profiles/adventurer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(command)))
            .andExpect(status().isCreated());
}
```

- **Publication Controller**: Pruebas para la gestión de publicaciones.
  - Archivo: `PublicationControllerIntegrationTest.java`
  - Endpoints probados: `/api/v1/publications`, `/api/v1/publications/{id}`

```java
// Ejemplo de prueba de integración para PublicationController
@Test
public void testGetPublicationById() throws Exception {
    // Arrange
    Long publicationId = 1L;
    Publication publication = new Publication();
    publication.setId(publicationId);
    Optional<Publication> optionalPublication = Optional.of(publication);

    doReturn(optionalPublication).when(publicationQueryService)
        .handle(any(GetPublicationByIdQuery.class));

    // Act & Assert
    mockMvc.perform(get("/api/v1/publications/{id}", publicationId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(publicationId));
}
```

Estas pruebas de integración aseguran que los controladores manejen correctamente las solicitudes HTTP, interactúen adecuadamente con los servicios y gestionen tanto los casos de éxito como los de error.

### 6.1.3. Core Behavior-Driven Development

Las pruebas BDD (Behavior-Driven Development) se centran en el comportamiento de la aplicación desde la perspectiva del usuario, utilizando un lenguaje natural para describir los escenarios de prueba.

#### Escenarios probados:

- **Gestión de publicaciones**: Creación, búsqueda y listado de publicaciones.
  - Archivo: `publication.feature`
  - Escenarios:
    - Crear una nueva publicación de aventura
    - Buscar una publicación por ID
    - Listar todas las publicaciones

```gherkin
# language: es
Característica: Gestión de publicaciones de aventuras

  Escenario: Crear una nueva publicación de aventura
    Dado un emprendedor con ID 1
    Y una aventura con título "Aventura en los Andes", descripción "Una increíble aventura en las montañas", capacidad 5 personas y duración 3 horas
    Y un costo de 500 soles
    Y una imagen "https://example.com/image.jpg"
    Cuando el emprendedor crea una nueva publicación
    Entonces la publicación se guarda correctamente con ID 1
    Y la publicación contiene la información correcta de la aventura
    Y la publicación tiene el costo correcto
    Y la publicación muestra la imagen correcta
```

- **Implementación**: Los pasos definidos en los archivos feature se implementan en clases Java que contienen la lógica de prueba.
  - Archivo: `PublicationStepDefinitions.java`
  - Funcionalidad: Implementa los pasos definidos en los escenarios BDD.

```java
// Ejemplo de implementación de pasos BDD
@Given("un emprendedor con ID {long}")
public void unEmprendedorConID(Long entrepreneurId) {
    context.entrepreneurId = entrepreneurId;
}

@When("el emprendedor crea una nueva publicación")
public void elEmprendedorCreaUnaNuevaPublicacion() {
    Adventure adventure = new Adventure();
    adventure.setNameActivity(context.adventureTitle);
    adventure.setDescription(context.adventureDescription);
    adventure.setCantPeople(context.adventureCapacity);
    adventure.setTimeDuration(context.adventureDuration);

    Publication publication = new Publication(
            new EntrepreneurId(context.entrepreneurId),
            adventure,
            context.cost,
            context.image
    );
    publication.setId(1L);

    when(publicationCommandService.handle(any(CreatePublicationCommand.class)))
        .thenReturn(publication);

    context.createdPublication = publication;
}

@Then("la publicación se guarda correctamente con ID {long}")
public void laPublicacionSeGuardaCorrectamenteConID(Long publicationId) {
    assertEquals(publicationId, context.createdPublication.getId());
}
```

Este enfoque BDD permite una mejor comunicación entre los desarrolladores, testers y stakeholders, ya que los escenarios de prueba están escritos en un lenguaje comprensible para todos.

### 6.1.4. Core System Tests

Las pruebas de sistema evalúan el sistema en su conjunto para verificar que cumpla con los requisitos y casos de uso definidos.

#### Historias de usuario probadas:

- **US01: Registro y autenticación de usuarios**
  - Archivo: `UserSystemTest.java`
  - Funcionalidad: Registro de nuevos usuarios, inicio de sesión y acceso a recursos protegidos.

```java
// Ejemplo de prueba de sistema para US01
@Test
public void testUserRegistrationAndAuthentication() throws Exception {
    // Paso 1: Registrar un nuevo usuario
    String uniqueSuffix = String.valueOf(System.currentTimeMillis());
    SignUpCommand signUpCommand = new SignUpCommand(
            "testuser_" + uniqueSuffix,
            "test_" + uniqueSuffix + "@example.com",
            "password123",
            null
    );

    mockMvc.perform(post("/api/v1/users/sign-up")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(signUpCommand)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").exists());

    // Paso 2: Iniciar sesión y obtener token de autenticación
    // ... código omitido para brevedad ...

    // Paso 3: Acceder a un recurso protegido usando el token
    mockMvc.perform(get("/api/v1/users/me")
                    .header("Authorization", "Bearer " + authToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value(signUpCommand.username()));
}
```

- **US04: Creación y gestión de publicaciones**
  - Archivo: `PublicationSystemTest.java`
  - Funcionalidad: Ciclo completo de creación, consulta, listado y eliminación de publicaciones.

```java
// Ejemplo de prueba de sistema para US04
@Test
public void testPublicationLifecycle() throws Exception {
    // Paso 1: Crear una nueva publicación
    Map<String, Object> publicationData = new HashMap<>();
    publicationData.put("entrepreneurId", 1L);
    publicationData.put("nameActivity", "Aventura en los Andes");
    publicationData.put("description", "Una increíble aventura en las montañas");
    publicationData.put("cantPeople", 5);
    publicationData.put("timeDuration", 3);
    publicationData.put("cost", 500);
    publicationData.put("image", "https://example.com/image.jpg");

    MvcResult createResult = mockMvc.perform(post("/api/v1/publications")
                    .header("Authorization", "Bearer " + authToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(publicationData)))
            .andExpect(status().isCreated())
            .andReturn();

    // ... obtención del ID de la publicación creada ...

    // Paso 2: Obtener la publicación creada por su ID
    mockMvc.perform(get("/api/v1/publications/{id}", createdPublicationId)
                    .header("Authorization", "Bearer " + authToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.adventure.nameActivity")
                    .value("Aventura en los Andes"));

    // Paso 3: Listar todas las publicaciones
    // Paso 4: Eliminar la publicación
    // Paso 5: Verificar que la publicación ya no existe
    // ... código omitido para brevedad ...
}
```

Estas pruebas de sistema aseguran que la aplicación funcione correctamente desde una perspectiva de usuario final, verificando flujos completos de interacción con el sistema.

## Configuración y Ejecución de Pruebas

Para ejecutar las pruebas, se puede utilizar Maven con el siguiente comando:

```bash
mvn test
```

Para ejecutar un tipo específico de pruebas, se pueden usar los siguientes comandos:

- Pruebas unitarias: `mvn test -Dtest=*Test`
- Pruebas de integración: `mvn test -Dtest=*IntegrationTest`
- Pruebas BDD: `mvn test -Dtest=CucumberRunner`
- Pruebas de sistema: `mvn test -Dtest=*SystemTest`

## Configuraciones Adicionales

### Dependencias para BDD
Se han agregado las dependencias de Cucumber para implementar las pruebas BDD:



### Configuración del Ejecutor de Cucumber
Se ha configurado un ejecutor de pruebas Cucumber para integrar las pruebas BDD:

```java
@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = "com.upc.aventurape.platform.bdd",
        plugin = {"pretty", "html:target/cucumber-report.html"},
        tags = "not @ignore"
)
public class CucumberRunner {
    // Esta clase actúa como punto de entrada para ejecutar pruebas de Cucumber
}
```

## Conclusiones

La implementación de estas diferentes capas de pruebas asegura la calidad del backend de AventurePe, verificando tanto el funcionamiento correcto de los componentes individuales como su integración y comportamiento desde la perspectiva del usuario final. Esta estrategia de pruebas permite detectar y corregir errores en diferentes niveles de la aplicación, contribuyendo a un producto más robusto y confiable.

Las pruebas implementadas siguen las mejores prácticas de la industria:
1. **Aislamiento**: Las pruebas unitarias están aisladas y no dependen de servicios externos.
2. **Cobertura**: Se cubren las entidades principales y los flujos críticos del sistema.
3. **Claridad**: Las pruebas BDD proporcionan una documentación viva de las funcionalidades.
4. **Completitud**: Las pruebas de sistema validan los escenarios completos de usuario. 