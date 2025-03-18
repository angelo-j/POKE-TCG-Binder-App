package com.pokebinderapp.dao;

import com.pokebinderapp.model.Binder;
import com.pokebinderapp.model.Card;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JdbcBinderDaoTest extends BaseDaoTest {
    private JdbcBinderDao binderDao;
    private JdbcUserDao userDao;
    private JdbcCardDao cardDao;

    @BeforeEach
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        this.cardDao = new JdbcCardDao(jdbcTemplate, null);
        this.binderDao = new JdbcBinderDao(jdbcTemplate, cardDao);
        this.userDao = new JdbcUserDao(jdbcTemplate);
    }

    @Test
    public void createBinder_ShouldReturnBinderWithId() {
        Binder binder = new Binder();
        binder.setName("Test Binder");
        binder.setUserId(1);

        Binder createdBinder = binderDao.createBinder(binder);

        assertNotNull(createdBinder);
        assertTrue(createdBinder.getBinderId() > 0);
    }

    @Test
    public void getBindersByUserId_ShouldReturnList() {
        Binder binder = new Binder();
        binder.setName("User Binder");
        binder.setUserId(1);
        binderDao.createBinder(binder);

        List<Binder> binders = binderDao.getBindersByUserId(1);
        assertFalse(binders.isEmpty());

        // Check if any of the binders match the expected name
        boolean found = binders.stream().anyMatch(b -> "User Binder".equals(b.getName()));
        assertTrue(found, "Expected to find 'User Binder' in the list of binders for user 1.");
    }

    @Test
    public void updateBinderName_ShouldUpdateSuccessfully() {
        Binder binder = new Binder();
        binder.setName("Old Name");
        binder.setUserId(1);
        Binder createdBinder = binderDao.createBinder(binder);

        boolean updated = binderDao.updateBinderName(createdBinder.getBinderId(), "New Name");

        assertTrue(updated);
        Binder updatedBinder = binderDao.getBinderById(createdBinder.getBinderId());
        assertEquals("New Name", updatedBinder.getName());
    }

    @Test
    public void deleteBinder_ShouldRemoveBinder() {
        Binder binder = new Binder();
        binder.setName("Binder To Delete");
        binder.setUserId(1);
        Binder createdBinder = binderDao.createBinder(binder);

        boolean deleted = binderDao.deleteBinder(createdBinder.getBinderId());

        assertTrue(deleted);
        assertNull(binderDao.getBinderById(createdBinder.getBinderId()));
    }
}
