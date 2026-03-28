CREATE DATABASE IF NOT EXISTS quizdb;
USE quizdb;

-- USERS TABLE
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100),
    email VARCHAR(150) UNIQUE,
    password VARCHAR(100),
    score INT DEFAULT 0,
    role VARCHAR(50)
);

-- QUESTIONS TABLE
CREATE TABLE IF NOT EXISTS questions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    question_text TEXT,
    option1 VARCHAR(255),
    option2 VARCHAR(255),
    option3 VARCHAR(255),
    option4 VARCHAR(255),
    correct_option INT,
    subject VARCHAR(100),
    topic VARCHAR(100),
    difficulty VARCHAR(50),
    year INT
);

-- SAFE USER INSERT
INSERT IGNORE INTO users (username, email, password, role)
VALUES ('mick', 'mick@gmail.com', '1234', 'student');

-- CLEAN TABLE (NO ERROR)
TRUNCATE TABLE questions;

-- =========================
-- 🔥 60 JAVA QUESTIONS
-- =========================

INSERT INTO questions 
(question_text, option1, option2, option3, option4, correct_option, subject, topic, difficulty, year)
VALUES

('What is Java?','Programming Language','OS','Hardware','None',1,'Java','Basics','Easy',2023),
('Which method is entry point?','start()','main()','run()','init()',2,'Java','Basics','Easy',2023),
('Which keyword creates object?','new','make','init','create',1,'Java','OOP','Easy',2023),
('Inheritance keyword?','this','super','extends','class',3,'Java','OOP','Medium',2023),
('Current object keyword?','this','super','object','class',1,'Java','OOP','Easy',2023),
('Not primitive type?','int','float','String','char',3,'Java','Data Types','Easy',2023),
('Comparison operator?','=','==','!=','>=',2,'Java','Operators','Easy',2023),
('Loop runs once?','for','while','do-while','none',3,'Java','Loops','Medium',2023),
('Define class keyword?','class','define','new','object',1,'Java','Basics','Easy',2023),
('Print function?','print()','println()','echo()','display()',2,'Java','Basics','Easy',2023),

('Stop loop keyword?','break','exit','stop','end',1,'Java','Control','Easy',2023),
('Decision keyword?','if','loop','switch','case',1,'Java','Control','Easy',2023),
('Exception handling keyword?','try','catch','throw','all',4,'Java','Exception','Medium',2023),
('Always executed block?','try','catch','finally','throw',3,'Java','Exception','Easy',2023),
('Throw exception keyword?','throw','throws','catch','try',1,'Java','Exception','Medium',2023),

('Interface keyword?','interface','class','abstract','extends',1,'Java','OOP','Medium',2023),
('Override annotation?','override','@Override','extends','implements',2,'Java','OOP','Medium',2023),
('Implement interface?','implements','extends','interface','class',1,'Java','OOP','Medium',2023),
('Static keyword use?','static','final','const','void',1,'Java','Basics','Easy',2023),
('Collection duplicates?','Set','List','Map','Tree',2,'Java','Collections','Medium',2023),

('Array index starts from?','0','1','-1','none',1,'Java','Arrays','Easy',2023),
('Size method of array?','size()','length','count()','getSize()',2,'Java','Arrays','Easy',2023),
('String is?','primitive','object','int','char',2,'Java','Strings','Easy',2023),
('Immutable means?','changeable','not changeable','delete','none',2,'Java','Strings','Medium',2023),
('Keyword for constant?','const','final','static','none',2,'Java','Basics','Easy',2023),

('Thread class keyword?','thread','Thread','Runnable','run',2,'Java','Multithreading','Medium',2023),
('Method overloading means?','same name diff params','diff name','same code','none',1,'Java','OOP','Medium',2023),
('Package keyword?','package','import','class','include',1,'Java','Packages','Easy',2023),
('Import keyword use?','add package','remove','create','none',1,'Java','Packages','Easy',2023),
('Scanner class used for?','input','output','delete','none',1,'Java','IO','Easy',2023),

('What is JDK?','Java Development Kit','Design Kit','Data Kit','None',1,'Java','Basics','Easy',2023),
('What is JRE?','Runtime Environment','Run Engine','Real Engine','None',1,'Java','Basics','Easy',2023),
('Keyword to inherit interface?','implements','extends','inherits','interface',1,'Java','OOP','Medium',2023),
('Encapsulation keyword?','private','public','protected','final',1,'Java','OOP','Medium',2023),
('Start thread method?','run()','start()','execute()','init()',2,'Java','Multithreading','Medium',2023),

('Parent class of all?','Object','Main','System','Class',1,'Java','OOP','Easy',2023),
('Prevent inheritance keyword?','final','static','const','private',1,'Java','OOP','Medium',2023),
('Logical AND operator?','&&','||','!','&',1,'Java','Operators','Easy',2023),
('Logical OR operator?','||','&&','!','|',1,'Java','Operators','Easy',2023),
('Scanner used for?','input','output','delete','none',1,'Java','IO','Easy',2023),

('Catch block use?','handle error','stop','run','none',1,'Java','Exception','Easy',2023),
('Array declaration?','[]','array','list','arr',1,'Java','Arrays','Easy',2023),
('String length method?','size()','length()','count()','get()',2,'Java','Strings','Easy',2023),
('Polymorphism means?','many forms','one form','none','all',1,'Java','OOP','Medium',2023),
('Map stores?','key-value','values','keys','none',1,'Java','Collections','Medium',2023),

('List is?','ordered','unordered','none','random',1,'Java','Collections','Medium',2023),
('Return keyword use?','exit method','loop','class','none',1,'Java','Control','Easy',2023),
('Continue keyword?','skip iteration','stop','exit','none',1,'Java','Control','Easy',2023),
('Date class use?','date','time','calendar','clock',1,'Java','API','Medium',2023),
('Import keyword?','import','include','package','use',1,'Java','Packages','Easy',2023),

('Runnable interface method?','run()','start()','init()','main()',1,'Java','Multithreading','Medium',2023),
('Thread pause method?','sleep()','wait()','stop()','pause()',1,'Java','Multithreading','Medium',2023);

-- CHECK
SELECT * FROM users;
SELECT * FROM questions;