package com.example.untitled.artist;

import com.example.untitled.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "m_artists")
@Data
@EqualsAndHashCode(callSuper = true)
public class Artist extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "m_artists_seq")
    @SequenceGenerator(name = "m_artists_seq", sequenceName = "m_artists_id_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String artistName;

    @Column(length = 25)
    private String unitName;

    @Column(length = 20)
    private String content;
}
