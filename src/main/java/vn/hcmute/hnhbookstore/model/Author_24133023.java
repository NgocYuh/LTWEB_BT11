package vn.hcmute.hnhbookstore.model;

import java.time.LocalDate;

public final class Author_24133023 {
    private final int id;
    private final String name;
    private final LocalDate dateOfBirth;
    private final int bookCount;

    public Author_24133023(int id, String name, LocalDate dateOfBirth, int bookCount) {
        this.id = id;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.bookCount = bookCount;
    }
    public int getId() { return id; }
    public int getAuthorId() { return id; }
    public String getName() { return name; }
    public String getAuthorName() { return name; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public int getBookCount() { return bookCount; }
}
