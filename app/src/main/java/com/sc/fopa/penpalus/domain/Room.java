package com.sc.fopa.penpalus.domain;
import java.util.List;

import lombok.Data;

@Data
public class Room {
    private String id;
    private String name;
    private String userA;
    private String userB;

    public Room() {
    }

    public Room(String id, String name, String userA, String userB) {
        this.id = id;
        this.name = name;
        this.userA = userA;
        this.userB = userB;
    }
}
