package com.jar.kiranaregister.model.DTOModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "product")
public class ProductDTO {

    private Integer id;
    private String name;
    private String description;

    public Integer getProductId() {
        return null;
    }
}
