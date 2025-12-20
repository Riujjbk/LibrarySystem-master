package com.library;

import com.library.dao.BookDao;
import com.library.bean.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.Disabled;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Disabled
public class DatabaseConnectionTest {

    @Autowired
    private BookDao bookDao;

    @Test
    public void testDatabaseConnection() {
        System.out.println("Testing database connection...");

        try {
            List<Book> books = bookDao.selectList(null);
            System.out.println("Successfully connected to database!");
            System.out.println("Found " + books.size() + " books in database");

            if (!books.isEmpty()) {
                System.out.println("First book: " + books.get(0).getName());
            }
        } catch (Exception e) {
            System.err.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

