--liquibase formatted sql
--changeset author:admin:002

INSERT INTO users (name, email, age, created_at) VALUES
 ('Федор Сергеев', 'fs@example.com', 23, NOW()),
 ('Светлана Петрова', 'svet@example.com', 39, NOW()),
 ('Дмитрий Сидоров', 'dmitry@example.com', 25, NOW()),
 ('Mark Twain', 'twain@example.com', 190, NOW());
