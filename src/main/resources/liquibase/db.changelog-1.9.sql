-- liquibase formatted sql
-- changeset minhln:1.9

-- Create table pm_pathology
drop table if exists pm_pathology;
create table pm_pathology
(
    id          serial primary key,
    name        varchar(255),
    description text,
    created_at  timestamp default now(),
    updated_at  timestamp default now(),
    created_by  bigint,
    updated_by  bigint,
    is_active   bool
);

-- Create table to mapping pathologies and patients
drop table if exists pm_patient_pathology;
create table pm_patient_pathology
(
    id           serial primary key,
    patient_id   bigint not null,
    pathology_id bigint not null
);