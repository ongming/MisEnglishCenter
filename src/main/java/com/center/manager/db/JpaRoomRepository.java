package com.center.manager.db;

import com.center.manager.model.Room;
import com.center.manager.repo.RoomRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class JpaRoomRepository implements RoomRepository {
    @Override
    public List<Room> findAll(EntityManager em) {
        TypedQuery<Room> query = em.createQuery("SELECT r FROM Room r WHERE r.status = 'Active'", Room.class);
        return query.getResultList();
    }

    @Override
    public Room findById(EntityManager em, Long roomId) {
        return em.find(Room.class, roomId);
    }
}

