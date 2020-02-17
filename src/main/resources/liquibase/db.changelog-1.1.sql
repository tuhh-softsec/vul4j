-- liquibase formatted sql
-- changeset taind:1.1

--Bac sy
CREATE TABLE "pm_doctor"
(
    "id"          SERIAL PRIMARY KEY,
    "name"        varchar(255) not null,
    "phone"       varchar(255),
    "address"     varchar(255),
    "description" text,
    "created_at"  timestamp,
    "updated_at"  timestamp,
    "created_by"  int8,
    "updated_by"  int8
);

--Phong kham
CREATE TABLE "pm_clinic"
(
    "id"          SERIAL PRIMARY KEY,
    "doctor_id"   int8         not null,
    "name"        varchar(255) not null,
    "phone"       varchar(255) not null,
    "address"     varchar(255) not null,
    "description" text,
    "created_at"  timestamp,
    "updated_at"  timestamp,
    "created_by"  int8,
    "updated_by"  int8
);

--Loai benh
CREATE TABLE "pm_disease"
(
    "id"          SERIAL PRIMARY KEY,
    "name"        varchar(255) not null,
    "description" text,
    "created_at"  timestamp,
    "updated_at"  timestamp,
    "created_by"  int8,
    "updated_by"  int8
);

--Vi thuoc
CREATE TABLE "pm_medicine_taste"
(
    "id"          SERIAL PRIMARY KEY,
    "disease_id"  int8         not null,
    "name"        varchar(255) not null,
    "description" text,
    "created_at"  timestamp,
    "updated_at"  timestamp,
    "created_by"  int8,
    "updated_by"  int8
);

--Vi thuoc cua loai benh
CREATE TABLE "pm_clinic_disease"
(
    "id"         SERIAL PRIMARY KEY,
    "clinic_id"  int8 not null,
    "disease_id" int8 not null
);

--Tinh trang tu van, du lieu addUser bang tay vao DB, khong co tinh nang quan ly
--Co 5 loai tinh trang tu van: Cân đối, Tiềm năng, Không có nhu cầu, Gọi lại sau, Đã khám
CREATE TABLE "pm_advisory_status"
(
    "id"   SERIAL PRIMARY KEY,
    "name" varchar(255) not null,
    "code" varchar(255) not null unique,
    "sort" int8
);

--Nguon quang cao
CREATE TABLE "pm_advertising_source"
(
    "id"          SERIAL PRIMARY KEY,
    "name"        varchar(255) not null,
    "description" text,
    "created_at"  timestamp,
    "updated_at"  timestamp,
    "created_by"  int8,
    "updated_by"  int8
);

--Benh nhan, bat buoc phai co it nhat 1 so dien thoai
CREATE TABLE "pm_patient"
(
    "id"          SERIAL PRIMARY KEY,
    "name"        varchar(255) not null,
    "age"         int8         not null,
    "address"     varchar(255) not null,
    "phone"       varchar(255),
    "zalo_phone"  varchar(255),
    "other_phone" varchar(255),
    "created_at"  timestamp,
    "updated_at"  timestamp,
    "created_by"  int8,
    "updated_by"  int8
);

--Benh an theo tung lan kham cua benh nhan
CREATE TABLE "pm_medical_record"
(
    "id"                    SERIAL PRIMARY KEY,
    "patient_id"            int8      not null,
    "user_code"             int8      not null,
    "advisory_date"         timestamp not null,
    "disease_id"            int8      not null,
    "advertising_source_id" int8      not null,
    "disease_status"        text      not null,
    "advisory_status_code"  text      not null,
    "note"                  text,
    "clinic_id"             int8      not null,
    "examination_date"      timestamp,
    "examination_times"     int8,
    "remedy_amount"         int8,
    "remedy_type"           varchar(255),
    "remedies"              text,
    "total_amount"          bigint,
    "transfer_amount"       bigint,
    "cod_amount"            bigint,
    "extra_note"            text
);

--Nhan vien phong kham
--Mot nhan vien chi co the lam viec duy nhat o mot phong kham
CREATE TABLE "pm_clinic_user"
(
    "id"        SERIAL PRIMARY KEY,
    "user_id"   int8 not null,
    "clinic_id" int8 not null
);

