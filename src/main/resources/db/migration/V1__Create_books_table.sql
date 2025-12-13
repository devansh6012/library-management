CREATE TABLE books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    author VARCHAR(50) NOT NULL,
    isbn VARCHAR(20) UNIQUE,
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    borrowed_by_member_id BIGINT,
    borrowed_date TIMESTAMP,
    due_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);