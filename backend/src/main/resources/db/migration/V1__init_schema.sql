CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    avatar_url VARCHAR(255),
    status VARCHAR(50) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email)
) ENGINE=InnoDB;

CREATE TABLE refresh_tokens (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    token_hash VARCHAR(255) NOT NULL,
    expires_at DATETIME(6) NOT NULL,
    revoked_at DATETIME(6),
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB;

CREATE TABLE user_sessions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    device_info VARCHAR(255),
    ip_address VARCHAR(45),
    last_active_at DATETIME(6) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_user_sessions_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB;

CREATE TABLE password_change_logs (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    changed_at DATETIME(6) NOT NULL,
    ip_address VARCHAR(45),
    PRIMARY KEY (id),
    CONSTRAINT fk_password_change_logs_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB;

CREATE TABLE wallets (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    balance DECIMAL(15,2) NOT NULL,
    is_archived BOOLEAN NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_wallets_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB;

CREATE TABLE wallet_balance_logs (
    id BIGINT NOT NULL AUTO_INCREMENT,
    wallet_id BIGINT NOT NULL,
    balance_before DECIMAL(15,2) NOT NULL,
    balance_after DECIMAL(15,2) NOT NULL,
    change_amount DECIMAL(15,2) NOT NULL,
    reason VARCHAR(255) NOT NULL,
    reference_id BIGINT,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_wallet_balance_logs_wallet FOREIGN KEY (wallet_id) REFERENCES wallets (id)
) ENGINE=InnoDB;

CREATE TABLE categories (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT,
    name VARCHAR(255) NOT NULL,
    icon VARCHAR(255),
    type VARCHAR(50) NOT NULL,
    is_default BOOLEAN NOT NULL,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_categories_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB;

CREATE TABLE transfer_groups (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    from_wallet_id BIGINT NOT NULL,
    to_wallet_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_transfer_groups_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_transfer_groups_from_wallet FOREIGN KEY (from_wallet_id) REFERENCES wallets (id),
    CONSTRAINT fk_transfer_groups_to_wallet FOREIGN KEY (to_wallet_id) REFERENCES wallets (id)
) ENGINE=InnoDB;

CREATE TABLE transactions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    wallet_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    transfer_group_id BIGINT,
    type VARCHAR(50) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    note VARCHAR(255),
    transaction_date DATE NOT NULL,
    deleted_at DATETIME(6),
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_transactions_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_transactions_wallet FOREIGN KEY (wallet_id) REFERENCES wallets (id),
    CONSTRAINT fk_transactions_category FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT fk_transactions_transfer_group FOREIGN KEY (transfer_group_id) REFERENCES transfer_groups (id)
) ENGINE=InnoDB;

CREATE TABLE transaction_audits (
    id BIGINT NOT NULL AUTO_INCREMENT,
    transaction_id BIGINT NOT NULL,
    action VARCHAR(50) NOT NULL,
    old_value JSON,
    new_value JSON,
    changed_by BIGINT NOT NULL,
    changed_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_transaction_audits_transaction FOREIGN KEY (transaction_id) REFERENCES transactions (id)
) ENGINE=InnoDB;

CREATE TABLE budgets (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    `year` INT NOT NULL,
    `month` INT NOT NULL,
    limit_amount DECIMAL(15,2) NOT NULL,
    spent_amount DECIMAL(15,2) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_budgets_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_budgets_category FOREIGN KEY (category_id) REFERENCES categories (id)
) ENGINE=InnoDB;

CREATE TABLE budget_alert_logs (
    id BIGINT NOT NULL AUTO_INCREMENT,
    budget_id BIGINT NOT NULL,
    threshold_percent INT NOT NULL,
    sent_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_budget_alert_logs_budget FOREIGN KEY (budget_id) REFERENCES budgets (id)
) ENGINE=InnoDB;

CREATE TABLE receipts (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    wallet_id BIGINT,
    transaction_id BIGINT,
    image_url VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_receipts_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_receipts_wallet FOREIGN KEY (wallet_id) REFERENCES wallets (id),
    CONSTRAINT fk_receipts_transaction FOREIGN KEY (transaction_id) REFERENCES transactions (id)
) ENGINE=InnoDB;

CREATE TABLE receipt_items (
    id BIGINT NOT NULL AUTO_INCREMENT,
    receipt_id BIGINT NOT NULL,
    item_name VARCHAR(255) NOT NULL,
    quantity DECIMAL(10,2) NOT NULL,
    unit_price DECIMAL(15,2) NOT NULL,
    total_price DECIMAL(15,2) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_receipt_items_receipt FOREIGN KEY (receipt_id) REFERENCES receipts (id)
) ENGINE=InnoDB;

CREATE TABLE receipt_extraction_results (
    id BIGINT NOT NULL AUTO_INCREMENT,
    receipt_id BIGINT NOT NULL,
    raw_ai_response JSON,
    merchant_name VARCHAR(255),
    total_amount DECIMAL(15,2),
    extracted_date DATE,
    confidence_score DECIMAL(4,2),
    PRIMARY KEY (id),
    CONSTRAINT fk_receipt_extraction_results_receipt FOREIGN KEY (receipt_id) REFERENCES receipts (id)
) ENGINE=InnoDB;

CREATE TABLE recurring_transactions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    wallet_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    frequency VARCHAR(50) NOT NULL,
    next_run_date DATE NOT NULL,
    is_active BOOLEAN NOT NULL,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_recurring_transactions_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_recurring_transactions_wallet FOREIGN KEY (wallet_id) REFERENCES wallets (id),
    CONSTRAINT fk_recurring_transactions_category FOREIGN KEY (category_id) REFERENCES categories (id)
) ENGINE=InnoDB;

CREATE TABLE notifications (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    content VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB;

CREATE TABLE export_jobs (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    filter_params JSON,
    status VARCHAR(50) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_export_jobs_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB;

CREATE TABLE export_files (
    id BIGINT NOT NULL AUTO_INCREMENT,
    export_job_id BIGINT NOT NULL,
    file_url VARCHAR(255) NOT NULL,
    file_size BIGINT NOT NULL,
    expires_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_export_files_export_job FOREIGN KEY (export_job_id) REFERENCES export_jobs (id)
) ENGINE=InnoDB;

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens (user_id);
CREATE INDEX idx_user_sessions_user_id ON user_sessions (user_id);
CREATE INDEX idx_wallets_user_id ON wallets (user_id);
CREATE INDEX idx_wallet_balance_logs_wallet_id ON wallet_balance_logs (wallet_id);
CREATE INDEX idx_categories_user_id ON categories (user_id);
CREATE INDEX idx_transactions_user_date ON transactions (user_id, transaction_date);
CREATE INDEX idx_transactions_user_category_date ON transactions (user_id, category_id, transaction_date);
CREATE INDEX idx_transactions_user_wallet_date ON transactions (user_id, wallet_id, transaction_date);
CREATE INDEX idx_budgets_user_year_month ON budgets (user_id, `year`, `month`);
CREATE INDEX idx_receipts_user_status ON receipts (user_id, status);
CREATE INDEX idx_notifications_user_status_created_at ON notifications (user_id, status, created_at);
CREATE INDEX idx_export_jobs_user_id ON export_jobs (user_id);
