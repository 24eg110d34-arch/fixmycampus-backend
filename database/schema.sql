-- FixMyCampus reference schema (MySQL 8).
-- You do NOT need to run this: Spring Boot / Hibernate creates the tables automatically
-- (spring.jpa.hibernate.ddl-auto=update). It is here for your report, viva and ER diagram.

CREATE DATABASE IF NOT EXISTS fixmycampus;
USE fixmycampus;

CREATE TABLE users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,            -- BCrypt hash
    role        VARCHAR(20)  NOT NULL,            -- STUDENT | STAFF | ADMIN
    created_at  DATETIME     NOT NULL
);

CREATE TABLE departments (
    id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    name  VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE issues (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    title          VARCHAR(150)  NOT NULL,
    category       VARCHAR(30)   NOT NULL,        -- FURNITURE | ELECTRICAL | WATER_LEAKAGE | CLEANLINESS | NETWORK | OTHER
    location       VARCHAR(150)  NOT NULL,
    description    VARCHAR(2000) NOT NULL,
    priority       VARCHAR(20)   NOT NULL,        -- LOW | MEDIUM | HIGH | CRITICAL
    status         VARCHAR(20)   NOT NULL,        -- REPORTED | ASSIGNED | IN_PROGRESS | RESOLVED
    image_path     VARCHAR(255),
    reported_by    BIGINT        NOT NULL,
    department_id  BIGINT,
    remarks        VARCHAR(1000),
    created_at     DATETIME      NOT NULL,
    updated_at     DATETIME      NOT NULL,
    resolved_at    DATETIME,
    FOREIGN KEY (reported_by)   REFERENCES users(id),
    FOREIGN KEY (department_id) REFERENCES departments(id)
);

CREATE TABLE issue_history (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    issue_id    BIGINT       NOT NULL,
    status      VARCHAR(20)  NOT NULL,
    note        VARCHAR(1000),
    changed_by  VARCHAR(100) NOT NULL,
    changed_at  DATETIME     NOT NULL,
    FOREIGN KEY (issue_id) REFERENCES issues(id)
);
