package ru.job4j.cinema.dto;

import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.model.Hall;

import java.time.LocalDateTime;

public class FilmSessionDto {

    private int id;
    private int fileId;
    private String filmName;
    private String filmDescription;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int price;
    private String hallName;
    private int rowCount;
    private int placeCount;

    public FilmSessionDto(Film film, FilmSession filmSession, Hall hall) {
        this.id = filmSession.getId();
        this.fileId = film.getFileId();
        this.filmName = film.getName();
        this.filmDescription = film.getDescription();
        this.startTime = filmSession.getStartTime();
        this.endTime = filmSession.getEndTime();
        this.price = filmSession.getPrice();
        this.hallName = hall.getName();
        this.rowCount = hall.getRowCount();
        this.placeCount = hall.getPlaceCount();
    }

    public FilmSessionDto(int id, int fileId, String filmName, String filmDescription, LocalDateTime startTime, LocalDateTime endTime, int price, String hallName, int rowCount, int placeCount) {
        this.id = id;
        this.fileId = fileId;
        this.filmName = filmName;
        this.filmDescription = filmDescription;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
        this.hallName = hallName;
        this.rowCount = rowCount;
        this.placeCount = placeCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public String getFilmName() {
        return filmName;
    }

    public void setFilmName(String filmName) {
        this.filmName = filmName;
    }

    public String getFilmDescription() {
        return filmDescription;
    }

    public void setFilmDescription(String filmDescription) {
        this.filmDescription = filmDescription;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getHallName() {
        return hallName;
    }

    public void setHallName(String hallName) {
        this.hallName = hallName;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public int getPlaceCount() {
        return placeCount;
    }

    public void setPlaceCount(int placeCount) {
        this.placeCount = placeCount;
    }
}
