CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY UNIQUE, 
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL CHECK (role IN ('DEV', 'QA', 'PM', 'AC')),
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'DISABLED')),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS access_requests (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id),
    systems TEXT[] NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    requested_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS inventory_assignments (
    id SERIAL PRIMARY KEY,
    description TEXT,
    user_id INTEGER REFERENCES users(id),
    serial_number VARCHAR(100) NOT NULL,
    assigned_at TIMESTAMP DEFAULT NOW(),
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS tokens (
    id SERIAL PRIMARY KEY UNIQUE,
    user_id INTEGER REFERENCES users(id),
    token VARCHAR(255) UNIQUE NOT NULL,
    token_status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (token_status IN ('ACTIVE', 'EXPIRED', 'REVOKED')),
    created_at TIMESTAMP DEFAULT NOW(),
    expires_at TIMESTAMP NOT NULL
);

INSERT INTO users (name, email, password, role, status)
VALUES ('Admin User', 'admin@test.com', 'admin123', 'AC', 'APPROVED');
