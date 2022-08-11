package by.aab.isp.dao.jdbc;

import by.aab.isp.dao.CustomerAccountDao;
import by.aab.isp.dao.DaoException;
import by.aab.isp.dao.TariffDao;
import by.aab.isp.dao.UserDao;
import by.aab.isp.entity.CustomerAccount;
import by.aab.isp.entity.User;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public final class CustomerAccountDaoJdbc extends AbstractRepositoryJdbc<CustomerAccount> implements CustomerAccountDao {

    private static final List<String> FIELDS = List.of("tariff_id", "balance", "permitted_overdraft", "payoff_date");

    private final TariffDao tariffDao;
    private final UserDao userDao;

    public CustomerAccountDaoJdbc(DataSource dataSource, TariffDao tariffDao, UserDao userDao) {
        super(dataSource, "customer_accounts", FIELDS);
        sqlInsert = "INSERT INTO " + tableName
                + FIELDS.stream()
                        .map(field -> field + ", ")
                        .reduce(new StringBuilder("("),
                                StringBuilder::append,
                                StringBuilder::append)
                + " user_id) VALUES ("
                + FIELDS.stream()
                        .map(field -> "?, ")
                        .reduce(new StringBuilder(),
                                StringBuilder::append,
                                StringBuilder::append)
                + "?)";
        sqlSelectWhereId = sqlSelect + " WHERE user_id=";
        sqlUpdateWhereId = sqlUpdate + " WHERE user_id=";
        this.tariffDao = tariffDao;
        this.userDao = userDao;
    }

    @Override
    public CustomerAccount save(CustomerAccount account) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sqlInsert);
            mapObjectToRow(account, statement);
            statement.setLong(FIELDS.size() + 1, account.getId());
            if (statement.executeUpdate() < 1) {
                throw new DaoException("Could not insert CustomerAccount");
            }
            return account;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    final String sqlCountWhereUserId = sqlCount + " WHERE user_id=";

    @Override
    public long countByUserId(long userId) {
        return count(sqlCountWhereUserId + userId);
    }

    @Override
    public Optional<CustomerAccount> findByUser(User user) {
        return findOne(sqlSelectWhereId + user.getId(), row -> {
            try {
                long tariffId = row.getLong("tariff_id");
                Timestamp payoffDate = row.getTimestamp("payoff_date");
                CustomerAccount account = new CustomerAccount();
                account.setUser(user);
                account.setTariff(tariffId != 0 ? tariffDao.findById(tariffId).orElseThrow()
                                                : null);
                account.setBalance(row.getBigDecimal("balance"));
                account.setPermittedOverdraft(row.getBigDecimal("permitted_overdraft"));
                account.setPayoffDate(payoffDate != null ? payoffDate.toInstant()
                                                         : null);
                return account;
            } catch (SQLException e) {
                throw new DaoException(e);
            }
        });
    }

    @Override
    void mapObjectToRow(CustomerAccount account, PreparedStatement row) {
        try {
            int c = 0;
            row.setLong(++c, account.getTariff() != null ? account.getTariff().getId()
                                                         : 0);
            row.setBigDecimal(++c, account.getBalance());
            row.setBigDecimal(++c, account.getPermittedOverdraft());
            row.setTimestamp(++c, account.getPayoffDate() != null ? Timestamp.from(account.getPayoffDate())
                                                                  : null);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    CustomerAccount mapRowToObject(ResultSet row) {
        try {
            long userId = row.getLong("user_id");
            long tariffId = row.getLong("tariff_id");
            Timestamp payoffDate = row.getTimestamp("payoff_date");
            CustomerAccount account = new CustomerAccount();
            account.setUser(userDao.findById(userId).orElseThrow());
            account.setTariff(tariffId != 0 ? tariffDao.findById(tariffId).orElseThrow()
                                            : null);
            account.setBalance(row.getBigDecimal("balance"));
            account.setPermittedOverdraft(row.getBigDecimal("permitted_overdraft"));
            account.setPayoffDate(payoffDate != null ? payoffDate.toInstant()
                                                     : null);
            return account;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    CustomerAccount objectWithId(CustomerAccount account, long id) {
        throw new RuntimeException("This should never happen");
    }
}
