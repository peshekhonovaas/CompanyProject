package org.company;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *  Class representing a company management service
 */
public class CompanyManagement {
    private static Logger LOGGER = LoggerFactory.getLogger(CompanyManagement.class);
    private CompanyEmployeeStorage companyEmployeeStorage;

    /**
     * Read an employees data from file and add an employees to the company structure
     * @param filePath path to the csv file
     */
    public void addNewEmployees(final String filePath) {
        this.companyEmployeeStorage = new CompanyEmployeeStorageImpl();

        try (final Reader reader = Files.newBufferedReader(Paths.get(filePath));
             final CSVParser csvParser = CSVFormat.RFC4180.builder().setHeader().setSkipHeaderRecord(true).build().parse(reader)) {
            for (final CSVRecord csvRecord : csvParser) {
                final Long id = Optional.ofNullable(csvRecord.get("Id")).filter(s -> !s.isEmpty())
                        .map(Long::valueOf).orElse(null);
                final Double salary = Optional.ofNullable(csvRecord.get("salary")).filter(s -> !s.isEmpty())
                        .map(Double::valueOf).orElse(null);
                final Long managerId = Optional.ofNullable(csvRecord.get("managerId")).filter(s -> !s.isEmpty())
                        .map(Long::valueOf).orElse(null);
                this.companyEmployeeStorage.addEmployee(id, csvRecord.get("firstName"),
                        csvRecord.get("lastName"), salary, managerId);
            }
        } catch (Exception ex) {
            LOGGER.error("Failed to process file at '{}'", filePath, ex);
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