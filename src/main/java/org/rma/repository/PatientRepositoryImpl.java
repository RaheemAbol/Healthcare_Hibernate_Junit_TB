package org.rma.repository;



import org.rma.model.Doctor;
import org.rma.model.Patient;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class PatientRepositoryImpl {

    private final SessionFactory sessionFactory;

    public PatientRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void create(Patient patient) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(patient);
            transaction.commit();
        }
    }

    public Patient findById(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "SELECT p FROM Patient p LEFT JOIN FETCH p.doctors WHERE p.patientId = :id", Patient.class)
                    .setParameter("id", id)
                    .uniqueResult();
        }
    }


    public List<Patient> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("select distinct p from Patient p left join fetch p.appointments left join fetch p.doctors", Patient.class).list();
        }
    }


    public void update(Patient patient) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.merge(patient);
            transaction.commit();
        }
    }

    public void delete(int id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Patient patient = session.get(Patient.class, id);
            if (patient != null) {
                session.remove(patient);
            }
            transaction.commit();
        }
    }

    public void addDoctorToPatient(int patientId, Doctor doctor) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Patient patient = session.get(Patient.class, patientId);
            if (patient != null && !patient.getDoctors().contains(doctor)) {
                patient.getDoctors().add(doctor);
                session.merge(patient);
            }
            transaction.commit();
        }
    }

    public void removeDoctorFromPatient(int patientId, Doctor doctor) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            Patient patient = session.get(Patient.class, patientId);
            if (patient != null && patient.getDoctors().contains(doctor)) {
                patient.getDoctors().remove(doctor);
                session.merge(patient);
            }
            tx.commit();
        }
    }
}