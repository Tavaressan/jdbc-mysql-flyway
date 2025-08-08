package br.com.dio.persistence;

import br.com.dio.persistence.entity.EmployeeEntity;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import static java.time.ZoneOffset.UTC;

public class AccessDAO {

    public void insert(final long employeeId, final long moduleId){
        try (
                var connection = ConnectionUtil.getConnection();
                var statement = connection.prepareStatement("INSERT INTO accesses (employee_id, module_id) values (?, ?);")
        ){
            statement.setLong(1, employeeId);
            statement.setLong(2, moduleId);
            statement.executeUpdate();

        }catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
