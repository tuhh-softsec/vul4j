-- liquibase formatted sql
-- changeset nghiemnc:1.5

alter table pm_patient
    drop column if exists za_lo_phone;

