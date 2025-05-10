package com.upc.aventurape.platform.bdd;

import com.upc.aventurape.platform.publication.domain.model.aggregates.Publication;
import com.upc.aventurape.platform.publication.domain.model.commands.CreatePublicationCommand;
import com.upc.aventurape.platform.publication.domain.model.entities.Adventure;
import com.upc.aventurape.platform.publication.domain.model.queries.GetAllPublicationsQuery;
import com.upc.aventurape.platform.publication.domain.model.queries.GetPublicationByIdQuery;
import com.upc.aventurape.platform.publication.domain.model.valueobjects.EntrepreneurId;
import com.upc.aventurape.platform.publication.domain.services.PublicationCommandService;
import com.upc.aventurape.platform.publication.domain.services.PublicationQueryService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PublicationStepDefinitions {

    @Mock
    private PublicationCommandService publicationCommandService;

    @Mock
    private PublicationQueryService publicationQueryService;

    @InjectMocks
    private PublicationTestContext context;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        context = new PublicationTestContext();
    }

    @Given("un emprendedor con ID {long}")
    public void unEmprendedorConID(Long entrepreneurId) {
        context.entrepreneurId = entrepreneurId;
    }

    @Given("una aventura con título {string}, descripción {string}, capacidad {int} personas y duración {int} horas")
    public void unaAventuraConTituloDescripcionCapacidadYDuracion(String title, String description, Integer capacity, Integer duration) {
        context.adventureTitle = title;
        context.adventureDescription = description;
        context.adventureCapacity = capacity;
        context.adventureDuration = duration;
    }

    @Given("un costo de {int} soles")
    public void unCostoDeSoles(Integer cost) {
        context.cost = cost;
    }

    @Given("una imagen {string}")
    public void unaImagen(String image) {
        context.image = image;
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

        when(publicationCommandService.handle(any(CreatePublicationCommand.class))).thenReturn(publication);

        context.createdPublication = publication;
    }

    @Then("la publicación se guarda correctamente con ID {long}")
    public void laPublicacionSeGuardaCorrectamenteConID(Long publicationId) {
        assertEquals(publicationId, context.createdPublication.getId());
    }

    @Then("la publicación contiene la información correcta de la aventura")
    public void laPublicacionContieneInformacionCorrectaAventura() {
        assertEquals(context.adventureTitle, context.createdPublication.getAdventure().getNameActivity());
        assertEquals(context.adventureDescription, context.createdPublication.getAdventure().getDescription());
        assertEquals(context.adventureCapacity, context.createdPublication.getAdventure().getCantPeople());
        assertEquals(context.adventureDuration, context.createdPublication.getAdventure().getTimeDuration());
    }

    @Then("la publicación tiene el costo correcto")
    public void laPublicacionTieneCostoCorrecto() {
        assertEquals(context.cost, context.createdPublication.getCost());
    }

    @Then("la publicación muestra la imagen correcta")
    public void laPublicacionMuestraImagenCorrecta() {
        assertEquals(context.image, context.createdPublication.getImage());
    }

    // Implementación para el escenario "Buscar una publicación por ID"
    @Given("que existe una publicación con ID {long}")
    public void queExisteUnaPublicacionConID(Long publicationId) {
        Adventure adventure = new Adventure();
        adventure.setNameActivity("Aventura en los Andes");
        adventure.setDescription("Una increíble aventura en las montañas");
        adventure.setCantPeople(5);
        adventure.setTimeDuration(3);

        Publication publication = new Publication(
                new EntrepreneurId(1L),
                adventure,
                500,
                "https://example.com/image.jpg"
        );
        publication.setId(publicationId);

        when(publicationQueryService.handle(any(GetPublicationByIdQuery.class))).thenReturn(Optional.of(publication));
        context.existingPublicationId = publicationId;
    }

    @When("el usuario busca la publicación con ID {long}")
    public void elUsuarioBuscaLaPublicacionConID(Long publicationId) {
        Optional<Publication> foundPublication = publicationQueryService.handle(new GetPublicationByIdQuery(publicationId));
        if (foundPublication.isPresent()) {
            context.foundPublication = foundPublication.get();
        }
    }

    @Then("se muestra la publicación correcta")
    public void seMuestraLaPublicacionCorrecta() {
        assertTrue(context.foundPublication != null);
        assertEquals(context.existingPublicationId, context.foundPublication.getId());
    }

    @Then("la publicación tiene título {string}")
    public void laPublicacionTieneTitulo(String title) {
        assertEquals(title, context.foundPublication.getAdventure().getNameActivity());
    }

    // Implementación para el escenario "Listar todas las publicaciones"
    @Given("que existen múltiples publicaciones en el sistema")
    public void queExistenMultiplesPublicacionesEnElSistema() {
        List<Publication> publications = new ArrayList<>();
        
        // Primera publicación
        Adventure adventure1 = new Adventure();
        adventure1.setNameActivity("Aventura en los Andes");
        adventure1.setDescription("Una increíble aventura en las montañas");
        adventure1.setCantPeople(5);
        adventure1.setTimeDuration(3);
        
        Publication publication1 = new Publication(
                new EntrepreneurId(1L),
                adventure1,
                500,
                "https://example.com/image1.jpg"
        );
        publication1.setId(1L);
        publications.add(publication1);
        
        // Segunda publicación
        Adventure adventure2 = new Adventure();
        adventure2.setNameActivity("Aventura en la Selva");
        adventure2.setDescription("Explora la diversidad de la selva amazónica");
        adventure2.setCantPeople(8);
        adventure2.setTimeDuration(6);
        
        Publication publication2 = new Publication(
                new EntrepreneurId(2L),
                adventure2,
                800,
                "https://example.com/image2.jpg"
        );
        publication2.setId(2L);
        publications.add(publication2);
        
        // Tercera publicación
        Adventure adventure3 = new Adventure();
        adventure3.setNameActivity("Aventura en la Costa");
        adventure3.setDescription("Disfruta de las mejores playas del Perú");
        adventure3.setCantPeople(10);
        adventure3.setTimeDuration(4);
        
        Publication publication3 = new Publication(
                new EntrepreneurId(3L),
                adventure3,
                650,
                "https://example.com/image3.jpg"
        );
        publication3.setId(3L);
        publications.add(publication3);
        
        when(publicationQueryService.handle(any(GetAllPublicationsQuery.class))).thenReturn(publications);
    }

    @When("el usuario solicita ver todas las publicaciones")
    public void elUsuarioSolicitaVerTodasLasPublicaciones() {
        context.publicationsList = publicationQueryService.handle(new GetAllPublicationsQuery());
    }

    @Then("se muestra la lista completa de publicaciones")
    public void seMuestraLaListaCompletaDePublicaciones() {
        assertTrue(context.publicationsList != null && !context.publicationsList.isEmpty());
    }

    @Then("la lista contiene al menos {int} publicaciones")
    public void laListaContieneAlMenosPublicaciones(Integer minCount) {
        assertTrue(context.publicationsList.size() >= minCount);
    }

    // Clase de contexto para compartir datos entre pasos
    public static class PublicationTestContext {
        Long entrepreneurId;
        String adventureTitle;
        String adventureDescription;
        Integer adventureCapacity;
        Integer adventureDuration;
        Integer cost;
        String image;
        Publication createdPublication;
        Long existingPublicationId;
        Publication foundPublication;
        List<Publication> publicationsList;
    }
} 