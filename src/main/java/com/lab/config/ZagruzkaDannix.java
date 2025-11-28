package com.lab.config;

import com.lab.entity.*;
import com.lab.entity.enums.Gender;
import com.lab.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class ZagruzkaDannix {

    @Bean
    public CommandLineRunner demo(PatientRepository patientRepository,
                                  OrderRepository orderRepository,
                                  TestTypeRepository testTypeRepository,
                                  TestRepository testRepository) {
        return args -> {
            Patient patient1 = new Patient(
                    "Антонив", "Владислав", "Дмитриевич",
                    LocalDate.of(2000, 12, 14),
                    Gender.MALE,
                    "+7911651445",
                    "vlad2000chess@gmail.com"
            );

            Patient patient2 = new Patient(
                    "Медведева", "Мария", "Романовна",
                    LocalDate.of(2003, 6, 13),
                    Gender.FEMALE,
                    "+7998498",
                    "medvedeva@mail.ru"
            );

            patientRepository.save(patient1);
            patientRepository.save(patient2);

            TestType bloodTest = new TestType(
                    "Общий анализ крови",
                    "OAK-001",
                    "Анализ крови с определением основных показателей",
                    new BigDecimal("500.00")
            );

            TestType psixTest = new TestType(
                    "Анализ на наличие психотропных веществ",
                    "АНП-001",
                    "Анализ на наличие психотропных веществ и наркотиков до кучи",
                    new BigDecimal("8000.00")
            );

            TestType biochemistry = new TestType(
                    "Биохимический анализ крови",
                    "BIO-001",
                    "Расширенный биохимический анализ, показывает то, что не показывает общй анализ крови",
                    new BigDecimal("2500.00")
            );

            testTypeRepository.save(bloodTest);
            testTypeRepository.save(psixTest);
            testTypeRepository.save(biochemistry);

            Order order1 = new Order(patient1, "Плановое обследование");
            Order order2 = new Order(patient2, "Диагностика из-за симптомов");

            orderRepository.save(order1);
            orderRepository.save(order2);

            // Тестовые тесты
            Test test1 = new Test(order1, bloodTest);
            Test test2 = new Test(order1, psixTest);
            Test test3 = new Test(order2, biochemistry);

            testRepository.save(test1);
            testRepository.save(test2);
            testRepository.save(test3);

            System.out.println("Dannie zagruzheni!");
            System.out.println("WEB: http://localhost:8080/web");
            System.out.println("REST API: http://localhost:8080/api/patients");
        };
    }
}