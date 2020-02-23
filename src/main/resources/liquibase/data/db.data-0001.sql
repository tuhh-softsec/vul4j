-- liquibase formatted sql
-- changeset minhln:0001

insert into pm_consulting_status(name, code, sort, is_active)
values ('Đã khám', 'TTTV001', 1, true),
       ('Cân đối', 'TTTV002', 2, true),
       ('Tiềm năng', 'TTTV003', 3, true),
       ('Không có nhu cầu', 'TTTV004', 4, true),
       ('Gọi lại sau', 'TTTV005', 5, true);
