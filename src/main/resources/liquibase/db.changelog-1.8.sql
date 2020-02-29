-- liquibase formatted sql
-- changeset nghiemnc:1.8

-- Create table pm_province and table pm_district
drop table if exists pm_province;
create table if not exists pm_province
(
    id   serial primary key,
    name varchar(255) not null,
    code bigint not null
);

alter table pm_patient
    add column if not exists province_code bigint default null;