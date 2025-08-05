package br.com.dio;



import br.com.dio.persistence.EmployeeDAO;
import br.com.dio.persistence.entity.EmployeeEntity;
import org.flywaydb.core.Flyway;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class Main {

    private final static EmployeeDAO employeeDAO = new EmployeeDAO();

    public static void main(String[] args) {
        try {
            var flyway = Flyway.configure()
                    .dataSource("jdbc:mysql://localhost:3307/jdbc-sample","root","root")
                    .load();
            flyway.migrate();

            // Insert
            /*var employee = new EmployeeEntity();
            employee.setName("Miguel");
            employee.setSalary(new BigDecimal("2800"));
            employee.setBirthday(OffsetDateTime.now().minusYears(18));
            System.out.println(employee);
            employeeDAO.insert(employee);
            System.out.println(employee);


            1	Lucas	3500.00	2005-08-02 17:52:12
    2	Joao	4800.00	1995-08-02 17:57:40
    3	Gabriel	5500.00	2007-08-01 16:55:49*/

            // List All
    //        employeeDAO.findAll().forEach(System.out::println);

            // List By ID
    //        System.out.println(employeeDAO.findById(1));

            // Update
    //        var employee = new EmployeeEntity();
    //        employee.setId(3);
    //        employee.setName("Gabriel");
    //        employee.setSalary(new BigDecimal("5500"));
    //        employee.setBirthday(OffsetDateTime.now().minusYears(18).minusDays(3));
    //        employeeDAO.update(employee);

//            employeeDAO.delete(4);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
