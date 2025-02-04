package org.rma.model;



import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Entity
@Table(name = "Offices")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "doctor")
public class Office {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OfficeID")
    private int officeId;

    @Column(name = "Location")
    private String location;

    @Column(name = "Phone")
    private String phone;

    @OneToOne
    @JoinColumn(name = "DoctorID", nullable = true)  // Allow DoctorID to be null
    private Doctor doctor;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Office office = (Office) o;
        return officeId == office.officeId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(officeId);
    }
}