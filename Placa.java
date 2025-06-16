import java.sql.*;
public class Placa {

    public static String converterParaMercosul(String placaAntiga) {
        
        if (!placaAntiga.matches("[A-Z]{3}-\\d{4}")) {
            throw new IllegalArgumentException("Formato de placa antiga inválido, use AAA-0000");
        }

        String placaSemHifen = placaAntiga.replace("-", ""); //Retira o hífen
        char[] placaArray = placaSemHifen.toCharArray();

        char[] trocaNum = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'}; //Substitui o número pela letra correspondente
        int numAntigo = Character.getNumericValue(placaArray[3]);
        placaArray[3] = trocaNum[numAntigo];

        return new String(placaArray);
    }

    public static boolean validarPlaca(String placa) {

        return placa.matches("[A-Z]{3}-\\d{4}") || //Placa antiga
                placa.matches("[A-Z]{3}\\d[A-Z]\\d{2}"); //Placa Mercosul
    }

    public static boolean existePlaca(String placa) {
        try (Connection conn = BancoDeDados.getConnection()) {
            String sql = "SELECT COUNT(*) AS total FROM Carros " +
                    "WHERE placa_antiga = ? OR placa_mercosul = ?";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, placa);
                stmt.setString(2, placa);

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao verificar placa: " + e.getMessage());
        }
        return false;
    }

    public static boolean atualizarPlaca(int carroId, String novaPlacaMercosul) {
        try (Connection conn = BancoDeDados.getConnection()) {
            String sql = "UPDATE Carros SET placa_antiga = NULL, placa_mercosul = ? WHERE id = ?";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, novaPlacaMercosul);
                stmt.setInt(2, carroId);

                int linhas = stmt.executeUpdate();
                return linhas > 0;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar placa: " + e.getMessage());
            return false;
        }
    }
}
