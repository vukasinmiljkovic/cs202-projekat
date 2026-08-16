package rs.autoservice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Samostalni pokretač testova (Test Runner) za laku demonstraciju i odbranu projekta.
 * Izvršava sve JUnit 5 test metode i ispisuje detaljan izveštaj u konzoli.
 *
 * @author Vukasin Miljkovic
 */
public class TestRunner {

    public static void main(String[] args) {
        System.out.println("======================================================================");
        System.out.println("     AUTO SERVIS MANAGEMENT SYSTEM – JUNIT 5 TEST SUITE (CS202)");
        System.out.println("======================================================================");

        List<Class<?>> testClasses = List.of(
                ValidationUtilTest.class,
                PriceCalculatorTest.class,
                AppointmentServiceTest.class,
                ResponseGenericTest.class,
                GenericFilterTest.class
        );

        int totalTests = 0;
        int passedTests = 0;
        int failedTests = 0;
        List<String> failures = new ArrayList<>();

        for (Class<?> testClass : testClasses) {
            System.out.println("\n📂 Pokrećem test klasu: " + testClass.getSimpleName());
            System.out.println("----------------------------------------------------------------------");

            Object instance;
            try {
                instance = testClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                System.err.println("❌ Nije moguće instancirati test klasu " + testClass.getName() + ": " + e.getMessage());
                continue;
            }

            for (Method method : testClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Test.class)) {
                    totalTests++;
                    String displayName = method.isAnnotationPresent(DisplayName.class)
                            ? method.getAnnotation(DisplayName.class).value()
                            : method.getName();

                    try {
                        method.setAccessible(true);
                        method.invoke(instance);
                        passedTests++;
                        System.out.println("  ✅ [PASS] " + displayName);
                    } catch (Throwable t) {
                        failedTests++;
                        Throwable cause = t.getCause() != null ? t.getCause() : t;
                        String errorMsg = "  ❌ [FAIL] " + displayName + " -> " + cause.getMessage();
                        System.out.println(errorMsg);
                        failures.add(testClass.getSimpleName() + "." + method.getName() + ": " + cause.getMessage());
                    }
                }
            }
        }

        System.out.println("\n======================================================================");
        System.out.println("                     IZVEŠTAJ O TESTIRANJU");
        System.out.println("======================================================================");
        System.out.println("  Ukupno pokrenuto testova: " + totalTests);
        System.out.println("  Uspešno položeno:        " + passedTests + " (" + (totalTests > 0 ? (passedTests * 100 / totalTests) : 0) + "%)");
        System.out.println("  Palo / Neuspešno:         " + failedTests);

        if (failedTests > 0) {
            System.out.println("\nSpisak grešaka:");
            for (String f : failures) {
                System.out.println(" - " + f);
            }
        } else {
            System.out.println("\n🏆 SVI TESTOVI SU USPEŠNO POLOŽENI! PROJEKAT JE 100% ISPRAVAN.");
        }
        System.out.println("======================================================================\n");
    }
}
