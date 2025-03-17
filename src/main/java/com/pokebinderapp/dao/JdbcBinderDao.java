package com.pokebinderapp.dao;

import com.pokebinderapp.model.Binder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcBinderDao implements BinderDao {

    private final JdbcTemplate jdbcTemplate;
    private final CardDao cardDao;

    @Autowired
    public JdbcBinderDao(JdbcTemplate jdbcTemplate, CardDao cardDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.cardDao = cardDao;
    }

    @Override
    public Binder createBinder(Binder binder) {
        String sql = "INSERT INTO binder (name, user_id) VALUES (?, ?) RETURNING binder_id";
        int newBinderId = jdbcTemplate.queryForObject(sql, Integer.class, binder.getName(), binder.getUserId());
        binder.setBinderId(newBinderId);
        return binder;
    }

    @Override
    public List<Binder> getAllBinders() {
        String sql = "SELECT * FROM binder";
        List<Binder> binders = new ArrayList<>();
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            Binder binder = mapRowToBinder(results);
            binder.setCards(cardDao.getCardsInBinder(binder.getBinderId()));
            binders.add(binder);
        }
        return binders;
    }

    @Override
    public List<Binder> getBindersByUserId(int userId) {
        String sql = "SELECT * FROM binder WHERE user_id = ?";
        List<Binder> binders = new ArrayList<>();
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        while (results.next()) {
            Binder binder = mapRowToBinder(results);
            binder.setCards(cardDao.getCardsInBinder(binder.getBinderId()));
            binders.add(binder);
        }
        return binders;
    }

    @Override
    public Binder getBinderById(int binderId) {
        String sql = "SELECT * FROM binder WHERE binder_id = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, binderId);
        if (result.next()) {
            Binder binder = mapRowToBinder(result);
            binder.setCards(cardDao.getCardsInBinder(binderId));
            return binder;
        }
        return null;
    }

    @Override
    public boolean updateBinderName(int binderId, String newName) {
        String sql = "UPDATE binder SET name = ? WHERE binder_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, newName, binderId);
        return rowsAffected > 0;
    }

    @Override
    public boolean deleteBinder(int binderId) {
        String deleteCardsSql = "DELETE FROM binder_cards WHERE binder_id = ?";
        jdbcTemplate.update(deleteCardsSql, binderId);
        String deleteBinderSql = "DELETE FROM binder WHERE binder_id = ?";
        int rowsAffected = jdbcTemplate.update(deleteBinderSql, binderId);
        return rowsAffected > 0;
    }

    private Binder mapRowToBinder(SqlRowSet rs) {
        Binder binder = new Binder();
        binder.setBinderId(rs.getInt("binder_id"));
        binder.setName(rs.getString("name"));
        binder.setUserId(rs.getInt("user_id"));
        return binder;
    }
}
