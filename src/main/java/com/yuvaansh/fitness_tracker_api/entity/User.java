package com.yuvaansh.fitness_tracker_api.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * User Entity - Represents a user in the database
 * 
 */
@Entity 
@Table(name = "users") 
public class User {
    
    /**
     * Primary Key - Every table needs a unique identifier
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Username - must be unique and not null
     * @Column is optional but good for clarity - specifies column properties
     */
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    
    /**
     * Password - will be hashed (encrypted) before storing
     * We'll use BCrypt later, so we need to store the hashed version
     */
    @Column(nullable = false)
    private String password;
    
    /**
     * Sex - M (Male) or F (Female) - needed for BMR calculation
     */
    @Column(nullable = false, length = 1)
    private String sex;
    
    /**
     * Height in centimeters (we'll convert from inches if needed)
     */
    @Column(nullable = false)
    private Double height;  // in cm
    
    /**
     * Weight in pounds (we'll keep it in lbs for macro calculations)
     */
    @Column(nullable = false)
    private Double weight;  // in lbs
    
    /**
     * Activity Level - SEDENTARY, LIGHTLY_ACTIVE, MODERATELY_ACTIVE, VERY_ACTIVE, EXTRA_ACTIVE
     * Used to calculate TDEE (Total Daily Energy Expenditure)
     */
    @Column(nullable = false, length = 20)
    private String activityLevel;
    
    /**
     * Goal - LOSE, MAINTAIN, or GAIN weight
     */
    @Column(nullable = false, length = 10)
    private String goal;
    
    /**
     * Goal Weight Change Per Week in pounds
     * Examples: -1.5 (lose 1.5 lbs/week), 0 (maintain), 1.0 (gain 1 lb/week)
     * Can be negative (lose) or positive (gain)
     */
    @Column(nullable = false)
    private Double goalWeightChangePerWeek;  // in lbs/week

    /**
     * Date of birth - used to derive age for the Mifflin-St Jeor BMR formula.
     * Nullable at the DB level so Hibernate ddl-auto=update can add the column to
     * tables that already have rows; the register DTO enforces it for new users.
     */
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    /**
     * Timestamp when the user account was created
     * @Column(name = "created_at") - maps to "created_at" column (snake_case is common in databases)
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Default constructor - JPA requires a no-arg constructor
     * JPA uses this to create instances when reading from database
     */
    public User() {
    }
    
    /**
     * Constructor for creating a new user
     * We'll set createdAt automatically, so we don't include it here
     */
    public User(String username, String password, String sex, Double height, 
                Double weight, String activityLevel, String goal, Double goalWeightChangePerWeek) {
        this(username, password, sex, height, weight, activityLevel, goal, goalWeightChangePerWeek, null);
    }

    /**
     * Constructor including date of birth, used by registration so BMR/TDEE can
     * be computed from the user's age.
     */
    public User(String username, String password, String sex, Double height,
                Double weight, String activityLevel, String goal, Double goalWeightChangePerWeek,
                LocalDate dateOfBirth) {
        this.username = username;
        this.password = password;
        this.sex = sex;
        this.height = height;
        this.weight = weight;
        this.activityLevel = activityLevel;
        this.goal = goal;
        this.goalWeightChangePerWeek = goalWeightChangePerWeek;
        this.dateOfBirth = dateOfBirth;
        this.createdAt = LocalDateTime.now();  // Set creation time automatically
    }
    
    // Getters and Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getSex() {
        return sex;
    }
    
    public void setSex(String sex) {
        this.sex = sex;
    }
    
    public Double getHeight() {
        return height;
    }
    
    public void setHeight(Double height) {
        this.height = height;
    }
    
    public Double getWeight() {
        return weight;
    }
    
    public void setWeight(Double weight) {
        this.weight = weight;
    }
    
    public String getActivityLevel() {
        return activityLevel;
    }
    
    public void setActivityLevel(String activityLevel) {
        this.activityLevel = activityLevel;
    }
    
    public String getGoal() {
        return goal;
    }
    
    public void setGoal(String goal) {
        this.goal = goal;
    }
    
    public Double getGoalWeightChangePerWeek() {
        return goalWeightChangePerWeek;
    }
    
    public void setGoalWeightChangePerWeek(Double goalWeightChangePerWeek) {
        this.goalWeightChangePerWeek = goalWeightChangePerWeek;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
