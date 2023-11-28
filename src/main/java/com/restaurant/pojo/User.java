package com.restaurant.pojo;


import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "user")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@NamedQuery(name = "User.findByEmailId",query = "SELECT u FROM  User as u where u.email=:email")
@NamedQuery(name = "User.getAllUser",query = "Select new com.restaurant.wrapper.UserWrapper(u.id,u.name,u.email,u.contactNumber,u.status) from User u where u.role='user'")
@NamedQuery(name = "User.updateStatus",query = "update User u set u.status=:status where u.id=:id")
@NamedQuery(name = "User.getAllAdmin",query = "Select u.email from User u where u.role='admin'")
public class User implements Serializable {

    private static final long serialVersionUID=1L;
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "id")
    private Integer id;

   @Column(name = "name")
   private String name;

   @Column(name = "contact_number")
   private String contactNumber;

   @Column(name = "email")
   private String email;

   @Column(name = "password")
   private String password;

   @Column(name = "status")
   private String status;

   @Column(name = "role")
   private String role;


}
