package org.rma.repository;


import org.rma.model.Doctor;
import org.rma.model.Office;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class OfficeRepositoryImpl  {

    private final SessionFactory sessionFactory;

    public OfficeRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void create(Office office) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            // Ensure the doctor is managed
            Doctor managedDoctor = session.get(Doctor.class, office.getDoctor().getDoctorId());
            if (managedDoctor == null) {
                throw new IllegalArgumentException("Doctor with ID " + office.getDoctor().getDoctorId() + " does not exist.");
            }

            office.setDoctor(managedDoctor); // Use the managed entity

            session.persist(office);
            transaction.commit();
        }
    }



    public Office findById(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Office.class, id);
        }
    }

    public List<Office> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Office", Office.class).list();
        }
    }

    public void updateOffice(Office office) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            // Ensure the new doctor is managed (if changed)
            if (office.getDoctor() != null) {
                Doctor managedDoctor = session.get(Doctor.class, office.getDoctor().getDoctorId());
                office.setDoctor(managedDoctor);
            }

            session.merge(office);
            transaction.commit();
        }
    }




    public void delete(int id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Office office = session.get(Office.class, id);
            if (office != null) {
                if (office.getDoctor() != null) {
                    office.getDoctor().setOffice(null); // Break the association
                    session.merge(office.getDoctor()); // Persist the change in the database
                }

                session.remove(office);
            }
            transaction.commit();
        }
    }

}