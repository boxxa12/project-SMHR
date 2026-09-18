package com.yourname.skyblock.data;

/**
 * Which JDBC backend is active. SQLITE requires no setup (a local file);
 * MYSQL is for a shared/production database. The rest of the plugin talks
 * to the database only through {@link DatabaseManager} and the DAO classes,
 * so switching backends never touches gameplay code.
 */
public enum StorageType {
    SQLITE,
    MYSQL
}
