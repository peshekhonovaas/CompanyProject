package org.company;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

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
        final Map<Employee, Integer> employeesWithTooLongReportingLine = this.companyManagement.getEmployeesWithTooLongReportingLine();
        Assert.assertTrue(employeesWithTooLongReportingLine.isEmpty());
    }

    @Test
    public void testGetManagersWithBigSalary() {
        this.companyManagement.addNewEmployees(VALID_PATH);
        final Map<Employee, Double> managersWithFilterBySalary = this.companyManagement.getManagersWithBigSalary();
        Assert.assertTrue(!managersWithFilterBySalary.isEmpty());
        Assert.assertEquals(1, managersWithFilterBySalary.keySet().stream().filter(employee -> employee.getId() == 9).count());
    }

    @Test
    public void testGetManagersWithSmallSalary() {
        this.companyManagement.addNewEmployees(VALID_PATH);
        final Map<Employee, Double> managersWithFilterBySalary = this.companyManagement.getManagersWithSmallSalary();
        Assert.assertTrue(!managersWithFilterBySalary.isEmpty());
        Assert.assertTrue(managersWithFilterBySalary.size() == 3);
        Assert.assertEquals(1,  managersWithFilterBySalary.keySet().stream().filter(employee -> employee.getId() == 7).count());
    }

    @Test
    public void testGetEmployeesWithTooLongReportingLine(){
        this.companyManagement.addNewEmployees(VALID_PATH);
        final Map<Employee, Integer> employeesWithTooLongReportingLine = this.companyManagement.getEmployeesWithTooLongReportingLine();
        Assert.assertTrue(!employeesWithTooLongReportingLine.isEmpty());
        Assert.assertTrue(employeesWithTooLongReportingLine.size() == 1);
        Assert.assertEquals(1,  employeesWithTooLongReportingLine.keySet().stream().filter(employee -> employee.getId() == 2).count());
    }
}