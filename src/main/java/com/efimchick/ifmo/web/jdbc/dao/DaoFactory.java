package com.efimchick.ifmo.web.jdbc.dao;

import com.efimchick.ifmo.web.jdbc.ConnectionSource;
import com.efimchick.ifmo.web.jdbc.domain.Department;
import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DaoFactory {

    private ResultSet getResultSet(String statement, int SQLint) {
        try {

            String SQLString = "";

            switch (SQLint) {
                case 1: SQLString = "SELECT * FROM EMPLOYEE ";
                    break;
                case 2: SQLString = "SELECT * FROM DEPARTMENT ";
                    break;
                case 3: SQLString = "INSERT INTO ";
                    break;
                case 4: SQLString = "DELETE FROM ";
                    break;
                case 5: SQLString = "UPDATE ";
                    break;
                default:
                    break;
            }

            return ConnectionSource
                    .instance()
                    .createConnection()
                    .createStatement()
                    .executeQuery(SQLString + statement);
        } catch (SQLException e) {
            return null;
        }
    }

    private List<Employee> getEmployeesArrayListByResultSet(String query) {
        List<Employee> employees = new ArrayList<>();

        try {
            ResultSet resultSet = getResultSet(query, 1);

            while (resultSet.next()) {
                employees.add(getEmployee(resultSet));
            }
        } catch (SQLException e) {
            return null;
        }
        return employees;
    }

    public EmployeeDao employeeDAO() {
        return new EmployeeDao() {
            @Override
            public List<Employee> getByDepartment(Department department) {
                return getEmployeesArrayListByResultSet("WHERE DEPARTMENT = " + department.getId());
            }

            @Override
            public List<Employee> getByManager(Employee employee) {
                return getEmployeesArrayListByResultSet("WHERE MANAGER = " + employee.getId());
            }

            @Override
            public Optional<Employee> getById(BigInteger Id) {
                try {
                    ResultSet resultSet = getResultSet("WHERE ID = " + Id, 1);

                    assert resultSet != null;
                    if (resultSet.next()) {
                        return Optional.ofNullable(getEmployee(resultSet));
                    } else {
                        return Optional.empty();
                    }
                } catch (SQLException e) {
                    return Optional.empty();
                }
            }

            @Override
            public List<Employee> getAll() {
                return getEmployeesArrayListByResultSet(null);
            }

            @Override
            public Employee save(Employee employee) {
                String longStatement = "('" +
                        employee.getId() + "', '" +
                        employee.getFullName().getFirstName() + "', '" +
                        employee.getFullName().getLastName() + "', '" +
                        employee.getFullName().getMiddleName() + "', '" +
                        employee.getPosition() + "', '" +
                        employee.getManagerId() + "', '" +
                        Date.valueOf(employee.getHired()) + "', '" +
                        employee.getSalary() + "', '" +
                        employee.getDepartmentId() + "')";

                getResultSet("EMPLOYEE VALUES " + longStatement, 3);

                return employee;
            }

            @Override
            public void delete(Employee employee) {
                getResultSet("EMPLOYEE WHERE ID = " + employee.getId(), 4);
            }
        };
    }

    public DepartmentDao departmentDAO() {
        return new DepartmentDao() {
            @Override
            public Optional<Department> getById(BigInteger Id) {
                try {
                    ResultSet resultSet = getResultSet("WHERE ID = " + Id, 2);
                    assert resultSet != null;
                    if (resultSet.next()) {
                        return Optional.ofNullable(getDepartment(resultSet));
                    } else {
                        return Optional.empty();
                    }
                } catch (SQLException e) {
                    return Optional.empty();
                }
            }

            @Override
            public List<Department> getAll() {
                List<Department> departments = new ArrayList<>();
                try {
                    ResultSet resultSet = getResultSet(null, 2);
                    assert resultSet != null;
                    while (resultSet.next()) {
                        departments.add(getDepartment(resultSet));
                    }
                    return departments;
                } catch (SQLException e) {
                    return null;
                }
            }

            @Override
            public Department save(Department department) {
                boolean statement = getById(department.getId()).equals(Optional.empty());
                if (statement) {

                    String longStatement = "('" +
                            department.getId() + "', '" +
                            department.getName() + "', '" +
                            department.getLocation() + "')";

                    getResultSet("DEPARTMENT VALUES" + longStatement, 3);
                } else {
                    String longStatement = department.getName() + "', LOCATION = '" +
                            department.getLocation() + "' WHERE ID = '" +
                            department.getId() + "'";

                    getResultSet("DEPARTMENT SET NAME = '" + longStatement, 5);
                }
                return department;
            }

            @Override
            public void delete(Department department) {
                getResultSet("DEPARTMENT WHERE ID = " + department.getId(), 4);

            }
        };
    }

    private Employee getEmployee(ResultSet resultSet) {
        try {
            return new Employee(
                    new BigInteger(resultSet.getString("ID")),
                    new FullName(
                            resultSet.getString("FIRSTNAME"),
                            resultSet.getString("LASTNAME"),
                            resultSet.getString("MIDDLENAME")
                    ),
                    Position.valueOf(resultSet.getString("POSITION")),
                    LocalDate.parse(resultSet.getString("HIREDATE")),
                    new BigDecimal(resultSet.getString("SALARY")),
                    BigInteger.valueOf(resultSet.getInt("MANAGER")),
                    BigInteger.valueOf(resultSet.getInt("DEPARTMENT"))
            );
        } catch (SQLException e) {
            return null;
        }
    }

    private Department getDepartment(ResultSet resultSet) {
        try {
            return new Department(
                    new BigInteger(resultSet.getString("ID")),
                    resultSet.getString("NAME"),
                    resultSet.getString("LOCATION")
            );
        } catch (SQLException e) {
            return null;
        }
    }

}