package com.jar.kiranaregister.feature_users.model.entity;

import com.jar.kiranaregister.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import java.util.List;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "users")
public class UserEntity {

    @Id private String id; // MongoDB uses String for ID

    @Indexed(unique = true)
    private String phoneNumber;

    private String userName;
    private String password;

    @Enumerated(EnumType.STRING)
    private List<Role> roles;
}
