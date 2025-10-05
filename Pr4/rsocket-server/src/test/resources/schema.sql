CREATE TABLE IF NOT EXISTS orders (
    id IDENTITY PRIMARY KEY,
    customer VARCHAR(128),
    address VARCHAR(256),
    items VARCHAR(512),
    status VARCHAR(32),
    created_at TIMESTAMP
);