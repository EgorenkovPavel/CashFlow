package com.epipasha.cashflow.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class CashFlowContract {

    public static final String AUTHORITY = "com.epipasha.cashflow";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_ACCOUNTS = "accounts";
    public static final String PATH_CATEGORY = "category";
    public static final String PATH_OPERATION = "operation";
    public static final String PATH_BUDGET = "budget";
    public static final String PATH_CATEGORY_COST = "category_cost";
    public static final String PATH_CATEGORY_COST_GROUPED = "category_cost_grouped";
    public static final String PATH_ACCOUNT_BALANCE = "account_balance";

    public static final class AccountEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ACCOUNTS).build();

        public static final String TABLE_NAME = "account";

        public static final String COLUMN_TITLE = "account_title";
        public static final String SERVICE_COLUMN_SUM = "account_sum";

        public static Uri buildAccountUriWithId(long id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(id))
                    .build();
        }
    }

    public static final class CategoryEntry implements BaseColumns{

        public static final String PATH_COST = "cost";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CATEGORY).build();

        public static final Uri CATEGORY_COST_URI =
                CONTENT_URI.buildUpon().appendPath(PATH_COST).build();

        public static final String TABLE_NAME = "category";

        public static final String COLUMN_TITLE = "category_title";
        public static final String COLUMN_TYPE = "category_type";
        public static final String COLUMN_BUDGET = "category_budget";

        public static Uri buildCategoryUriWithId(long id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(id))
                    .build();
        }

        public static Uri buildCategoryCostUri(int year, int month) {
            return CATEGORY_COST_URI.buildUpon()
                    .appendPath(Integer.toString(year))
                    .appendPath(Integer.toString(month))
                    .build();
        }
    }

    public static final class OperationEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_OPERATION).build();

        public static final String TABLE_NAME = "operation";

        public static final String COLUMN_DATE = "operation_date";
        public static final String COLUMN_TYPE = "operation_type";
        public static final String COLUMN_ACCOUNT_ID = "operation_account_id";
        public static final String COLUMN_CATEGORY_ID = "operation_category_id";
        public static final String COLUMN_RECIPIENT_ACCOUNT_ID = "operation_recipient_account_id";
        public static final String COLUMN_SUM = "operation_sum";

        public static final String SERVICE_COLUMN_ACCOUNT_TITLE = "operation_account_title";
        public static final String SERVICE_COLUMN_CATEGORY_TITLE = "operation_category_title";
        public static final String SERVICE_COLUMN_RECIPIENT_ACCOUNT_TITLE = "operation_recipient_account_title";

        public static Uri buildOperationUriWithId(long id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(id))
                    .build();
        }
    }

    public static final class AccountBalanceEntry{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ACCOUNT_BALANCE).build();

        public static final String TABLE_NAME = "account_balance";

        public static final String COLUMN_DATE = "account_balance_date";
        public static final String COLUMN_OPERATION_ID = "account_balance_operation_id";
        public static final String COLUMN_ACCOUNT_ID = "account_balance_account_id";
        public static final String COLUMN_SUM = "account_balance_sum";

    }

    public static final class CategoryCostEntry{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CATEGORY_COST).build();

        public static final String TABLE_NAME = "category_cost";

        public static final String COLUMN_DATE = "category_cost_date";
        public static final String COLUMN_YEAR = "category_cost_year";
        public static final String COLUMN_MONTH = "category_cost_month";
        public static final String COLUMN_OPERATION_ID = "category_cost_operation_id";
        public static final String COLUMN_ACCOUNT_ID = "category_cost_account_id";
        public static final String COLUMN_CATEGORY_ID = "category_cost_category_id";
        public static final String COLUMN_SUM = "category_cost_balance_sum";

        public static Uri buildCategoryCostUriWithId(long id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(id))
                    .build();
        }

        public static Uri buildCategoryCostUriWithOperationType() {
            return BASE_CONTENT_URI.buildUpon()
                    .appendPath(PATH_CATEGORY_COST_GROUPED)
                    .build();
        }

    }

}
