CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY UNIQUE, 
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role VARCHAR(50) NOT NULL CHECK (role IN ('DEV', 'QA', 'PM', 'HR')),
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS access_requests (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id),
    systems TEXT[] NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    requested_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS computer_assignments (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id),
    serial_number VARCHAR(100) NOT NULL,
    assigned_at TIMESTAMP DEFAULT NOW()
);

INSERT INTO users (name, email, role, status) VALUES ('Admin User', 'admin@company.com', 'HR', 'APPROVED');