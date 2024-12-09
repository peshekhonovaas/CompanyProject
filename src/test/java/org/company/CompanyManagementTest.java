package org.company;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Map;

class CompanyManagementTest {
    private static String INVALID_PATH_VALUE = "src/test/resources/test_data_invalid.csv";
    private static String VALID_PATH = "src/test/resources/test_data_10.csv";
    private CompanyManagement companyManagement;

    @BeforeEach
    public void setUp() {
        this.companyManagement = new CompanyManagement();
    }

    @Test
    void testInvalidPath() {
        this.companyManagement.addNewEmployees("Not_existing_path");
        final Map<Employee, Integer> employeesWithTooLongReportingLine = this.companyManagement.getEmployeesWithTooLongReportingLine();
        Assert.assertTrue(employeesWithTooLongReportingLine.isEmpty());
    }

    @Test
    void testAddNewEmployeesFails() {
        this.companyManagement.addNewEmployees(INVALID_PATH_VALUE);
        final Map<Employee, Integer> employeesWithTooLongReportingLine = this.companyManagement.getEmployeesWithTooLongReportingLine();
        Assert.assertTrue(employeesWithTooLongReportingLine.isEmpty());
    }

    @Test
    void testGetManagersWithBigSalary() {
        this.companyManagement.addNewEmployees(VALID_PATH);
        final Map<Employee, Double> managersWithFilterBySalary = this.companyManagement.getManagersWithBigSalary();
        Assert.assertTrue(!managersWithFilterBySalary.isEmpty());
        Assert.assertEquals(1, managersWithFilterBySalary.keySet().stream().filter(employee -> employee.getId() == 9).count());
    }

    @Test
    void testGetManagersWithSmallSalary() {
        this.companyManagement.addNewEmployees(VALID_PATH);
        final Map<Employee, Double> managersWithFilterBySalary = this.companyManagement.getManagersWithSmallSalary();
        Assert.assertTrue(!managersWithFilterBySalary.isEmpty());
        Assert.assertTrue(managersWithFilterBySalary.size() == 3);
        Assert.assertEquals(1,  managersWithFilterBySalary.keySet().stream().filter(employee -> employee.getId() == 7).count());
        Assert.assertEquals(Double.valueOf(127.0),  managersWithFilterBySalary.values().stream().findFirst().get());
    }

    @Test
    void testGetEmployeesWithTooLongReportingLine(){
        this.companyManagement.addNewEmployees(VALID_PATH);
        final Map<Employee, Integer> employeesWithTooLongReportingLine = this.companyManagement.getEmployeesWithTooLongReportingLine();
        Assert.assertTrue(!employeesWithTooLongReportingLine.isEmpty());
        Assert.assertTrue(employeesWithTooLongReportingLine.size() == 1);
        Assert.assertEquals(1,  employeesWithTooLongReportingLine.keySet().stream().filter(employee -> employee.getId() == 2).count());
    }
}