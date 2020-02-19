-- liquibase formatted sql
-- changeset minhln:1.2

alter table pm_advertising_source
    add column if not exists is_active bool;
alter table pm_clinic
    add column if not exists is_active bool;
alter table pm_consulting_status
    add column if not exists is_active bool;
alter table pm_disease
    add column if not exists is_active bool;
alter table pm_medical_record
    add column if not exists is_active bool;
alter table pm_medicine
    add column if not exists is_active bool;
alter table pm_patient
    add column if not exists is_active bool;
alter table pm_doctor
    add column if not exists is_active bool;


