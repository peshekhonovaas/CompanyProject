package org.company.storage;

import org.company.models.Employee;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * An implementation of {@link CompanyEmployeeStorage}
 * <p>
 *  Defines the operations to calculate the company structure and provide employees filtering by salary
 * </p>
 */
public class CompanyEmployeeStorageImpl implements CompanyEmployeeStorage{
    private final static Logger LOGGER = Logger.getLogger(CompanyEmployeeStorageImpl.class.getName());
    private final Map<Long, Employee> employees;
    private final Employee ceo;
    private final Map<Employee, Integer> reportingLineMap;
    private final Map<Long, List<Double>> subordinateSalaryMap;

    public CompanyEmployeeStorageImpl() {
        this.employees = new HashMap<>();
        this.reportingLineMap = new HashMap<>();
        this.subordinateSalaryMap = new HashMap<>();
        this.ceo = null;
    }

    /**
     * Add an employee to the company structure
     * @param newEmployee the new employee instance
     */
    @Override
    public void addEmployee(final Employee newEmployee){
        this.employees.put(newEmployee.id(), newEmployee);
        if (Objects.nonNull(newEmployee.managerId())) {
            this.subordinateSalaryMap.putIfAbsent(newEmployee.managerId(), new ArrayList<>());
            this.subordinateSalaryMap.get(newEmployee.managerId()).add(newEmployee.salary());
        }
    }

    /**
     * Calculate the structure of the company that provides a length of a reporting line for every employee.
     * The reporting line depth is the number of levels up to the CEO.
     * @return the map where a key is employee and value is a length of reporting line
     */
    @Override
    public Map<Employee, Integer> calculateCompanyStructure() {
        try {
            return employees.values().stream()
                    .collect(Collectors.toMap(
                            Function.identity(),
                            employee -> this.calculateReportingLineDepth(employee, 1)
                    ));
        }  catch (IllegalStateException ex) {
            LOGGER.log(Level.WARNING, String.format("Failed to calculate company structure: %s", ex.getMessage()), ex);
            return Collections.emptyMap();
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
    private Integer calculateReportingLineDepth(final Employee employee, final int recursionDepth) throws IllegalStateException {
        if (recursionDepth > employees.size()) {
            throw new IllegalStateException(String.format("Exceeded maximum allowed depth %s", employees.size()));
        }
        if (Objects.isNull(employee.managerId())) return 0;
        if (this.reportingLineMap.containsKey(employee))
            return this.reportingLineMap.get(employee);
        final Employee manager = employees.get(employee.managerId());
        int depth = 1 + this.calculateReportingLineDepth(manager, recursionDepth + 1);
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
        return this.subordinateSalaryMap.entrySet().stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(
                        this.employees.get(entry.getKey()),
                        this.calculateAverageDifference(entry, salaryComparisonFunction)
                ))
                .filter(entry -> entry.getValue() > 0.0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Double calculateAverageDifference(final Map.Entry<Long, List<Double>> entry, final BiFunction<Double, Double, Double> salaryComparisonFunction) {
        double avgSubordinateSalary = entry.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double managerSalary = employees.get(entry.getKey()).salary();
        return salaryComparisonFunction.apply(managerSalary, avgSubordinateSalary);
    }

    /**
     * Checks if a CEO has been defined in the company structure
     *
     * @return true if a CEO is defined, otherwise false
     */
    @Override
    public boolean isCEODefined() {
        return Objects.nonNull(this.ceo);
    }
}