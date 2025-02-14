package asm.org.MusicStudio.services;

import asm.org.MusicStudio.db.DatabaseConnection;
import asm.org.MusicStudio.entity.Schedule;
import asm.org.MusicStudio.entity.Room;
import asm.org.MusicStudio.entity.Course;
import asm.org.MusicStudio.entity.Artist;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class ScheduleService {
    
    private static ScheduleService instance;
    
    public static ScheduleService getInstance() {
        if (instance == null) {
            instance = new ScheduleService();
        }
        return instance;
    }
    
    /**
     * Retrieves schedule based on date and view type
     * @param date The selected date
     * @param viewType The type of view (Daily/Weekly/Monthly)
     * @return List of schedules, never null
     */
    public List<Schedule> getSchedule(LocalDate date, String viewType) throws SQLException {
        String sql = """
            SELECT s.*, c.name as course_name, c.description,
                   u.name as teacher_name,
                   r.location as room_location, r.capacity
            FROM schedules s
            JOIN courses c ON s.course_id = c.id
            JOIN users u ON c.teacher_id = u.id
            JOIN rooms r ON s.room_id = r.id
            WHERE s.status = 'ACTIVE'
            AND s.day_of_week = ?
            ORDER BY s.start_time""";
            
        List<Schedule> schedules = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, date.getDayOfWeek().toString());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Course course = Course.builder()
                        .id(rs.getInt("course_id"))
                        .name(rs.getString("course_name"))
                        .description(rs.getString("description"))
                        .instructor(rs.getString("teacher_name"))
                        .build();
                        
                    Room room = Room.builder()
                        .roomId(rs.getInt("room_id"))
                        .location(rs.getString("room_location"))
                        .capacity(rs.getInt("capacity"))
                        .build();
                        
                    Schedule schedule = Schedule.builder()
                        .scheduleId(rs.getInt("id"))
                        .course(course)
                        .room(room)
                        .date(date)
                        .dayOfWeek(rs.getString("day_of_week"))
                        .startTime(rs.getTime("start_time").toLocalTime())
                        .endTime(rs.getTime("end_time").toLocalTime())
                        .status(rs.getString("status"))
                        .build();
                        
                    schedules.add(schedule);
                }
            }
        }
        return schedules;
    }
    
    /**
     * Updates an existing schedule
     * @param schedule The schedule to update
     */
    public void updateSchedule(Schedule schedule) {
        //TODO: Implement schedule update in database
        //TODO: Validate schedule conflicts
        //TODO: Update related tables if necessary
    }
    
    /**
     * Creates a new schedule entry
     * @param schedule The new schedule to create
     */
    public void createSchedule(Schedule schedule) {
        //TODO: Implement schedule creation in database
        //TODO: Validate room availability
        //TODO: Validate teacher availability
        //TODO: Check for schedule conflicts
    }
    
    /**
     * Creates a course schedule
     * @param course The course to schedule
     * @param room The assigned room
     * @param dayOfWeek Day of the week
     * @param startTime Start time
     * @param endTime End time
     */
    public void createCourseSchedule(Course course, Room room, 
            String dayOfWeek, LocalTime startTime, LocalTime endTime) {
        //TODO: Validate room capacity against course.maxStudents
        //TODO: Check for room availability
        //TODO: Create schedule entry
        //TODO: Update room schedules
    }
    
    /**
     * Creates an artist room booking
     * @param artist The booking artist
     * @param room The room to book
     * @param dayOfWeek Day of the week
     * @param startTime Start time
     * @param endTime End time
     */
    public void createArtistBooking(Artist artist, Room room,
            String dayOfWeek, LocalTime startTime, LocalTime endTime) {
        //TODO: Check room availability
        //TODO: Validate booking duration limits
        //TODO: Create schedule entry
        //TODO: Update artist's bookedRooms
        //TODO: Update room schedules
    }
    
    /**
     * Retrieves all schedules
     * @return List of all schedules, never null
     */
    public List<Schedule> getAllSchedules() throws SQLException {
        return getSchedule(LocalDate.now(), "ALL");
    }

    /**
     * Retrieves schedules for a specific teacher
     * @param teacherId The ID of the teacher
     * @return List of schedules for the teacher, never null
     */
    public List<Schedule> getSchedulesByTeacher(int teacherId) throws SQLException {
        String sql = """
            SELECT s.*, c.name as course_name, c.description,
                   u.name as teacher_name,
                   r.location as room_location, r.capacity
            FROM schedules s
            JOIN courses c ON s.course_id = c.id
            JOIN users u ON c.teacher_id = u.id
            JOIN rooms r ON s.room_id = r.id
            WHERE s.status = 'ACTIVE'
            AND c.teacher_id = ?
            ORDER BY s.day_of_week, s.start_time""";
            
        List<Schedule> schedules = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, teacherId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Course course = Course.builder()
                        .id(rs.getInt("course_id"))
                        .name(rs.getString("course_name"))
                        .description(rs.getString("description"))
                        .instructor(rs.getString("teacher_name"))
                        .build();
                        
                    Room room = Room.builder()
                        .roomId(rs.getInt("room_id"))
                        .location(rs.getString("room_location"))
                        .capacity(rs.getInt("capacity"))
                        .build();
                        
                    Schedule schedule = Schedule.builder()
                        .scheduleId(rs.getInt("id"))
                        .course(course)
                        .room(room)
                        .dayOfWeek(rs.getString("day_of_week"))
                        .startTime(rs.getTime("start_time").toLocalTime())
                        .endTime(rs.getTime("end_time").toLocalTime())
                        .status(rs.getString("status"))
                        .build();
                        
                    schedules.add(schedule);
                }
            }
        }
        return schedules;
    }

    /**
     * Retrieves schedules for a specific student
     * @param studentId The ID of the student
     * @return List of schedules for the student, never null
     */
    public List<Schedule> getSchedulesByStudent(int studentId) throws SQLException {
        String sql = """
            SELECT s.*, c.name as course_name, c.description,
                   u.name as teacher_name,
                   r.location as room_location, r.capacity
            FROM schedules s
            JOIN courses c ON s.course_id = c.id
            JOIN users u ON c.teacher_id = u.id
            JOIN rooms r ON s.room_id = r.id
            JOIN enrollments e ON c.id = e.course_id
            WHERE s.status = 'ACTIVE'
            AND e.student_id = ?
            ORDER BY s.day_of_week, s.start_time""";
            
        List<Schedule> schedules = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, studentId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Course course = Course.builder()
                        .id(rs.getInt("course_id"))
                        .name(rs.getString("course_name"))
                        .description(rs.getString("description"))
                        .instructor(rs.getString("teacher_name"))
                        .build();
                        
                    Room room = Room.builder()
                        .roomId(rs.getInt("room_id"))
                        .location(rs.getString("room_location"))
                        .capacity(rs.getInt("capacity"))
                        .build();
                        
                    Schedule schedule = Schedule.builder()
                        .scheduleId(rs.getInt("id"))
                        .course(course)
                        .room(room)
                        .dayOfWeek(rs.getString("day_of_week"))
                        .startTime(rs.getTime("start_time").toLocalTime())
                        .endTime(rs.getTime("end_time").toLocalTime())
                        .status(rs.getString("status"))
                        .build();
                        
                    schedules.add(schedule);
                }
            }
        }
        return schedules;
    }

    public List<Schedule> getTeacherSchedules(Integer teacherId, LocalDate date) throws SQLException {
        String sql = """
            SELECT s.*, c.name as course_name, c.teacher_id,
                   u.name as teacher_name, r.location as room_name
            FROM schedules s
            JOIN courses c ON s.course_id = c.id
            JOIN users u ON c.teacher_id = u.id
            LEFT JOIN rooms r ON s.room_id = r.id
            WHERE c.teacher_id = ? AND s.date = ?
            ORDER BY s.start_time
            """;
            
        List<Schedule> schedules = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, teacherId);
            stmt.setDate(2, java.sql.Date.valueOf(date));
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Schedule schedule = new Schedule();
                schedule.setId(rs.getInt("id"));
                schedule.setCourseId(rs.getInt("course_id"));
                schedule.setCourseName(rs.getString("course_name"));
                schedule.setTeacherId(rs.getInt("teacher_id"));
                schedule.setTeacherName(rs.getString("teacher_name"));
                schedule.setRoomId(rs.getInt("room_id"));
                schedule.setRoomName(rs.getString("room_name"));
                
                LocalDate scheduleDate = rs.getDate("date").toLocalDate();
                LocalTime startTime = rs.getTime("start_time").toLocalTime();
                LocalTime endTime = rs.getTime("end_time").toLocalTime();
                
                schedule.setDate(scheduleDate);
                schedule.setTimeRange(
                    LocalDateTime.of(scheduleDate, startTime),
                    LocalDateTime.of(scheduleDate, endTime)
                );
                
                schedule.setStatus(rs.getString("status"));
                schedules.add(schedule);
            }
        }
        
        return schedules;
    }
} 