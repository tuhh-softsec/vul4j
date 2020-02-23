-- liquibase formatted sql
-- changeset nghiemnc:1.7


alter table pm_medical_record
    drop column if exists user_id;

