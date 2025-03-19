package dat.controllers.impl;

import dat.config.HibernateConfig;
import dat.controllers.IController;
import dat.daos.impl.TodoDAO;
import dat.dtos.TodoDTO;
import dat.exceptions.ApiException;
import dk.bugelhartmann.UserDTO;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;

import java.util.List;

public class TodoController implements IController<TodoDTO, Integer> {
    private final TodoDAO dao;

    public TodoController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.dao = TodoDAO.getInstance(emf);
    }

    @Override
    public void read(Context ctx) throws ApiException, ApiException {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            TodoDTO todoDTO = dao.read(id);
            ctx.res().setStatus(200);
            ctx.json(todoDTO, TodoDTO.class);
        }
        catch (NumberFormatException e) {
            throw new ApiException(400, "Missing required parameter: id");
        }
    }

    @Override
    public void readAll(Context ctx) throws ApiException {
        List<TodoDTO> todoDTOS = dao.readAll();
        ctx.res().setStatus(200);
        ctx.json(todoDTOS, TodoDTO.class);
    }

    public void readAllFromUser(Context ctx) throws ApiException {
        UserDTO user = ctx.attribute("user");
        List<TodoDTO> todoDTOS = dao.readAllFromUser(user);
        ctx.res().setStatus(200);
        ctx.json(todoDTOS, TodoDTO.class);
    }

    @Override
    public void create(Context ctx) throws ApiException {
        TodoDTO todoDTO = ctx.bodyAsClass(TodoDTO.class);
        UserDTO user = ctx.attribute("user");
        todoDTO.setUser(user);
        todoDTO = dao.create(todoDTO);
        ctx.res().setStatus(201);
        ctx.json(todoDTO, TodoDTO.class);
    }

    @Override
    public void update(Context ctx) throws ApiException {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            TodoDTO todoDTOfromJson = ctx.bodyAsClass(TodoDTO.class);
            TodoDTO todoDTO = dao.update(id, todoDTOfromJson);
            ctx.res().setStatus(200);
            ctx.json(todoDTO, TodoDTO.class);
            ctx.res().setStatus(200);
            ctx.json(todoDTO, TodoDTO.class);
        }
        catch (NumberFormatException e) {
            throw new ApiException(400, "Missing required parameter: id");
        }
    }

    @Override
    public void delete(Context ctx) throws ApiException {
        try {
            UserDTO userDTO = ctx.attribute("user");
            int id = Integer.parseInt(ctx.pathParam("id"));
            dao.delete(id, userDTO);
            ctx.res().setStatus(204);
        }
        catch (NumberFormatException e) {
            throw new ApiException(400, "Missing required parameter: id");
        }
    }

    public void populate(Context ctx) throws ApiException {
        try {
            TodoDTO[] todoDTOS = dao.populate();
            ctx.res().setStatus(200);
            ctx.json("{ \"message\": \"Database has been populated with todos\" }");
        }
        catch (PersistenceException e) {
            throw new ApiException(400, "Populator went wrong, dude");
        }

    }

}
