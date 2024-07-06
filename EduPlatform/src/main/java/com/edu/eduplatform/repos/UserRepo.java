package com.edu.eduplatform.repos;

import com.edu.eduplatform.dtos.UserDTO;
import com.edu.eduplatform.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User,Long>{
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String username);
    boolean existsByEmail(String email);
    boolean existsById(Long userId);

    @Query("SELECT COUNT(u) FROM User u")
    long countAllUsers();

    @Query("SELECT COUNT(s) FROM Student s")
    long countAllStudents();

    @Query("SELECT COUNT(i) FROM Instructor i")
    long countAllInstructors();


    List<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(String username, String email);
    List<User> findByUserID(long userId);
    List<User> findByUserType(User.UserType userType);





    User getUserByUserID(Long userId);
}
