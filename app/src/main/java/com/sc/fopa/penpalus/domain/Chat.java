package com.sc.fopa.penpalus.domain;

import java.util.List;

import lombok.Data;

@Data
public class Chat {
    private List<Room> roomList;

    private List<User> userList;
}
