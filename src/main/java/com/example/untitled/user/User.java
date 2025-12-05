package com.example.untitled.user;

import com.example.untitled.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "m_users")
@Data
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "m_users_seq")
    @SequenceGenerator(name = "m_users_seq", sequenceName = "m_users_id_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String userName;

    @Column(nullable = false, length = 20)
    private String password;
}
