package dat.entities;

import dat.daos.impl.TodoDAO;
import dat.dtos.TodoDTO;
import dat.security.entities.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private String description;
    private boolean done;

    @ToString.Exclude   // Avoid recursion
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "username", referencedColumnName = "username")
    private User user = null;

    public Todo(TodoDTO todoDTO) {
        this.id = todoDTO.getId();
        this.title = todoDTO.getTitle();
        this.description = todoDTO.getDescription();
        this.done = todoDTO.isDone();
        this.user = new User(todoDTO.getUser());
    }

    public void removeUser() {
        if (this.user != null) {
            this.user.getTodos().remove(this);
        }
        this.user = null;
    }

}
