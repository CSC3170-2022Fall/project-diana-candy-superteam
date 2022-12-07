[![Open in Visual Studio Code](https://classroom.github.com/assets/open-in-vscode-c66648af7eb3fe8bc4f294546bfd86ef473780cde1dea487d3c4ff354943c9ae.svg)](https://classroom.github.com/online_ide?assignment_repo_id=9433861&assignment_repo_type=AssignmentRepo)
# CSC3170 Course Project

## Project Overall Description

This is our implementation for the course project of CSC3170, 2022 Fall, CUHK(SZ). For details of the project, you can refer to [project-description.md](project-description.md). In this project, we will utilize what we learned in the lectures and tutorials in the course, and implement either one of the following major jobs:

<!-- Please fill in "x" to replace the blank space between "[]" to tick the todo item; it's ticked on the first one by default. -->

- [ ] **Application with Database System(s)**
- [x] **Implementation of a Database System**

## Team Members

Our team consists of the following members, listed in the table below (the team leader is shown in the first row, and is marked with 🚩 behind his/her name):

<!-- change the info below to be the real case -->

| Student ID | Student Name | GitHub Account (in Email) | GitHub UserName |
| ---------- | ------------ | ------------------------- | --------------- |
| 119020401  | 王嘉茗 🚩     | 119020401@link.cuhk.edu.cn|                  |
| 121090003  | 包景致        | zqlwmatt@gmail.com        | @[ZqlwMatt](https://github.com/ZqlwMatt) |
| 120040061  | 汪宁远        | 120040061@link.cuhk.edu.cn|                 |
| 120090108  | 周炜          | 120090108@link.cuhk.edu.cn|                 |
| 120090792  | 黄子蒙        | 2207070095@qq.com         |                 |
| 120090784  | 尹启骅        | 120090784@link.cuhk.edu.cn|                 |
| 119010306  | 王睿奕        | 119010306@link.cuhk.edu.cn|                 |

## Project Specification

<!-- You should remove the terms/sentence that is not necessary considering your option/branch/difficulty choice -->

After thorough discussion, our team made the choice and the specification information is listed below:

- Our option choice is: **Option 3**
- Our branch choice is: **Not applied**
- The difficulty level is: **Not applied**

## Project Description

> This project involves writing a miniature relational database management system (DBMS) that stores tables of data, where a table consists of some number of labeled columns of information. Our system will include a very simple query language for extracting information from these tables. For the purposes of this project, we will deal only with very small databases, and therefore will not consider speed and efficiency at all.

## Project Abstract

![project_description](https://user-images.githubusercontent.com/34508318/205655270-b90103a7-7664-4879-9aa8-5bd52fde1c8e.png)

Our project mainly focuses on the `Command Interpreter` module and database structures of database system. It will basically support these functions:

- Interprete SQL language.
- Support the basic structures of database system (e.g. database file, table, row, column).
- Do fundamental queries on database system, including `select`, `from`, `where` clauses.

**Project codes:** https://github.com/CSC3170-2022Fall/project-diana-candy-superteam/tree/db61b


## Timeline

<!--Todo-->

## \*Optional Work

### \[Option4\] CMU 15-445/645, Fall 2021: Database Systems

- [x] Lab 1: Buffer Pool Manager
    - [x] [LRU Replacement Policy](https://15445.courses.cs.cmu.edu/fall2021/project1/#replacer)
    - [x] [Buffer Pool Manager Instance](https://15445.courses.cs.cmu.edu/fall2021/project1/#buffer-pool-instance)
    - [x] [Parallel Buffer Pool Manager](https://15445.courses.cs.cmu.edu/fall2021/project1/#parallel-buffer-pool)
- [ ] Lab 2: Hash Index

    - [ ] [Page Layouts](https://15445.courses.cs.cmu.edu/fall2021/project2/#task1)
    - [ ] [Extendible Hashing Implementation](https://15445.courses.cs.cmu.edu/fall2021/project2/#task2)
    - [ ] [Concurrency Control](https://15445.courses.cs.cmu.edu/fall2021/project2/#task3)
- [ ] Lab 3: Query Execution

    - [ ] [Executors](https://15445.courses.cs.cmu.edu/fall2021/project3/)
- [ ] Lab 4: Concurrency Control
    - [x] [Lock Manager](https://15445.courses.cs.cmu.edu/fall2021/project4/#lock_manager)
    - [ ] [Deadlock Prevention](https://15445.courses.cs.cmu.edu/fall2021/project4/#deadlock_prevention)
    - [ ] [Concurrent Query Execution](https://15445.courses.cs.cmu.edu/fall2021/project4/#execution_engine) (has dependency on Lab 3)

**Project codes:** https://github.com/CSC3170-2022Fall/project-diana-candy-superteam/tree/cmu15445

### Result from Autograder

[Lab 1-result](https://cdn.zmatt.cn/img/archived/github/result-cmu15445-lab1.png)
