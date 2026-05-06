package com.ecotrack.repository;

import com.ecotrack.entity.DataPenanaman;

import java.util.List;

public interface PenanamanRepository {
    List<DataPenanaman> findAll();      // Q-001
    boolean save(DataPenanaman d);      // Q-002
    boolean update(DataPenanaman d);    // Q-003
    boolean delete(String idPenanaman); // Q-004
}
