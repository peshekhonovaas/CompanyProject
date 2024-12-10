package org.company;

import org.company.models.Employee;
import org.company.models.SalaryComparisonType;
import org.company.parser.CSVEmployeeDataParser;
import org.company.storage.CompanyEmployeeStorage;
import org.company.storage.CompanyEmployeeStorageImpl;
import org.company.parser.EmployeeDataParser;

import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *  Class representing a company management service
 */
public class CompanyManagement {
    private final static Logger LOGGER = Logger.getLogger(CompanyManagement.class.getName());
    /**
     * The constant for representing a BIG salary difference.
     */
    private static final double BIG_SALARY_THRESHOLD = 1.5;

    /**
     * The constant for representing a "SMALL" salary difference.
     */
    private static final double SMALL_SALARY_THRESHOLD = 1.2;
    /**
     * The constant for representing a too long reporting line between employee and
     */
    private static final int MAX_REPORTING_LINE_LENGTH = 4;

    private final CompanyEmployeeStorage companyEmployeeStorage;

    public CompanyManagement() {
        this.companyEmployeeStorage = new CompanyEmployeeStorageImpl();
    }
    public void addNewEmployees(final String filePath) {
        final EmployeeDataParser parser = new CSVEmployeeDataParser(companyEmployeeStorage);
        parser.addNewEmployees(filePath);
    }

    /**
     * Retrieves managers with significant salary difference based on the specified comparison type.
     * A BIG salary difference indicates that the manager's salary is more than BIG_SALARY_THRESHOLD compare with
     * the average salary of their subordinates. A SMALL salary difference indicates
     * that the manager's salary is less than the average salary of their subordinates by more than SMALL_SALARY_THRESHOLD.
     *
     * @param comparisonType The type of salary comparison (BIG or SMALL)
     * @return The Optional containing map of managers to their calculated salary difference.
     * The Optional is empty if there is an error in processing, like an unsupported comparison type.
     */
    public Optional<Map<Employee, Double>> getManagersWithSalaryDifference(final SalaryComparisonType comparisonType) {
        try {
            final Map<Employee, Double> result = this.companyEmployeeStorage.getManagersWithFilterBySalary(
                    (managerSalary, avgSubordinateSalary) ->
                            this.calculateSalaryDifference(managerSalary, avgSubordinateSalary, comparisonType));
            return Optional.of(result);
        } catch (IllegalArgumentException ex) {
            LOGGER.log(Level.WARNING, "Failed to process salary calculation", ex);
            return Optional.empty();
        }
    }

    /**
     * Calculates the salary difference based on the specified comparison type.
     * The method calculates how much a manager's salary is different from an average salary of their subordinates.
     *
     * @param managerSalary The salary of the manager
     * @param avgSubordinateSalary The average salary of the subordinates
     * @param comparisonType The type of salary comparison (BIG or SMALL):
     *                       - BIG: Checks if the manager's salary is more than the average subordinate salary by BIG_SALARY_THRESHOLD.
     *                       - SMALL: Checks if the manager's salary is less than the average subordinate salary by SMALL_SALARY_THRESHOLD.
     * @return The salary difference. Returns 0.0 if the salary does not meet the specified criteria
     * @throws IllegalArgumentException In case of unsupported comparison type is provided
     */
   private Double calculateSalaryDifference(final Double managerSalary, final Double avgSubordinateSalary,
                                             final SalaryComparisonType comparisonType) {
        return switch (comparisonType) {
            case BIG -> managerSalary > avgSubordinateSalary * BIG_SALARY_THRESHOLD ?
                        managerSalary - avgSubordinateSalary * BIG_SALARY_THRESHOLD : 0.0;
            case SMALL-> managerSalary < avgSubordinateSalary * SMALL_SALARY_THRESHOLD ?
                        avgSubordinateSalary * SMALL_SALARY_THRESHOLD - managerSalary : 0.0;
            default ->
                throw new IllegalArgumentException("Unsupported salary comparison type: " + comparisonType);
        };
    }

    /**
     * Return a map with employees and their reporting line which is too long.
     * The reporting line is too long if the length is greater than MAX_REPORTING_LINE_LENGTH.
     *
     * @return The map where each key is an employee and each value is the length of the reporting line that exceeds MAX_REPORTING_LINE_LENGTH
     */
    public Map<Employee, Integer> getEmployeesWithTooLongReportingLine() {
        final Map<Employee, Integer> reportingLineMap = this.companyEmployeeStorage.calculateCompanyStructure();

        return reportingLineMap.entrySet().stream()
                .filter(entry -> entry.getValue() > MAX_REPORTING_LINE_LENGTH)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}