package org.company;

import java.util.Map;

public class CompanyDisplay {
    public static void main(String[] args) {
        final CompanyManagement companyManagement = new CompanyManagement();
        companyManagement.addNewEmployees("src/main/resources/test_data_10.csv");

        System.out.println("Employees have a reporting line which is too long:");
        final Map<Employee, Integer> employeesWithTooLongReportingLine = companyManagement.getEmployeesWithTooLongReportingLine();
        for (final Employee employee : employeesWithTooLongReportingLine.keySet()) {
            System.out.println(String.format("id: %d, name: %s, last name: %s, reporting line: %d", employee.getId(),
                    employee.getFirstName(), employee.getLastName(), employeesWithTooLongReportingLine.get(employee)));
        }

        System.out.println("Managers earn more than they should:");
        final Map<Employee, Double> managersWithBigSalary = companyManagement.getManagersWithBigSalary();
        for (final Employee employee : managersWithBigSalary.keySet()) {
            System.out.println(String.format("id: %d, name: %s, last name: %s, earns more by: %f", employee.getId(),
                    employee.getFirstName(), employee.getLastName(), managersWithBigSalary.get(employee)));
        }

        System.out.println("Managers earn less than they should:");
        final Map<Employee, Double> managersWithSmallSalary = companyManagement.getManagersWithSmallSalary();
        for (final Employee employee : managersWithSmallSalary.keySet()) {
            System.out.println(String.format("id: %d, name: %s, last name: %s, earns more by: %f", employee.getId(),
                    employee.getFirstName(), employee.getLastName(), managersWithSmallSalary.get(employee)));
        }
    }
}