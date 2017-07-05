package com.schenkbarnabas.tlog16rs.entities;

import javax.persistence.*;

/**
 * Created by bschenk on 7/5/17.
 */
@Entity
@lombok.Getter
@lombok.Setter
@Table(name = "test")
public class TestEntity {

    @Column(name = "text")
    String text;
    @Id
    @GeneratedValue
    @Column(name = "id")
    Integer id;
}
