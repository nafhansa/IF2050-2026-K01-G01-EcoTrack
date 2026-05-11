package com.ecotrack.repository;

import com.ecotrack.entity.DataPohon;

import java.util.List;

public interface PohonRepository {
    List<DataPohon> findAll();              // Q-007
    DataPohon findByIdOrNama(String q);     // Q-008
    boolean save(DataPohon d);              // Q-009
    boolean update(DataPohon d);            // Q-010
    boolean delete(String id);              // Q-011
}
