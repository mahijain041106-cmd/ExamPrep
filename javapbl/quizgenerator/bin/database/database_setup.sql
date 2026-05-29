-- ExamPrep database setup (quizdb)
-- Run this script in MySQL Workbench or: mysql -u root -p < database_setup.sql

CREATE DATABASE IF NOT EXISTS quizdb;
USE quizdb;

CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('admin','student') DEFAULT 'student',
    total_score INT DEFAULT 0,
    total_quiz_attempted INT DEFAULT 0,
    total_questions_attempted INT DEFAULT 0,
    correct_answers INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS questions (
    question_id INT PRIMARY KEY AUTO_INCREMENT,
    question_text TEXT NOT NULL,
    option1 VARCHAR(255) NOT NULL,
    option2 VARCHAR(255) NOT NULL,
    option3 VARCHAR(255) NOT NULL,
    option4 VARCHAR(255) NOT NULL,
    correct_option INT NOT NULL,
    subject VARCHAR(100),
    topic VARCHAR(100),
    difficulty ENUM('easy','medium','hard'),
    year YEAR
);

-- Default admin (change password after first login)
INSERT INTO users (name, email, password, role)
SELECT 'Admin', 'admin@gmail.com', 'admin123', 'admin'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@gmail.com');

-- Sample questions (optional – for testing Start Quiz)
INSERT INTO questions (question_text, option1, option2, option3, option4, correct_option, subject, topic, difficulty)
SELECT 'What is JVM?', 'Java Virtual Machine', 'Java Variable Method', 'Joint Virtual Mode', 'None', 1, 'Java', 'Basics', 'easy'
WHERE NOT EXISTS (SELECT 1 FROM questions LIMIT 1);

INSERT INTO questions (question_text, option1, option2, option3, option4, correct_option, subject, topic, difficulty)
SELECT 'Which keyword defines a class in Java?', 'class', 'struct', 'define', 'object', 1, 'Java', 'OOP', 'medium'
WHERE (SELECT COUNT(*) FROM questions WHERE difficulty='medium') < 1;

INSERT INTO questions (question_text, option1, option2, option3, option4, correct_option, subject, topic, difficulty)
SELECT 'What is normalization in DBMS?', 'Reduce redundancy', 'Increase redundancy', 'Delete database', 'Encrypt tables', 1, 'DBMS', 'Design', 'hard'
WHERE (SELECT COUNT(*) FROM questions WHERE difficulty='hard') < 1;
