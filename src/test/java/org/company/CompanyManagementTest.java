package org.company;

import org.company.models.Employee;
import org.company.models.SalaryComparisonType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.Optional;

public class CompanyManagementTest {
    private final static String VALID_PATH = "src/test/resources/test_data_10.csv";
    private CompanyManagement companyManagement;

    @Before
    public void setUp() {
        this.companyManagement = new CompanyManagement();
    }

    @Test
    public void testInvalidPath() {
        this.companyManagement.addNewEmployees("Not_existing_path");
        final Map<Employee, Integer> employeesWithTooLongReportingLine = this.companyManagement.getEmployeesWithTooLongReportingLine();
        Assert.assertTrue(employeesWithTooLongReportingLine.isEmpty());
    }

    @Test
    public void testAddNewEmployeesFails() {
        this.companyManagement.addNewEmployees("src/test/resources/test_data_invalid.csv");
        final Map<Employee, Integer> employeesWithTooLongReportingLine =
                this.companyManagement.getEmployeesWithTooLongReportingLine();
        Assert.assertTrue(employeesWithTooLongReportingLine.isEmpty());
    }

    @Test
    public void testGetManagersWithBigSalary() {
        this.companyManagement.addNewEmployees(VALID_PATH);
        final Optional<Map<Employee, Double>> managersWithFilterBySalary =
                this.companyManagement.getManagersWithSalaryDifference(SalaryComparisonType.BIG);
        Assert.assertTrue(managersWithFilterBySalary.isPresent());
        Assert.assertEquals(1, managersWithFilterBySalary.get().keySet()
                .stream().filter(employee -> employee.id() == 9).count());
    }
    @Test
    public void testGetManagersWithSmallSalary() {
        this.companyManagement.addNewEmployees(VALID_PATH);
        final Optional<Map<Employee, Double>> managersWithFilterBySalary = this.companyManagement.getManagersWithSalaryDifference(SalaryComparisonType.SMALL);
        Assert.assertTrue(managersWithFilterBySalary.isPresent());
        Assert.assertEquals(3, managersWithFilterBySalary.get().size());
        Assert.assertEquals(1,  managersWithFilterBySalary.get().keySet().stream().filter(employee -> employee.id() == 7).count());
    }

    @Test
    public void testGetEmployeesWithTooLongReportingLine(){
        this.companyManagement.addNewEmployees(VALID_PATH);
        final Map<Employee, Integer> employeesWithTooLongReportingLine = this.companyManagement.getEmployeesWithTooLongReportingLine();
        Assert.assertFalse(employeesWithTooLongReportingLine.isEmpty());
        Assert.assertEquals(1,employeesWithTooLongReportingLine.size());
        Assert.assertEquals(1,  employeesWithTooLongReportingLine.keySet().stream().filter(employee -> employee.id() == 2).count());
    }
}