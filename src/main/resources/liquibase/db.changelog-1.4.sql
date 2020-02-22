-- liquibase formatted sql
-- changeset nghiemnc:1.4

-- Change data type to decimal for storing currency
alter table pm_patient
    drop column za_lo_phone;





