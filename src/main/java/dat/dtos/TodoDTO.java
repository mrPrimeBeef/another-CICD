package dat.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dat.entities.Todo;
import dat.security.entities.User;
import dk.bugelhartmann.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TodoDTO {
    private Integer id;
    private String title;
    private String description;
    private boolean done;
    private UserDTO user = null;

    public TodoDTO(Todo todo){
        this.id = todo.getId();
        this.title = todo.getTitle();
        this.description = todo.getDescription();
        this.done = todo.isDone();
        if (todo.getUser() != null) {
            User userEntity = todo.getUser();
            this.user = new UserDTO(userEntity.getUsername(), userEntity.getRolesAsStrings());
        }
    }

}
