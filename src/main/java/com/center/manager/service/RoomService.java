package com.center.manager.service;

import com.center.manager.model.Room;

import java.util.List;

public interface RoomService {

    List<Room> getAllRooms() throws Exception;

    Room getRoomById(Long roomId) throws Exception;
}
