package org.rma.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.rma.model.Appointment;

import java.util.List;
import java.util.function.Consumer;

public class AppointmentRepositoryImpl {
    private final SessionFactory sessionFactory;

    public AppointmentRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void create(Appointment appointment) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(appointment);
            transaction.commit();
        }
    }

    public Appointment findById(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Appointment.class, id);
        }
    }

    public List<Appointment> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Appointment", Appointment.class).list();
        }
    }

    public void update(Appointment appointment) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.merge(appointment);
            transaction.commit();
        }
    }

    public void delete(int id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Appointment appointment = session.get(Appointment.class, id);
            if (appointment != null) {
                session.remove(appointment);
            }
            transaction.commit();
        }
    }

    public boolean hasOtherAppointmentsBetween(int doctorId, int patientId) {
        try (Session session = sessionFactory.openSession()) {
            String query = "SELECT COUNT(a) FROM Appointment a " +
                    "WHERE a.doctor.doctorId = :doctorId " +
                    "AND a.patient.patientId = :patientId";
            Long count = session.createQuery(query, Long.class)
                    .setParameter("doctorId", doctorId)
                    .setParameter("patientId", patientId)
                    .uniqueResult();
            return count != null && count > 1;  // ✅ Fix: Check for more than 1 instead of just > 0
        }
    }


}
