package dat.routes;

import dat.controllers.impl.TodoController;
import dat.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.delete;

public class TodoRoute {
    private final TodoController todoController = new TodoController();

    protected EndpointGroup getRoutes() {

        return () -> {
            post("/populate", todoController::populate, Role.ANYONE);
            post("/", todoController::create, Role.USER);
            get("/", todoController::readAll, Role.ANYONE);
            get("/mine", todoController::readAllFromUser, Role.USER);
            get("/{id}", todoController::read, Role.USER);
            put("/{id}", todoController::update, Role.USER);
            delete("/{id}", todoController::delete, Role.USER);
        };
    }
}
