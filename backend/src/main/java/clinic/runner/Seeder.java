package ro.boa.clinic.runner;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import ro.boa.clinic.model.Sex;

import java.time.LocalDate;

@RequiredArgsConstructor
@Component
public class Seeder implements ApplicationRunner {
    private final SeederHelper seederHelper;

    @Override
    public void run(ApplicationArguments args) {
        var emmaJohnson = seederHelper.createPatientAccountAndProfile(
                "Emma", "Johnson", Sex.FEMALE, LocalDate.of(1985, 1, 15));
        var ethanSmith = seederHelper.createPatientAccountAndProfile(
                "Ethan Liam", "Smith", Sex.MALE, LocalDate.of(1992, 5, 18));
        var oliviaWilliams = seederHelper.createPatientAccountAndProfile(
                "Olivia", "Williams", Sex.FEMALE, LocalDate.of(1988, 11, 30));
        var noahDavid = seederHelper.createPatientAccountAndProfile(
                "Noah John", "Davis", Sex.MALE, LocalDate.of(2000, 6, 8));

        seederHelper.createDoctorAccountAndProfile("Sarah", "Anderson", "Cardiology");
        seederHelper.createDoctorAccountAndProfile("Michael", "Roberts", "Pediatrics");
        seederHelper.createDoctorAccountAndProfile("Emily", "Davis", "Neurology");
        seederHelper.createDoctorAccountAndProfile("Natalie", "Brown", "Cardiology");
        seederHelper.createDoctorAccountAndProfile("James", "Miller", "Orthopedic Surgery");
        seederHelper.createDoctorAccountAndProfile("Lauren", "Taylor", "Dermatologist");
        seederHelper.createDoctorAccountAndProfile("Benjamin", "Carter", "Psychiatry");
        seederHelper.createDoctorAccountAndProfile("Sophia", "Lewis", "Obstetrics/Gynecology");
        seederHelper.createDoctorAccountAndProfile("David", "Martinez", "Cardiology");
        seederHelper.createDoctorAccountAndProfile("Ryan", "Anderson", "Neurology");
        seederHelper.createDoctorAccountAndProfile("Justin", "Johnson", "Dermatology");
        seederHelper.createDoctorAccountAndProfile("Alex", "Turner", "Gastroenterology");

        seederHelper.createTicket(oliviaWilliams, "Chest Pain",
                "I experience sharp chest pain after physical activity", "Cardiology");
        seederHelper.createTicket(emmaJohnson, "Child's Persistent Cough",
                "My child has been coughing persistently for a week", "Pediatrics");
        seederHelper.createTicket(emmaJohnson, "Chronic Headaches",
                "I suffer from chronic headaches and migraines", "Neurology");
        seederHelper.createTicket(emmaJohnson, "Joint Pain and Stiffness",
                "I have been experiencing joint pain and stiffness in my knees", "Orthopedic Surgery");
        seederHelper.createTicket(oliviaWilliams, "Acne Troubles",
                "I've been struggling with persistent acne breakouts", "Dermatology");
        seederHelper.createTicket(noahDavid, "Skin Rash",
                "I have developed a rash on my arms and need assistance", "Dermatology");
        seederHelper.createTicket(oliviaWilliams, "Menstrual Irregularities",
                "I am experiencing irregularities in my menstrual cycle", "Obstetrics/Gynecology");
        seederHelper.createTicket(ethanSmith, "Anxiety and Sleep Issues",
                "I am struggling with anxiety and facing difficulties with sleep", "Psychiatry");
        seederHelper.createTicket(ethanSmith, "Digestive Discomfort",
                "I am experiencing discomfort in my stomach after eating fried potatoes", "Gastroenterology");
    }
}
