package asm.org.MusicStudio.dao;

import asm.org.MusicStudio.db.DatabaseConnection;
import asm.org.MusicStudio.entity.Artist;
import asm.org.MusicStudio.entity.Role;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArtistDAO {
    public void createArtist(Artist artist) throws SQLException {
        String sql = "INSERT INTO artists (name, email, id) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, artist.getName());
            pstmt.setString(2, artist.getEmail());
            pstmt.setInt(3, artist.getId());

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    artist.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public Artist findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM artists WHERE email = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Artist artist = new Artist();  // Use no-args constructor
                    artist.setId(rs.getInt("id"));
                    artist.setName(rs.getString("name"));
                    artist.setEmail(rs.getString("email"));
                    artist.setRole(Role.ARTIST);
                    return artist;
                }
            }
        }
        return null;
    }

    public void updateArtist(Artist artist) throws SQLException {
        String sql = "UPDATE artists SET name = ?, email = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, artist.getName());
            pstmt.setString(2, artist.getEmail());
            pstmt.setInt(3, artist.getId());

            pstmt.executeUpdate();
        }
    }

    public void deleteArtistById(int artistId) throws SQLException {
        String sql = "DELETE FROM artists WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, artistId);
            pstmt.executeUpdate();
        }
    }

    public List<Artist> getAllArtists() throws SQLException {
        String sql = "SELECT * FROM artists";
        List<Artist> artists = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Artist artist = new Artist();
                artist.setId(rs.getInt("id"));
                artist.setName(rs.getString("name"));
                artist.setEmail(rs.getString("email"));
                artists.add(artist);
            }
        }
        return artists;
    }

    public Artist findById(int id) throws SQLException {
        String sql = "SELECT * FROM artists WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Artist artist = new Artist();
                artist.setId(rs.getInt("id"));
                artist.setName(rs.getString("name"));
                artist.setEmail(rs.getString("email"));
                artist.setRole(Role.ARTIST);
                return artist;
            }
        }
        return null;
    }

    private Artist mapArtist(ResultSet rs) throws SQLException {
        Artist artist = new Artist();
        artist.setId(rs.getInt("id"));
        artist.setName(rs.getString("name"));
        artist.setEmail(rs.getString("email"));
        artist.setRole(Role.ARTIST);
        return artist;
    }
}