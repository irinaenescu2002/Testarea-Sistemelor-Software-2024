package ro.boa.clinic.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @NonNull
    private Patient patient;

    @Column(name = "title", nullable = false)
    @NonNull
    private String title;

    @Column(name = "description", nullable = false)
    @NonNull
    private String description;

    @Column(name = "specialization")
    @NonNull
    private String specialization;

    @Column(name = "status")
    @NonNull
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "response")
    @Nullable
    private String response;

    public Ticket(Doctor doctor,
                  @NonNull Patient patient,
                  @NonNull String title,
                  @NonNull String description,
                  @NonNull String specialization,
                  @NonNull Status status) {
        this.doctor = doctor;
        this.patient = patient;
        this.title = title;
        this.description = description;
        this.specialization = specialization;
        this.status = status;
    }
}