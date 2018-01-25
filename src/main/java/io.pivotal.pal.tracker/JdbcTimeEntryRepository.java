package io.pivotal.pal.tracker;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class JdbcTimeEntryRepository implements  TimeEntryRepository{

    //private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    public JdbcTimeEntryRepository(DataSource dataSource) {
      //  this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        String query = "INSERT INTO time_entries (project_id, user_id, date, hours)" +
                        " VALUES (?,?,?,?)";


        final PreparedStatementCreator psc = new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(final Connection connection) throws SQLException {
                final PreparedStatement pst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

                pst.setLong(1, timeEntry.getProjectId());
                pst.setLong(2, timeEntry.getUserId());
                pst.setDate(3, Date.valueOf(timeEntry.getDate()));
                pst.setInt(4, timeEntry.getHours());

                return pst;
            }
        };

        final KeyHolder holder = new GeneratedKeyHolder();
        jdbcTemplate.update(psc, holder);

       final Number id = holder.getKey();

       timeEntry.setId(id.longValue());

       return timeEntry;
    }

    @Override
    public TimeEntry find(long id) {
        String query = "select project_id projectId, user_id userId, date, hours, id from time_entries where id=?";
        TimeEntry timeEntry = null;
        try {
            timeEntry = (TimeEntry) jdbcTemplate.queryForObject(query, new Object[]{id}, new BeanPropertyRowMapper<TimeEntry>(TimeEntry.class));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        return timeEntry;
    }

    @Override
    public List<TimeEntry> list() {
        String query = "select id, project_id projectId, user_id userId, date, hours from time_entries";
        List<TimeEntry> timeEntries = jdbcTemplate.query(query, new BeanPropertyRowMapper<>(TimeEntry.class));
        return timeEntries;
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {
        String query = "update time_entries set project_id=?, user_id=?, date=?, hours=? where id=?";
        jdbcTemplate.update(query, new Object[]{
                    timeEntry.getProjectId(),
                    timeEntry.getUserId(),
                    Date.valueOf(timeEntry.getDate()),
                    timeEntry.getHours(),
                    id
        });

        timeEntry.setId(id);
        return timeEntry;
    }

    @Override
    public void delete(long id) {
        String query = "delete from time_entries where id=?";
        jdbcTemplate.update(query, new Object[]{id});
    }
}
