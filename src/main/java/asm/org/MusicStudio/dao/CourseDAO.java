package asm.org.MusicStudio.dao;

import asm.org.MusicStudio.db.DatabaseConnection;
import asm.org.MusicStudio.entity.Course;
import asm.org.MusicStudio.entity.Schedule;
import asm.org.MusicStudio.entity.Room;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseDAO {

    public List<Course> findAllActiveCourses() throws SQLException {
        String sql = """
                SELECT c.*, u.name as teacher_name,
                       s.id as schedule_id, s.day_of_week, s.start_time, s.end_time,
                       r.id as room_id, r.location as room_location, r.capacity
                FROM courses c
                JOIN users u ON c.teacher_id = u.id
                LEFT JOIN schedules s ON c.id = s.course_id AND s.status = 'ACTIVE'
                LEFT JOIN rooms r ON s.room_id = r.id
                WHERE c.active = true
                ORDER BY c.name""";

        Map<Integer, Course> courseMap = new HashMap<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int courseId = rs.getInt("id");
                Course course = courseMap.computeIfAbsent(courseId, k -> {
                    try {
                        return Course.builder()
                                .id(courseId)
                                .name(rs.getString("name"))
                                .description(rs.getString("description"))
                                .instructor(rs.getString("teacher_name"))
                                .monthlyFee(rs.getDouble("monthly_fee"))
                                .maxStudents(rs.getInt("max_students"))
                                .schedules(new ArrayList<>())
                                .build();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    return null;
                });

                // Add schedule if exists
                if (rs.getObject("schedule_id") != null) {
                    Room room = Room.builder()
                            .roomId(rs.getInt("room_id"))
                            .location(rs.getString("room_location"))
                            .capacity(rs.getInt("capacity"))
                            .build();

                    Schedule schedule = Schedule.builder()
                            .scheduleId(rs.getInt("schedule_id"))
                            .dayOfWeek(rs.getString("day_of_week"))
                            .startTime(rs.getTime("start_time").toLocalTime())
                            .endTime(rs.getTime("end_time").toLocalTime())
                            .room(room)
                            .course(course)
                            .status("ACTIVE")
                            .build();

                    course.getSchedules().add(schedule);
                }
            }
        }

        return new ArrayList<>(courseMap.values());
    }

    public boolean hasAvailableSlots(Integer courseId) throws SQLException {
        String sql = """
                    SELECT (c.max_students > (
                        SELECT COUNT(*) FROM enrollments e
                        WHERE e.course_id = ? AND e.status = 'ACTIVE'
                    )) as has_slots
                    FROM courses c
                    WHERE c.id = ?
                """;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, courseId);
            pstmt.setInt(2, courseId);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getBoolean("has_slots");
            }
        }
    }

    public void saveCourse(Course course) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        conn.setAutoCommit(false);

        try {
            // First insert the course
            String courseSql = """
                    INSERT INTO courses (name, description, teacher_id, monthly_fee, max_students)
                    VALUES (?, ?, (SELECT id FROM users WHERE name = ?), ?, ?)
                    RETURNING id""";

            int courseId;
            try (PreparedStatement pstmt = conn.prepareStatement(courseSql)) {
                pstmt.setString(1, course.getName());
                pstmt.setString(2, course.getDescription());
                pstmt.setString(3, course.getInstructor());
                pstmt.setDouble(4, course.getMonthlyFee());
                pstmt.setInt(5, course.getMaxStudents());

                ResultSet rs = pstmt.executeQuery();
                if (!rs.next()) {
                    throw new SQLException("Failed to get generated course ID");
                }
                courseId = rs.getInt(1);
            }

            // Then insert all schedules
            if (!course.getSchedules().isEmpty()) {
                String scheduleSql = """
                        INSERT INTO schedules (course_id, room_id, day_of_week, start_time, end_time, status)
                        VALUES (?, ?, ?, ?, ?, ?)""";

                try (PreparedStatement pstmt = conn.prepareStatement(scheduleSql)) {
                    for (Schedule schedule : course.getSchedules()) {
                        pstmt.setInt(1, courseId);
                        pstmt.setInt(2, schedule.getRoom().getRoomId());
                        pstmt.setString(3, schedule.getDayOfWeek());
                        pstmt.setTime(4, Time.valueOf(schedule.getStartTime()));
                        pstmt.setTime(5, Time.valueOf(schedule.getEndTime()));
                        pstmt.setString(6, "ACTIVE");
                        pstmt.addBatch();
                    }
                    pstmt.executeBatch();
                }
            }

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public void deleteCourse(int courseId) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        conn.setAutoCommit(false);

        try {
            // First delete associated schedules
            String deleteSchedulesSql = "DELETE FROM schedules WHERE course_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteSchedulesSql)) {
                pstmt.setInt(1, courseId);
                pstmt.executeUpdate();
            }

            // Then delete the course
            String deleteCourseSql = "DELETE FROM courses WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteCourseSql)) {
                pstmt.setInt(1, courseId);
                pstmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public void updateCourse(Course course) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        conn.setAutoCommit(false);

        try {
            // Update course details
            String courseSql = """
                    UPDATE courses
                    SET name = ?,
                        description = ?,
                        teacher_id = (SELECT id FROM users WHERE name = ?),
                        monthly_fee = ?,
                        max_students = ?
                    WHERE id = ?""";

            try (PreparedStatement pstmt = conn.prepareStatement(courseSql)) {
                pstmt.setString(1, course.getName());
                pstmt.setString(2, course.getDescription());
                pstmt.setString(3, course.getInstructor());
                pstmt.setDouble(4, course.getMonthlyFee());
                pstmt.setInt(5, course.getMaxStudents());
                pstmt.setInt(6, course.getId());

                pstmt.executeUpdate();
            }

            // Delete existing schedules
            String deleteSchedulesSql = "DELETE FROM schedules WHERE course_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteSchedulesSql)) {
                pstmt.setInt(1, course.getId());
                pstmt.executeUpdate();
            }

            // Insert new schedules
            if (!course.getSchedules().isEmpty()) {
                String scheduleSql = """
                        INSERT INTO schedules (course_id, room_id, day_of_week, start_time, end_time, status)
                        VALUES (?, ?, ?, ?, ?, ?)""";

                try (PreparedStatement pstmt = conn.prepareStatement(scheduleSql)) {
                    for (Schedule schedule : course.getSchedules()) {
                        pstmt.setInt(1, course.getId());
                        pstmt.setInt(2, schedule.getRoom().getRoomId());
                        pstmt.setString(3, schedule.getDayOfWeek());
                        pstmt.setTime(4, Time.valueOf(schedule.getStartTime()));
                        pstmt.setTime(5, Time.valueOf(schedule.getEndTime()));
                        pstmt.setString(6, "ACTIVE");
                        pstmt.addBatch();
                    }
                    pstmt.executeBatch();
                }
            }

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    private String buildScheduleString(ResultSet rs) throws SQLException {
        Time startTime = rs.getTime("start_time");
        Time endTime = rs.getTime("end_time");
        String dayOfWeek = rs.getString("day_of_week");
        String location = rs.getString("location");

        if (startTime != null && endTime != null && dayOfWeek != null && location != null) {
            return String.format("%s %s-%s (%s)",
                    dayOfWeek,
                    startTime.toLocalTime().toString(),
                    endTime.toLocalTime().toString(),
                    location);
        }
        return "Schedule to be announced";
    }

    public Course findById(Integer id) throws SQLException {
        String sql = """
                    SELECT c.*, u.name as teacher_name,
                           s.day_of_week, s.start_time, s.end_time, r.location
                    FROM courses c
                    JOIN users u ON c.teacher_id = u.id
                    LEFT JOIN schedules s ON c.id = s.course_id
                    LEFT JOIN rooms r ON s.room_id = r.id
                    WHERE c.id = ?
                """;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String schedule = buildScheduleString(rs);
                    return Course.builder()
                            .id(rs.getInt("id"))
                            .name(rs.getString("name"))
                            .description(rs.getString("description"))
                            .monthlyFee(rs.getDouble("monthly_fee"))
                            .instructor(rs.getString("teacher_name"))
                            .maxStudents(rs.getInt("max_students"))
                            .schedule(schedule)
                            .build();
                }
                return null; // Return null if no course found
            }
        }
    }
}