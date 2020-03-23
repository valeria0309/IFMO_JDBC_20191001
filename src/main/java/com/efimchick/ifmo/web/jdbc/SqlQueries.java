package com.efimchick.ifmo.web.jdbc;

/**
 * Implement sql queries like described
 */
public class SqlQueries {
    //Select all employees sorted by last name in ascending order
    //language=HSQLDB
    public String select01 = "SELECT * FROM employee ORDER BY lastname";

    //Select employees having no more than 5 characters in last name sorted by last name in ascending order
    //language=HSQLDB
    public String select02 = "SELECT * FROM employee WHERE length(lastname) <= 5 ORDER BY lastname";

    //Select employees having salary no less than 2000 and no more than 3000
    //language=HSQLDB
    public String select03 = "SELECT * FROM employee WHERE salary >= 2000 AND salary <= 3000";


    //Select employees having salary no more than 2000 or no less than 3000
    //language=HSQLDB
    public String select04 = "SELECT * FROM employee WHERE salary >=3000 OR  salary <= 2000 ";


    //Select employees assigned to a department and corresponding department name
    //language=HSQLDB
    public String select05 = "select employee.id, employee.firstname, employee.lastname, employee.salary, department.name from employee, department where employee.department = department.id";

    //Select all employees and corresponding department name if there is one.
    //Name column containing name of the department "depname".
    //language=HSQLDB

    public String select06 = "SELECT employee.LASTNAME,  employee.SALARY, DEPARTMENT.NAME AS depname FROM employee LEFT OUTER JOIN DEPARTMENT ON employee.DEPARTMENT = DEPARTMENT.ID";


    //Select total salary pf all employees. Name it "total".
    //language=HSQLDB

    public String select07 = "SELECT sum(salary) AS total FROM employee";

    //Select all departments and amount of employees assigned per department
    //Name column containing name of the department "depname".
    //Name column containing employee amount "staff_size".
    //language=HSQLDB

    public String select08 ="SELECT DEPARTMENT.NAME as depname, COUNT(employee.ID) as staff_size FROM DEPARTMENT, employee WHERE DEPARTMENT.ID = employee.DEPARTMENT GROUP BY DEPARTMENT.NAME";

    //Select all departments and values of total and average salary per department
    //Name column containing name of the department "depname".
    //language=HSQLDB

    public String select09 = "SELECT DEPARTMENT.NAME AS depname, sum(employee.SALARY) AS total, AVG(EMPLOYEE.SALARY) AS average FROM employee INNER JOIN DEPARTMENT on DEPARTMENT.ID = employee.DEPARTMENT GROUP BY DEPARTMENT.NAME";

    //Select all employees and their managers if there is one.
    //Name column containing employee lastname "employee".
    //Name column containing manager lastname "manager".
    //language=HSQLDB

    public String select10 ="SELECT em.LASTNAME AS employee, ma.LASTNAME AS manager FROM employee AS em LEFT OUTER JOIN employee AS ma ON em.MANAGER = ma.ID";


}
