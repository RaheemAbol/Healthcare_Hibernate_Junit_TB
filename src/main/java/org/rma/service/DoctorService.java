package org.rma.service;


import org.rma.model.Doctor;
import org.rma.model.Patient;
import org.rma.repository.DoctorRepositoryImpl;

import java.util.List;

public class DoctorService {

    private final DoctorRepositoryImpl doctorRepository;

    public DoctorService(DoctorRepositoryImpl doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public void createDoctor(Doctor doctor) {
        doctorRepository.create(doctor);
    }

    public Doctor getDoctorById(int id) {
        return doctorRepository.findById(id);
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public void updateDoctor(Doctor doctor) {
        doctorRepository.update(doctor);
    }

    public void deleteDoctor(int id) {
        doctorRepository.delete(id);
    }

    public void addPatientToDoctor(int doctorId, Patient patient) {
        doctorRepository.addPatientToDoctor(doctorId, patient);
    }

    public void removePatientFromDoctor(int doctorId, Patient patient) {
        doctorRepository.removePatientFromDoctor(doctorId, patient);
    }


}