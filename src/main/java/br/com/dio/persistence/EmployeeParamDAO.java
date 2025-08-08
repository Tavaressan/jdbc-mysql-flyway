package br.com.dio.persistence;

import br.com.dio.persistence.entity.ContactEntity;
import br.com.dio.persistence.entity.EmployeeEntity;
import br.com.dio.persistence.entity.ModuleEntity;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static java.time.ZoneOffset.UTC;
import static java.util.TimeZone.LONG;

public class EmployeeParamDAO {

    private final ContactDAO contactDAO = new ContactDAO();

    private final AccessDAO accessDAO  = new AccessDAO();

    public void insert(final EmployeeEntity entity){
       final String sql = "INSERT INTO employees (name, salary, birthday) values (?, ?, ?);";
        try (
            var connection = ConnectionUtil.getConnection();
            var statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ){
            statement.setString(1, entity.getName());
            statement.setBigDecimal(2, entity.getSalary());
            statement.setTimestamp(3, Timestamp.valueOf(entity.getBirthday().atZoneSameInstant(UTC).toLocalDateTime())
            );
            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0){
                throw new SQLException("Creating employee failed, no rows affected.");
            }

            try (var generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getLong(1));

            entity.getModules().stream()
                    .map(ModuleEntity::getId)
                    .forEach(moduleId -> accessDAO.insert(entity.getId(), moduleId));

                } else {
                    throw new SQLException("Creating employees failed, no ID obtained");
                }
            }
        }catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void insertWithProcedure(final EmployeeEntity entity){
        try (
                var connection = ConnectionUtil.getConnection();
                var statement = connection.prepareCall(
                        "call prc_insert_employee(?, ?, ?, ?);"
                )
        ){
            statement.registerOutParameter(1 ,LONG);
            statement.setString(2, entity.getName());
            statement.setBigDecimal(3, entity.getSalary());
            statement.setTimestamp(4,
                    Timestamp.valueOf(entity.getBirthday().atZoneSameInstant(UTC).toLocalDateTime())
            );
            statement.execute();
            entity.setId(statement.getLong(1));
        }catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void insert(final List<EmployeeEntity> entities) {
        try (var connection = ConnectionUtil.getConnection()) {
            String sql = "INSERT INTO employees (name, salary, birthday) values (?, ?, ?);";
            try (var statement = connection.prepareStatement(
                    sql)) {
                connection.setAutoCommit(false);
                for (int i = 0; i < entities.size(); i++) {
                    statement.setString(1, entities.get(i).getName());
                    statement.setBigDecimal(2, entities.get(i).getSalary());
                    var timeStamp = entities.get(i).getBirthday().atZoneSameInstant(UTC)
                            .toLocalDateTime();
                    statement.setTimestamp(3, Timestamp.valueOf(timeStamp)
                    );
                    statement.addBatch();
                    if (i % 1000 == 0 || i == entities.size() - 1) statement.executeBatch();

                    if (i == 8000) throw new SQLException();
                }
                connection.commit();
            } catch (SQLException ex) {
                connection.rollback();
                ex.printStackTrace();
            }
        } catch (
                SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void update(final EmployeeEntity entity){
        try (
                var connection = ConnectionUtil.getConnection();
                var statement = connection.prepareStatement(
                        "UPDATE employees SET name = ?, salary = ?, birthday = ? WHERE id = ?"
                )
        ){
            statement.setString(1, entity.getName());
            statement.setBigDecimal(2, entity.getSalary());
            statement.setTimestamp(3, Timestamp.valueOf(entity.getBirthday().atZoneSameInstant(UTC).toLocalDateTime())
            );
            statement.setLong(4, entity.getId());
            statement.executeUpdate();
        }catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void delete(final long id){
        try (
                var connection = ConnectionUtil.getConnection();
                var statement = connection.prepareStatement("DELETE FROM employees WHERE id =  ?")
        ){
            statement.setLong(1, id);
            statement.executeUpdate();
        }catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public List<EmployeeEntity> findAll(){
        List<EmployeeEntity> entities = new ArrayList<>();
        try (
                var connection = ConnectionUtil.getConnection();
                var statement = connection.createStatement()
        ){
            statement.executeQuery("SELECT * FROM employees ORDER BY name");
            var resultSet = statement.getResultSet();
            while (resultSet.next()){
                var entity = new EmployeeEntity();
                entity.setId(resultSet.getLong("id"));
                entity.setName(resultSet.getString("name"));
                entity.setSalary(resultSet.getBigDecimal("salary"));
                var birthdayInstant = resultSet.getTimestamp("birthday").toInstant();
                entity.setBirthday(OffsetDateTime.ofInstant(birthdayInstant,UTC));
                entity.setContacts(contactDAO.findByEmployeeId(resultSet.getLong("id")));
                entities.add(entity);
            }
        }catch (SQLException ex) {
            ex.printStackTrace();
        }
        return entities;
    }



    public EmployeeEntity findById(final long id){

        var entity = new EmployeeEntity();
        final String sql = "SELECT e.id employee_id,\n" +
                "\t   e.name,\n" +
                "\t   e.salary,\n" +
                "\t   e.birthday, \n" +
                "\t   c.id contact_id,\n" +
                "\t   c.description,\n" +
                "\t   c.type\n" +
                "FROM employees e \n" +
                "LEFT JOIN contacts c \n" +
                " ON c.employee_id = e.id \n" +
                "WHERE e.id = ?";
        try (
                var connection = ConnectionUtil.getConnection();
                var statement = connection.prepareStatement(sql)
        ){
            statement.setLong(1, id);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            if (resultSet.next()){
                entity.setId(resultSet.getLong("employee_id"));
                entity.setName(resultSet.getString("name"));
                entity.setSalary(resultSet.getBigDecimal("salary"));
                var birthdayInstant = resultSet.getTimestamp("birthday").toInstant();
                entity.setBirthday(OffsetDateTime.ofInstant(birthdayInstant,UTC));
                entity.setContacts(new ArrayList<>());
                do {
                    var contact = new ContactEntity();
                    contact.setId(resultSet.getLong("contact_id"));
                    contact.setDescription(resultSet.getString("description"));
                    contact.setType(resultSet.getString("type"));
                    entity.getContacts().add(contact);
                } while (resultSet.next());
            }
        }catch (SQLException ex) {
            ex.printStackTrace();
        }
        return entity;
    }

    private String formatOffSetDateTime(final OffsetDateTime dateTime){
        var utcDatetime = dateTime.withOffsetSameInstant(UTC);
        return utcDatetime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
