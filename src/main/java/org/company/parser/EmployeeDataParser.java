package org.company.parser;

/**
 * The interface defines the contract for parsing employee data from various sources (CSV, JSON, or XML)
 *
 */
public interface EmployeeDataParser {
    void addNewEmployees(String filePath);
}
