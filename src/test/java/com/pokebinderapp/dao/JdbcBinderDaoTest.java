package com.pokebinderapp.dao;

import com.pokebinderapp.model.Binder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JdbcBinderDaoTest extends BaseDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JdbcBinderDao binderDao;

    @BeforeEach
    public void setup() {
        String insertUserSql = "INSERT INTO users (username, password_hash, role, money) VALUES ('testuser', 'password', 'ROLE_USER', 100.00)";
        jdbcTemplate.update(insertUserSql);
    }

    @Test
    void createBinder_ShouldReturnBinderWithId() {
        // Arrange
        String insertSql = "INSERT INTO binder (name, user_id) VALUES ('Test Binder', 1)";
        jdbcTemplate.update(insertSql);

        // Act
        Binder binder = binderDao.getBinderById(1);

        // Assert
        assertNotNull(binder);
        assertEquals(1, binder.getBinderId());
        assertEquals("Test Binder", binder.getName());

        // Clean up
        jdbcTemplate.update("DELETE FROM binder WHERE binder_id = 1");
    }

    @Test
    void getAllBinders_ShouldReturnListOfBinders() {
        // Arrange
        jdbcTemplate.update("INSERT INTO binder (name, user_id) VALUES ('Test Binder 1', 1)");
        jdbcTemplate.update("INSERT INTO binder (name, user_id) VALUES ('Test Binder 2', 1)");

        // Act
        List<Binder> binders = binderDao.getAllBinders();

        // Assert
        assertNotNull(binders);
        assertEquals(2, binders.size());

        // Clean up
        jdbcTemplate.update("DELETE FROM binder WHERE name LIKE 'Test Binder%'");
    }

    @Test
    void updateBinderName_ShouldUpdateName() {
        // Arrange
        Binder binder = new Binder();
        binder.setName("Old Binder Name");
        binder.setUserId(1);
        Binder createdBinder = binderDao.createBinder(binder);

        // Act
        boolean updated = binderDao.updateBinderName(createdBinder.getBinderId(), "New Binder Name");
        Binder updatedBinder = binderDao.getBinderById(createdBinder.getBinderId());

        // Assert
        assertTrue(updated, "Binder name should be updated");
        assertNotNull(updatedBinder, "Updated binder should not be null");
        assertEquals("New Binder Name", updatedBinder.getName(), "Binder name should be updated to the new value");
    }

    @Test
    void deleteBinder_ShouldRemoveBinder() {
        // Arrange
        Binder binder = new Binder();
        binder.setName("Binder to Delete");
        binder.setUserId(1);
        Binder createdBinder = binderDao.createBinder(binder);

        // Act
        boolean deleted = binderDao.deleteBinder(createdBinder.getBinderId());
        Binder retrievedBinder = binderDao.getBinderById(createdBinder.getBinderId());

        // Assert
        assertTrue(deleted, "Binder should be deleted successfully");
        assertNull(retrievedBinder, "After deletion, getBinderById should return null");
    }

}
