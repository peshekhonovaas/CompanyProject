package org.company.storage;

import org.company.models.Employee;

import java.util.Map;
import java.util.function.BiFunction;

/**
 * Interface for company employee storage.
 * <p>
 * This interface defines the operations to calculate the company structure and provide employees filtering by salary
 * </p>
 */
public interface CompanyEmployeeStorage {
    /**
     * Add an employee to the company structure
     * @param newEmployee the new employee instance
     */
    void addEmployee(Employee newEmployee);

    /**
     * Calculate the structure of the company
     * @return the map where a key is employee and some value
     */
    Map<Employee, Integer> calculateCompanyStructure();

    /**
     * Get the map with the managers filtering by salary function
     * @param salaryComparisonFunction the function for the salary comparison
     * @return the map where a key is a manager and a result of the salary comparison
     */
    Map<Employee, Double> getManagersWithFilterBySalary(BiFunction<Double, Double, Double> salaryComparisonFunction);

    /**
     * Checks if a CEO has been defined in the company structure
     * @return true if a CEO is defined, otherwise false
     */
    boolean isCEODefined();
}
