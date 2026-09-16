-- ============================================================
-- CINEMA CNJ65 - DATABASE SCHEMA + SAMPLE DATA
-- He thong quan ly van hanh rap chieu phim
-- Java Servlet + JSP + JSTL + JDBC + MySQL
-- ============================================================

DROP DATABASE IF EXISTS cinema_cnj65;
CREATE DATABASE cinema_cnj65 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE cinema_cnj65;

-- ============================================================
-- 1. BANG USERS (Customer / Staff / Admin dung chung 1 bang)
-- ============================================================
CREATE TABLE users (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    username        VARCHAR(50)  NOT NULL UNIQUE,
    password        VARCHAR(255) NOT NULL,          -- BCrypt hash
    full_name       VARCHAR(100) NOT NULL,
    email           VARCHAR(100) NOT NULL UNIQUE,
    phone           VARCHAR(15)  NOT NULL,
    role            ENUM('CUSTOMER','STAFF','ADMIN') NOT NULL DEFAULT 'CUSTOMER',
    status          ENUM('ACTIVE','LOCKED') NOT NULL DEFAULT 'ACTIVE',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================================
-- 2. BANG GENRES (The loai phim)
-- ============================================================
CREATE TABLE genres (
    id      INT AUTO_INCREMENT PRIMARY KEY,
    name    VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB;

-- ============================================================
-- 3. BANG MOVIES (Phim)
-- ============================================================
CREATE TABLE movies (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    genre_id        INT NOT NULL,
    title           VARCHAR(200) NOT NULL,
    director        VARCHAR(100),
    actors          VARCHAR(255),
    duration        INT NOT NULL COMMENT 'Thoi luong (phut)',
    description     TEXT,
    poster_url      VARCHAR(255),
    trailer_url     VARCHAR(255),
    release_date    DATE,
    age_rating      VARCHAR(10) DEFAULT 'P' COMMENT 'P, C13, C16, C18',
    status          ENUM('COMING_SOON','NOW_SHOWING','ENDED') NOT NULL DEFAULT 'COMING_SOON',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (genre_id) REFERENCES genres(id)
) ENGINE=InnoDB;

-- ============================================================
-- 4. BANG ROOMS (Phong chieu)
-- ============================================================
CREATE TABLE rooms (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(50) NOT NULL,
    room_type       ENUM('2D','3D','IMAX') NOT NULL DEFAULT '2D',
    total_seats     INT NOT NULL DEFAULT 0,
    status          ENUM('ACTIVE','MAINTENANCE') NOT NULL DEFAULT 'ACTIVE'
) ENGINE=InnoDB;

-- ============================================================
-- 5. BANG SEATS (Ghe - co dinh theo tung phong)
-- ============================================================
CREATE TABLE seats (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    room_id     INT NOT NULL,
    seat_row    VARCHAR(2)  NOT NULL COMMENT 'A, B, C...',
    seat_number INT         NOT NULL COMMENT '1, 2, 3...',
    seat_code   VARCHAR(5)  NOT NULL COMMENT 'A01, A02...',
    seat_type   ENUM('NORMAL','VIP','COUPLE') NOT NULL DEFAULT 'NORMAL',
    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE,
    UNIQUE KEY uq_room_seat (room_id, seat_code)
) ENGINE=InnoDB;

-- ============================================================
-- 6. BANG SHOWTIMES (Suat chieu)
-- ============================================================
CREATE TABLE showtimes (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    movie_id        INT NOT NULL,
    room_id         INT NOT NULL,
    show_date       DATE NOT NULL,
    start_time      TIME NOT NULL,
    end_time        TIME NOT NULL,
    price_normal    DECIMAL(10,0) NOT NULL DEFAULT 80000,
    price_vip       DECIMAL(10,0) NOT NULL DEFAULT 100000,
    price_couple    DECIMAL(10,0) NOT NULL DEFAULT 150000,
    status          ENUM('ACTIVE','CANCELLED') NOT NULL DEFAULT 'ACTIVE',
    FOREIGN KEY (movie_id) REFERENCES movies(id),
    FOREIGN KEY (room_id)  REFERENCES rooms(id)
) ENGINE=InnoDB;

-- ============================================================
-- 7. BANG SHOWTIME_SEATS (Trang thai ghe theo TUNG suat chieu)
--    Day la bang QUAN TRONG NHAT de chong trung ghe + giu ghe tam
-- ============================================================
CREATE TABLE showtime_seats (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    showtime_id     INT NOT NULL,
    seat_id         INT NOT NULL,
    status          ENUM('AVAILABLE','LOCKED','BOOKED') NOT NULL DEFAULT 'AVAILABLE',
    locked_by       INT NULL COMMENT 'user_id dang giu ghe',
    locked_at       DATETIME NULL,
    FOREIGN KEY (showtime_id) REFERENCES showtimes(id) ON DELETE CASCADE,
    FOREIGN KEY (seat_id)     REFERENCES seats(id) ON DELETE CASCADE,
    FOREIGN KEY (locked_by)   REFERENCES users(id),
    UNIQUE KEY uq_showtime_seat (showtime_id, seat_id)
) ENGINE=InnoDB;

-- ============================================================
-- 8. BANG TICKETS (Ve)
-- ============================================================
CREATE TABLE tickets (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    ticket_code     VARCHAR(20) NOT NULL UNIQUE,
    user_id         INT NULL COMMENT 'NULL neu khach le do Staff ban tai quay',
    showtime_id     INT NOT NULL,
    staff_id        INT NULL COMMENT 'Nhan vien ban ve tai quay (NULL neu khach tu dat online)',
    guest_name      VARCHAR(100) NULL,
    guest_phone     VARCHAR(15)  NULL,
    total_amount    DECIMAL(12,0) NOT NULL,
    status          ENUM('PENDING','PAID','CANCELLED','CHECKED_IN') NOT NULL DEFAULT 'PENDING',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    checkin_at      DATETIME NULL,
    FOREIGN KEY (user_id)     REFERENCES users(id),
    FOREIGN KEY (showtime_id) REFERENCES showtimes(id),
    FOREIGN KEY (staff_id)    REFERENCES users(id)
) ENGINE=InnoDB;

-- ============================================================
-- 9. BANG TICKET_DETAILS (Chi tiet ghe trong 1 ve)
-- ============================================================
CREATE TABLE ticket_details (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    ticket_id   INT NOT NULL,
    seat_id     INT NOT NULL,
    price       DECIMAL(10,0) NOT NULL,
    FOREIGN KEY (ticket_id) REFERENCES tickets(id) ON DELETE CASCADE,
    FOREIGN KEY (seat_id)   REFERENCES seats(id)
) ENGINE=InnoDB;

-- ============================================================
-- 10. BANG PAYMENTS (Thanh toan)
-- ============================================================
CREATE TABLE payments (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    ticket_id   INT NOT NULL,
    amount      DECIMAL(12,0) NOT NULL,
    method      ENUM('CASH','CARD','MOMO','VNPAY','ZALOPAY') NOT NULL DEFAULT 'CASH',
    status      ENUM('SUCCESS','FAILED') NOT NULL DEFAULT 'SUCCESS',
    paid_at     DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ticket_id) REFERENCES tickets(id)
) ENGINE=InnoDB;

-- ============================================================
-- INDEX bo sung cho hieu nang
-- ============================================================
CREATE INDEX idx_movies_status ON movies(status);
CREATE INDEX idx_showtimes_date ON showtimes(show_date);
CREATE INDEX idx_showtime_seats_status ON showtime_seats(status, locked_at);
CREATE INDEX idx_tickets_user ON tickets(user_id);

-- ============================================================
-- DU LIEU MAU
-- ============================================================

-- Tai khoan mau (password thuc te la: 123456 -> da hash bang BCrypt)
-- BCrypt hash cua "123456" (cost=10)
INSERT INTO users (username, password, full_name, email, phone, role, status) VALUES
('admin',   '$2a$10$ryc.hfRALEwJWjC8m.Z6PO6O3D6udFfMQhl03cZu50UkW0c3gg7I.', 'Quan Tri Vien', 'admin@cnj65.vn', '0900000001', 'ADMIN', 'ACTIVE'),
('staff01', '$2a$10$ryc.hfRALEwJWjC8m.Z6PO6O3D6udFfMQhl03cZu50UkW0c3gg7I.', 'Nguyen Van Nhan Vien', 'staff01@cnj65.vn', '0900000002', 'STAFF', 'ACTIVE'),
('khang',   '$2a$10$ryc.hfRALEwJWjC8m.Z6PO6O3D6udFfMQhl03cZu50UkW0c3gg7I.', 'Tran Van Khang', 'khang@gmail.com', '0912345678', 'CUSTOMER', 'ACTIVE'),
('linh',    '$2a$10$ryc.hfRALEwJWjC8m.Z6PO6O3D6udFfMQhl03cZu50UkW0c3gg7I.', 'Le Thi Linh', 'linh@gmail.com', '0987654321', 'CUSTOMER', 'ACTIVE');

-- The loai
INSERT INTO genres (name) VALUES
('Hanh dong'), ('Kinh di'), ('Hoat hinh'), ('Tinh cam'), ('Hai huoc'), ('Vien tuong');

-- Phim
INSERT INTO movies (genre_id, title, director, actors, duration, description, poster_url, trailer_url, release_date, age_rating, status) VALUES
(1, 'Avengers: Ky Nguyen Moi', 'Anthony Russo', 'Robert Downey Jr, Chris Evans', 145, 'Cac sieu anh hung tap hop chong lai the luc ngoai hanh tinh moi.', '/assets/images/movie1.jpg', 'https://youtube.com', '2026-08-01', 'C13', 'NOW_SHOWING'),
(3, 'Conan: Bi An Ngoi Sao', 'Yamamoto', 'Conan Edogawa', 110, 'Tham tu Conan pha vu vu an bi mat lien quan den ngoi sao bang.', '/assets/images/movie2.jpg', 'https://youtube.com', '2026-08-10', 'P', 'NOW_SHOWING'),
(2, 'Ngoi Nha Ma Am', 'Nguyen Van A', 'Ngo Thanh Van', 100, 'Mot gia dinh chuyen den can nha co va phat hien bi mat kinh hoang.', '/assets/images/movie3.jpg', 'https://youtube.com', '2026-08-15', 'C16', 'NOW_SHOWING'),
(4, 'Mai Sau Con Yeu', 'Tran Thanh', 'Tran Thanh, Ninh Duong Lan Ngoc', 120, 'Cau chuyen tinh yeu day cam xuc giua hai nguoi tre.', '/assets/images/movie4.jpg', 'https://youtube.com', '2026-09-01', 'P', 'COMING_SOON'),
(6, 'Hanh Tinh Song Song', 'James Cameron', 'Sam Worthington', 150, 'Cuoc phieu luu vao vu tru song song day ky ao.', '/assets/images/movie5.jpg', 'https://youtube.com', '2026-09-15', 'C13', 'COMING_SOON');

-- Phong chieu
INSERT INTO rooms (name, room_type, total_seats, status) VALUES
('Phong 01', '2D', 40, 'ACTIVE'),
('Phong 02', '3D', 40, 'ACTIVE'),
('Phong 03', 'IMAX', 24, 'ACTIVE');

-- Sinh ghe cho Phong 01 (5 hang A-E, 8 cot, hang E la ghe doi)
DELIMITER $$
CREATE PROCEDURE gen_seats(IN p_room_id INT, IN p_rows INT, IN p_cols INT, IN p_couple_row INT)
BEGIN
    DECLARE r INT DEFAULT 0;
    DECLARE c INT DEFAULT 0;
    DECLARE row_char CHAR(1);
    DECLARE s_type VARCHAR(10);
    WHILE r < p_rows DO
        SET row_char = CHAR(65 + r);
        SET c = 1;
        WHILE c <= p_cols DO
            IF r = p_couple_row THEN
                SET s_type = 'COUPLE';
            ELSEIF r >= p_rows - 2 THEN
                SET s_type = 'VIP';
            ELSE
                SET s_type = 'NORMAL';
            END IF;
            INSERT INTO seats (room_id, seat_row, seat_number, seat_code, seat_type)
            VALUES (p_room_id, row_char, c, CONCAT(row_char, LPAD(c,2,'0')), s_type);
            SET c = c + 1;
        END WHILE;
        SET r = r + 1;
    END WHILE;
END$$
DELIMITER ;

CALL gen_seats(1, 5, 8, 4);   -- Phong 01: 5 hang x 8 cot, hang E (index 4) la ghe doi
CALL gen_seats(2, 5, 8, 4);   -- Phong 02
CALL gen_seats(3, 4, 6, -1);  -- Phong 03: khong co ghe doi

DROP PROCEDURE gen_seats;

-- Suat chieu mau (hom nay + ngay mai)
INSERT INTO showtimes (movie_id, room_id, show_date, start_time, end_time, price_normal, price_vip, price_couple, status) VALUES
(1, 1, CURDATE(), '13:00:00', '15:25:00', 80000, 100000, 150000, 'ACTIVE'),
(1, 1, CURDATE(), '19:00:00', '21:25:00', 90000, 110000, 160000, 'ACTIVE'),
(2, 2, CURDATE(), '14:00:00', '15:50:00', 70000, 90000, 140000, 'ACTIVE'),
(3, 3, CURDATE(), '20:00:00', '21:40:00', 100000, 130000, 180000, 'ACTIVE'),
(1, 1, CURDATE() + INTERVAL 1 DAY, '13:00:00', '15:25:00', 80000, 100000, 150000, 'ACTIVE'),
(2, 2, CURDATE() + INTERVAL 1 DAY, '16:00:00', '17:50:00', 70000, 90000, 140000, 'ACTIVE');

-- Sinh showtime_seats cho tat ca suat chieu (tat ca ghe = AVAILABLE)
INSERT INTO showtime_seats (showtime_id, seat_id, status)
SELECT st.id, se.id, 'AVAILABLE'
FROM showtimes st
JOIN seats se ON se.room_id = st.room_id;

-- Danh dau vai ghe da BOOKED de demo giao dien "ghe da ban"
UPDATE showtime_seats SET status='BOOKED'
WHERE showtime_id = 2 AND seat_id IN (SELECT id FROM seats WHERE room_id=1 AND seat_code IN ('C03','C04','C05'));

-- Ghi chu: mat khau demo cho tat ca tai khoan mau la "123456"
