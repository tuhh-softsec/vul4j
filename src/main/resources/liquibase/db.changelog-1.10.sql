-- liquibase formatted sql
-- changeset tuanpm:1.10

-- Create table pm_clinic_branch
drop table if exists pm_clinic_branch;
create table pm_clinic_branch
(
    id          serial primary key,
    name        varchar(255),
    created_at  timestamp default now(),
    updated_at  timestamp default now(),
    created_by  bigint,
    updated_by  bigint,
    is_active   bool
);

alter table pm_medical_record
    add column if not exists clinic_branch_id bigint default null;
