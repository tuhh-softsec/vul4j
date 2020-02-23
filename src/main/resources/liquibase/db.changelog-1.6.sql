-- liquibase formatted sql
-- changeset nghiemnc:1.6

alter table pm_medical_record
    drop constraint if exists pm_medical_record_disease_id_fkey;

alter table pm_medical_record
    alter column disease_id drop not null;

