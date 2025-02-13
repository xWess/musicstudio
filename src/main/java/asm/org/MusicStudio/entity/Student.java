package asm.org.MusicStudio.entity;
    
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Student extends User {
    private Set<Course> courses = new HashSet<>();
    private List<Enrollment> enrollments = new ArrayList<>();

    public Student() {
        super();
        setRole(Role.STUDENT);
    }

    public Student(Integer id, String name, String email) {
        super();
        setId(id);
        setName(name);
        setEmail(email);
        setRole(Role.STUDENT);
        this.courses = new HashSet<>();
        this.enrollments = new ArrayList<>();
    }

    public void addEnrollment(Enrollment enrollment) {
        enrollments.add(enrollment);
        courses.add(enrollment.getCourse());
    }
    
    public void removeEnrollment(Enrollment enrollment) {
        enrollments.remove(enrollment);
        updateCourses();
    }
    
    private void updateCourses() {
        courses.clear();
        enrollments.stream()
            .map(Enrollment::getCourse)
            .forEach(courses::add);
    }
    
    public boolean isEnrolledIn(Course course) {
        return courses.contains(course);
    }
} 