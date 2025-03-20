package dat.controllers.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dat.config.ApplicationConfig;
import dat.config.HibernateConfig;
import dat.daos.impl.TodoDAO;
import dat.dtos.TodoDTO;
import dat.entities.Todo;
import dat.exceptions.ApiException;
import dat.security.controllers.SecurityController;
import dat.security.daos.SecurityDAO;
import dat.security.entities.User;
import dat.security.exceptions.ValidationException;
import dk.bugelhartmann.UserDTO;
import io.javalin.Javalin;
import io.restassured.common.mapper.TypeRef;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TodoControllerTest {

    private final static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
    private final TodoController todoController = new TodoController();
    private final static TodoDAO todoDAO = TodoDAO.getInstance(emf);
    private final static SecurityDAO securityDAO = new SecurityDAO(emf);
    private final static SecurityController securityController = SecurityController.getInstance();
    private static Javalin app;
    private static UserDTO userDTO, adminDTO;
    private static String userToken, adminToken;
    private static final String BASE_URL = "http://localhost:7070/api";
    ObjectMapper objectMapper = new ObjectMapper();

    private static TodoDTO[] todoDTOS;
    private static UserDTO[] userDTOS;

    @BeforeAll
    void setUpAll() {
        HibernateConfig.setTest(true);
        app = ApplicationConfig.startServer(7070);
    }

    @BeforeEach
    void setUp() throws ApiException {
        System.out.println("Populating database with todos and users");

        todoDTOS = todoDAO.populate();
        userDTOS = todoDAO.getAllUsers();
        userDTO = userDTOS[0];
        adminDTO = userDTOS[1];

        try {
            UserDTO verifiedUser = securityDAO.getVerifiedUser(userDTO.getUsername(), userDTO.getPassword());
            UserDTO verifiedAdmin = securityDAO.getVerifiedUser(adminDTO.getUsername(), adminDTO.getPassword());
            userToken = "Bearer " + securityController.createToken(verifiedUser);
            adminToken = "Bearer " + securityController.createToken(verifiedAdmin);
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void tearDown() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Todo ").executeUpdate();
            em.createQuery("DELETE FROM Role").executeUpdate();
            em.createQuery("DELETE FROM User ").executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    void tearDownAll() {
        ApplicationConfig.stopServer(app);
    }

    @Test
    void read() {
        TodoDTO todoDTO =
                given()
                        .when()
                        .header("Authorization", userToken)
                        .get(BASE_URL + "/todos/1")
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(TodoDTO.class);

        assertThat(todoDTO.getTitle(), is("Buy milk"));
    }

    @Test
    void readAll() {
        List<TodoDTO> todoDTOList =
                given()
                        .when()
                        .header("Authorization", userToken)
                        .get(BASE_URL + "/todos")
                        .then()
                        .statusCode(200)
                        .body("size()", is(3))
                        .log().all()
                        .extract()
                        .as(new TypeRef<List<TodoDTO>>() {
                        });

        assertThat(todoDTOList.size(), is(3));
        assertThat(todoDTOList.get(0).getTitle(), is("Buy milk"));
    }

    @Test
    void readAllFromUser() {
        given()
                .when()
                .header("Authorization", userToken)
                .get(BASE_URL + "/todos")
                .then()
                .statusCode(200);
    }

    @Test
    void create() {
        User user = new User(userDTOS[0]);

        Todo todo = Todo.builder()
                .done(false)
                .title("TEST")
                .description("testy")
                .user(user)
                .build();
        TodoDTO todoDTO1 = new TodoDTO(todo);

        try {
            String json = objectMapper.writeValueAsString(todoDTO1);

           TodoDTO todoDTO = given()
                    .when()
                    .header("Authorization", userToken)
                    .body(json)
                    .post(BASE_URL + "/todos")
                    .then()
                    .statusCode(201)
                    .extract().as(TodoDTO.class);

            assertThat(todoDTO.getTitle(), is("TEST"));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }

    @Test
    void populate() {
    }
}