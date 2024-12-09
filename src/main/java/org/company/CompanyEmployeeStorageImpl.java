package org.company;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * An implementation of {@link CompanyEmployeeStorage}
 * <p>
 *  Defines the operations to calculate the company structure and provide employees filtering by salary
 * </p>
 */
public class CompanyEmployeeStorageImpl implements CompanyEmployeeStorage{
    private static Logger LOGGER = LoggerFactory.getLogger(CompanyEmployeeStorageImpl.class);
    private Map<Long, Employee> employees;
    private Map<Employee, Integer> reportingLineMap;
    private Map<Long, List<Double>> subordinateSalaryMap;

    public CompanyEmployeeStorageImpl() {
        this.employees = new HashMap<>();
        this.reportingLineMap = new HashMap<>();
        this.subordinateSalaryMap = new HashMap<>();
    }

    /**
     * Add an employee to the company structure
     * @param newEmployee the new employee instance
     */
    @Override
    public void addEmployee(final Employee newEmployee){
        this.employees.put(newEmployee.getId(), newEmployee);
        if (Objects.nonNull(newEmployee.getManagerId())) {
            this.subordinateSalaryMap.putIfAbsent(newEmployee.getManagerId(), new ArrayList<>());
            this.subordinateSalaryMap.get(newEmployee.getManagerId()).add(newEmployee.getSalary());
        }
    }

    /**
     * Add an employee to the company structure
     * @param id the employee identifier
     * @param firstName the first name of the employee
     * @param lastName the last name of the employee
     * @param salary the salary of the employee
     * @param managerId the manager identifier of the employee
     */
    @Override
    public void addEmployee(final Long id, final String firstName, final String lastName,
                            final Double salary, final Long managerId){
        final Employee newEmployee = new Employee(id, firstName, lastName, salary, managerId);
        this.employees.put(id, newEmployee);
        if (Objects.nonNull(newEmployee.getManagerId())) {
            this.subordinateSalaryMap.putIfAbsent(newEmployee.getManagerId(), new ArrayList<>());
            this.subordinateSalaryMap.get(newEmployee.getManagerId()).add(newEmployee.getSalary());
        }
    }

    /**
     * Calculate the structure of the company that provides a length of a reporting line for every employee
     * @return the map where a key is employee and value is a length of reporting line
     */
    @Override
    public Map<Employee, Integer> calculateCompanyStructure() {
        try {
            return employees.values().stream()
                    .collect(Collectors.toMap(
                            employee -> employee,
                            employee -> this.getReportLine(employee, 1)
                    ));
        }  catch (IllegalStateException ex) {
            LOGGER.error("Failed to calculate company structure: {}", ex.getMessage(), ex);
            return Map.of();
        }
    }

    /**
     * Recursive function that allow to calculate a reporting line of the employee
     * @param employee the employee
     * @param recursionDepth the depth of the recursion in the current step
     * @return the reporting line for an employee
     * @throws IllegalStateException if recursion exceeds a maximum allowed depth (count of employees).
     * Need to avoid an endless loop.
     */
    private Integer getReportLine(final Employee employee, final int recursionDepth) throws IllegalStateException {
        if (recursionDepth > employees.size()) {
            throw new IllegalStateException(String.format("Exceeded maximum allowed depth %s", employees.size()));
        }
        if (Objects.isNull(employee.getManagerId())) return 0;
        if (this.reportingLineMap.containsKey(employee))
            return this.reportingLineMap.get(employee);
        int depth = 1 + this.getReportLine(this.employees.get(employee.getManagerId()), recursionDepth + 1);
        this.reportingLineMap.put(employee, depth);
        return depth;
    }

    /**
     * Get the map with the managers filtering by salary function
     * @param salaryComparisonFunction the function for the comparison of the manager and employee salaries
     * @return the map where a key is a manager and a result of the salary comparison
     */
    @Override
    public Map<Employee, Double> getManagersWithFilterBySalary(final BiFunction<Double, Double, Double> salaryComparisonFunction) {
        return this.subordinateSalaryMap.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        entry -> this.employees.get(entry.getKey()),
                        entry -> {
                            double avgSubordinateSalary = entry.getValue().stream()
                                    .mapToDouble(Double::doubleValue).average().orElse(0.0);
                            double managerSalary = employees.get(entry.getKey()).getSalary();
                            return salaryComparisonFunction.apply(managerSalary, avgSubordinateSalary);
                        }))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() > 0.0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}