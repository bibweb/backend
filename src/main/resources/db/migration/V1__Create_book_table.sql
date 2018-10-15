CREATE TABLE book (
	id BIGINT NOT NULL AUTO_INCREMENT,
	title VARCHAR(200),
	isbn VARCHAR(200),
	release_year INTEGER,
	number_of_pages INTEGER,
	booktype INTEGER,
	PRIMARY KEY(id)
);

INSERT INTO book(id, title, isbn, number_of_pages, release_year, booktype) VALUES
  (1, 'Super exciting thriller', '978-3-86680-192-9', 325, 2015, 0),
  (2, 'A funny book', '978-3-86680-192-4', 120, 1989, 1);