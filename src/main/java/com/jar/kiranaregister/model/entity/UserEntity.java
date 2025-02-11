package com.jar.kiranaregister.model.entity;

import com.jar.kiranaregister.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "users")
public class UserEntity {

    @Id
    private String id;  // MongoDB uses String for ID

    private String phoneNumber;
    private String userName;
    private String password;

    @Enumerated(EnumType.STRING)
    private List<Role> roles;
}