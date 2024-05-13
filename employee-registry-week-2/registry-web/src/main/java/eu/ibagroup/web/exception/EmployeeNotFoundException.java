/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.web.exception;

/**
 * Exception thrown when an Employee cannot be found
 *
 *  @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 *  @since 4Q2023
 */
public class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException(String id) {
        super("Could not find employee: " + id);
    }
}
