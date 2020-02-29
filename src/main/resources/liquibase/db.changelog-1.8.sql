-- liquibase formatted sql
-- changeset nghiemnc:1.8

-- Create table pm_province and table pm_district
drop table if exists pm_province;
create table if not exists pm_province(
    id serial primary key ,
    name varchar(255) not null,
    type varchar(255) not null
);

drop table if exists pm_district;
create table if not exists pm_district(
  id serial primary key ,
  name varchar(255) not null ,
  type varchar(255) not null,
  province_id bigint not null
);