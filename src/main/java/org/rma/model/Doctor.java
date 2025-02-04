package org.rma.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "Doctors")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = { "patients", "appointments"})
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DoctorID")
    private int doctorId;

    @Column(name = "FirstName")
    private String firstName;

    @Column(name = "LastName")
    private String lastName;

    @Column(name = "Specialty")
    private String specialty;

    @Column(name = "Email")
    private String email;

    @OneToOne(mappedBy = "doctor", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Office office;


    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Appointment> appointments = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "Doctor_Patient",
            joinColumns = @JoinColumn(name = "DoctorID"),
            inverseJoinColumns = @JoinColumn(name = "PatientID")
    )
    private Set<Patient> patients = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Doctor doctor = (Doctor) o;
        return doctorId == doctor.doctorId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(doctorId);
    }
}