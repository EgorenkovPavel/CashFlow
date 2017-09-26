package com.epipasha.cashflow.db;

import android.provider.BaseColumns;

public class CashFlowContract {

    public static final class AccountEntry implements BaseColumns{

        public static final String TABLE_NAME = "account";

        public static final String COLUMN_TITLE = "account_title";
    }

    public static final class CategoryEntry implements BaseColumns{

        public static final String TABLE_NAME = "category";

        public static final String COLUMN_TITLE = "category_title";
        public static final String COLUMN_TYPE = "category_type";
        public static final String COLUMN_BUDJET = "category_budjet";

    }

    public static final class OperationEntry implements BaseColumns{

        public static final String TABLE_NAME = "operation";

        public static final String COLUMN_DATE = "operation_date";
        public static final String COLUMN_TYPE = "operation_type";
        public static final String COLUMN_ACCOUNT_ID = "operation_account_id";
        public static final String COLUMN_CATEGORY_ID = "operation_category_id";
        public static final String COLUMN_RECIPIENT_ACCOUNT_ID = "operation_recipient_account_id";
        public static final String COLUMN_SUM = "operation_sum";

    }

    public static final class AccountBalanceEntry{

        public static final String TABLE_NAME = "account_balance";

        public static final String COLUMN_DATE = "account_balance_date";
        public static final String COLUMN_OPERATION_ID = "account_balance_operation_id";
        public static final String COLUMN_ACCOUNT_ID = "account_balance_account_id";
        public static final String COLUMN_SUM = "account_balance_sum";

    }

    public static final class CategoryCostEntry{

        public static final String TABLE_NAME = "category_cost";

        public static final String COLUMN_DATE = "category_cost_date";
        public static final String COLUMN_OPERATION_ID = "category_cost_operation_id";
        public static final String COLUMN_ACCOUNT_ID = "category_cost_account_id";
        public static final String COLUMN_CATEGORY_ID = "category_cost_category_id";
        public static final String COLUMN_SUM = "account_balance_sum";

    }

}
