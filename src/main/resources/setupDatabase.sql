USE freedb_LedgerDB;

DROP TABLES accounts, users, savings, transactions, loans;

-- Table for users
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) COLLATE utf8mb4_bin NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL
);

-- Table for accounts
CREATE TABLE IF NOT EXISTS accounts (
    user_id INT PRIMARY KEY,
    balance DOUBLE DEFAULT 0.0,
    savings DOUBLE DEFAULT 0.0,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Table for transactions
CREATE TABLE IF NOT EXISTS transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    amount DOUBLE,
    description VARCHAR(255),
    date DATE,
    time TIME,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Table for loans
CREATE TABLE IF NOT EXISTS loans (
    loan_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    principal DOUBLE,
    interest_rate DOUBLE,
    repayment_period INT,
    total_repayment DOUBLE,
    monthly_installment DOUBLE,
    due_date DATE,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Table for savings
CREATE TABLE IF NOT EXISTS savings (
    user_id INT PRIMARY KEY,
    savings_percentage DOUBLE,
    activation_date DATE,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);