import java.sql.*;
import java.util.Scanner;

public class Mercosul {

    public static void converterPlaca() {
        Scanner scan = new Scanner(System.in);
        System.out.println("\n--- CONVERSÃO DE PLACA PARA MERCOSUL ---");
        System.out.print("Digite a placa no formato exemplificado (AAA-0000): ");
        String placaAntiga = scan.nextLine().toUpperCase();

        if (!placaAntiga.matches("[A-Z]{3}-\\d{4}")) {
            System.out.println("Formato de placa inválid.");
            return;
        }

        try {
            String novaPlaca = Placa.converterParaMercosul(placaAntiga);
            System.out.println("Nova placa Mercosul: " + novaPlaca);

            try (Connection conn = BancoDeDados.getConnection()) {
                Carro carro = Carro.buscarPorPlaca(placaAntiga);
                if (carro == null) {
                    System.out.println("Veículo não encontrado.");
                    return;
                }

                String sql = "UPDATE Carros SET placa_antiga = NULL, placa_mercosul = ? WHERE id = ?";

                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, novaPlaca);
                    stmt.setInt(2, carro.id);

                    int linhas = stmt.executeUpdate();
                    if (linhas > 0) {
                        System.out.println("Placa atualizada no banco de dados.");
                    }
                }
            } catch (SQLException e) {
                System.err.println("Erro no banco de dados: " + e.getMessage());
            }

        } catch (IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}
