public class Proprietario {

    public static boolean verificadorCPF(String cpf) {

        String cpfLimpo = cpf.replaceAll("[^0-9]", "");

        if (cpfLimpo.length() != 11 || cpfLimpo.matches("(\\d)\\1{10}")) {
            return false;
        }

        int soma = 0; // Cálculo do primeiro dígito verificador
        for (int i = 0; i < 9; i++) {
            soma += Character.getNumericValue(cpfLimpo.charAt(i)) * (10 - i);
        }
        int numero1 = 11 - (soma % 11);
        if (numero1 > 9) numero1 = 0;

        soma = 0; // Cálculo do segundo dígito verificador
        for (int i = 0; i < 10; i++) {
            soma += Character.getNumericValue(cpfLimpo.charAt(i)) * (11 - i);
        }
        int numero2 = 11 - (soma % 11);
        if (numero2 > 9) numero2 = 0;

        return (numero1 == Character.getNumericValue(cpfLimpo.charAt(9)) &&
                numero2 == Character.getNumericValue(cpfLimpo.charAt(10)));
    }
}
