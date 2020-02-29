-- liquibase formatted sql
-- changeset nghiemnc:0002

INSERT INTO pm_province
VALUES (1, 'Thành phố Hà Nội', 01);
INSERT INTO pm_province
VALUES (2, 'Tỉnh Hà Giang', 02);
INSERT INTO pm_province
VALUES (3, 'Tỉnh Cao Bằng', 04);
INSERT INTO pm_province
VALUES (4, 'Tỉnh Bắc Kạn', 06);
INSERT INTO pm_province
VALUES (5, 'Tỉnh Tuyên Quang', 08);
INSERT INTO pm_province
VALUES (6, 'Tỉnh Lào Cai', 10);
INSERT INTO pm_province
VALUES (7, 'Tỉnh Điện Biên', 11);
INSERT INTO pm_province
VALUES (8, 'Tỉnh Lai Châu', 12);
INSERT INTO pm_province
VALUES (9, 'Tỉnh Sơn La', 14);
INSERT INTO pm_province
VALUES (10, 'Tỉnh Yên Bái', 15);
INSERT INTO pm_province
VALUES (11, 'Tỉnh Hoà Bình', 17);
INSERT INTO pm_province
VALUES (12, 'Tỉnh Thái Nguyên', 19);
INSERT INTO pm_province
VALUES (13, 'Tỉnh Lạng Sơn', 20);
INSERT INTO pm_province
VALUES (14, 'Tỉnh Quảng Ninh', 22);
INSERT INTO pm_province
VALUES (15, 'Tỉnh Bắc Giang', 24);
INSERT INTO pm_province
VALUES (16, 'Tỉnh Phú Thọ', 25);
INSERT INTO pm_province
VALUES (17, 'Tỉnh Vĩnh Phúc', 26);
INSERT INTO pm_province
VALUES (18, 'Tỉnh Bắc Ninh', 27);
INSERT INTO pm_province
VALUES (19, 'Tỉnh Hải Dương', 30);
INSERT INTO pm_province
VALUES (20, 'Thành phố Hải Phòng', 31);
INSERT INTO pm_province
VALUES (21, 'Tỉnh Hưng Yên', 33);
INSERT INTO pm_province
VALUES (22, 'Tỉnh Thái Bình', 34);
INSERT INTO pm_province
VALUES (23, 'Tỉnh Hà Nam', 35);
INSERT INTO pm_province
VALUES (24, 'Tỉnh Nam Định', 36);
INSERT INTO pm_province
VALUES (25, 'Tỉnh Ninh Bình', 37);
INSERT INTO pm_province
VALUES (26, 'Tỉnh Thanh Hóa', 38);
INSERT INTO pm_province
VALUES (27, 'Tỉnh Nghệ An', 40);
INSERT INTO pm_province
VALUES (28, 'Tỉnh Hà Tĩnh', 42);
INSERT INTO pm_province
VALUES (29, 'Tỉnh Quảng Bình', 44);
INSERT INTO pm_province
VALUES (30, 'Tỉnh Quảng Trị', 45);
INSERT INTO pm_province
VALUES (31, 'Tỉnh Thừa Thiên Huế', 46);
INSERT INTO pm_province
VALUES (32, 'Thành phố Đà Nẵng', 48);
INSERT INTO pm_province
VALUES (33, 'Tỉnh Quảng Nam', 49);
INSERT INTO pm_province
VALUES (34, 'Tỉnh Quảng Ngãi', 51);
INSERT INTO pm_province
VALUES (35, 'Tỉnh Bình Định', 52);
INSERT INTO pm_province
VALUES (36, 'Tỉnh Phú Yên', 54);
INSERT INTO pm_province
VALUES (37, 'Tỉnh Khánh Hòa', 56);
INSERT INTO pm_province
VALUES (38, 'Tỉnh Ninh Thuận', 58);
INSERT INTO pm_province
VALUES (39, 'Tỉnh Bình Thuận', 60);
INSERT INTO pm_province
VALUES (40, 'Tỉnh Kon Tum', 62);
INSERT INTO pm_province
VALUES (41, 'Tỉnh Gia Lai', 64);
INSERT INTO pm_province
VALUES (42, 'Tỉnh Đắk Lắk', 66);
INSERT INTO pm_province
VALUES (43, 'Tỉnh Đắk Nông', 67);
INSERT INTO pm_province
VALUES (44, 'Tỉnh Lâm Đồng', 68);
INSERT INTO pm_province
VALUES (45, 'Tỉnh Bình Phước', 70);
INSERT INTO pm_province
VALUES (46, 'Tỉnh Tây Ninh', 72);
INSERT INTO pm_province
VALUES (47, 'Tỉnh Bình Dương', 74);
INSERT INTO pm_province
VALUES (48, 'Tỉnh Đồng Nai', 75);
INSERT INTO pm_province
VALUES (49, 'Tỉnh Bà Rịa - Vũng Tàu', 77);
INSERT INTO pm_province
VALUES (50, 'Thành phố Hồ Chí Minh', 79);
INSERT INTO pm_province
VALUES (51, 'Tỉnh Long An', 80);
INSERT INTO pm_province
VALUES (52, 'Tỉnh Tiền Giang', 82);
INSERT INTO pm_province
VALUES (53, 'Tỉnh Bến Tre', 83);
INSERT INTO pm_province
VALUES (54, 'Tỉnh Trà Vinh', 84);
INSERT INTO pm_province
VALUES (55, 'Tỉnh Vĩnh Long', 86);
INSERT INTO pm_province
VALUES (56, 'Tỉnh Đồng Tháp', 87);
INSERT INTO pm_province
VALUES (57, 'Tỉnh An Giang', 89);
INSERT INTO pm_province
VALUES (58, 'Tỉnh Kiên Giang', 91);
INSERT INTO pm_province
VALUES (59, 'Thành phố Cần Thơ', 92);
INSERT INTO pm_province
VALUES (60, 'Tỉnh Hậu Giang', 93);
INSERT INTO pm_province
VALUES (61, 'Tỉnh Sóc Trăng', 94);
INSERT INTO pm_province
VALUES (62, 'Tỉnh Bạc Liêu', 95);
INSERT INTO pm_province
VALUES (63, 'Tỉnh Cà Mau', 96);

ALTER SEQUENCE pm_province_id_seq RESTART WITH 64;
