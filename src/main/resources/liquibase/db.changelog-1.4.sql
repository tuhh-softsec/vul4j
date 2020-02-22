-- liquibase formatted sql
-- changeset minhln:1.4

alter table pm_medicine
    drop column if exists disease_id;

drop table if exists pm_medicine_disease;
create table pm_medicine_disease
(
    id          serial primary key,
    medicine_id bigint not null,
    disease_id  bigint not null
);