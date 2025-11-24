DROP DATABASE IF EXISTS ltm_final_project;
CREATE DATABASE IF NOT EXISTS ltm_final_project;
USE ltm_final_project;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS tasks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    topic VARCHAR(100) NOT NULL,
    source_content LONGTEXT NOT NULL,
    target_content LONGTEXT,
    comparison_details LONGTEXT,
    status ENUM('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED') DEFAULT 'PENDING',
    result INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Create dedicated application user (change credentials to fit your environment)
CREATE USER IF NOT EXISTS 'ltm_app_user'@'localhost' IDENTIFIED BY 'ChangeMe123!';
GRANT SELECT, INSERT, UPDATE, DELETE, ALTER ON ltm_final_project.* TO 'ltm_app_user'@'localhost';
FLUSH PRIVILEGES;

-- Insert dummy web user (password: 123456) for initial login
INSERT INTO users (username, password, full_name)
VALUES ('admin', '123456', 'Administrator')
ON DUPLICATE KEY UPDATE full_name = VALUES(full_name);
