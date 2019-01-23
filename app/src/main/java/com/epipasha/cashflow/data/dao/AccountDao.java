package com.epipasha.cashflow.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.epipasha.cashflow.data.entites.Account;
import com.epipasha.cashflow.data.entites.AccountWithBalance;

import java.util.List;

@Dao
public interface AccountDao {

    @Query("SELECT * FROM accounts ORDER BY title")
    LiveData<List<Account>> loadAllAccounts();

    @Query("SELECT * FROM accounts ORDER BY title")
    List<Account> getAllAccounts();

    @Query("SELECT * FROM accounts WHERE id != :id ORDER BY title")
    LiveData<List<Account>> loadAllAccountsExceptId(int id);

    @Query("SELECT accounts.id as id, "
            + "accounts.title as title, "
            + "balance.sum as sum "
            + "FROM accounts as accounts "
            + "LEFT OUTER JOIN "
            + "(SELECT "
            + "balance.account_id as account_id, "
            + "SUM(balance.sum) as sum "
            + "FROM balance "
            + "GROUP BY balance.account_id) as balance "
            + "ON accounts.id = balance.account_id "
            + "ORDER BY title")
    LiveData<List<AccountWithBalance>> loadAllAccountsWithBalance();

    @Query("SELECT accounts.id as id, "
            + "accounts.title as title, "
            + "balance.sum as sum "
            + "FROM accounts as accounts "
            + "LEFT OUTER JOIN "
            + "(SELECT "
            + "balance.account_id as account_id, "
            + "SUM(balance.sum) as sum "
            + "FROM balance "
            + "GROUP BY balance.account_id) as balance "
            + "ON accounts.id = balance.account_id "
            + "ORDER BY title")
    List<AccountWithBalance> getAllAccountsWithBalance();

    @Query("SELECT accounts.id as id, "
            + "accounts.title as title, "
            + "balance.sum as sum "
            + "FROM accounts as accounts "
            + "LEFT OUTER JOIN "
            + "(SELECT "
            + "balance.account_id as account_id, "
            + "SUM(balance.sum) as sum "
            + "FROM balance "
            + "GROUP BY balance.account_id) as balance "
            + "ON accounts.id = balance.account_id "
            + "WHERE accounts.id != :id "
            + "ORDER BY title")
    LiveData<List<AccountWithBalance>> loadAllAccountsWithBalanceExceptId(int id);

    @Insert
    void insertAccount(Account account);

    @Insert
    void insertAccounts(List<Account> accounts);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateAccount(Account account);

    @Delete
    void deleteAccount(Account account);

    @Query("DELETE FROM accounts")
    void deleteAll();

    @Query("SELECT * FROM accounts WHERE id = :id")
    LiveData<Account> loadAccountById(int id);

    @Query("SELECT * FROM accounts WHERE id = :id")
    Account getAccountById(int id);

}
