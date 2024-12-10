package org.company;

import org.company.models.Employee;
import org.company.models.SalaryComparisonType;

import java.util.Map;
import java.util.Optional;

public class CompanyDisplay {
    public static void main(String[] args) {
        final CompanyManagement companyManagement = new CompanyManagement();
        companyManagement.addNewEmployees("src/main/resources/test_data_10.csv");

        reportEmployeesWithLongReportingLines(companyManagement);
        reportSalaryDiscrepancies(companyManagement, SalaryComparisonType.BIG, "earn more than they should");
        reportSalaryDiscrepancies(companyManagement, SalaryComparisonType.SMALL, "earn less than they should");
    }

    private static void reportEmployeesWithLongReportingLines(final CompanyManagement companyManagement) {
        System.out.println("Employees have a reporting line which is too long:");
        final Map<Employee, Integer> employeesWithTooLongReportingLine = companyManagement.getEmployeesWithTooLongReportingLine();
        employeesWithTooLongReportingLine.forEach((employee, lineLength) ->
                System.out.printf("id: %d, name: %s, last name: %s, reporting line: %d%n",
                        employee.id(), employee.firstName(), employee.lastName(), lineLength)
        );
    }

    private static void reportSalaryDiscrepancies(final CompanyManagement companyManagement,
                                                  final SalaryComparisonType type, final String message) {
        System.out.println(String.format("Managers who %s:", message));
        printManagersWithSalaryDifference(companyManagement.getManagersWithSalaryDifference(type));
    }

    private static void printManagersWithSalaryDifference(final Optional<Map<Employee, Double>> managersWithSalaryDifference) {
        managersWithSalaryDifference.ifPresentOrElse(managers -> {
            if (managers.isEmpty()) {
                System.out.println("No salary differences met the criteria");
            } else {
                managers.forEach((employee, difference) ->
                    System.out.printf("id: %d, name: %s, last name: %s, salary difference: %.2f%n", employee.id(),
                            employee.firstName(), employee.lastName(), difference)
                );
            }
        },() -> System.out.println("No data available for salary differences"));
    }
}