package com.notificationservice.persistence;

public class DaoConfig {

    private String tableName;
    private DaoType daoType;
    private String host;
    private String dbName;

    public static DaoConfig emtyConfig() {
        return new DaoConfig();
    }

    private DaoConfig() {
    }

    public DaoConfig(String tableName, DaoType daoType, String host, String dbName) {
        this.tableName = tableName;
        this.daoType = daoType;
        this.host = host;
        this.dbName = dbName;
    }

    public DaoConfig(DaoType daoType, String host) {
        this.daoType = daoType;
        this.host = host;
        this.dbName = "dbName";
        this.tableName = "tableName";
    }

    public DaoConfig(DaoType daoType, String host, String dbname) {
        this.daoType = daoType;
        this.host = host;
        this.dbName = dbname;
        this.tableName = "tableName";
    }

    public DaoType getDaoType() {
        return daoType;
    }

    public String getTableName() {
        return tableName;
    }

    public String getHost() {
        return host;

    }

    public String getDbName() {
        return dbName;
    }
}

