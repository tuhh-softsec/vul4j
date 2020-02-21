-- liquibase formatted sql
-- changeset minhln:1.3

-- Add fields to table `pm_medical_record`
alter table pm_medical_record
    add column if not exists created_by bigint,
    add column if not exists updated_by bigint,
    add column if not exists created_at timestamp default now(),
    add column if not exists updated_by timestamp default now();

-- Change data type to decimal for storing currency
alter table pm_medical_record
    rename advisory_status_code to consulting_status_code;
alter table pm_medical_record
    alter column total_amount type decimal(19, 2);

alter table pm_medical_record
    alter column transfer_amount type decimal(19, 2);
alter table pm_medical_record
    alter column cod_amount type decimal(19, 2);


