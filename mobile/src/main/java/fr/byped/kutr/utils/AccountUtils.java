package fr.byped.kutr.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Log;

import fr.byped.kutr.R;

/**
 * Created by cyril on 13/01/2017.
 */

public class AccountUtils {

    /**
     * The tag used for log
     */
    private static final String LOG_TAG = AccountUtils.class.getSimpleName();

    /**
     * Make the account name
     * @param server
     * @param username
     * @return The account name
     */
    public static String makeAccountName(String server, String username) {
        return username + " at " + server;
    }

    /** Split the server name from the account name */
    public static String getServerFromAccountName(String accountName) {
        return accountName.substring(accountName.lastIndexOf(' ')+1);
    }
    /** Split the username from the account name */
    public static String getUserFromAccountName(String accountName) {
        return accountName.substring(0, accountName.lastIndexOf(" at "));
    }

    /**
     * Check if an account with the specific user name exists or not
     * @param context  The context used
     * @param username The user name of the account
     * @return If the account exists, return the account
     *         Otherwise return null
     */
    public static Account getUserAccount(Context context, String server, String username) {
        AccountManager accountManager = AccountManager.get(context);
        String accountName = makeAccountName(server, username);
        try {
            Account[] accounts = accountManager.getAccountsByType(context.getResources().getString(R.string.account_type));
            for (Account account : accounts) {
                if (account.name.equalsIgnoreCase(accountName)) {
                    return account;
                }
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getLocalizedMessage(), e);
        }
        return null;
    }

    /**
     * Obtains the password stored in the account manager.
     * Password is encrypted, so it must be decrypted to get its real value
     * @param context     The context used
     * @param server      The server url
     * @param username    The account name that the password is associated
     * @return The password stored in the account manager
     *         "" if the account is not set
     */
    public static String getPasswordyByAccount(Context context, String server, String username) {
        AccountManager accountManager = AccountManager.get(context);
        String encryptedPassword;
        String decryptedPassword = "";

        Account account = getUserAccount(context, server, username);
        if (account != null) {
            encryptedPassword = accountManager.getPassword(account);
            try {
                decryptedPassword = SecurityUtils.decrypt(encryptedPassword);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getLocalizedMessage(), e);
            }
        }

        return decryptedPassword;
    }

    /**
     * Set the encrypted password in the user account. If the account doesn't exist
     * before, it creates a new one.
     * @param password The password to be stored in the account manager
     * @param server      The server url
     * @param username    The account name that the password is associated
     * @return true if the password has been set
     *         false otherwise
     */
    public static boolean setPasswordByUserName(Context context, String server, String username, String password) {
        String accountType = context.getResources().getString(R.string.account_type);
        AccountManager accountManager = AccountManager.get(context);

        // Encrypt the password
        try {
            String encryptedPassword = SecurityUtils.encryptToHex(password);
            Account account = getUserAccount(context, server, username);
            if (account != null) {
                // Check if the old password is the same as the new one
                String oldPassword = accountManager.getPassword(account);
                if (!oldPassword.equalsIgnoreCase(encryptedPassword)) {
                    accountManager.setPassword(account, encryptedPassword);
                }
            }
            // If the account doesn't exist before, create it.
            else {
                String accountName = makeAccountName(server, username);
                Account newUserAccount = new Account(accountName, accountType);
                accountManager.addAccountExplicitly(newUserAccount, encryptedPassword, null);
            }
            return true;
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getLocalizedMessage(), e);
            return false;
        }
    }
}
