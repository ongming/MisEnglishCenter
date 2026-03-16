package com.center.manager.service.impl;

import com.center.manager.db.TransactionManager;
import com.center.manager.model.Room;
import com.center.manager.repo.RoomRepository;
import com.center.manager.service.RoomService;

import java.util.List;

/**
 * Implement RoomService de truy van danh sach phong hoc.
 */
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepo;
    private final TransactionManager tx;

    public RoomServiceImpl(RoomRepository roomRepo, TransactionManager tx) {
        this.roomRepo = roomRepo;
        this.tx = tx;
    }

    @Override
    public List<Room> getAllRooms() throws Exception {
        return tx.runInTransaction(roomRepo::findAll);
    }

    @Override
    public Room getRoomById(Long roomId) throws Exception {
        return tx.runInTransaction(em -> roomRepo.findById(em, roomId));
    }
}

