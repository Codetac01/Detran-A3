import java.sql.*;
import java.util.Scanner;

public class Carro {

    int id, ano;
    String placaAntiga, placaMercosul, marca, modelo, cor, proprietario, cpf;
    Scanner scan = new Scanner(System.in);

    public static void setCarro() {
        try (Connection conn = BancoDeDados.getConnection()) {
            System.out.println("\n--- CADASTRO DE NOVO VEÍCULO ---");
            Scanner scan = new Scanner(System.in);

            System.out.print("Placa (formato AAA-0000 ou AAA0A00): ");
            String placa = scan.nextLine().toUpperCase();

            if (!Placa.validarPlaca(placa)) {
                System.out.println("Formato de placa inválido!");
                return;
            }

            System.out.print("Marca: ");
            String marca = scan.nextLine();

            System.out.print("Modelo: ");
            String modelo = scan.nextLine();

            System.out.print("Cor: ");
            String cor = scan.nextLine();

            System.out.print("Proprietário: ");
            String proprietario = scan.nextLine();

            System.out.print("CPF (XXX.XXX.XXX-XX): ");
            String cpf = scan.nextLine();

            if (!Proprietario.verificadorCPF(cpf)) {
                System.out.println("CPF inválido!");
                return;
            }

            System.out.print("Ano: ");
            int ano = scan.nextInt();

            String placaAntiga = null;
            String placaMercosul = null;

            if (placa.contains("-")) {
                placaAntiga = placa;
            } else {
                placaMercosul = placa;
            }

            if (Placa.existePlaca(placa)) {
                System.out.println("Placa já cadastrada no sistema!");
                return;
            }

            String sql = "INSERT INTO Carros (placa_antiga, placa_mercosul, marca, modelo, cor, proprietario, cpf, ano) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, placaAntiga);
                stmt.setString(2, placaMercosul);
                stmt.setString(3, marca);
                stmt.setString(4, modelo);
                stmt.setString(5, cor);
                stmt.setString(6, proprietario);
                stmt.setString(7, cpf);
                stmt.setInt(8, ano);

                int linhas = stmt.executeUpdate();
                if (linhas > 0) {
                    System.out.println("Veículo cadastrado com sucesso!");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro no banco de dados: " + e.getMessage());
        }
    }

    public static void transferencia() {
        Scanner scan = new Scanner(System.in);
        try (Connection conn = BancoDeDados.getConnection()) {
            System.out.println("\n--- TRANSFERÊNCIA DE PROPRIEDADE ---");
            System.out.print("Placa do veículo: ");
            String placa = scan.nextLine().toUpperCase();

            Carro carro = Carro.buscarPorPlaca(placa);
            if (carro == null) {
                System.out.println("Veículo não encontrado!");
                return;
            }

            System.out.print("Novo proprietário: ");
            String novoProprietario = scan.nextLine();

            System.out.print("Novo CPF (XXX.XXX.XXX-XX): ");
            String novoCPF = scan.nextLine();

            if (!Proprietario.verificadorCPF(novoCPF)) {
                System.out.println("CPF inválido!");
                return;
            }

            String updateSql = "UPDATE Carros SET proprietario = ?, cpf = ? WHERE id = ?";

            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                stmt.setString(1, novoProprietario);
                stmt.setString(2, novoCPF);
                stmt.setInt(3, carro.id);

                int linhas = stmt.executeUpdate();
                if (linhas > 0) {
                    Historico.registrarTransferencia(
                            carro.id,
                            carro.proprietario,
                            carro.cpf,
                            novoProprietario,
                            novoCPF
                    );

                    System.out.println("Propriedade transferida com sucesso!");

                    if (carro.placaAntiga != null) {
                        String novaPlaca = Placa.converterParaMercosul(carro.placaAntiga);
                        Placa.atualizarPlaca(carro.id, novaPlaca);
                        System.out.println("Placa atualizada para Mercosul: " + novaPlaca);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro no banco de dados: " + e.getMessage());
        }
    }

    public static Carro buscarPorPlaca(String placa) throws SQLException {
        try (Connection conn = BancoDeDados.getConnection()) {
            String sql = "SELECT * FROM Carros WHERE placa_antiga = ? OR placa_mercosul = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, placa);
                stmt.setString(2, placa);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    Carro carro = new Carro();
                    carro.id = rs.getInt("id");
                    carro.placaAntiga = rs.getString("placa_antiga");
                    carro.placaMercosul = rs.getString("placa_mercosul");
                    carro.marca = rs.getString("marca");
                    carro.modelo = rs.getString("modelo");
                    carro.cor = rs.getString("cor");
                    carro.proprietario = rs.getString("proprietario");
                    carro.cpf = rs.getString("cpf");
                    carro.ano = rs.getInt("ano");
                    return carro;
                }
            }
        }
        return null;
    }
}
