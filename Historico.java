import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Historico extends Carro {

    public static void registrarTransferencia(int carroId, String antigoProprietario,
                                              String antigoCPF, String novoProprietario,
                                              String novoCPF) throws SQLException {
        try (Connection conn = BancoDeDados.getConnection()) {
            String sql = "INSERT INTO HistoricoTransferencias (carro_id, antigo_proprietario, "
                    + "antigo_cpf, novo_proprietario, novo_cpf) "
                    + "VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, carroId);
                stmt.setString(2, antigoProprietario);
                stmt.setString(3, antigoCPF);
                stmt.setString(4, novoProprietario);
                stmt.setString(5, novoCPF);
                stmt.executeUpdate();
            }
        }
    }

    public static void consultarBancoDeDados() throws SQLException {
        Scanner scan = new Scanner(System.in);
        System.out.println("\n--- CONSULTA DE VEÍCULOS ---");
        System.out.println("Critérios de busca:");
        System.out.println("1 - Nome do proprietário");
        System.out.println("2 - Placa do veículo");
        System.out.println("3 - CPF do proprietário");
        System.out.println("4 - Excluir veículo");
        System.out.print("Opção: ");

        int op = scan.nextInt();

        switch (op) {
            case 1:
                System.out.print("Nome do proprietário: ");
                scan.next();
                buscarPorProprietario(scan.nextLine());
                break;
            case 2:
                System.out.print("Placa do veículo: ");
                scan.next();
                Carro carro = Carro.buscarPorPlaca(scan.nextLine());
                break;
            case 3:
                System.out.print("CPF do proprietário: ");
                scan.next();
                buscarPorCPF(scan.nextLine());
                break;
            case 4:
                System.out.print("Placa do veículo a excluir: ");
                excluirCarro();
                break;
            default:
                System.out.println("Opção inválida!");
        }
    }

    public static void verificarBancoDeDados() {
        System.out.println("\n--- TODOS OS VEÍCULOS CADASTRADOS ---");

        try (Connection conn = BancoDeDados.getConnection()) {
            String sql = "SELECT * FROM Carros";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    exibirDetalhesVeiculo(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro no banco de dados: " + e.getMessage());
        }
    }

    public static List<Carro> buscarPorProprietario(String nome) {
        List<Carro> resultados = new ArrayList<>();
        String sql = "SELECT * FROM Carros WHERE proprietario LIKE ?";

        try (Connection conn = BancoDeDados.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + nome + "%");  // Partial match

        } catch (SQLException e) {
            System.err.println("Erro na busca por proprietário: " + e.getMessage());
        }
        return resultados;
    }

    public static List<Carro> buscarPorCPF(String cpf) {
        List<Carro> resultados = new ArrayList<>();
        String sql = "SELECT * FROM Carros WHERE cpf = ?";

        try (Connection conn = BancoDeDados.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);  // Exact match

        } catch (SQLException e) {
            System.err.println("Erro na busca por CPF: " + e.getMessage());
        }
        return resultados;
    }

    public static void exibirDetalhesVeiculo(ResultSet rs) throws SQLException {
        System.out.println("\n--- DETALHES DO VEÍCULO ---");
        System.out.println("ID: " + rs.getInt("id"));
        System.out.println("Marca: " + rs.getString("marca"));
        System.out.println("Modelo: " + rs.getString("modelo"));
        System.out.println("Cor: " + rs.getString("cor"));
        System.out.println("Ano: " + rs.getInt("ano"));
        System.out.println("Placa Antiga: " +
                (rs.getString("placa_antiga") != null ? rs.getString("placa_antiga") : "N/A"));
        System.out.println("Placa Mercosul: " +
                (rs.getString("placa_mercosul") != null ? rs.getString("placa_mercosul") : "N/A"));
        System.out.println("Proprietário: " + rs.getString("proprietario"));
        System.out.println("CPF: " + rs.getString("cpf"));
        System.out.println("---------------------------------");
    }

    public static boolean excluirCarro() {
        Scanner scan = new Scanner(System.in);
        System.out.print("Placa do veículo a excluir: ");
        String placa = scan.nextLine().toUpperCase();

        try (Connection conn = BancoDeDados.getConnection()) {
            String sqlSelect = "SELECT id FROM Carros WHERE placa_antiga = ? OR placa_mercosul = ?";
            int carroId = -1;

            try (PreparedStatement stmtSelect = conn.prepareStatement(sqlSelect)) {
                stmtSelect.setString(1, placa);
                stmtSelect.setString(2, placa);
                ResultSet rs = stmtSelect.executeQuery();

                if (rs.next()) {
                    carroId = rs.getInt("id");
                } else {
                    System.out.println("Veículo não encontrado!");
                    return false;
                }
            }

            String sqlDeleteHistory = "DELETE FROM HistoricoTransferencias WHERE carro_id = ?";
            try (PreparedStatement stmtHistory = conn.prepareStatement(sqlDeleteHistory)) {
                stmtHistory.setInt(1, carroId);
                stmtHistory.executeUpdate();
            }

            String sqlDeleteCar = "DELETE FROM Carros WHERE id = ?";
            try (PreparedStatement stmtCar = conn.prepareStatement(sqlDeleteCar)) {
                stmtCar.setInt(1, carroId);
                int rowsAffected = stmtCar.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Veículo excluído com sucesso!");
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao excluir veículo: " + e.getMessage());
        }
        return false;
    }
}
