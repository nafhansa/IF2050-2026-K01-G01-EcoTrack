package com.ecotrack.repository;

import com.ecotrack.entity.DataPohon;

import java.util.List;

public interface PohonRepository {
    List<DataPohon> findAll();           // Q-005
    boolean save(DataPohon d);           // Q-006
    DataPohon findById(String id);       // Q-007
    boolean update(DataPohon d);         // Q-008
    boolean delete(String id);           // Q-009
    DataPohon findByNama(String nama);   // Q-010
}
