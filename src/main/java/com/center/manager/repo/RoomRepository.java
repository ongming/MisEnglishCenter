package com.center.manager.repo;

import com.center.manager.model.Room;
import jakarta.persistence.EntityManager;
import java.util.List;

public interface RoomRepository {
    List<Room> findAll(EntityManager em);
    Room findById(EntityManager em, Long roomId);
}

