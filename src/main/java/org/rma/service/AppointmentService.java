package org.rma.service;

import org.rma.model.Appointment;
import org.rma.model.Doctor;
import org.rma.model.Patient;
import org.rma.repository.AppointmentRepositoryImpl;
import org.rma.repository.DoctorRepositoryImpl;
import org.rma.repository.PatientRepositoryImpl;

import java.util.List;

public class AppointmentService {

    private final AppointmentRepositoryImpl appointmentRepository;
    private final DoctorRepositoryImpl doctorRepository;
    private final PatientRepositoryImpl patientRepository;

    public AppointmentService(AppointmentRepositoryImpl appointmentRepository,DoctorRepositoryImpl doctorRepository,PatientRepositoryImpl patientRepository) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    public void createAppointment(Appointment appointment) {
        appointmentRepository.create(appointment);
    }

    public Appointment getAppointmentById(int id) {
        return appointmentRepository.findById(id);
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }


    public void deleteAppointment(int id) {
        Appointment appointment = appointmentRepository.findById(id);

        if (appointment != null) {
            Doctor doctor = doctorRepository.findById(appointment.getDoctor().getDoctorId()); // ✅ Fetch doctor eagerly
            Patient patient = patientRepository.findById(appointment.getPatient().getPatientId()); // ✅ Fetch patient eagerly

            // Delete the appointment first
            appointmentRepository.delete(id);

            // Check if this was the last appointment between the doctor and patient
            boolean hasOtherAppointments = appointmentRepository.hasOtherAppointmentsBetween(doctor.getDoctorId(), patient.getPatientId());

            if (!hasOtherAppointments) {
                // If no other appointments exist, remove the relationship
                doctor.getPatients().remove(patient);
                patient.getDoctors().remove(doctor);
                doctorRepository.update(doctor);
                patientRepository.update(patient);
            }

            System.out.println("Appointment deleted successfully.");
        } else {
            System.out.println("Appointment not found.");
        }
    }

    public void updateAppointment(Appointment appointment) {
        Appointment existingAppointment = appointmentRepository.findById(appointment.getAppointmentId());

        if (existingAppointment != null) {
            Doctor oldDoctor = doctorRepository.findById(existingAppointment.getDoctor().getDoctorId()); // ✅ Fetch eagerly
            Patient oldPatient = patientRepository.findById(existingAppointment.getPatient().getPatientId()); // ✅ Fetch eagerly
            Doctor newDoctor = doctorRepository.findById(appointment.getDoctor().getDoctorId());
            Patient newPatient = patientRepository.findById(appointment.getPatient().getPatientId());

            if (!oldDoctor.equals(newDoctor)) {
                // Check if old doctor-patient relationship should be removed
                boolean hasOtherAppointments = appointmentRepository.hasOtherAppointmentsBetween(oldDoctor.getDoctorId(), oldPatient.getPatientId());

                if (!hasOtherAppointments) {
                    oldDoctor.getPatients().remove(oldPatient);
                    oldPatient.getDoctors().remove(oldDoctor);
                    doctorRepository.update(oldDoctor);
                    patientRepository.update(oldPatient);
                }

                // Add the new doctor-patient relationship
                newDoctor.getPatients().add(newPatient);
                newPatient.getDoctors().add(newDoctor);
                doctorRepository.update(newDoctor);
                patientRepository.update(newPatient);
            }

            if (!oldPatient.equals(newPatient)) {
                boolean hasOtherAppointments = appointmentRepository.hasOtherAppointmentsBetween(oldDoctor.getDoctorId(), oldPatient.getPatientId());

                if (!hasOtherAppointments) {
                    oldDoctor.getPatients().remove(oldPatient);
                    oldPatient.getDoctors().remove(oldDoctor);
                    doctorRepository.update(oldDoctor);
                    patientRepository.update(oldPatient);
                }

                newDoctor.getPatients().add(newPatient);
                newPatient.getDoctors().add(newDoctor);
                doctorRepository.update(newDoctor);
                patientRepository.update(newPatient);
            }

            // Update the appointment itself
            appointmentRepository.update(appointment);
        } else {
            System.out.println("Appointment not found.");
        }
    }


    public boolean hasOtherAppointmentsBetween(int doctorId, int patientId) {
        return appointmentRepository.hasOtherAppointmentsBetween(doctorId, patientId);
    }


}
