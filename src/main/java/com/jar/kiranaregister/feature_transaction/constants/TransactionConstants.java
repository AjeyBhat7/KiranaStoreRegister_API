package com.jar.kiranaregister.feature_transaction.constants;

public class TransactionConstants {

    public static final String LOG_ADD_TRANSACTION =
            "Adding transaction for amount: {0} and currency: {1}";
    public static final String LOG_INVALID_CURRENCY = "Invalid currency: {0}";
    public static final String LOG_TRANSACTION_INITIATED_BY_USER =
            "Transaction initiated by user: {0}";
    public static final String LOG_TRANSACTION_SAVED_SUCCESSFULLY =
            "Transaction saved successfully for user: {0}";
    public static final String LOG_RETRIEVED_TRANSACTIONS =
            "Retrieved {0} transactions for user: {1}";
    public static final String LOG_FETCHING_TRANSACTION_BY_ID =
            "Fetching transaction with ID: {0} for user: {1}";
    public static final String LOG_TRANSACTION_NOT_FOUND =
            "Transaction not found with ID: {0} for user: {1}";
    public static final String LOG_DELETE_TRANSACTION_REQUEST =
            "Request to delete transaction with ID: {0}";
    public static final String LOG_TRANSACTION_DELETED_SUCCESSFULLY =
            "Transaction with ID: {0} deleted successfully";
    public static final String LOG_FETCHING_TRANSACTIONS_FOR_INTERVAL =
            "Fetching transactions for interval: {0}";
    public static final String LOG_RETRIEVED_TRANSACTIONS_FOR_INTERVAL =
            "Retrieved {0} transactions for interval: {1}";
    public static final String LOG_BILL_GENERATED_SUCCESSFULLY =
            "Bill generated successfully : amount: {0} currency: {1}";
}
