package com.efimchick.ifmo.web.jdbc;

import java.util.Set;

import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class SetMapperFactory {

    public SetMapper<Set<Employee>> employeesSetMapper() {

        return new SetMapper<Set<Employee>>() {
            @Override
            public Set<Employee> mapSet(ResultSet resultSet) {
                Set<Employee> employees = new HashSet<>();
                try {
                    while (resultSet.next()) {
                        employees.add(getEmployeeHierarchy(resultSet));
                    }
                    return employees;
                } catch (SQLException e) {
                    throw new UnsupportedOperationException();
                }
            }

            private Employee getEmployeeHierarchy(ResultSet resultSet) throws SQLException {
                Employee manager = null;
                resultSet.getString("manager");
                if (!resultSet.wasNull()) {
                    int currentCursor = resultSet.getRow();
                    BigInteger managerId = getBigInteger(resultSet, "manager");
                    resultSet.beforeFirst();
                    while (resultSet.next()) {
                        if (getBigInteger(resultSet, "id").equals(managerId)) {
                            manager = getEmployeeHierarchy(resultSet);
                        }
                    }
                    resultSet.absolute(currentCursor);
                }
                return new Employee(getBigInteger(resultSet, "id"),
                        new FullName(resultSet.getString("firstName"),
                                resultSet.getString("lastName"),
                                resultSet.getString("middleName")),
                        Position.valueOf(resultSet.getString("position")),
                        resultSet.getDate("hireDate").toLocalDate(),
                        resultSet.getBigDecimal("salary"),
                        manager);
            }

            private BigInteger getBigInteger(ResultSet resultSet, String column) throws SQLException {
                return resultSet.getBigDecimal(column).toBigInteger();
            }
        };
    }
}
