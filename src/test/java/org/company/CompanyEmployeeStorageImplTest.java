package org.company;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CompanyEmployeeStorageImplTest {
    private CompanyEmployeeStorage storage;
    private List<Employee> employees;

    @BeforeEach
    public void setUp() {
        this.storage = new CompanyEmployeeStorageImpl();
        this.employees = List.of(
                new Employee(123L, "Joe", "Doe", 60000.0, null),
                new Employee(124L, "Martin", "Chekov", 45000.0, 123L),
                new Employee(125L, "Bob", "Ronstad", 47000.0, 123L),
                new Employee(300L, "Alice", "Hasacat", 50000.0, 124L),
                new Employee(305L, "Brett", "Hardleaf", 34000.0, 300L));
    }

    @Test
    void testCalculateCompanyStructure() {
        this.employees.forEach(employee -> this.storage.addEmployee(employee));
        final Map<Employee, Integer> reportingLineMap = this.storage.calculateCompanyStructure();
        assertNotNull(reportingLineMap);

        assertEquals(0, reportingLineMap.get(this.employees.get(0)));
        assertEquals(1, reportingLineMap.get(this.employees.get(1)));
        assertEquals(1, reportingLineMap.get(this.employees.get(2)));
        assertEquals(2, reportingLineMap.get(this.employees.get(3)));
        assertEquals(3, reportingLineMap.get(this.employees.get(4)));
    }

    @Test
    void testEmptyCalculateCompanyStructure() {
        final Map<Employee, Integer> reportingLineMap = this.storage.calculateCompanyStructure();
        assertNotNull(reportingLineMap);
        assertTrue(reportingLineMap.isEmpty());
    }

    @Test
    void testManagersWithFilterBySalary() {
        this.employees.forEach(employee -> this.storage.addEmployee(employee));
        final Map<Employee, Double> poorManagers = this.storage.getManagersWithFilterBySalary(
                (managerSalary, avgSubordinateSalary) -> (managerSalary < avgSubordinateSalary) ?
                        avgSubordinateSalary - managerSalary : 0.0);
        assertNotNull(poorManagers);
        assertEquals(1, poorManagers.size());
        assertTrue(poorManagers.keySet().contains(this.employees.get(1)));
        assertEquals(5000.0, poorManagers.get(this.employees.get(1)));
    }
}