COMMENT ON COLUMN pm_medical_record.user_code IS 'Nhân viên tư vấn, bắt buộc nhân viên tư vấn phải nhập';
COMMENT ON COLUMN pm_medical_record.advisory_date IS 'Ngày tư vấn, bắt buộc nhân viên tư vấn phải nhập';
COMMENT ON COLUMN pm_medical_record.disease_id IS 'Loại bệnh, bắt buộc nhân viên tư vấn phải nhập';
COMMENT ON COLUMN pm_medical_record.advertising_source_id IS 'Nguồn quảng cáo, bắt buộc nhân viên tư vấn phải nhập';
COMMENT ON COLUMN pm_medical_record.disease_status IS 'Tình trạng bệnh, bắt buộc nhân viên tư vấn phải nhập';
COMMENT ON COLUMN pm_medical_record.advisory_status_code IS 'Tình trạng tư vấn, bắt buộc nhân viên tư vấn phải nhập';

COMMENT ON COLUMN pm_medical_record.clinic_id IS 'Phòng khám đã thực hiện lần khám này, bắt buộc nhân viên tư vấn phải nhập, nhân viên phòng khám có thể sửa';
COMMENT ON COLUMN pm_medical_record.examination_date IS 'Ngày khám, bắt buộc nhân viên phòng khám phải nhập';
COMMENT ON COLUMN pm_medical_record.examination_times IS 'Lần khám, hệ thống tự tính';
COMMENT ON COLUMN pm_medical_record.remedy_amount IS 'Số thang, bắt buộc nhân viên phòng khám phải nhập';
COMMENT ON COLUMN pm_medical_record.remedy_type IS 'Loại thuốc, bắt buộc nhân viên phòng khám phải nhập';
COMMENT ON COLUMN pm_medical_record.remedies IS 'Bài thuốc, bắt buộc nhân viên phòng khám phải nhập';
COMMENT ON COLUMN pm_medical_record.total_amount IS 'Tổng tiền mặt, bắt buộc nhân viên phòng khám phải nhập, mặc định là 0';
COMMENT ON COLUMN pm_medical_record.transfer_amount IS 'Tiền chuyển khoản, bắt buộc nhân viên phòng khám phải nhập, mặc định là 0';
COMMENT ON COLUMN pm_medical_record.cod_amount IS 'Tiền gửi COD, bắt buộc nhân viên phòng khám phải nhập, mặc định là 0';
COMMENT ON COLUMN pm_medical_record.extra_note IS 'Ghi chú của nhân viên phòng khám';

CREATE TABLE "pm_medical_record_medical_taste"
(
    "id"                SERIAL PRIMARY KEY,
    "medical_record_id" int not null,
    "medicine_taste_id" int not null,
    "qty"               int not null
);

ALTER TABLE "pm_clinic"
    ADD FOREIGN KEY ("doctor_id") REFERENCES "pm_doctor" ("id");

ALTER TABLE "pm_clinic_disease"
    ADD FOREIGN KEY ("clinic_id") REFERENCES "pm_clinic" ("id");

ALTER TABLE "pm_clinic_disease"
    ADD FOREIGN KEY ("disease_id") REFERENCES "pm_disease" ("id");

ALTER TABLE "pm_medicine_taste"
    ADD FOREIGN KEY ("disease_id") REFERENCES "pm_disease" ("id");

ALTER TABLE "pm_medical_record_medical_taste"
    ADD FOREIGN KEY ("medicine_taste_id") REFERENCES "pm_medicine_taste" ("id");

ALTER TABLE "pm_medical_record_medical_taste"
    ADD FOREIGN KEY ("medical_record_id") REFERENCES "pm_medical_record" ("id");

ALTER TABLE "pm_medical_record"
    ADD FOREIGN KEY ("patient_id") REFERENCES "pm_patient" ("id");

ALTER TABLE "pm_medical_record"
    ADD FOREIGN KEY ("advertising_source_id") REFERENCES "pm_advertising_source" ("id");

ALTER TABLE "pm_medical_record"
    ADD FOREIGN KEY ("clinic_id") REFERENCES "pm_clinic" ("id");

ALTER TABLE "pm_medical_record"
    ADD FOREIGN KEY ("disease_id") REFERENCES "pm_disease" ("id");

ALTER TABLE "pm_medical_record"
    ADD FOREIGN KEY ("advisory_status_code") REFERENCES "pm_advisory_status" ("code");
