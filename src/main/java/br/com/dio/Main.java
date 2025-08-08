package br.com.dio;



import br.com.dio.persistence.ConnectionUtil;
import br.com.dio.persistence.ContactDAO;
import br.com.dio.persistence.EmployeeAuditDAO;
import br.com.dio.persistence.EmployeeParamDAO;
import br.com.dio.persistence.ModuleDAO;
import br.com.dio.persistence.entity.ContactEntity;
import br.com.dio.persistence.entity.EmployeeEntity;
import br.com.dio.persistence.entity.ModuleEntity;
import net.datafaker.Faker;
import org.flywaydb.core.Flyway;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Locale;
import java.util.stream.Stream;

import static java.time.ZoneOffset.UTC;


public class Main {

    private final static EmployeeAuditDAO employeeAuditDAO = new EmployeeAuditDAO();
    private final static EmployeeParamDAO employeeParamDAO = new EmployeeParamDAO();
    private final static Faker faker = new Faker(Locale.getDefault());
    private final static ContactDAO contactDAO = new ContactDAO();
    private final static ModuleDAO moduleDAO = new ModuleDAO();

    public static void main(String[] args) {
        try {
            var flyway = Flyway.configure()
//                    .dataSource("jdbc:mysql://localhost:3307/jdbc-sample","root","root")
                    .dataSource(
                            ConnectionUtil.getDB_URL(),
                            ConnectionUtil.getDB_USER(),
                            ConnectionUtil.getDB_PASSWORD()
                    )
                    .cleanDisabled(true)
                    .load();

            // 1. Limpa o banco de dados (apaga todas as tabelas)
//            System.out.println("Running Flyway clean...");
//            flyway.clean();
//            System.out.println("Flyway clean finished.");

            // 2. Executa as migrações (recria as tabelas e triggers)
            System.out.println("Running Flyway migrate...");
            flyway.migrate();
            System.out.println("Flyway migrate finished.");

            // Insert
            /*var insert = new EmployeeEntity();
            insert.setName("Miguel'");
            insert.setSalary(new BigDecimal("5300"));
            insert.setBirthday(OffsetDateTime.now().minusYears(18));
            System.out.println(insert);
            employeeParamDAO.insert(insert);
            System.out.println(insert);


           /* 1	Lucas	3500.00	2005-08-02 17:52:12
    2	Joao	4800.00	1995-08-02 17:57:40
    3	Gabriel	5500.00	2007-08-01 16:55:49*/

            // List All
    //        employeeParamDAO.findAll().forEach(System.out::println);

            // List By ID
    //        System.out.println(employeeParamDAO.findById(1));

            // Update
            /*var update = new EmployeeEntity();
            update.setId(3);
            update.setName("Rafael");
            update.setSalary(new BigDecimal("6500"));
            update.setBirthday(OffsetDateTime.now().minusYears(19).minusDays(3));
            employeeParamDAO.update(update);

            employeeParamDAO.delete(insert.getId());

            employeeAuditDAO.findAll().forEach(System.out::println);*/

            /*var entities = Stream.generate(() -> {
                var employee = new EmployeeEntity();
                employee.setName(faker.name().fullName());
                employee.setSalary(new BigDecimal(faker.number().digits(4)));
                employee.setBirthday(OffsetDateTime.of(LocalDate.now().minusYears(faker.number().numberBetween(40,20)), LocalTime.MIN, UTC));
                return employee;
            }).limit(10000).toList();

            employeeParamDAO.insert(entities);*/

            /*var employee = new EmployeeEntity();
            employee.setName("Miguel");
            employee.setSalary(new BigDecimal("3200"));
            employee.setBirthday(OffsetDateTime.now().minusYears(25));
            System.out.println(employee);
            employeeParamDAO.insert(employee);
            System.out.println(employee);

            var contact1 = new ContactEntity();
            contact1.setDescription("miguel@miguel.com");
            contact1.setType("e-mail");
            contact1.setEmployee(employee);
            contactDAO.insert(contact1);

            var contact2 = new ContactEntity();
            contact2.setDescription("33963365002");
            contact2.setType("celular");
            contact2.setEmployee(employee);
            contactDAO.insert(contact2);*/





//            System.out.println(employeeParamDAO.findById(1));


            /*var entities = Stream.generate(() -> {
                var employee = new EmployeeEntity();
                employee.setName(faker.name().fullName());
                employee.setSalary(new BigDecimal(faker.number().digits(4)));
                employee.setBirthday(OffsetDateTime.of(LocalDate.now().minusYears(faker.number().numberBetween(40, 20)), LocalTime.MIN, UTC));
                employee.setModules(new ArrayList<>());
                var moduleAmount = faker.number().numberBetween(1, 4);
                for (int i = 0; i < moduleAmount; i++) {
                    var module = new ModuleEntity();
                    module.setId(i + 1);
                    employee.getModules().add(module);
                }
                return employee;
            }).limit(3).toList();
//            employeeParamDAO.findAll().forEach(System.out::println);
            entities.forEach(employeeParamDAO::insert);*/

//            moduleDAO.findAll().forEach(System.out::println);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
