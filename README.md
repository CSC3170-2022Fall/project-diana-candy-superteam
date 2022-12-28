[![Open in Visual Studio Code](https://classroom.github.com/assets/open-in-vscode-c66648af7eb3fe8bc4f294546bfd86ef473780cde1dea487d3c4ff354943c9ae.svg)](https://classroom.github.com/online_ide?assignment_repo_id=9433861&assignment_repo_type=AssignmentRepo)
# CSC3170 Course Project

## Project Overall Description

This is our implementation for the course project of CSC3170, 2022 Fall, CUHK(SZ). For details of the project, you can refer to [project-description.md](project-description.md). In this project, we will utilize what we learned in the lectures and tutorials in the course, and implement either one of the following major jobs:

<!-- Please fill in "x" to replace the blank space between "[]" to tick the todo item; it's ticked on the first one by default. -->

- [ ] **Application with Database System(s)**
- [x] **Implementation of a Database System**

## Team Members

Our team consists of the following members, listed in the table below (the team leader is shown in the first row, and is marked with 🚩 behind his/her name):

| Student ID | Student Name | GitHub Account (in Email) | GitHub UserName |
| ---------- | ------------ | ------------------------- | --------------- |
| 119020401  | 王嘉茗 🚩     | 119020401@link.cuhk.edu.cn| @[Jiuma141](https://github.com/Jiuma141) |
| 121090003  | 包景致        | zqlwmatt@gmail.com        | @[ZqlwMatt](https://github.com/ZqlwMatt) |
| 120040061  | 汪宁远        | 120040061@link.cuhk.edu.cn| @[0x727AA7D](https://github.com/0x727AA7D) |
| 120090108  | 周炜          | 120090108@link.cuhk.edu.cn| @[WilhelmZhou](https://github.com/WilhelmZhou) |
| 120090792  | 黄子蒙        | 2207070095@qq.com         | @[Master-Pyda](https://github.com/Master-Pyda) |
| 120090784  | 尹启骅        | 120090784@link.cuhk.edu.cn| @[edward-coding](https://github.com/edward-coding) |
| 119010306  | 王睿奕        | 119010306@link.cuhk.edu.cn| @[wry1205](https://github.com/wry1205) |

## Project Specification

After thorough discussion, our team made the choice and the specification information is listed below:

- Our option choice is: **Option 3**
- Our branch choice is: **Not applied**
- The difficulty level is: **Not applied**

---

## 📖Project Description

> This project involves writing a miniature relational database management system (DBMS) that stores tables of data, where a table consists of some number of labeled columns of information. Our system will include a very simple query language for extracting information from these tables. For the purposes of this project, we will deal only with very small databases, and therefore will not consider speed and efficiency at all.

**Full description:** https://inst.eecs.berkeley.edu/~cs61b/fa14/hw/proj1.pdf

## 📝Project Abstract

![project_description](https://user-images.githubusercontent.com/34508318/205655270-b90103a7-7664-4879-9aa8-5bd52fde1c8e.png)

Our project mainly focuses on the `Command Interpreter` module and database structures of database system. It will basically support these functions:

- Interprete SQL language.
- Support the basic structures of database system (e.g. database files, tables, rows, columns).
- Do fundamental queries on database system, including `select`, `from`, `where`, `insert into`, `create table` clauses.

**Project codes:** https://github.com/CSC3170-2022Fall/project-diana-candy-superteam/tree/db61b

## 🏅Achievement Display

![achievement](https://user-images.githubusercontent.com/34508318/208721246-1d4b5342-ab5a-45b7-a302-8964ff9e3a15.png)

## 💭Design Thoughts

### Database

The class that handles table instances. Specifically, the database class supports operations to store, retrieve, and delete tables. We utilize two ArrayList to store table instances and table names.

### Table

We consider `Column` to be an important attribute in the Table，and it is only constructed when the database loads `.db` files or executes `create` clause (for convenience, we call these tables `database tables`, which are stored directory in heap). namely, the `Column` is constructed at the same time as the `initial table`.

> Idea: the columns of a new table obtained by the `select clause` is a subset of the `database tables`. (We define a `temporary table` is a table created by a `select clause`, otherwise `standard table`. More specifically, the temporary table is one without explicit name)

To maintain this property, the Table should support two ways of construction:

1. Table(String[] columnTitles), used for `standard table`. (The columns are initialized by string array)
2. Table(List<Column> columns), used for `temporary table`. (The table borrows columns from the table in database)

By emphasizing the importance of `Column`, the role of `_columnTitles` (type: String[]) is diminished. We'll just use them as column titles to display to the user.

### Column

To reinforce the importance of the column, the `Column` class needs to record:

1. it belongs to which table,
2. its name,
3. the full name (`tableName_columnName`）

With item 3, we can solve the problem of attribute name dupilication.

> To supprt condition filering, we need to know where the rows in the table after cartesian product come from which table in `from clause`. We define a `super column` having the attribute of `offset`, which records the offset in the ultimate columns after cartesian product. Specifically, the `offset` attribute is derived when we do table join (cartesian product).

### Condition

We just fill out the framework provided by this project.

### Row

The simpliest unit records all data in String type. We just fill out the framework provided by this project.

## ⏰Timeline

![timeline](https://user-images.githubusercontent.com/34508318/209438409-99ef5fef-2cf6-4c11-a7a2-16e4f7b6d6bd.png)

## 🌟Highlights

In addition to the features mentioned in the design idea, we also made optimization in these aspects:

1. `Table.toString`: print table with adaptive adjustment.
2. `Table.select(List<String> columnNames, List<Condition> conditions)`: supports column names and conditions filtering, and we increase the robustness of the function.
3. `Column(String name, Table... tables)`: If the length of tables = 1, we are constructing a database table. Otherwise, we are constructing a super column. (robustness)
4. `Colume._offset`: use offset in super columns to derive the data after cartesian product.

## Presentation Video

[![](https://user-images.githubusercontent.com/34508318/209759625-98b32ebc-4e96-4b59-8229-edd329dcf70e.png)](https://www.bilibili.com/video/BV1wD4y1j7jP)

## 📍\*Optional Work

### 📘Notion Pamphlet (manual)

https://magic-chair-572.notion.site/CSC3170-Project-Pamphlet-c0cbaadee8814760a55b3a1eef0328b3

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

[Lab 1 result](https://cdn.zmatt.cn/img/archived/github/result-cmu15445-lab1.png)
