CREATE TABLE book (
	id BIGSERIAL NOT NULL,
	title VARCHAR(200),
	isbn VARCHAR(200),
	release_year INTEGER,
	number_of_pages INTEGER,
	booktype INTEGER,
	PRIMARY KEY(id)
);

INSERT INTO book(title, isbn, number_of_pages, release_year, booktype) VALUES
  ('Super exciting thriller', '978-3-86680-192-9', 325, 2015, 0),
  ('A funny book', '978-3-86680-192-4', 120, 1989, 1);