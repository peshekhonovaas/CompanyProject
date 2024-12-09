package org.company;

/**
 * Class representing an employee of a company.
 */
public class Employee {
    private Long id;
    private String firstName;
    private String lastName;
    private Double salary;
    private Long managerId;

    /**
     * Get identifier of an employee
     * @return long identifier
     */
    public Long getId() {
        return id;
    }

    /**
     * Get a salary of an employee
     * @return salary
     */
    public Double getSalary() {
        return salary;
    }

    /**
     * Get a name of an employee
     * @return name of an employee
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Get a surname of an employee
     * @return surname of an employee
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Get a manager identifier
     * @return long identifier
     */
    public Long getManagerId() {
        return managerId;
    }

    /**
     * Constructs a new employee instance with specified id, name, last name, salary, manager identifier.
     *
     * @param id the identifier of the employee. This parameter cannot be null.
     * @param firstName the name of the person.
     * @param lastName the last name of the person.
     * @param salary the salary of the person.
     * @param managerId the identifier of the manager employee.
     */
    public Employee(final Long id, final String firstName, final String lastName, final Double salary, final Long managerId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.salary = salary;
        this.managerId = managerId;
    }
}
