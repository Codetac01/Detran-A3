import java.sql.*;

public class BancoDeDados {
    private static final String URL = "jdbc:mysql://localhost:3306/sistema_veiculos";
    private static final String USUARIO = "root";
    private static final String SENHA = "MyJavaCarSystem123!";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }

    public static void inicializarBanco() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            String sqlCarros = "CREATE TABLE IF NOT EXISTS Carros ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "placa_antiga VARCHAR(8) UNIQUE,"
                    + "placa_mercosul VARCHAR(7) UNIQUE,"
                    + "marca VARCHAR(50) NOT NULL,"
                    + "modelo VARCHAR(50) NOT NULL,"
                    + "cor VARCHAR(20) NOT NULL,"
                    + "proprietario VARCHAR(100) NOT NULL,"
                    + "cpf VARCHAR(14) NOT NULL,"
                    + "ano INT NOT NULL"
                    + ")";

            String sqlHistorico = "CREATE TABLE IF NOT EXISTS HistoricoTransferencias ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "carro_id INT NOT NULL,"
                    + "data_transferencia TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "antigo_proprietario VARCHAR(100) NOT NULL,"
                    + "antigo_cpf VARCHAR(14) NOT NULL,"
                    + "novo_proprietario VARCHAR(100) NOT NULL,"
                    + "novo_cpf VARCHAR(14) NOT NULL,"
                    + "FOREIGN KEY (carro_id) REFERENCES Carros(id)"
                    + ")";

            stmt.execute(sqlCarros);
            stmt.execute(sqlHistorico);

            System.out.println("Banco de dados inicializado com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro ao inicializar banco de dados: " + e.getMessage());
        }
    }
}
