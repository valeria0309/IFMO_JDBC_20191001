package com.efimchick.ifmo.web.jdbc.service;

import com.efimchick.ifmo.web.jdbc.ConnectionSource;
import com.efimchick.ifmo.web.jdbc.domain.Department;
import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServiceFactory {
    public EmployeeService employeeService(){

    public ResultSet getResultSet(String SQLString) throws SQLException {
        try {
            Connection connection = ConnectionSource.instance().createConnection();
            return connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE).executeQuery(SQLString);
        } catch (SQLException e) {
            return null;
        }
    }



    public Employee getEmployeeWithChain(ResultSet resultSet, boolean chain, boolean isManagerNeeds) throws SQLException {
        Employee manager = null;

        if (resultSet.getObject("manager") != null) {
            if (chain || isManagerNeeds) {
                BigInteger managerId = new BigInteger(resultSet.getString("manager"));

                if (chain) {
                    manager = getSortedEmployees(
                            true,
                            false,
                            "SELECT * FROM employee WHERE id = " + managerId).get(0);
                } else {
                    manager = getSortedEmployees(false,
                            false,
                            "SELECT * FROM employee WHERE id = " + managerId).get(0);
                }
            }
        }

        Department department = null;

        if (resultSet.getObject("department") != null) {
            BigInteger departmentId = BigInteger.valueOf(resultSet.getInt("department"));
            department = getDepartmentById(departmentId);
        }

        return new Employee(
                new BigInteger(resultSet.getString("id")),
                new FullName(

                        resultSet.getString("lastname"),
                        resultSet.getString("firstname"),
                        resultSet.getString("middlename")
                ),
                Position.valueOf(resultSet.getString("position")),
                LocalDate.parse(resultSet.getString("hi")),
                new BigDecimal(resultSet.getString("SALARY")),
                manager,
                department
        );
    }
    public List<Employee> getSortedEmployees(boolean chain, boolean isManagerNeeds, String SQLString) {
        List<Employee> employeeList = new ArrayList<>();
        try {
            ResultSet resultSet = getResultSet(SQLString);

            while (resultSet.next()) {
                Employee employee = getEmployeeWithChain(resultSet, chain, isManagerNeeds);
                employeeList.add(employee);
            }
            return employeeList;

        } catch (SQLException e) {
            return null;
        }
    }

    public Department getDepartmentById(BigInteger id) {
        try {
            Department department = null;
            ResultSet resultSet = getResultSet("SELECT * FROM DEPARTMENT WHERE ID =" + id);
            while (resultSet.next()) {
                department = getDepartment(resultSet);
            }
            return department;
        } catch (SQLException e) {
            return null;
        }
    }

    public Department getDepartment(ResultSet resultSet) throws  SQLException {
        BigInteger id = new BigInteger(resultSet.getString("ID"));
        String name = resultSet.getString("NAME");
        String location = resultSet.getString("LOCATION");

        return new Department(id, name, location);
    }

   public List<Employee> getRequestedPage(Paging paging, String SQLString) {
        List<Employee> employeeList = getSortedEmployees(false, true, SQLString);
        return employeeList.subList(Math.max((paging.page - 1) * paging.itemPerPage, 0),
                Math.min((paging.page) * paging.itemPerPage,
                        employeeList.size()));
    }


   public EmployeeService employeeService(){

        return new EmployeeService() {
            @Override
            public List<Employee> getAllSortBySalary(Paging paging) {
                return getRequestedPage(paging, "SELECT * FROM EMPLOYEE ORDER BY SALARY");
            }

            @Override
            public List<Employee> getAllSortByHireDate(Paging paging) {
                return getRequestedPage(paging, "SELECT * FROM EMPLOYEE ORDER BY HIREDATE");
            }

            @Override
            public List<Employee> getAllSortByLastname(Paging paging) {
                return getRequestedPage(paging, "SELECT * FROM EMPLOYEE ORDER BY LASTNAME");
            }


            @Override
            public List<Employee> getAllSortByDepartmentNameAndLastname(Paging paging) {
                return getRequestedPage(paging, "SELECT * FROM EMPLOYEE ORDER BY DEPARTMENT, LASTNAME");
            }


            @Override
            public List<Employee> getByDepartmentSortByHireDate(Department department, Paging paging) {
                return getRequestedPage(paging, "SELECT * FROM EMPLOYEE WHERE DEPARTMENT = " + department.getId() + "ORDER BY HIREDATE");
            }

            @Override
            public List<Employee> getByDepartmentSortBySalary(Department department, Paging paging) {
                return getRequestedPage(paging, "SELECT * FROM EMPLOYEE WHERE DEPARTMENT = " + department.getId() + " ORDER BY SALARY");
            }

            @Override
            public List<Employee> getByDepartmentSortByLastname(Department department, Paging paging) {
                return getRequestedPage(paging, "SELECT * FROM EMPLOYEE WHERE DEPARTMENT = " + department.getId() + " ORDER BY LASTNAME");
            }


            @Override
            public List<Employee> getByManagerSortByLastname(Employee manager, Paging paging) {
                return getRequestedPage(paging, "SELECT * FROM EMPLOYEE WHERE MANAGER = " + manager.getId() + " ORDER BY LASTNAME");
            }

            @Override
            public List<Employee> getByManagerSortByHireDate(Employee manager, Paging paging) {
                return getRequestedPage(paging, "SELECT * FROM EMPLOYEE WHERE MANAGER = " + manager.getId() + " ORDER BY HIREDATE");
            }

            @Override
            public List<Employee> getByManagerSortBySalary(Employee manager, Paging paging) {
                return getRequestedPage(paging, "SELECT * FROM EMPLOYEE WHERE MANAGER = " + manager.getId() + " ORDER BY SALARY");
            }

            @Override
            public Employee getWithDepartmentAndFullManagerChain(Employee employee) {
                return getSortedEmployees(true, true, "SELECT * FROM EMPLOYEE WHERE ID = " + employee.getId()).get(0);
            }

            @Override
            public Employee getTopNthBySalaryByDepartment(int salaryRank, Department department) {
                return getSortedEmployees(false, true, "SELECT * FROM EMPLOYEE WHERE DEPARTMENT = " + department.getId() + " ORDER BY SALARY DESC").get(salaryRank-1);
            }
        };
    }
}
