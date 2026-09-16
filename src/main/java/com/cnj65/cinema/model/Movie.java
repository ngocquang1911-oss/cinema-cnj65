package com.cnj65.cinema.model;

import java.time.LocalDate;
import com.cnj65.cinema.util.FormatUtil;

public class Movie {
    private int id;
    private int genreId;
    private String genreName;   // dung khi JOIN de hien thi
    private String title;
    private String director;
    private String actors;
    private int duration;
    private String description;
    private String posterUrl;
    private String trailerUrl;
    private LocalDate releaseDate;
    private String ageRating;
    private String status;      // COMING_SOON, NOW_SHOWING, ENDED

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getGenreId() { return genreId; }
    public void setGenreId(int genreId) { this.genreId = genreId; }
    public String getGenreName() { return genreName; }
    public void setGenreName(String genreName) { this.genreName = genreName; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }
    public String getActors() { return actors; }
    public void setActors(String actors) { this.actors = actors; }
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }
    public String getTrailerUrl() { return trailerUrl; }
    public void setTrailerUrl(String trailerUrl) { this.trailerUrl = trailerUrl; }
    public LocalDate getReleaseDate() { return releaseDate; }
    public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }
    public String getAgeRating() { return ageRating; }
    public void setAgeRating(String ageRating) { this.ageRating = ageRating; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    /**
     * Getter tien ich CHO JSP: JSTL fmt:formatDate KHONG the dinh dang LocalDate
     * (chi ho tro java.util.Date), nen moi Model co truong ngay/gio deu cung
     * cap san getter *Formatted() de JSP goi truc tiep bang ${movie.releaseDateFormatted}
     * thay vi phai dung fmt:formatDate (se loi runtime neu dung sai kieu).
     */
    public String getReleaseDateFormatted() {
        return FormatUtil.date(releaseDate);
    }
}
