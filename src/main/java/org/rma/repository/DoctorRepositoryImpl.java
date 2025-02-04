package org.rma.repository;

import org.rma.model.Doctor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.rma.model.Patient;

import java.util.List;

public class DoctorRepositoryImpl{

    private final SessionFactory sessionFactory;

    public DoctorRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void create(Doctor doctor) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(doctor);
            transaction.commit();
        }
    }

    public Doctor findById(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "SELECT d FROM Doctor d LEFT JOIN FETCH d.patients WHERE d.doctorId = :id", Doctor.class)
                    .setParameter("id", id)
                    .uniqueResult();
        }
    }

    public List<Doctor> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Doctor", Doctor.class).list();
        }
    }

    public void update(Doctor doctor) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.merge(doctor);
            transaction.commit();
        }
    }

    public void delete(int id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Doctor doctor = session.get(Doctor.class, id);

            if (doctor != null) {

                if (doctor.getOffice() != null) {
                    doctor.getOffice().setDoctor(null);
                    session.merge(doctor.getOffice());
                }

                session.remove(doctor);
            }

            transaction.commit();
        }
    }


    public void addPatientToDoctor(int doctorId, Patient patient) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Doctor doctor = session.get(Doctor.class, doctorId);
            if (doctor != null && !doctor.getPatients().contains(patient)) {
                doctor.getPatients().add(patient);
                session.merge(doctor);
            }
            transaction.commit();
        }
    }

    public void removePatientFromDoctor(int doctorId, Patient patient) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            Doctor doctor = session.get(Doctor.class, doctorId);
            if (doctor != null && doctor.getPatients().contains(patient)) {
                doctor.getPatients().remove(patient);
                session.merge(doctor);
            }
            tx.commit();
        }
    }
}