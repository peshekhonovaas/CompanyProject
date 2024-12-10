package org.company.parser;

import org.company.models.Employee;
import org.company.storage.CompanyEmployeeStorage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * * An implementation of {@link EmployeeDataParser}
 * The class handles the specifics of parsing CSV files
 */
public class CSVEmployeeDataParser implements EmployeeDataParser {
    private static final Logger LOGGER = Logger.getLogger(CSVEmployeeDataParser.class.getName());
    private static final int ID_INDEX = 0;
    private static final int FIRST_NAME_INDEX = 1;
    private static final int LAST_NAME_INDEX = 2;
    private static final int SALARY_INDEX = 3;
    private static final int MANAGER_ID_INDEX = 4;
    private static final int EXPECTED_FIELDS = 5;
    private static final int EXPECTED_FIELDS_WITHOUT_MANAGER = 4;

    private final CompanyEmployeeStorage companyEmployeeStorage;

    public CSVEmployeeDataParser(final CompanyEmployeeStorage storage) {
        this.companyEmployeeStorage = storage;
    }

    /**
     * The method reads an employee data from a csv file and adds each employee to the company's storage system.
     * The method assuming that the csv file contains header and skips the first line. Each line represents
     * an employee (CEO included) formatted as: id,firstName,lastName,salary,managerId.
     * CEO has no manager specified this why managerID is optional.
     * This method parses each line and adds an employee to the storage only if the line
     * is correctly formatted overwise skip that line. Any parsing error is logged.
     *
     * @param filePath The path to the csv file with employee data
     */
    @Override
    public void addNewEmployees(String filePath){
        String line;

        try (final BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                final String[] values = line.split(",");
                if (!this.isValidCSVFormat(values)) {
                    LOGGER.log(Level.WARNING, "Invalid CSV format at line: " + line);
                    continue;
                }
                try {
                    this.companyEmployeeStorage.addEmployee(this.parseEmployeeFromCSVLine(values));
                } catch (IllegalArgumentException e) {
                    LOGGER.log(Level.WARNING, "Skipping line due to parsing error: " + line, e);
                }
            }
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, String.format("Failed to process file at: %s", filePath), ex);
        }
    }
    /**
     * The method to validate expected CSV format:
     * id,firstName,lastName,salary,managerId
     * 1,John,Doe,50000
     * 2,Jane,Smith,60000,2
     * THe method checks:
     * 1. Mandatory fields presence (id, first name, last name, salary)
     * 2. Additional checks for CEO. Only CEO has no manager specified.The CEO is only one in the company.
     * 3. All fields including managerId for non-CEO employees
     *
     * @param values The expected employee information
     * @return Is format valid
     */
    private boolean isValidCSVFormat(final String[] values) {
        if (values == null || values.length == 0) {
            return false;
        }
        if (values.length < EXPECTED_FIELDS_WITHOUT_MANAGER) {
            return false;
        }

        if (values[ID_INDEX].trim().isEmpty() || values[FIRST_NAME_INDEX].trim().isEmpty() ||
                values[LAST_NAME_INDEX].trim().isEmpty() || values[SALARY_INDEX].trim().isEmpty()) {
            return false;
        }

        if (values.length == EXPECTED_FIELDS_WITHOUT_MANAGER) {
            return !companyEmployeeStorage.isCEODefined();
        }

        return values.length == EXPECTED_FIELDS && !values[MANAGER_ID_INDEX].trim().isEmpty();
    }

    private Employee parseEmployeeFromCSVLine(final String[] values) {
        try {
            final Long id = Long.parseLong(values[ID_INDEX].trim());
            final String firstName = values[FIRST_NAME_INDEX].trim();
            final String lastName = values[LAST_NAME_INDEX].trim();
            final Double salary = Double.parseDouble(values[SALARY_INDEX].trim());
            final Long managerId = values.length > MANAGER_ID_INDEX ? Long.parseLong(values[MANAGER_ID_INDEX].trim()) : null;
            return new Employee(id, firstName, lastName, salary, managerId);
        } catch (NumberFormatException ex) {
            LOGGER.log(Level.WARNING, "Failed to parse employee fields", ex);
            throw new IllegalArgumentException("Invalid field format", ex);
        }
    }
}
