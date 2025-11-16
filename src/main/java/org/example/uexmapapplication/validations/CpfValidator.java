package org.example.uexmapapplication.validations;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class CpfValidator implements ConstraintValidator<CPF, String> {

    private static final Pattern NOT_DIGITS = Pattern.compile("[^\\d]");

    private static final Pattern ALL_EQUAL = Pattern.compile("^(\\d)\\1{10}$");

    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext context) {
        if (cpf == null || cpf.isBlank()) {
            return true;
        }

        final String cleanedCpf = NOT_DIGITS.matcher(cpf).replaceAll("");

        if (cleanedCpf.length() != 11) {
            return false;
        }

        if (ALL_EQUAL.matcher(cleanedCpf).matches()) {
            return false;
        }

        try {
            int sum = 0;
            for (int i = 0; i < 9; i++) {
                sum += (cleanedCpf.charAt(i) - '0') * (10 - i);
            }
            int firstDigit = 11 - (sum % 11);
            if (firstDigit > 9) {
                firstDigit = 0;
            }

            if ((cleanedCpf.charAt(9) - '0') != firstDigit) {
                return false;
            }

            sum = 0;
            for (int i = 0; i < 10; i++) {
                sum += (cleanedCpf.charAt(i) - '0') * (11 - i);
            }
            int secondDigit = 11 - (sum % 11);
            if (secondDigit > 9) {
                secondDigit = 0;
            }

            return (cleanedCpf.charAt(10) - '0') == secondDigit;

        } catch (Exception e) {
            return false;
        }
    }
}