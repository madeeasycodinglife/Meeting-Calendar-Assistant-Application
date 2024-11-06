package com.madeeasy.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.List;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;

    @OneToMany(mappedBy = "employee")
    @JsonManagedReference  // Indicates that this is the parent side of the relationship
    @ToString.Exclude  // Exclude this field from the toString() method
    private List<CalendarSlot> calendarSlots;
}
