package com.restaurant.pojo;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;

@Data
@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "category")
@NamedQuery(name = "Category.getAllCategory", query = "Select c from Category c where c.id in (select p from Product  p where p.status='true') ")
public class Category implements Serializable {

    private static final long serialVersionUUID=1l;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;



    



}
