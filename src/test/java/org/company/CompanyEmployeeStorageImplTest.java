package org.company;

import org.company.models.Employee;
import org.company.storage.CompanyEmployeeStorage;
import org.company.storage.CompanyEmployeeStorageImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class CompanyEmployeeStorageImplTest {
    private CompanyEmployeeStorage storage;
    private List<Employee> employees;

    @Before
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
    public void testCalculateCompanyStructure() {
        this.employees.forEach(employee -> this.storage.addEmployee(employee));
        final Map<Employee, Integer> reportingLineMap = this.storage.calculateCompanyStructure();
        Assert.assertNotNull(reportingLineMap);

        Assert.assertEquals(Integer.valueOf(0), reportingLineMap.get(this.employees.get(0)));
        Assert.assertEquals(Integer.valueOf(1), reportingLineMap.get(this.employees.get(1)));
        Assert.assertEquals(Integer.valueOf(1), reportingLineMap.get(this.employees.get(2)));
        Assert.assertEquals(Integer.valueOf(2), reportingLineMap.get(this.employees.get(3)));
        Assert.assertEquals(Integer.valueOf(3), reportingLineMap.get(this.employees.get(4)));
    }

    @Test
    public void testEmptyCalculateCompanyStructure() {
        final Map<Employee, Integer> reportingLineMap = this.storage.calculateCompanyStructure();
        Assert.assertNotNull(reportingLineMap);
        Assert.assertTrue(reportingLineMap.isEmpty());
    }

    @Test
    public void testManagersWithFilterBySalary() {
        this.employees.forEach(employee -> this.storage.addEmployee(employee));
        final Map<Employee, Double> poorManagers = this.storage.getManagersWithFilterBySalary(
                (managerSalary, avgSubordinateSalary) -> (managerSalary < avgSubordinateSalary) ?
                        avgSubordinateSalary - managerSalary : 0.0);
        Assert.assertNotNull(poorManagers);
        Assert.assertEquals(1, poorManagers.size());
        Assert.assertTrue(poorManagers.containsKey(this.employees.get(1)));
        Assert.assertEquals(Double.valueOf(5000.0), poorManagers.get(this.employees.get(1)));
    }
}