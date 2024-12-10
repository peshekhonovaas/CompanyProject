package org.company.models;

/**
 * Class holding employee data
 */
public record Employee(Long id, String firstName, String lastName, Double salary, Long managerId) {}