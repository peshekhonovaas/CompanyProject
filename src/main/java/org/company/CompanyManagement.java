package org.company;

import java.io.*;
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
    private CompanyEmployeeStorage companyEmployeeStorage;

    /**
     * Read an employees data from file and add an employees to the company structure
     * @param filePath path to the csv file
     */
    public void addNewEmployees(final String filePath) {
        this.companyEmployeeStorage = new CompanyEmployeeStorageImpl();
        String line;

        try (final BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine();
            while ((line = br.readLine()) != null) {
                final String[] values = line.split(",");
                final Long id = Optional.ofNullable(values[0]).filter(s -> !s.isEmpty())
                        .map(Long::valueOf).orElse(null);
                final String firstName = values[1];
                final String lastName = values[2];
                final Double salary = Optional.ofNullable(values[3]).filter(s -> !s.isEmpty())
                        .map(Double::valueOf).orElse(null);
                final Long managerId = (values.length > 4) ? Optional.ofNullable(values[4]).filter(s -> !s.isEmpty())
                        .map(Long::valueOf).orElse(null) : null;
                this.companyEmployeeStorage.addEmployee(id, firstName, lastName, salary, managerId);
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, String.format("Failed to process file at: %s", filePath), ex);
        }
    }

    /**
     * Return a map with information which managers earn more than they should, and by how much
     * @return the map with the key witch represents a manager with a salary more than 50% more
     * than that average salary of its direct subordinates and value is showing the difference
     */
    public Map<Employee, Double> getManagersWithBigSalary() {
        return this.companyEmployeeStorage.getManagersWithFilterBySalary(
                (managerSalary, avgSubordinateSalary) -> (managerSalary > avgSubordinateSalary * 1.5) ?
                        managerSalary - avgSubordinateSalary * 1.5 : 0.0);
    }
    /**
     * Return a map with information which managers earn less than they should, and by how much
     * @return the map with the key witch represents a manager with a salary less than 20% more
     * than that average salary of its direct subordinates and value is showing the difference
     */
    public Map<Employee, Double> getManagersWithSmallSalary() {
        return this.companyEmployeeStorage.getManagersWithFilterBySalary(
                (managerSalary, avgSubordinateSalary) -> (managerSalary < avgSubordinateSalary * 1.2) ?
                        avgSubordinateSalary * 1.2 - managerSalary : 0.0);
    }

    /**
     * Return a map with employees and their reporting line which is too long (>4)
     * @return the map with the key witch represents an employee and value is showing the length of the reporting line > 4
     */
    public Map<Employee, Integer> getEmployeesWithTooLongReportingLine() {
        final Map<Employee, Integer> reportingLineMap = this.companyEmployeeStorage.calculateCompanyStructure();

        return reportingLineMap.entrySet().stream().filter(entry -> reportingLineMap.get(entry.getKey()) > 4)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}