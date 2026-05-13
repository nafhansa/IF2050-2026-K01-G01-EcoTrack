INSERT INTO data_pohon (id_pohon, id_user, nama_pohon, usia, lokasi, serapan_karbon, file_foto)
VALUES 
('P-01', 'user-001', 'Trembesi', 5, 'Bandung Utara', 28.5, 'pohon1.jpg'),
('P-02', 'user-002', 'Mahoni', 3, 'Bandung Tengah', 15.2, 'pohon2.jpg'),
('P-03', 'user-001', 'Beringin', 10, 'Alun-alun Bandung', 45.0, 'pohon3.jpg'),
('P-04', 'user-002', 'Sengon', 2, 'Ciburial', 10.5, 'pohon4.jpg'),
('P-05', 'user-001', 'Pinus', 7, 'Lembang', 20.0, 'pohon5.jpg'),
('P-06', 'user-002', 'Trembesi', 4, 'Pasteur', 22.1, 'pohon6.jpg'),
('P-07', 'user-001', 'Ketapang', 6, 'Cihampelas', 18.3, 'pohon7.jpg'),
('P-08', 'user-002', 'Jati', 12, 'Padalarang', 35.8, 'pohon8.jpg'),
('P-09', 'user-001', 'Angsana', 5, 'Dago', 14.2, 'pohon9.jpg'),
('P-10', 'user-002', 'Akasia', 3, 'Antapani', 12.0, 'pohon10.jpg');


INSERT INTO data_penanaman (id_penanaman, id_user, id_pohon, lokasi, jenis_pohon, jumlah_pohon, tanggal, estimasi_karbon)
VALUES 
('T-01', 'user-001', 'P-01', 'Taman Lansia', 'Trembesi', 20, '2026-01-10', 570.0),
('T-02', 'user-002', 'P-02', 'Babakan Siliwangi', 'Mahoni', 50, '2026-02-15', 760.0),
('T-03', 'user-001', 'P-04', 'Bukit Moko', 'Sengon', 100, '2026-03-20', 1050.0),
('T-04', 'user-002', 'P-06', 'Taman Film', 'Trembesi', 15, '2026-04-05', 331.5),
('T-05', 'user-001', 'P-09', 'Hutan Kota', 'Angsana', 30, '2026-05-12', 426.0);