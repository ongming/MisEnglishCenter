package com.center.manager.service;

import com.center.manager.model.Room;
import com.center.manager.repo.RoomRepository;
import com.center.manager.db.TransactionManager;
import jakarta.persistence.EntityManager;
import java.util.List;

public class RoomService {
    private final RoomRepository roomRepo;
    private final TransactionManager tx;

    public RoomService(RoomRepository roomRepo, TransactionManager tx) {
        this.roomRepo = roomRepo;
        this.tx = tx;
    }

    public List<Room> getAllRooms() throws Exception {
        return tx.runInTransaction(roomRepo::findAll);
    }

    public Room getRoomById(Long roomId) throws Exception {
        return tx.runInTransaction(em -> roomRepo.findById(em, roomId));
    }
}

