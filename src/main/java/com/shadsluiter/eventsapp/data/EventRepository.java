package com.shadsluiter.eventsapp.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.shadsluiter.eventsapp.models.EventEntity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EventRepository implements EventRepositoryInterface {

    private final JdbcTemplate jdbcTemplate;
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;


    @Autowired
    public EventRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<EventEntity> findByOrganizerid(Long organizerid) {
        String sql = "SELECT * FROM events WHERE organizerid = " + organizerid;
        return jdbcTemplate.query(sql, new EventModelRowMapper());
    }

    @Override
    public List<EventEntity> findAll() {
        String sql = "SELECT * FROM events";
        return jdbcTemplate.query(sql, new EventModelRowMapper());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM events WHERE id = " + id;
        jdbcTemplate.update(sql);
    }

    @Override
    public EventEntity save(EventEntity event) {
        if (event.getId() == null) {
            String sql = "INSERT INTO events (name, date, location, organizerid, description) " +
                         "VALUES ('" + event.getName() + "', '" + event.getDate() + "', '" + event.getLocation() + "', '" + event.getOrganizerid() + "', '" + event.getDescription() + "')";
            jdbcTemplate.update(sql);
            Long id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
            event.setId(id);
        } else {
            String sql = "UPDATE events SET name = '" + event.getName() + "', date = '" + event.getDate() + "', location = '" + event.getLocation() + "', organizerid = '" + event.getOrganizerid() + "', description = '" + event.getDescription() + 
                         "' WHERE id = " + event.getId();
            jdbcTemplate.update(sql);
        }
        return event;
    }

    @Override
    public EventEntity findById(Long id) {
        String sql = "SELECT * FROM events WHERE id = " + id;
        return jdbcTemplate.queryForObject(sql, new EventModelRowMapper());
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM events WHERE id = " + id;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null && count > 0;
    }

    private static class EventModelRowMapper implements RowMapper<EventEntity> {
        @Override
        public EventEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
            EventEntity event = new EventEntity();
            event.setId(rs.getLong("id"));
            event.setName(rs.getString("name"));
            event.setDate(rs.getDate("date"));
            event.setLocation(rs.getString("location"));
            event.setOrganizerid(rs.getString("organizerid"));
            event.setDescription(rs.getString("description"));
            return event;
        }
    }

    @Override
    public List<EventEntity> findByDescription(String description) { 
        String sql = "SELECT * FROM events WHERE description LIKE ?";
        
        return jdbcTemplate.query(sql, new EventModelRowMapper());
    }
    // @Override
    // public List<EventEntity> findByDescription(String description) { 
    //     String sql = "SELECT * FROM events WHERE description LIKE '%" + description + "%'";
    //     System.out.println(sql);
    //     List<EventEntity> eventEntities = new ArrayList<>();
    //     try (Connection conn = DriverManager.getConnection(url, username, password);
    //          Statement stmt = conn.createStatement();
    //          ResultSet rs = stmt.executeQuery(sql)) {
    
    //         while (rs.next()) {
    //             EventEntity order = new EventEntity();
    //             order.setId(rs.getLong("id"));
    //             order.setName(rs.getString("name"));
    //             order.setDate(rs.getDate("date"));
    //             order.setLocation(rs.getString("location"));
    //             order.setOrganizerid(rs.getString("notes"));
    //             order.setDescription(rs.getString("description"));
    //             eventEntities.add(order);
    //         }
    //     } catch (SQLException e) {
    //         e.printStackTrace();
    //     }
    //     return eventEntities;

}
