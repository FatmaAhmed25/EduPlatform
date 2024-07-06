package com.edu.eduplatform.models;

import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
    public static Specification<User> containsTextInAttributes(String text, String userType, Long userId) {
        return (root, query, cb) -> {
            String pattern = "%" + text + "%";
            return cb.and(
                    cb.or(
                            cb.like(root.get("username"), pattern),
                            cb.like(root.get("email"), pattern),
                            cb.like(root.get("bio"), pattern)
                    ),
                    userType != null ? cb.equal(root.get("userType"), userType) : cb.conjunction(),
                    userId != null ? cb.equal(root.get("userId"), userId) : cb.conjunction()
            );
        };
    }
}
