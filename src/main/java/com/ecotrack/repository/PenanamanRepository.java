package com.ecotrack.repository;

import com.ecotrack.entity.DataPenanaman;

import java.util.List;

public interface PenanamanRepository {
    List<DataPenanaman> findAll();          // Q-001
    DataPenanaman findById(String id);      // Q-002
    boolean save(DataPenanaman d);          // Q-003
    boolean update(DataPenanaman d);        // Q-004
    boolean delete(String idPenanaman);     // Q-005
}
