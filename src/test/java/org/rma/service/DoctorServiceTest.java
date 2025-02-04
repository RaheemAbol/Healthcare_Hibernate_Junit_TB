package org.rma.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.rma.model.Doctor;
import org.rma.repository.DoctorRepositoryImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DoctorServiceTest {

    private DoctorService doctorService;
    private SessionFactory sessionFactory;
    private Session session;
    private Transaction transaction;

    @BeforeEach
    public void setUp() {
        sessionFactory = new Configuration().configure("hibernate-test.cfg.xml").buildSessionFactory();
        session = sessionFactory.openSession();
        transaction = session.beginTransaction();

        DoctorRepositoryImpl doctorRepository = new DoctorRepositoryImpl(sessionFactory);
        doctorService = new DoctorService(doctorRepository);
    }

    @AfterEach
    public void tearDown() {
        if (transaction != null) {
            transaction.rollback();
        }
        if (session != null) {
            session.close();
        }
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @Test
    public void testCreateDoctor() {
        Doctor doctor = new Doctor();
        doctor.setFirstName("Jane");
        doctor.setLastName("Smith");
        doctor.setSpecialty("Cardiology");
        doctor.setEmail("jane.smith@example.com");

        doctorService.createDoctor(doctor);
        assertNotNull(doctor.getDoctorId());
    }

    @Test
    public void testGetDoctorById() {
        Doctor doctor = new Doctor();
        doctor.setFirstName("Gregory");
        doctor.setLastName("House");
        doctor.setSpecialty("Diagnostics");
        doctor.setEmail("house@example.com");

        doctorService.createDoctor(doctor);

        Doctor fetchedDoctor = doctorService.getDoctorById(doctor.getDoctorId());

        assertNotNull(fetchedDoctor, "Doctor should exist in the database.");
        assertEquals(doctor.getDoctorId(), fetchedDoctor.getDoctorId());
        assertEquals("Diagnostics", fetchedDoctor.getSpecialty());
    }

    @Test
    public void testGetAllDoctors() {
        Doctor doctor1 = new Doctor();
        doctor1.setFirstName("Lisa");
        doctor1.setLastName("Cuddy");
        doctor1.setSpecialty("Endocrinology");
        doctor1.setEmail("cuddy@example.com");
        doctorService.createDoctor(doctor1);

        Doctor doctor2 = new Doctor();
        doctor2.setFirstName("James");
        doctor2.setLastName("Wilson");
        doctor2.setSpecialty("Oncology");
        doctor2.setEmail("wilson@example.com");
        doctorService.createDoctor(doctor2);

        List<Doctor> doctors = doctorService.getAllDoctors();

        assertNotNull(doctors);
        assertTrue(doctors.size() >= 2, "There should be at least two doctors in the database.");
    }


    @Test
    public void testUpdateDoctor() {
        Doctor doctor = new Doctor();
        doctor.setFirstName("Jane");
        doctor.setLastName("Smith");
        doctor.setSpecialty("Cardiology");
        doctor.setEmail("jane.smith@example.com");

        doctorService.createDoctor(doctor);
        doctor.setSpecialty("Neurology");
        doctorService.updateDoctor(doctor);

        Doctor updatedDoctor = doctorService.getDoctorById(doctor.getDoctorId());
        assertEquals("Neurology", updatedDoctor.getSpecialty());
    }

    @Test
    public void testDeleteDoctor() {
        Doctor doctor = new Doctor();
        doctor.setFirstName("John");
        doctor.setLastName("Doe");
        doctor.setSpecialty("Dermatology");
        doctor.setEmail("john.doe@example.com");

        doctorService.createDoctor(doctor);
        int id = doctor.getDoctorId();
        doctorService.deleteDoctor(id);

        assertNull(doctorService.getDoctorById(id));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Cardiology", "Neurology", "Dermatology"})
    public void testCreateDoctorWithDifferentSpecialties(String specialty) {
        Doctor doctor = new Doctor();
        doctor.setFirstName("Jane");
        doctor.setLastName("Smith");
        doctor.setSpecialty(specialty);
        doctor.setEmail("jane.smith@example.com");

        doctorService.createDoctor(doctor);
        assertNotNull(doctor.getDoctorId());
        assertEquals(specialty, doctor.getSpecialty());
    }
}
