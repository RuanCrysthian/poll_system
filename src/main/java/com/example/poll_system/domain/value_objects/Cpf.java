package com.example.poll_system.domain.value_objects;

import com.example.poll_system.domain.exceptions.InvalidFieldException;

public class Cpf {
    private final String cpf;

    public Cpf(String value) {
        if (!validate(value)) {
            throw new InvalidFieldException("Invalid CPF");
        }
        this.cpf = value;
    }

    public String getCpf() {
        return cpf;
    }

    public boolean validate(String cpf) {
        cpf = clean(cpf);
        if (!hasMinimumLength(cpf) || isDigitsRepeated(cpf))
            return false;
        int[] cpfNumber = toArray(cpf);
        return checkDigits(cpfNumber);
    }

    public static String clean(String cpf) {
        return cpf.replaceAll("[^0-9]", "");
    }

    public static boolean hasMinimumLength(String cpf) {
        return cpf.length() == 11;
    }

    public static boolean isDigitsRepeated(String cpf) {
        return cpf.matches("(\\d)\\1{10}");
    }

    public static int[] toArray(String cpf) {
        int[] cpfNumber = new int[11];
        for (int i = 0; i < 11; i++) {
            cpfNumber[i] = Character.getNumericValue(cpf.charAt(i));
        }
        return cpfNumber;
    }

    public static boolean checkDigits(int[] cpfNumbers) {
        int sum;
        int rest;
        int digit1;
        int digit2;
        sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += cpfNumbers[i] * (10 - i);
        }
        rest = sum % 11;
        digit1 = (rest < 2) ? 0 : (11 - rest);
        if (cpfNumbers[9] != digit1) {
            return false;
        }
        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += cpfNumbers[i] * (11 - i);
        }
        rest = sum % 11;
        digit2 = (rest < 2) ? 0 : (11 - rest);
        return cpfNumbers[10] == digit2;
    }
}
