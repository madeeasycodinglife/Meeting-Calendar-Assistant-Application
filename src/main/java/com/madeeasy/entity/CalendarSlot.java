package com.madeeasy.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CalendarSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
//    @JsonIgnore  // Ignores this field in serialization to prevent infinite recursion // this is alternate
    @JsonBackReference  // Prevents infinite recursion by stopping the serialization here
    @ToString.Exclude  // Exclude this field from the toString() method
    private Employee employee;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean isAvailable;
}
